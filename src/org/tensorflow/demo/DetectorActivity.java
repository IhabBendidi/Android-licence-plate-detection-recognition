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

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker defined in the project
 * to detect and then track objects.
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener, TextToSpeech.OnInitListener {
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
  private int currentSpeechTurn = 1;
  private static final int TALK_SPEECH_TURN = 0;
  private int limitWithoutTalk = ONE_OBJECT_TURN_LIMIT;
  private static final int ONE_OBJECT_TURN_LIMIT = 2;
  private static final int TWO_OBJECT_TURN_LIMIT = 4;
  private static final int THREE_OBJECT_TURN_LIMIT = 5;
  private static final int FOUR_OBJECT_TURN_LIMIT = 6;
  private static final int FIVE_OBJECT_TURN_LIMIT = 7;
  private static final int HIGHER_OBJECT_TURN_LIMIT = 10;





  //TextToSpeech  Engine initialized
  private TextToSpeech tts;



  /**
   * Configuration values for tensorflow Detection API Model. It is using the CoCo dataset Labels
   * The model could be changed through here easily, but BEWARE, the input size should be changed to
   * match the input size of each model used!
   */

  private static final int TF_OD_API_INPUT_SIZE = 300;
  private static final String TF_OD_API_MODEL_FILE =
      "file:///android_asset/ssd_mobilenet_v1_android_export.pb";
  //private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list.txt";
  private static String TF_OD_API_LABELS_FILE ;

  /**
   * (Ihab) I will be adding here new Detection modes based on other models to be able to provide
   * packs of recognition that are adapted to each situation and distribute them as such.
   */
  private enum DetectorMode {
    TF_OD_API
  }
  private static final DetectorMode MODE = DetectorMode.TF_OD_API;

  // Minimum detection confidence to track a detection.
  private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.7f;


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
    /**********************TTS CODE partie 2********************/
    tts = new TextToSpeech(this, this);
    /**********************************************************/
    final float textSizePx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);

    tracker = new MultiBoxTracker(this);

    int cropSize = TF_OD_API_INPUT_SIZE;
    String lang=Locale.getDefault().getLanguage();
    if(lang.equals("fr")){
      TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list_fr.txt";
    }
    else{
      TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list.txt";
    }
    try {
      detector = TensorFlowObjectDetectionAPIModel.create(
              getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE);
      cropSize = TF_OD_API_INPUT_SIZE;
    } catch (final IOException e) {
      LOGGER.e("Exception initializing classifier!", e);
      Toast toast = Toast.makeText(
                getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
      toast.show();
      finish();
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
                  final RectF location = result.getLocation();
                  if (location != null && result.getConfidence() >= minimumConfidence) {
                    canvas.drawRect(location, paint);

                    cropToFrameTransform.mapRect(location);
                    result.setLocation(location);
                    mappedRecognitions.add(result);
                  }
                }



                //Here we are trying to not let tracked boxes follow the object when it isn't being recognized anymore
                // TODO(Ihab): there is a much better method to use the tracker, Once there is time I should
                // the recognitions of the tracker get SAFELY (I stress the word) into mappedRecognitions to be reused later
                if (currentSpeechTurn==TALK_SPEECH_TURN){
                  tracker.trackResults(mappedRecognitions, luminanceCopy, currTimestamp);
                } else if (currentSpeechTurn==limitWithoutTalk){
                  tracker = new MultiBoxTracker(context);
                }

                trackingOverlay.postInvalidate();
                requestRender();


                /**
                 * This is the thread where the recognized results get processed to know whether they
                 * should be "spoken" or not and how and when
                 */
                Thread logoTimer = new Thread() {
                  public void run() {
                    try {
                      //These conditions are in order to know when to speak, whenever there are results
                      // and the its the turn of speaking.
                      if (mappedRecognitions.size()==0 && currentSpeechTurn == TALK_SPEECH_TURN){
                        // In this case, there is no object recognized, so we stay in the turn of speaking
                        // until we find an object to recognize.
                        currentSpeechTurn = TALK_SPEECH_TURN;
                      }else if (mappedRecognitions.size()>0 && currentSpeechTurn == TALK_SPEECH_TURN){

                        /**
                         * here we create an array list of the Occurences of each object, using the
                         * mappedRecognitions while avoiding repetition. This is in order to give concise speech as output
                         * with processed results and copy normal speech while using all gramatical rules as much as possible
                         * Yep! Abstraction is important even in speech! :D
                         */
                        List<Occurences> mappedOccurences = new ArrayList<>();
                        for (Classifier.Recognition mapped : mappedRecognitions){

                          int occu = 0;
                          Occurences tempOccurence = new Occurences(mapped.getTitle());

                          for (Classifier.Recognition mapping : mappedRecognitions){
                            if (mapped.getTitle().equals(mapping.getTitle())){
                              occu++;
                            }
                          }

                          tempOccurence.setObjectOccurence(occu + "");
                          tempOccurence.occ = occu;
                          //

                          boolean add = true;
                          if (mappedOccurences.size()>0){
                            for ( Occurences mapOccu : mappedOccurences){
                              if (!tempOccurence.getObjectTitle().equals(mapOccu.getObjectTitle())){
                                add = true;
                              }else{
                                add = false;
                                break;
                              }
                            }
                            if(add){
                              mappedOccurences.add(tempOccurence);
                            }

                          }else{
                            mappedOccurences.add(tempOccurence);
                          }
                        }


                        /**
                         * This is a crude way to dynamically change the duration of wait for each speech,
                         * depending on how long it is each time, to make it very comfortable with no wait time.
                         */
                        // TODO (Ihab) : I gotta make this little part into a method to be reused later
                        // in future Models.
                        if(mappedOccurences.size()==1){
                          limitWithoutTalk = ONE_OBJECT_TURN_LIMIT;
                        } else if (mappedOccurences.size()==2){
                          limitWithoutTalk = TWO_OBJECT_TURN_LIMIT;
                        } else if (mappedOccurences.size()==3){
                          limitWithoutTalk = THREE_OBJECT_TURN_LIMIT;
                        } else if (mappedOccurences.size()== 4){
                          limitWithoutTalk = FOUR_OBJECT_TURN_LIMIT;
                        } else if (mappedOccurences.size()==5){
                          limitWithoutTalk = FIVE_OBJECT_TURN_LIMIT;
                        }else{
                          limitWithoutTalk = HIGHER_OBJECT_TURN_LIMIT;
                        }
                        sleep(500);
                        //This firstSpeak will flush (interrupt) the older speech. They already had ample time to finish their speech
                        // and we don't need a long Queue of words waiting their turn. It has to be as "real-time" as possible.
                        firstSpeak("");
                        int occurenceIndex = 0;
                        for (Occurences spokenOccurence : mappedOccurences){
                          String plural_s;

                          /**
                           * This part makes the recognitions into plural if there are many occurences of it
                           * The words feel cringeworthy to a grammatical nazi like me if they aren't perfect
                           * the irregular words are changed first into their plural, and then an "s" is added to regular words.
                           * The world should be changed if the labels are changed.
                           */
                          // TODO (Ihab) : Make this too a method, so that its reused in other Neural Networks
                          if (spokenOccurence.occ >1){
                            plural_s = "";
                            if (spokenOccurence.getObjectTitle().equals("person")){
                              spokenOccurence.setObjectTitle("people");
                            } else if (spokenOccurence.getObjectTitle().equals("bus")){
                              spokenOccurence.setObjectTitle("buses");
                            } else if (spokenOccurence.getObjectTitle().equals("bench")){
                              spokenOccurence.setObjectTitle("benches");
                            } else if (spokenOccurence.getObjectTitle().equals("skis")){
                              spokenOccurence.setObjectTitle("skis");
                            } else if (spokenOccurence.getObjectTitle().equals("wine glass")){
                              spokenOccurence.setObjectTitle("wine glasses");
                            } else if (spokenOccurence.getObjectTitle().equals("sandwich")){
                              spokenOccurence.setObjectTitle("sandwiches");
                            } else if (spokenOccurence.getObjectTitle().equals("couch")){
                              spokenOccurence.setObjectTitle("couches");
                            } else if (spokenOccurence.getObjectTitle().equals("scissors")){
                              spokenOccurence.setObjectTitle("scissors");
                            } else if (spokenOccurence.getObjectTitle().equals("piece of bread")){
                              spokenOccurence.setObjectTitle("pieces of bread");
                            } else {
                              plural_s = "s";
                            }
                          }else{
                            plural_s = "";
                          }

                          /**
                           * Gramma Nazi mode again :D
                           * "And" should be added before the last recognition to be spoken. That way,
                           * it gives a smooth speech, and would signal the end of the speech.
                           */
                          String and;
                          if (mappedOccurences.size()==1){
                            and = "";
                          } else if(occurenceIndex == mappedOccurences.size()-1){
                            and = "and ";
                          } else {
                            and = "";
                          }
                          occurenceIndex++;
                          speakOut(and + spokenOccurence.getObjectOccurence()+ " " +spokenOccurence.getObjectTitle() + plural_s);
                        }
                        currentSpeechTurn++;
                      }else if(currentSpeechTurn==limitWithoutTalk){
                        //When you reach the end of cyclical turn, u go back to the start, which is the turn of the speech
                        currentSpeechTurn = TALK_SPEECH_TURN;
                      }else{
                        currentSpeechTurn++;
                      }
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }

                  }
                };
                logoTimer.start();

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
    if (tts != null) {
      firstSpeak(getResources().getString(R.string.detector_msg2));
      tts.stop();
      tts.shutdown();
    }
    super.onDestroy();
  }


  /**
   * TextToSpeech initialization at the launch of the class
   * @param status
   */
  //TODO (Ihab) : Add the condition where if a phone doesnt have the modules necessary for TextToSpeech
  // it autimatically detects that and downloads it on its own.
  @Override
  public void onInit(int status) {

    if (status == TextToSpeech.SUCCESS) {

      int result;
      String lang=Locale.getDefault().getLanguage();
      if(lang.equals("fr")){
        result = tts.setLanguage(Locale.FRANCE);
      }
      else{
        result = tts.setLanguage(Locale.US);
      }

      if (result == TextToSpeech.LANG_MISSING_DATA
              || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        Log.e("TTS", "This Language is not supported");
      } else {
        //btnSpeak.setEnabled(true);
        firstSpeak(getResources().getString(R.string.detector_msg1));
      }

    } else {
      Log.e("TTS", "Initilization Failed!");
    }

  }

  private void speakOut(String text) {
    tts.speak(text, TextToSpeech.QUEUE_ADD, null);
  }
  private void firstSpeak(String text){
    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
  }



}
