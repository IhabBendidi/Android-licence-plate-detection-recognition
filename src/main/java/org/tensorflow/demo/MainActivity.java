package org.tensorflow.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        final Context currentContext = this;
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Intent intentHome = new Intent(currentContext, MainActivity.class);
                        startActivity(intentHome);
                        break;
                    case R.id.action_history:
                        Intent intentHistory = new Intent(currentContext, HistoryActivity.class);
                        startActivity(intentHistory);
                        break;
                    case R.id.action_settings:
                        Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_renew:
                        Toast.makeText(MainActivity.this, "Renew", Toast.LENGTH_SHORT).show();
                        break;          }
                return true;
            }
        });
    }

    public void continuous_camera(View view){
        Intent intent = new Intent(this, DetectorActivity.class);
        startActivity(intent);
    }

    public void one_shot_camera(View view){
        Intent intent = new Intent(this, ShotDetectionActivity.class);
        startActivity(intent);
    }

    public void history(View view){
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
}
