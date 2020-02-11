package org.tensorflow.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HotelHomeActivity extends AppCompatActivity {
    FragmentTransaction transaction;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_home);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.hotel_bottom_navigation);
    }
}
