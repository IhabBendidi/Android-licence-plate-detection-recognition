package org.tensorflow.demo;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.tensorflow.demo.R;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.SubscriberKit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class VideoActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks,
        Session.SessionListener,
        PublisherKit.PublisherListener,
        SubscriberKit.SubscriberListener{

    private static final String LOG_TAG = VideoActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;



    //

    // Suppressing this warning. mWebServiceCoordinator will get GarbageCollected if it is local.
    @SuppressWarnings("FieldCanBeLocal")


    //Ajouter :
            Bundle extra;
    DatabaseReference dbVol;
    String idm,idd,email,email2,user,points;

    int mv,vo,get;
    private FirebaseFirestore db;
    Map<String, Object> streams;
    Map<String,String> sw;



    int i;
    /// Ajouter :D

  //  ArrayList<Str> ls;
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;

	@SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        i=0;
        // initialize view objects from your layout
        //mPublisherViewContainer = (FrameLayout)findViewById(R.id.publisher_container);
        mSubscriberViewContainer = (FrameLayout)findViewById(R.id.subscriber_container);
        dbVol= FirebaseDatabase.getInstance().getReference("volontaire");

        extra=this.getIntent().getExtras();
        if(extra!=null)
        {
            user=extra.getString("user");
            idd=extra.getString("id");
            email=extra.getString("email");
            email2=extra.getString("email2");
            points=extra.getString("points");
            if(user.equals("MV"));
                idm=extra.getString("idm");
        }
		if(user.equals("MV"))
        {
            ((at.markushi.ui.CircleButton) findViewById(R.id.discall)).setVisibility(View.GONE);
           // ((ImageButton) findViewById(R.id.swap)).visi
        }
		
        requestPermissions();
        db = FirebaseFirestore.getInstance();
        streams=new HashMap<>();
        get=mv=vo=0;

    }
    
	
	public void discall(View v)
    {
        if(user.equals("VO"))
        {
            Intent intent=new Intent(VideoActivity.this,BenevoleActivity.class);
            dbVol.child(idd).setValue(new Volontaire(idd,email,email2,"end","",points));
            idd=dbVol.push().getKey();
            dbVol.child(idd).setValue(new Volontaire(idd,email,"","","",points));

            intent.putExtra("act","vid");
            intent.putExtra("id",idd);
            intent.putExtra("email",email);
            startActivityForResult(intent,1);
            mSubscriber = null;
            mPublisher=null;
            mSubscriberViewContainer.removeAllViews();

        }
    }
    public void btnswap(View v)
    {

    }
    /* Activity lifecycle methods */

    @Override
    protected void onPause() {

        Log.d(LOG_TAG, "onPause");

        super.onPause();

        if (mSession != null) {
            mSession.onPause();
        }

    }

    @Override
    protected void onResume() {

        Log.d(LOG_TAG, "onResume");

        super.onResume();

        if (mSession != null) {
            mSession.onResume();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        Log.d(LOG_TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        Log.d(LOG_TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show();
        }
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {

        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // if there is no server URL set
            if (openTokConfig.CHAT_SERVER_URL == null) {
                // use hard coded session values
                if (openTokConfig.areHardCodedConfigsValid()) {
                    initializeSession(openTokConfig.API_KEY, openTokConfig.SESSION_ID, openTokConfig.TOKEN);
                } else {
                    showConfigError("Configuration Error", openTokConfig.hardCodedConfigErrorMessage);
                }
            } else {
                // otherwise initialize WebServiceCoordinator and kick off request for session data
                // session initialization occurs once data is returned, in onSessionConnectionDataReady

                    showConfigError("Configuration Error", openTokConfig.webServerConfigErrorMessage);
                }

        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), RC_VIDEO_APP_PERM, perms);
        }
    }

    private void initializeSession(String apiKey, String sessionId, String token) {

        mSession = new Session.Builder(this, apiKey, sessionId).build();
        mSession.setSessionListener(this);
        mSession.connect(token);

    }

    /* Web Service Coordinator delegate methods */


    /* Session Listener methods */

    @Override
    protected void onStart() {
        super.onStart();

        if(user.equals("MV"))
        {
                dbVol.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Volontaire us = userSnapshot.getValue(Volontaire.class);
                            if(us.email.equals(email) && us.email2.equals(email2) && us.call.equals("end"))
                            {
                                dbVol.child(us.id).removeValue();
                                mSubscriber = null;
                                mPublisher=null;
                                mSubscriberViewContainer.removeAllViews();
                                Intent intent=new Intent(VideoActivity.this,DialogRateActivity.class);
                                startActivity(intent);

                            }
                    }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        }
    }

    @Override
    public void onConnected(Session session) {

        Log.d(LOG_TAG, "onConnected: Connected to session: "+session.getSessionId());
        // initialize Publisher and set this object to listen to Publisher events
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        // set publisher video style to fill view
        mPublisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);

