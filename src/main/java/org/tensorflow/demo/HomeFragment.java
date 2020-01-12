package org.tensorflow.demo;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.os.Trace;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static java.lang.Thread.sleep;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements ImageReader.OnImageAvailableListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static final String EXTRA_INFO = "default";
    ContextWrapper cw ;

    private OnFragmentInteractionListener mListener;
    private static final Logger LOGGER = new Logger();

    private static final int PERMISSIONS_REQUEST = 1;

    private FusedLocationProviderClient fusedLocationClient;
    protected TextView locationText;
    public String text_recon = "__";
    Bitmap rotatedBitmap;
    Context context = getContext();

    static boolean continuousScan = false;



    /**
     * Configuration values for tensorflow Detection API Model. It is using the CoCo dataset Labels
     * The model could be changed through here easily, but BEWARE, the input size should be changed to
     * match the input size of each model used!
     */

    private static final int TF_OD_API_INPUT_SIZE = 480;
    private static final String TF_OD_API_MODEL_FILE =
            "file:///android_asset/detect_plate.pb";








    private enum DetectorMode {
        TF_OD_API
    }
    private static final DetectorActivity.DetectorMode MODE = DetectorActivity.DetectorMode.TF_OD_API;

    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;



    private String imagePath;

    private String time;

    private Bitmap temporaryBitmap;
    Bitmap resultsBitmap;



    private static final boolean MAINTAIN_ASPECT = false;



    //private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
    private static final Size DESIRED_PREVIEW_SIZE = new Size(700, 600);

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
    OverlayView trackingOverlay;


    private BorderedText borderedText;

    ////////////////////////////
    private Speaker speaker;
    PlateDbHelper dbHelper;
    /////////////////////////////////////////////////////:





    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;


    protected Toast toast;

    protected FileWriter fWriter ;


    // Filename of the file with the detection output absolute paths, and location and time of the detection
    protected String outputFileName = "resul.txt";


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

    private static final String TOG ="HomeCameraFragment";


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



        speaker = new Speaker(getActivity());
        dbHelper = new PlateDbHelper(getContext());
        cw = new ContextWrapper(getActivity());
        toast = Toast.makeText(getContext(), "Saving the detection results", Toast.LENGTH_SHORT);
        try{

            //Log.e(TOG,getApplicationContext().getFilesDir().getAbsolutePath()); ///data/user/0/org.tensorflow.demo/files
            ContextWrapper cwd = new ContextWrapper(getContext());
            File the_directory = cwd.getDir("assets", Context.MODE_APPEND);
            File outpath=new File(the_directory,outputFileName);
            if(!outpath.exists()){
                outpath.createNewFile();
            }
            // fWriter = new FileWriter(the_directory.getAbsolutePath() + "/" + outputFileName,true);
            //Log.e(TOG,outpath.exists() + "    2");
            fWriter = new FileWriter(outpath,true);
            Log.e(TOG,outpath.getAbsolutePath() + "    1");
            //Log.e(TOG,outpath.exists() + "    3");
        } catch (FileNotFoundException e){
            Log.e(TOG,"This is so weird :  " + e.toString());
        } catch (IOException e){
            Log.e(TOG,e.toString());
        }

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //setContentView(R.layout.activity_camera);
        locationText = new TextView(getActivity());
        String pays = getDeviceCountryCode(getContext());
        CountryCodes c = new CountryCodes();
        String country = CountryCodes.getKey(c.map,pays.toUpperCase());

        locationText.setText(country);

    }

    // Sets the camera Connection Fragment
    protected  void setFragment(ViewGroup container,LayoutInflater inflater) {
        String cameraId = chooseCamera();
        androidx.fragment.app.Fragment fragment;
        //if (useCamera2API) {
        final HomeFragment  homeFragment = this;
        CameraConnectionFragment2 camera2Fragment =
                CameraConnectionFragment2.newInstance(
                        new CameraConnectionFragment2.ConnectionCallback() {
                            @Override
                            public void onPreviewSizeChosen(final Size size, final int rotation) {
                                previewHeight = size.getHeight();
                                previewWidth = size.getWidth();
                                homeFragment.onPreviewSizeChosen(size, rotation);
                            }
                            },
                        this,
                        getLayoutId(),
                        getDesiredPreviewFrameSize(container,inflater));

        camera2Fragment.setCamera(cameraId);
        fragment = camera2Fragment;
        //}
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.initialContainer, fragment,"CameraTag")
                .commit();

    }

    private String chooseCamera() {
        final CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View cameraView = inflater.inflate(R.layout.fragment_home, container, false);
        if (hasPermission()) {
            setFragment(container,inflater);
            /*if (continuousScan){
                setFragment();
            }*/
        } else {
            requestPermission();
            if (hasPermission()) {
                /*if (continuousScan){
                    setFragment();
                }*/
                setFragment(container,inflater);
            }
        }
        final HomeFragment hFragment = this;
        Button activeScan = cameraView.findViewById(R.id.activeScan);
        activeScan.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hFragment.continuousScan=true;
                    }
                }
        );
        //return inflater.inflate(R.layout.fragment_home, container, false);
        return cameraView;

    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Callback for Camera2 API
     * This callback checks the presence of new images to be processed in the ImageReader Queue and
     * processes it and recognizes the objects in it
     */
    @Override
    public void onImageAvailable(final ImageReader reader) {
        //We need wait until we have some size from onPreviewSizeChosen
        Log.e(TOG,"OnImageAvailable");
        if (previewWidth == 0 || previewHeight == 0) {
            Log.e(TOG,"wallou");
            return;
        }
        if (rgbBytes == null) {
            Log.e(TOG,"wallou 2");
            rgbBytes = new int[previewWidth * previewHeight];
            Log.e(TOG,"wallou 3");
        }
        try {
            final Image image = reader.acquireLatestImage();

            if (image == null) {
                Log.e(TOG,"wallou 4");
                return;
            }

            if (isProcessingFrame) {
                Log.e(TOG,"wallou 5");
                image.close();
                Log.e(TOG,"wallou 6");
                return;
            }
            Log.e(TOG,"wallou 7");
            isProcessingFrame = true;
            Trace.beginSection("imageAvailable");
            final Image.Plane[] planes = image.getPlanes();
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

            Log.e(TOG,"wallou 8");
            //isProcessingFrame = false;
            //image.close();

            postInferenceCallback =
                    new Runnable() {
                        @Override
                        public void run() {
                            image.close();
                            isProcessingFrame = false;
                        }
                    };

            //processImage();

            if(this.continuousScan == true){
                Log.e("Continu STATE:::","1");
                processImage();
            }else{
                Log.e("Continu STATE:::","0");
                postInferenceCallback.run();
            }


        } catch (final Exception e) {
            Log.e(TOG,"wallou 9");
            Log.e(TOG,e.getMessage());

            Trace.endSection();
            return;
        }
        /*if(this.continuousScan == true){
            Log.e("Continu STATE:::","1");
            processImage();
        }else{
            Log.e("Continu STATE:::","0");
        }*/
        Trace.endSection();
    }



    protected void fillBytes(final Image.Plane[] planes, final byte[][] yuvBytes) {
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



    protected byte[] getLuminance() {
        return yuvBytes[0];
    }


    protected int[] getRgbBytes() {
        imageConverter.run();
        return rgbBytes;
    }

    protected int getLuminanceStride() {
        return yRowStride;
    }


    protected void readyForNextImage() {
        if (postInferenceCallback != null) {
            postInferenceCallback.run();
        }
    }


    protected synchronized void runInBackground(final Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }


    public void requestRender() {
        final OverlayView overlay = (OverlayView) getView().findViewById(R.id.debug_overlay);
        if (overlay != null) {
            overlay.postInvalidate();
        }
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


    public Bitmap rescaleBitmap(Bitmap b,int h){
        float aspectRatio = b.getWidth() /
                (float) b.getHeight();
        int width = h;
        int height = Math.round(width / aspectRatio);

        Bitmap result_bitmap = Bitmap.createScaledBitmap(
                b, width, height, false);
        return result_bitmap;
    }


    private String saveToInternalStorage(Bitmap bitmapImage,String name){
        //ContextWrapper cw = new ContextWrapper(getActivity());
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



    public boolean isDebug() {
        return debug;
    }



    public boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getActivity().checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    getActivity().checkSelfPermission(PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA) ||
                    shouldShowRequestPermissionRationale(PERMISSION_STORAGE)) {
                Toast.makeText(getActivity(),
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





    /**
     * Method that uses the recognizeImage method of the TensorFlowObjectDetectionAPIModel after processing
     * images to be in the format and size specified by the API Model.
     */

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
                        paint.setStyle(Paint.Style.STROKE);
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
                        tracker = new MultiBoxTracker(getContext());
                        try {
                            sleep(1000);
                        } catch (final Exception e){}
                        computingDetection = false;

                    }
                });
    }




    /**
     * This method is called after the creation of the CameraActivity, and through this method we initialize
     * the TextToSpeech Engine and the Classifier.
     * @param size
     * @param rotation
     */

    public void onPreviewSizeChosen(final Size size, final int rotation) {
        Log.e(TOG," (onPreviewSizeChosen) ");
        //////////////////////////////////////
        //speaker = new Speaker(getActivity());
        //dbHelper = new PlateDbHelper(getContext());

        ///////////////////////////////
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        tracker = new MultiBoxTracker(getContext());

        int cropSize = TF_OD_API_INPUT_SIZE;
        String lang=Locale.getDefault().getLanguage();
        try {
            detector = TensorFlowObjectDetectionAPIModel.create(
                    getActivity().getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_INPUT_SIZE);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            LOGGER.e("Exception initializing classifier!", e);
            Toast toast = Toast.makeText(
                    getContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            //finish();
        }


        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        Log.e(TOG,Integer.toString(previewHeight));

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = (OverlayView) getView().findViewById(R.id.tracking_overlay);
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



    protected int getScreenOrientation() {
        switch (getActivity().getWindowManager().getDefaultDisplay().getRotation()) {
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

    public void addCallback(final OverlayView.DrawCallback callback) {
        final OverlayView overlay = (OverlayView) getView().findViewById(R.id.debug_overlay);
        if (overlay != null) {
            overlay.addCallback(callback);
        }
    }



    protected int getLayoutId() {
        Log.e(TOG," (getLayoutId) ");
        return R.layout.camera_connection_fragment_tracking;
    }


    protected Size getDesiredPreviewFrameSize(ViewGroup group,LayoutInflater inflater) {
        Log.e(TOG," (getDesiredPreviewFrameSize) ");

        double heightPercent = 0.6;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        double height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Log.e("MACHINEWIDTH",Double.toString(height));




        int desiredWidth = width;
        double doubledesiredHeight =  height*heightPercent + 1;
        int desiredHeight = (int) doubledesiredHeight;

        Log.e("GETDESIREDPREVIEW",Integer.toString(desiredHeight));


        Size previewDesireSize = new Size(desiredHeight,desiredWidth);
        return previewDesireSize;

        //return DESIRED_PREVIEW_SIZE;
    }


    @Override
    public void onDestroy() {
        speaker.close();
        dbHelper.close();

        Log.e(TOG,"Its onDestroy that has been called");


        try{
            fWriter.close();
        }catch (IOException e){
            Log.e(TOG,e.toString());
        }

        /*FragmentManager mFragmentMgr= getActivity().getSupportFragmentManager();
        FragmentTransaction mTransaction = mFragmentMgr.beginTransaction();
        Fragment childFragment =mFragmentMgr.findFragmentByTag("CameraTag");
        mTransaction.remove(childFragment);
        mTransaction.commit();*/

        super.onDestroy();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        //launch the inference thread
        Log.e(TOG,"Its OnResume that has been called");
        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }




    @Override
    public synchronized void onPause() {
        Log.e(TOG,"Its onPause that has been called");

        /*if (!getActivity().isFinishing()) {
            getActivity().finish();
        }*/

        handlerThread.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (final InterruptedException e) {
            LOGGER.e(e, "Exception!");
        }

        super.onPause();
    }



}
