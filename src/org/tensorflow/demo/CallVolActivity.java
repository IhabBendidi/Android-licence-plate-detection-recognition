package org.tensorflow.demo;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.tensorflow.demo.R;

public class CallVolActivity extends AppCompatActivity {

    DatabaseReference dbVol;
    String id,email,email2,points;
    Ringtone ringtone;
    int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call__vol);
        Context mContext = getApplicationContext();
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(mContext,uri);
        ringtone.play();

        i=0;
        Bundle extra=this.getIntent().getExtras();
        if(extra!=null)
        {
            id=extra.getString("id");
            email=extra.getString("email");
            email2=extra.getString("email2");
            points=extra.getString("points");
        }
        dbVol= FirebaseDatabase.getInstance().getReference("volontaire");
        dbVol.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Volontaire us = userSnapshot.getValue(Volontaire.class);
                    if(us.email.equals(email) && us.rec2.equals("ret"))
                    {
                        ringtone.stop();
                        dbVol.child(us.id).setValue(new Volontaire(us.id, us.email,"","","",us.points));

                        Intent intent=new Intent(CallVolActivity.this,BenevoleActivity.class);
                        intent.putExtra("id",us.id);
                        intent.putExtra("email",us.email);
                        intent.putExtra("points",us.points);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
	

    public void accept(View view) {
        dbVol.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (i == 0) {
                    i = 1;
                    dbVol.child(id).setValue(new Volontaire(id, email, email2, "yes", "", points));
                    ringtone.stop();
                    Intent intent = new Intent(CallVolActivity.this, VideoActivity.class);
                    intent.putExtra("user", "VO");
                    intent.putExtra("id", id);
                    intent.putExtra("email", email);
                    intent.putExtra("email2", email2);
                    intent.putExtra("points", points);
                    startActivityForResult(intent, 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

	public void reject(View view) {
        dbVol.child(id).setValue(new Volontaire(id, email, email2, "no","",points));
        ringtone.stop();
        Intent intent=new Intent(CallVolActivity.this,BenevoleActivity.class);
        intent.putExtra("act","");
        intent.putExtra("id",id);
        intent.putExtra("email",email);
        intent.putExtra("points",points);
        startActivityForResult(intent,1);

    }
}