/*        mPublisherViewContainer.addView(mPublisher.getView());
        if (mPublisher.getView() instanceof GLSurfaceView) {
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }
*/
        if(user.equals("MV"))
            mPublisher.cycleCamera();
        mSession.publish(mPublisher);

    }

    @Override
    public void onDisconnected(Session session) {

        Log.d(LOG_TAG, "onDisconnected: Disconnected from session: "+session.getSessionId());
        if(user.equals("MV"))
        {
            mSubscriber=null;
            mSubscriberViewContainer.removeAllViews();
            Intent intent=new Intent(VideoActivity.this,DialogRateActivity.class);
            startActivity(intent);
        }
    }


     @Override
    public void onStreamReceived(Session session, final Stream stream) {

        
        if(user.equals("MV"))
        {
            if(mSubscriber==null) {
                db.collection("streamsVO").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("id").equals(idd) && doc.get("stream").equals(stream.getStreamId()) && get==0) {
                                mSubscriber = new Subscriber.Builder(VideoActivity.this, stream).build();
                                mSubscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                                mSubscriber.setSubscriberListener(VideoActivity.this);
                                mSession.subscribe(mSubscriber);
                                mSubscriberViewContainer.addView(mSubscriber.getView());
                                Log.v("rceee", stream.getStreamId());
                                get = 1;
                                break;
                            }
                        }
                    }
                });
            }
        }
        else
            if(user.equals("VO"))
        {

            if(mSubscriber==null){
                db.collection("streamsMV").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("id").equals(idd) && doc.get("stream").equals(stream.getStreamId()) && get==0) {
                                mSubscriber = new Subscriber.Builder(VideoActivity.this, stream).build();
                                mSubscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                                mSubscriber.setSubscriberListener(VideoActivity.this);
                                mSession.subscribe(mSubscriber);
                                mSubscriberViewContainer.addView(mSubscriber.getView());
                                Log.v("rceee", stream.getStreamId());
                                get = 1;
                                break;
                            }
                        }
                    }
                });

            }
        }
        Log.d(LOG_TAG, "onStreamReceived: New Stream Received "+stream.getStreamId() + " in session: "+session.getSessionId());


    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

        Log.d(LOG_TAG, "onStreamDropped: Stream Dropped: "+stream.getStreamId() +" in session: "+session.getSessionId());

        /*if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }*/
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "onError: "+ opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() + " - "+opentokError.getMessage() + " in session: "+ session.getSessionId());

        showOpenTokError(opentokError);
    }

    /* Publisher Listener methods */

    @Override
    public void onStreamCreated(PublisherKit publisherKit,final Stream stream) {

        streams.put("id",idd);
        if(user.equals("MV"))
        {
            streams.put("stream",stream.getStreamId());
            db.collection("streamsMV").add(streams);
            //db.collection("streamsMV").add(streams);

        }
        else
            if(user.equals("VO")) {
                streams.put("stream", stream.getStreamId());
                db.collection("streamsVO").add(streams);
                //db.collection("streamsVO").add(streams);

            }

        Log.d("puuub", "onStreamCreated: Publisher Stream Created. Own stream "+stream.getStreamId());

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        publisherKit.destroy();
        if(user.equals("MV"))
        {
            mSubscriberViewContainer.removeAllViews();
            mSubscriber=null;
            mPublisher=null;
            mSubscriberViewContainer.removeAllViews();
            Intent intent=new Intent(VideoActivity.this,DialogRateActivity.class);
            startActivity(intent);
        }
        Log.d(LOG_TAG, "onStreamDestroyed: Publisher Stream Destroyed. Own stream "+stream.getStreamId());
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

        Log.e(LOG_TAG, "onError: "+opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() +  " - "+opentokError.getMessage());

        showOpenTokError(opentokError);
    }

    @Override
    public void onConnected(SubscriberKit subscriberKit) {

        Log.d(LOG_TAG, "onConnected: Subscriber connected. Stream: "+subscriberKit.getStream().getStreamId());

    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {


        Log.d(LOG_TAG, "onDisconnected: Subscriber disconnected. Stream: "+subscriberKit.getStream().getStreamId());
    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {

        Log.e(LOG_TAG, "onError: "+opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() +  " - "+opentokError.getMessage());

        showOpenTokError(opentokError);
    }
    /*@Override
    public void onConnected(SubscriberKit subscriberKit) {

        Log.d(LOG_TAG, "onConnected: Subscriber connected. Stream: "+subscriberKit.getStream().getStreamId());

    }*/
    private void showOpenTokError(OpentokError opentokError) {

        Toast.makeText(this, opentokError.getErrorDomain().name() +": " +opentokError.getMessage() + " Please, see the logcat.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void showConfigError(String alertTitle, final String errorMessage) {
        Log.e(LOG_TAG, "Error " + alertTitle + ": " + errorMessage);
        new AlertDialog.Builder(this)
                .setTitle(alertTitle)
                .setMessage(errorMessage)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        VideoActivity.this.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
