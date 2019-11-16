/*
 * Copyright 2016 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.demo;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.widget.Toast;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.tensorflow.demo.R;

import org.tensorflow.demo.env.BorderedText;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;
import org.tensorflow.demo.tracking.MultiBoxTracker;

import static java.lang.Thread.sleep;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker defined in the project
 * to detect and then track objects.
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
  private static final Logger LOGGER = new Logger();
  Context context = this;


  /**
   * This is a class of objects, relating objects to their number of occurence in one single picture
   */
  public class Occurences {
    private String objectTitle;
    private String objectOccurence;
    public int occ;

    public Occurences(String objectTitle) {
      this.objectTitle = objectTitle;
    }



    public String getObjectTitle(){
      return this.objectTitle;
    }
    public String getObjectOccurence(){
      return this.objectOccurence;
    }

    public void setObjectTitle(String title){
      this.objectTitle = title;
    }
    public void setObjectOccurence(String occurence){
      this.objectOccurence = occurence;
    }
  }


  /**
   * This is cyclic speech turn to give the application the ability to speak without being interrupted
   * by new recognized objects.
   * The application only "speaks" the classes recognized after a limit of passed turns without speaking
   */
  private int currentSpeechTurn = 0;
  private static final int TALK_SPEECH_TURN = 0;
  private int limitWithoutTalk = ONE_OBJECT_TURN_LIMIT;
  private static final int ONE_OBJECT_TURN_LIMIT = 1;
  private static final int HIGHER_OBJECT_TURN_LIMIT = 1;





  //TextToSpeech  Engine initialized
  //private TextToSpeech tts;



  /**
   * Configuration values for tensorflow Detection API Model. It is using the CoCo dataset Labels
   * The model could be changed through here easily, but BEWARE, the input size should be changed to
   * match the input size of each model used!
   */

  private static final int TF_OD_API_INPUT_SIZE = 480;
  private static final String TF_OD_API_MODEL_FILE =
      "file:///android_asset/detect_plate.pb";
  //private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list.txt";
 // private static String TF_OD_API_LABELS_FILE ;

  /**
   * (Ihab) I will be adding here new Detection modes based on other models to be able to provide
   * packs of recognition that are adapted to each situation and distribute them as such.
   */
  private enum DetectorMode {
    TF_OD_API
  }
  private static final DetectorMode MODE = DetectorMode.TF_OD_API;

  // Minimum detection confidence to track a detection.
  private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;


  private static final boolean MAINTAIN_ASPECT = false;

  private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);

  private static final boolean SAVE_PREVIEW_BITMAP = false;
  private static final float TEXT_SIZE_DIP = 10;

  private Integer sensorOrientation;

  private Classifier detector;

  private long lastProcessingTimeMs;
  private Bitmap rgbFrameBitmap = null;
  private Bitmap croppedBitmap = null;
  private Bitmap cropCopyBitmap = null;

  private boolean computingDetection = false;

  private long timestamp = 0;

  private Matrix frameToCropTransform;
  private Matrix cropToFrameTransform;

  private MultiBoxTracker tracker;

  private byte[] luminanceCopy;
  private static final String TOG = "DetectorActivity";

  private BorderedText borderedText;


  /**
   * This method is called after the creation of the CameraActivity, and through this method we initialize
   * the TextToSpeech Engine and the Classifier.
   * @param size
   * @param rotation
   */
  @Override
  public void onPreviewSizeChosen(final Size size, final int rotation) {
    Log.e(TOG," (onPreviewSizeChosen) ");
    final float textSizePx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);

    tracker = new MultiBoxTracker(this);

    int cropSize = TF_OD_API_INPUT_SIZE;
    String lang=Locale.getDefault().getLanguage();
    try {
      detector = TensorFlowObjectDetectionAPIModel.create(
              getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_INPUT_SIZE);
      cropSize = TF_OD_API_INPUT_SIZE;
    } catch (final IOException e) {
      LOGGER.e("Exception initializing classifier!", e);
      Toast toast = Toast.makeText(
                getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
      toast.show();
      //finish();
    }


    previewWidth = size.getWidth();
    previewHeight = size.getHeight();

    sensorOrientation = rotation - getScreenOrientation();
    LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

    LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
    croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

    frameToCropTransform =
        ImageUtils.getTransformationMatrix(
            previewWidth, previewHeight,
            cropSize, cropSize,
            sensorOrientation, MAINTAIN_ASPECT);

    cropToFrameTransform = new Matrix();
    frameToCropTransform.invert(cropToFrameTransform);

    trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
    trackingOverlay.addCallback(
        new OverlayView.DrawCallback() {
          @Override
          public void drawCallback(final Canvas canvas) {
            tracker.draw(canvas);
            if (isDebug()) {
              tracker.drawDebug(canvas);
            }
          }
        });

    addCallback(
        new OverlayView.DrawCallback() {
          @Override
          public void drawCallback(final Canvas canvas) {
            if (!isDebug()) {
              return;
            }
            final Bitmap copy = cropCopyBitmap;
            if (copy == null) {
              return;
            }

            final int backgroundColor = Color.argb(100, 0, 0, 0);
            canvas.drawColor(backgroundColor);

            final Matrix matrix = new Matrix();
            final float scaleFactor = 2;
            matrix.postScale(scaleFactor, scaleFactor);
            matrix.postTranslate(
                canvas.getWidth() - copy.getWidth() * scaleFactor,
                canvas.getHeight() - copy.getHeight() * scaleFactor);
            canvas.drawBitmap(copy, matrix, new Paint());

            final Vector<String> lines = new Vector<String>();
            if (detector != null) {
              final String statString = detector.getStatString();
              final String[] statLines = statString.split("\n");
              for (final String line : statLines) {
                lines.add(line);
              }
            }
            lines.add("");

            lines.add("Frame: " + previewWidth + "x" + previewHeight);
            lines.add("Crop: " + copy.getWidth() + "x" + copy.getHeight());
            lines.add("View: " + canvas.getWidth() + "x" + canvas.getHeight());
            lines.add("Rotation: " + sensorOrientation);
            lines.add("Inference time: " + lastProcessingTimeMs + "ms");

            borderedText.drawLines(canvas, 10, canvas.getHeight() - 10, lines);
          }
        });
  }

  OverlayView trackingOverlay;

  /**
   * Method that uses the recognizeImage method of the TensorFlowObjectDetectionAPIModel after processing
   * images to be in the format and size specified by the API Model.
   */
  @Override
  protected void processImage() {
    Log.e(TOG," (processImage) ");
    ++timestamp;
    final long currTimestamp = timestamp;
    byte[] originalLuminance = getLuminance();
    tracker.onFrame(
        previewWidth,
        previewHeight,
        getLuminanceStride(),
        sensorOrientation,
        originalLuminance,
        timestamp);
    trackingOverlay.postInvalidate();

    // No mutex needed as this method is not reentrant.
    if (computingDetection) {
      readyForNextImage();
      return;
    }

    computingDetection = true;
    LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

    if (luminanceCopy == null) {
      luminanceCopy = new byte[originalLuminance.length];
    }
    System.arraycopy(originalLuminance, 0, luminanceCopy, 0, originalLuminance.length);
    readyForNextImage();

    final Canvas canvas = new Canvas(croppedBitmap);
    canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
    // For examining the actual TF input.
    if (SAVE_PREVIEW_BITMAP) {
      ImageUtils.saveBitmap(croppedBitmap);
    }

    runInBackground(
            new Runnable() {
              @Override
              public void run() {
                LOGGER.i("Running detection on image " + currTimestamp);
                final long startTime = SystemClock.uptimeMillis();
                final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                final Canvas canvas = new Canvas(cropCopyBitmap);
                final Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Style.STROKE);
                paint.setStrokeWidth(2.0f);

                float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;

                final List<Classifier.Recognition> mappedRecognitions =
                          new LinkedList<Classifier.Recognition>();

                for (final Classifier.Recognition result : results) {
                  final RectF location = result.getBoxes();
                  if (location != null && result.getScore() >= minimumConfidence) {
                    canvas.drawRect(location, paint);

                    cropToFrameTransform.mapRect(location);
                    result.setBoxes(location);
                    mappedRecognitions.add(result);
                  }
                }

                tracker.trackResults(mappedRecognitions, luminanceCopy, currTimestamp);
                try {
                  sleep(500);
                } catch (final Exception e){}

                tracker = new MultiBoxTracker(context);
                trackingOverlay.postInvalidate();
                requestRender();
                computingDetection = false;
              }
            });
  }



  @Override
  protected int getLayoutId() {
    Log.e(TOG," (getLayoutId) ");
    return R.layout.camera_connection_fragment_tracking;
  }

  @Override
  protected Size getDesiredPreviewFrameSize() {
    Log.e(TOG," (getDesiredPreviewFrameSize) ");
    return DESIRED_PREVIEW_SIZE;
  }

  @Override
  public void onSetDebug(final boolean debug) {
    Log.e(TOG," (onSetDebug) ");
    detector.enableStatLogging(debug);
  }
  /**********************TTS CODE partie 4********************/
  @Override
  public void onDestroy() {
    // Don't forget to shutdown tts!

    super.onDestroy();
  }


  /**
   * TextToSpeech initialization at the launch of the class
   * @param status
   */
  //TODO (Ihab) : Add the condition where if a phone doesnt have the modules necessary for TextToSpeech
  // it autimatically detects that and downloads it on its own.







}
