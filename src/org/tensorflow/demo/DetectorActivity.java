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

import android.content.Context;
import android.content.ContextWrapper;
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
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import org.tensorflow.demo.env.BorderedText;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;
import org.tensorflow.demo.tracking.MultiBoxTracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import static java.lang.Thread.sleep;
import static java.security.AccessController.getContext;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker defined in the project
 * to detect and then track objects.
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
  private static final Logger LOGGER = new Logger();
  Context context = this;





  /**
   * This is cyclic speech turn to give the application the ability to speak without being interrupted
   * by new recognized objects.
   * The application only "speaks" the classes recognized after a limit of passed turns without speaking
   */

  private int limitWithoutTalk = ONE_OBJECT_TURN_LIMIT;
  private static final int ONE_OBJECT_TURN_LIMIT = 1;


  public String text_recon = "__";
  Bitmap rotatedBitmap;















  /**
   * Configuration values for tensorflow Detection API Model. It is using the CoCo dataset Labels
   * The model could be changed through here easily, but BEWARE, the input size should be changed to
   * match the input size of each model used!
   */

  private static final int TF_OD_API_INPUT_SIZE = 480;
  private static final String TF_OD_API_MODEL_FILE =
      "file:///android_asset/detect_plate.pb";








  public enum DetectorMode {
    TF_OD_API
  }
  private static final DetectorMode MODE = DetectorMode.TF_OD_API;

  // Minimum detection confidence to track a detection.
  private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;



  private String imagePath;

  private String time;

  private Bitmap temporaryBitmap;
  Bitmap resultsBitmap;



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


  private String timeName;

  String outputName;

  private byte[] luminanceCopy;
  private static final String TOG = "DetectorActivity";

  private BorderedText borderedText;

