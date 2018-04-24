package org.tensorflow.demo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

//import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

	private Button mLoginButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText loginText;
    EditText passText;
    String email, password, points;
    private FirebaseFirestore db;
    DatabaseReference dbVol;
    DataBase dbl;
    int i,j;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

		mAuth = FirebaseAuth.getInstance();
        i=0;//même que signUp
		
        db = FirebaseFirestore.getInstance();
        dbl=new DataBase(this);
        points="0";
        dbVol= FirebaseDatabase.getInstance().getReference("volontaire");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Login", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Login", "onAuthStateChanged:signed_out");
                }
            }
        };
		
		setContentView();
	}
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        i=0;
    }
	@Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
	private void setContentView() {
        loginText = (EditText) findViewById(R.id.login_page_social_login_text);
        passText = (EditText) findViewById(R.id.login_page_social_login_password);
        Typeface sRobotoThin = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Thin.ttf");

        loginText.setTypeface(sRobotoThin);
        passText.setTypeface(sRobotoThin);
		/** Button de cconnexion.**/
		 mLoginButton = (Button) findViewById(R.id.sign_in);
		 mLoginButton.setOnClickListener(new View.OnClickListener() {
		 public void onClick(View view) {
			if(i==0) {				
			j=0;
            i = 1;
            email = loginText.getText().toString();
            password = passText.getText().toString();
            if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this,getResources().getString(R.string.error_field_required),
                            Toast.LENGTH_SHORT).show();
                }else{
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("Login", "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w("Login", "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, "signInWithEmail Failed",
                                        Toast.LENGTH_SHORT).show();
                                i=0;
                            } else {
                                dbVol.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                            Volontaire us = userSnapshot.getValue(Volontaire.class);
                                            if(us.email.equals(email))
                                            {
                                                i=0;
                                                j=1;
                                                // Remplacer par alerte
                                                Toast.makeText(LoginActivity.this,"Volontaire déjà connecté",Toast.LENGTH_LONG);
                                                /*new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                        .setTitleText("Login échouée!")
                                                        .setContentText("Volontaire déjà connecté")
                                                        .setConfirmText("   OK   ")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.cancel();
                                                            }
                                                        })
                                                        .show();*/
                                                break;
                                            }
                                        }
                                        if(j==0)
                                        {
                                            db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(QuerySnapshot value, FirebaseFirestoreException e) {
                                                    for (QueryDocumentSnapshot doc : value) {
                                                        if (((String)doc.get("mail")).equals(email)) {
                                                            points=doc.get("born")+"";
                                                            Log.d("points",points);
                                                            break;
                                                        }
                                                    }
                                                }
                                            });
                                            Intent intent = new Intent(LoginActivity.this, BenevoleActivity.class);
                                            intent.putExtra("email", email);
                                            intent.putExtra("act","");
                                            intent.putExtra("rec","");
                                            intent.putExtra("email2","");
                                            intent.putExtra("mdp", password);
                                            intent.putExtra("points",points);//ajouter points
                                            dbl.insertData_vo(email,points);//fromdb
                                            startActivityForResult(intent, 1);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }


                        }
                    });
                    }
            }
		 }
		 });

		/** Button d'inscription.**/
		TextView register;
		register = (TextView) findViewById(R.id.register);
		register.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});

	}

}
