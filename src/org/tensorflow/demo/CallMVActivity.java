package org.tensorflow.demo;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.tensorflow.demo.R;

import java.util.Locale;

import at.markushi.ui.CircleButton;

public class CallMVActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    DatabaseReference dbVol;
    DatabaseReference dbid;
    int j,i,apl;
    Volontaire v=null;
    long idMVoyant;
    String idm;
    CircleButton mEndCallButton;
    //private String toSpeak ;
	Thread logotimer;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_mvoyant);
        dbVol= FirebaseDatabase.getInstance().getReference("volontaire");
        dbVol.setPriority(idm);
        dbid=FirebaseDatabase.getInstance().getReference("id_MVoyant");
        idm=dbid.getKey();
		apl=0;
        Bundle extra=this.getIntent().getExtras();
        if(extra!=null)
        {
            idMVoyant=Long.parseLong(extra.getString("imv"));
            Log.v("imv",idMVoyant+"");
        }
        /******************************************/
        tts = new TextToSpeech(this, this);
        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    sleep(500);
                    speakOut("Start Calling");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        logoTimer.start();
        /*****************************************/

        /** Button endcall.**
        mEndCallButton = (CircleButton) findViewById(R.id.discall);
        mEndCallButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(CallMVActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });*/
    }

        protected void onStart() {
        super.onStart();
        i=0;
        j=0;
        Log.v("LogThre","onstart");
                dbVol.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // whenever data at this location is updated.
                        if(j==0)
                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                             Volontaire us = userSnapshot.getValue(Volontaire.class);
                            Log.v("LogThre",us.email+"  "+us.email2+"  "+us.call);
                            if(us.email2.equals(idMVoyant+""))
                            {
                                if(us.call.equals("yes"))
                                {
                                    j=1;
                                    Log.v("LogThre","jawb");
                                    Intent intent=new Intent(CallMVActivity.this,VideoActivity.class);
                                    intent.putExtra("user","MV");
                                    intent.putExtra("email2",""+us.email2);
                                    intent.putExtra("email",us.email);
                                    intent.putExtra("id",us.id);
                                    intent.putExtra("idm",idm+"");

                                    startActivityForResult(intent,1);
                                    finish();
                                    break;
                                }
                                else
                                    if(us.call.equals("no"))
                                    {
                                        Log.v("LogThre","no");
                                        j=0;
                                        dbVol.child(us.id).setValue(new Volontaire(us.id, us.email,"","","",us.points));
                                        i=0;
                                    }
                            }
                            else
                            if(us.email2.equals("") && !us.call.equals("yes") && i==0)
                            {
                                i=1;
                                us.email2=idMVoyant+"";
                                dbVol.child(us.id).setValue(new Volontaire(us.id, us.email, idMVoyant + "", "", "", us.points));
                                v=us;
                                /*logotimer = new Thread() {
                                    public void run() {
                                        try {
                                            sleep(15000);
                                            apl=1;
                                            delete(v);

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                logotimer.start();*/
                            }
                            else
                                Log.v("change_no",us.email+"    "+ us.email2);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("Volontaire", "Failed to read value.", error.toException());
                    }
                });

}public void delete(Volontaire us)
    {
        if(apl==1 && us.call.equals(""))
        {
            apl=0;
            dbVol.child(us.id).setValue(new Volontaire(us.id, us.email,us.email2,"no","ret",us.points));
            logotimer=null;
        }
    }
	/***************************************************************************/
    // act on result of TTS data check
    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut("Start Calling");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut(String toSpeak){
        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

}