////////////////////////////
  private Speaker speaker;
  PlateDbHelper dbHelper;
  /////////////////////////////////////////////////////:


  /**
   * This method is called after the creation of the CameraActivity, and through this method we initialize
   * the TextToSpeech Engine and the Classifier.
   * @param size
   * @param rotation
   */
  @Override
  public void onPreviewSizeChosen(final Size size, final int rotation) {
    Log.e(TOG," (onPreviewSizeChosen) ");
    //////////////////////////////////////
    speaker = new Speaker(this);
    dbHelper = new PlateDbHelper(this);

      ///////////////////////////////
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
                temporaryBitmap =  Bitmap.createBitmap(rgbFrameBitmap);

                final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                final Canvas canvas = new Canvas(cropCopyBitmap);
                final Paint paint = new Paint();
                paint.setColor(Color.GREEN);
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
                if (mappedRecognitions.size()>0){
                    speaker.speakOut("Detected");
                }

                tracker.trackResults(mappedRecognitions, luminanceCopy, currTimestamp);
                //try {
                    //sleep(1000);
                //} catch (final Exception e){}
                //tracker = new MultiBoxTracker(context);
                trackingOverlay.postInvalidate();
                requestRender();
                //computingDetection = false;
                Log.e(TOG,"1");





                Runnable myRunnable =
                        new Runnable(){
                    public void run(){
                        Log.e(TOG,"2");
                        List<Classifier.Recognition> crops = mappedRecognitions;
                        Log.e(TOG,"3");
                        if(crops.size()>0){
                            Log.e(TOG,"3");

                            toast.show();
                            for (final Classifier.Recognition r : crops) {
                                Log.e(TOG,"4");
                                final RectF l = r.getBoxes();
                                Log.e(TOG,"5");

                                Log.e(TOG,"6");
                                if (temporaryBitmap.getWidth() >= (int)l.right && temporaryBitmap.getHeight()>= (int)l.bottom){
                                    resultsBitmap = Bitmap.createBitmap(temporaryBitmap, (int) l.left,(int)l.top,(int)l.right - (int) l.left, (int)l.bottom - (int)l.top);
                                    Log.e(TOG,"6.5");
                                    final Bitmap outputBitmap = drawBoxes(temporaryBitmap,l);/////// This is the make the full image with the green boxes inside
                                    Bitmap scaledBitmap = rescaleBitmap(resultsBitmap,80);
                                    Matrix matrix = new Matrix();
                                    matrix.postRotate(90);
                                    rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(),
                                            matrix, true);
                                    //text_recon = "_";
                                    // Firebase text detection stuff
                                    FirebaseVisionImage textImage = FirebaseVisionImage.fromBitmap(rotatedBitmap);
                                    FirebaseVisionTextRecognizer fireDetector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                                    final Task<FirebaseVisionText> text_result =
                                          fireDetector.processImage(textImage)
                                                  .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                                    @Override
                                                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                                      // Task completed successfully
                                                        Log.e(TOG,"2 - Text extracted successfully ...... : " + firebaseVisionText.getText());
                                                        //text_recon = firebaseVisionText.getText();
                                                        text_recon = "";
                                                        for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()){
                                                            for (FirebaseVisionText.Line line: block.getLines()){
                                                                for (FirebaseVisionText.Element element: line.getElements()){
                                                                    text_recon += element.getText();
                                                                }
                                                            }
                                                        }
                                                        Log.e(TOG,"7");
                                                        timeName = "" + System.currentTimeMillis();
                                                        //imagePath = saveToInternalStorage(resultsBitmap,timeName);// /data/user/0/org.tensorflow.demo/app_imageDir/1574040156601.jpg
                                                        Log.e(TOG,"8");
                                                        imagePath = saveToInternalStorage(outputBitmap,timeName); // this is to save the full image with the green boxes inside
                                                        Log.e(TOG,imagePath);
                                                        time = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                                        outputName = imagePath + " " + text_recon + " " + locationText.getText() + " " + time + "\r\n";
                                                        // Writing output ( Paths for images, location and time of the detection)
                                                        Plate plate = new Plate( locationText.getText().toString(),  time,  text_recon,  imagePath);
                                                        dbHelper.addPlate(plate);
                                                        //try{
                                                            //fWriter.write(outputName);
                                                        //}catch(IOException e){
                                                           // Log.e(TOG,e.toString());
                                                        //}
                                                    }
                                                  })
                                                  .addOnFailureListener(
                                                          new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                              // Task failed with an exception
                                                              Log.e(TOG,"Task failed miserably, with exception ...... : " + e.toString());
                                                            }
                                                          });
                                    //text_recon=text_result.onSuccessTask(text_result).getResult().getText();


                                }
                            }
                        }
                    }
                };
                myRunnable.run();
                try {
                    sleep(300);
                } catch (final Exception e){}
                tracker = new MultiBoxTracker(context);
                try {
                    sleep(1000);
                } catch (final Exception e){}
                computingDetection = false;

              }
            });
  }





  private String saveToInternalStorage(Bitmap bitmapImage,String name){
    ContextWrapper cw = new ContextWrapper(getApplicationContext());
    // path to /data/data/yourapp/app_data/imageDir
    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

    String fullName = name + ".jpg";
    // Create imageDir
    File mypath=new File(directory,fullName);

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(mypath);
      // Use the compress method on the BitMap object to write image to the OutputStream
      bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        fos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return directory.getAbsolutePath() + "/" + fullName;
  }


  protected Bitmap drawBoxes(Bitmap bitmap,RectF rect){
      Paint myPaint = new Paint();
      Bitmap b = Bitmap.createBitmap(bitmap);
      myPaint.setColor(Color.GREEN);
      myPaint.setStyle(Paint.Style.STROKE);
      myPaint.setStrokeWidth(2);
      Canvas canvas = new Canvas(b);
      canvas.drawRect(rect,myPaint);
      return b;

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
    speaker.close();
    dbHelper.close();


    try{
      fWriter.close();
    }catch (IOException e){
      Log.e(TOG,e.toString());
    }

    super.onDestroy();
  }

    public Bitmap rescaleBitmap(Bitmap b,int h){
        float aspectRatio = b.getWidth() /
                (float) b.getHeight();
        int width = h;
        int height = Math.round(width / aspectRatio);

        Bitmap result_bitmap = Bitmap.createScaledBitmap(
                b, width, height, false);
        return result_bitmap;
    }










}
