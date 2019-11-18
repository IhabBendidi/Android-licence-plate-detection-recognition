/*
 * This activity gives necessary method to
 */

package org.tensorflow.demo;

import android.Manifest;
import com.google.android.gms.location.LocationServices;

import android.location.Location;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;
import org.tensorflow.demo.R; // Explicit import needed for internal Google builds.


public abstract class CameraActivity extends Activity
    implements OnImageAvailableListener, Camera.PreviewCallback {
  private static final Logger LOGGER = new Logger();

  private static final int PERMISSIONS_REQUEST = 1;

  private FusedLocationProviderClient fusedLocationClient;
  protected TextView locationText;





  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
  private static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;


  protected Toast toast;

  protected FileWriter fWriter ;


  // Filename of the file with the detection output absolute paths, and location and time of the detection
  protected String outputFileName = "PATHS.txt";


  private boolean debug = false;

  private Handler handler;
  private HandlerThread handlerThread;
  private boolean useCamera2API;
  private boolean isProcessingFrame = false;
  private byte[][] yuvBytes = new byte[3][];
  private int[] rgbBytes = null;
  private int yRowStride;

  protected int previewWidth = 0;
  protected int previewHeight = 0;

  private Runnable postInferenceCallback;
  private Runnable imageConverter;

  private static final String TOG ="CameraActivity";





  //This method creates the first camera view, and calls the cameraconnectionFragment using setFragment
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(null);
    toast = Toast.makeText(getApplicationContext(), "Saving the detection results", Toast.LENGTH_SHORT);
    try{
      fWriter = new FileWriter(getApplicationContext().getFilesDir() + "/" + outputFileName,true);
    }catch (IOException e){
      Log.e(TOG,e.toString());
    }

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.activity_camera);
    locationText = findViewById(R.id.locationvalue);
    String country = getDeviceCountryCode(getApplicationContext());
    locationText.setText(country);

    if (hasPermission()) {
      setFragment();
    } else {
      requestPermission();
      if (hasPermission()) {
        setFragment();
      }
    }
  }

  private byte[] lastPreviewFrame;




  private static String getDeviceCountryCode(Context context) {
    String countryCode;

    // try to get country code from TelephonyManager service
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if(tm != null) {
      // query first getSimCountryIso()
      countryCode = tm.getSimCountryIso();
      if (countryCode != null && countryCode.length() == 2)
        return countryCode.toLowerCase();

      if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
        // special case for CDMA Devices
        countryCode = getCDMACountryIso();
      } else {
        // for 3G devices (with SIM) query getNetworkCountryIso()
        countryCode = tm.getNetworkCountryIso();
      }

      if (countryCode != null && countryCode.length() == 2)
        return countryCode.toLowerCase();
    }

    // if network country not available (tablets maybe), get country code from Locale class
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      countryCode = context.getResources().getConfiguration().getLocales().get(0).getCountry();
    } else {
      countryCode = context.getResources().getConfiguration().locale.getCountry();
    }

    if (countryCode != null && countryCode.length() == 2)
      return  countryCode.toLowerCase();

    // general fallback to "us"
    return "us";
  }


  private static String getCDMACountryIso() {
    try {
      // try to get country code from SystemProperties private class
      Class<?> systemProperties = Class.forName("android.os.SystemProperties");
      Method get = systemProperties.getMethod("get", String.class);

      // get homeOperator that contain MCC + MNC
      String homeOperator = ((String) get.invoke(systemProperties,
              "ro.cdma.home.operator.numeric"));

      // first 3 chars (MCC) from homeOperator represents the country code
      int mcc = Integer.parseInt(homeOperator.substring(0, 3));

      // mapping just countries that actually use CDMA networks
      switch (mcc) {
        case 330: return "PR";
        case 310: return "US";
        case 311: return "US";
        case 312: return "US";
        case 316: return "US";
        case 283: return "AM";
        case 460: return "CN";
        case 455: return "MO";
        case 414: return "MM";
        case 619: return "SL";
        case 450: return "KR";
        case 634: return "SD";
        case 434: return "UZ";
        case 232: return "AT";
        case 204: return "NL";
        case 262: return "DE";
        case 247: return "LV";
        case 255: return "UA";
      }
    } catch (ClassNotFoundException ignored) {
    } catch (NoSuchMethodException ignored) {
    } catch (InvocationTargetException ignored) {
    } catch (IllegalAccessException ignored) {
    } catch (NullPointerException ignored) {
    }

    return null;
  }




  protected int[] getRgbBytes() {
    imageConverter.run();
    return rgbBytes;
  }

  protected int getLuminanceStride() {
    return yRowStride;
  }

  protected byte[] getLuminance() {
    return yuvBytes[0];
  }

  /**
   * Callback for android.hardware.Camera API
   */
  @Override
  public void onPreviewFrame(final byte[] bytes, final Camera camera) {
    if (isProcessingFrame) {
      return;
    }

    try {
      // Initialize the storage bitmaps once when the resolution is known.
      if (rgbBytes == null) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        previewHeight = previewSize.height;
        previewWidth = previewSize.width;
        rgbBytes = new int[previewWidth * previewHeight];
        onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
      }
    } catch (final Exception e) {
      return;
    }

    isProcessingFrame = true;
    lastPreviewFrame = bytes;
    yuvBytes[0] = bytes;
    yRowStride = previewWidth;

    imageConverter =
        new Runnable() {
          @Override
          public void run() {
            ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
          }
        };

    postInferenceCallback =
        new Runnable() {
          @Override
          public void run() {
            camera.addCallbackBuffer(bytes);
            isProcessingFrame = false;
          }
        };
    processImage();

  }

  /**
   * Callback for Camera2 API
   * This callback checks the presence of new images to be processed in the ImageReader Queue and
   * processes it and recognizes the objects in it
   */
  @Override
  public void onImageAvailable(final ImageReader reader) {
    //We need wait until we have some size from onPreviewSizeChosen
    if (previewWidth == 0 || previewHeight == 0) {
      return;
    }
    if (rgbBytes == null) {
      rgbBytes = new int[previewWidth * previewHeight];
    }
    try {
      final Image image = reader.acquireLatestImage();

      if (image == null) {
        return;
      }

      if (isProcessingFrame) {
        image.close();
        return;
      }

      isProcessingFrame = true;
      Trace.beginSection("imageAvailable");
      final Plane[] planes = image.getPlanes();
      fillBytes(planes, yuvBytes);
      yRowStride = planes[0].getRowStride();
      final int uvRowStride = planes[1].getRowStride();
      final int uvPixelStride = planes[1].getPixelStride();

      imageConverter =
          new Runnable() {
            @Override
            public void run() {
              ImageUtils.convertYUV420ToARGB8888(
                  yuvBytes[0],
                  yuvBytes[1],
                  yuvBytes[2],
                  previewWidth,
                  previewHeight,
                  yRowStride,
                  uvRowStride,
                  uvPixelStride,
                  rgbBytes);
            }
          };

      postInferenceCallback =
          new Runnable() {
            @Override
            public void run() {
              image.close();
              isProcessingFrame = false;
            }
          };
      processImage();

    } catch (final Exception e) {
      Trace.endSection();
      return;
    }
    Trace.endSection();
  }

  @Override
  public synchronized void onStart() {
    super.onStart();
  }

  @Override
  public synchronized void onResume() {
    super.onResume();
    //launch the inference thread
    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
    try{
      fWriter = new FileWriter(outputFileName,true);
    }catch (IOException e){
      Log.e(TOG,e.toString());
    }
  }

  @Override
  public synchronized void onPause() {

    if (!isFinishing()) {
      finish();
    }

    handlerThread.quitSafely();
    try {
      handlerThread.join();
      handlerThread = null;
      handler = null;
    } catch (final InterruptedException e) {
      LOGGER.e(e, "Exception!");
    }
    try{
      fWriter.close();
    }catch (IOException e){
      Log.e(TOG,e.toString());
    }

    super.onPause();
  }

  @Override
  public synchronized void onStop() {
    try{
      fWriter.close();
    }catch (IOException e){
      Log.e(TOG,e.toString());
    }
    super.onStop();
  }

  @Override
  public synchronized void onDestroy() {
    try{
      fWriter.close();
    }catch (IOException e){
      Log.e(TOG,e.toString());
    }
    super.onDestroy();
  }



  protected synchronized void runInBackground(final Runnable r) {
    if (handler != null) {
      handler.post(r);
    }
  }

  @Override
  public void onRequestPermissionsResult(
      final int requestCode, final String[] permissions, final int[] grantResults) {
    if (requestCode == PERMISSIONS_REQUEST) {
      if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED
          && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        setFragment();
      } else {
        requestPermission();
      }
    }
  }

  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
          checkSelfPermission(PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA) ||
          shouldShowRequestPermissionRationale(PERMISSION_STORAGE)) {
        Toast.makeText(CameraActivity.this,
            "Camera AND storage AND Location permission are required for this demo", Toast.LENGTH_LONG).show();
      }
      requestPermissions(new String[] {PERMISSION_CAMERA, PERMISSION_STORAGE}, PERMISSIONS_REQUEST);
    }
  }

  // Returns true if the device supports the required hardware level, or better.
  private boolean isHardwareLevelSupported(
      CameraCharacteristics characteristics, int requiredLevel) {
    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
      return requiredLevel == deviceLevel;
    }
    // deviceLevel is not LEGACY, can use numerical sort
    return requiredLevel <= deviceLevel;
  }
  /*
  chooses the camera the use, to choose whether front or back camera, and whether Camera2API is available
   */
  private String chooseCamera() {
    final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      for (final String cameraId : manager.getCameraIdList()) {
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        // We don't use a front facing camera in this sample.
        final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
          continue;
        }

        final StreamConfigurationMap map =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        if (map == null) {
          continue;
        }

        // Fallback to camera1 API for internal cameras that don't have full support.
        // This should help with legacy situations where using the camera2 API causes
        // distorted or otherwise broken previews.
        useCamera2API = (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
            || isHardwareLevelSupported(characteristics, 
                                        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
        return cameraId;
      }
    } catch (CameraAccessException e) {
      LOGGER.e(e, "Not allowed to access camera");
    }

    return null;
  }
  // Sets the camera Connection Fragment
  protected void setFragment() {
    String cameraId = chooseCamera();
    Fragment fragment;
    if (useCamera2API) {
      CameraConnectionFragment camera2Fragment =
          CameraConnectionFragment.newInstance(
              new CameraConnectionFragment.ConnectionCallback() {
                @Override
                public void onPreviewSizeChosen(final Size size, final int rotation) {
                  previewHeight = size.getHeight();
                  previewWidth = size.getWidth();
                  CameraActivity.this.onPreviewSizeChosen(size, rotation);
                }
              },
              this,
              getLayoutId(),
              getDesiredPreviewFrameSize());

      camera2Fragment.setCamera(cameraId);
      fragment = camera2Fragment;
    } else {
      fragment =
          new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
    }
    getFragmentManager()
        .beginTransaction()
        .replace(R.id.container, fragment)
        .commit();

  }

  protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (int i = 0; i < planes.length; ++i) {
      final ByteBuffer buffer = planes[i].getBuffer();
      if (yuvBytes[i] == null) {
        yuvBytes[i] = new byte[buffer.capacity()];
      }
      buffer.get(yuvBytes[i]);
    }
  }

  public boolean isDebug() {
    return debug;
  }

  public void requestRender() {
    final OverlayView overlay = (OverlayView) findViewById(R.id.debug_overlay);
    if (overlay != null) {
      overlay.postInvalidate();
    }
  }

  public void addCallback(final OverlayView.DrawCallback callback) {
    final OverlayView overlay = (OverlayView) findViewById(R.id.debug_overlay);
    if (overlay != null) {
      overlay.addCallback(callback);
    }
  }

  public void onSetDebug(final boolean debug) {
    Log.e(TOG," (onSetDebug) ");
  }

  @Override
  public boolean onKeyDown(final int keyCode, final KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
      debug = !debug;
      requestRender();
      onSetDebug(debug);
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  protected void readyForNextImage() {
    if (postInferenceCallback != null) {
      postInferenceCallback.run();
    }
  }

  protected int getScreenOrientation() {
    switch (getWindowManager().getDefaultDisplay().getRotation()) {
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_90:
        return 90;
      default:
        return 0;
    }
  }

  protected abstract void processImage();

  protected abstract void onPreviewSizeChosen(final Size size, final int rotation);
  protected abstract int getLayoutId();
  protected abstract Size getDesiredPreviewFrameSize();
}
