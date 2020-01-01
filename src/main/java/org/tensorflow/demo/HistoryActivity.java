package org.tensorflow.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {


    static final String TOG = "HistoryActivity";


    FileInputStream is;


    static LinearLayout layout;
    PlateDbHelper dbHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();

    }

    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_history);
        getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
        layout = this.findViewById(R.id.the_layout);
        dbHelper = new PlateDbHelper(this);
        createHistoryView(dbHelper);
        BottomNavigationView bottomNavigationViewHistory = (BottomNavigationView) findViewById(R.id.bottom_navigation_history);
        final Context currentContext = this;
        bottomNavigationViewHistory.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.e(TOG,"0.5");
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Log.e(TOG,"1");
                        Intent intentHome = new Intent(currentContext, MainActivity.class);
                        Log.e(TOG,"2");
                        Log.e(TOG,"3");
                        startActivity(intentHome);
                        Log.e(TOG,"4");
                        break;
                    case R.id.action_history:
                        Log.e(TOG,"5");
                        Intent intentHistory = new Intent(currentContext, HistoryActivity.class);
                        Log.e(TOG,"6");
                        Log.e(TOG,"7");
                        startActivity(intentHistory);
                        Log.e(TOG,"8");
                        break;
                    case R.id.action_settings:
                        Toast.makeText(currentContext, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_renew:
                        Toast.makeText(currentContext, "Renew", Toast.LENGTH_SHORT).show();
                        break;          }
                return true;
            }
        });
    }



    public void createHistoryView(PlateDbHelper dbHelper){
        ArrayList<Plate> plates = dbHelper.readPlateTexts();
        for (Plate plate : plates){
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            CardView card = new CardView(this);
            card.setRadius(0.2f);
            card.setCardBackgroundColor(Color.parseColor("#ffffff"));


            LinearLayout linear = new LinearLayout(this);
            linear.setOrientation(LinearLayout.VERTICAL);

            // Text handling
            TextView recon_text = new TextView(this);
            recon_text.setLayoutParams(lparams);
            recon_text.setText(plate.getText());
            recon_text.setTextColor(Color.parseColor("#151515"));

            //ID handling

            String id = plate.get_ID();
            final TextView id_text = new TextView(this);
            id_text.setLayoutParams(lparams);
            id_text.setText(id);
            id_text.setVisibility(View.GONE);

            //Button for going to the plateactivity for more details
            Button button= new Button(this);
            button.setText("More Details");
            button.setTextColor(Color.parseColor("#FFFFFF"));
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), PlateActivity.class);
                    intent.putExtra("ID", id_text.getText());
                    Log.e(TOG,id_text.getText().toString());
                    startActivity(intent);

                }
            });
            linear.addView(recon_text);
            linear.addView(id_text);
            linear.addView(button);
            card.addView(linear);
            this.layout.addView(card);


        }
    }

}
