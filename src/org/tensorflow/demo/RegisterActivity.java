package org.tensorflow.demo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import org.tensorflow.demo.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private  FirebaseFirestore db;
    EditText emailText;
    EditText passText;
    EditText cpassText;
    String email,password, cpassword, points;
    Map<String, Object> users;
    FirebaseUser user;
	DataBase dbl;
    int i;

	private Button mRegisterButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
		dbl=new DataBase(this);
		mAuth = FirebaseAuth.getInstance();
        i=0;// s'il a cliqué 2 fois sur le bouton signUp l'app ne va pas créer 2 users
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("SignUp", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("SignUp", "onAuthStateChanged:signed_out");
                }
                db = FirebaseFirestore.getInstance();

            }
        };
		points="0";
		setContentView();
	}
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
	private void setContentView() {
        emailText = (EditText) findViewById(R.id.email);
        passText = (EditText) findViewById(R.id.password);
        cpassText = (EditText) findViewById(R.id.confirmpassword);
        Typeface sRobotoThin = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Thin.ttf");
        emailText.setTypeface(sRobotoThin);
        passText.setTypeface(sRobotoThin);
        cpassText.setTypeface(sRobotoThin);
		/** Button d'inscription.**/
		 mRegisterButton = (Button) findViewById(R.id.sign_up);
		 mRegisterButton.setOnClickListener(new View.OnClickListener() {
		 public void onClick(View view) {
			if(i==0){
				i=1;
				
				email=emailText.getText().toString();
				password=passText.getText().toString();
                cpassword=cpassText.getText().toString();
                if(!password.equals(cpassword)){
                    if(email.isEmpty() || password.isEmpty() || cpassword.isEmpty()){
                        Toast.makeText(RegisterActivity.this,getResources().getString(R.string.error_field_required),
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
				users = new HashMap<>();
				users.put("mail", email);
				users.put("mot de pass", password);
				users.put("born",points);
				mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("SignUpC", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,getResources().getString(R.string.register_failed),
                                    Toast.LENGTH_SHORT).show();
                            i=0;
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this,getResources().getString(R.string.register_succed),
                                    Toast.LENGTH_SHORT).show();
                            db.collection("users")
                                    .add(users)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("users", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("users", "Error adding document", e);
                                        }
                                    });
                            Intent intent=new Intent(RegisterActivity.this, BenevoleActivity.class);
                            intent.putExtra("id",user.getUid());
                            intent.putExtra("act","");
                            intent.putExtra("rec","");
                            intent.putExtra("email2","");
                            intent.putExtra("email",email);
                            intent.putExtra("points",points);
                            dbl.insertData_vo(email,points);
                            startActivityForResult(intent,1);
                        }
                    }
                });}
                }
			}
			

		 });
	}
}
