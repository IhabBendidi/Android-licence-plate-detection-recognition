package org.tensorflow.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void continuous_camera(View view){
        Intent intent = new Intent(this, DetectorActivity.class);
        startActivity(intent);
    }

    public void one_shot_camera(View view){
        Intent intent = new Intent(this, OneShotCameraActivity.class);
        startActivity(intent);
    }

    public void history(View view){
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
}
