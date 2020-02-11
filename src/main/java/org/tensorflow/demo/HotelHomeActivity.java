package org.tensorflow.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HotelHomeActivity extends AppCompatActivity implements HotelHomeFragment.OnFragmentInteractionListener,HotelHistoryFragment.OnFragmentInteractionListener, SettingFragment.OnFragmentInteractionListener{
    FragmentTransaction transaction;
    BottomNavigationView bottomNavigationView;

    @Override
    public void onFragmentInteraction(Uri uri){

    }

    @Override
    public void onBackPressed(){

        Fragment hotelHomeFragment = new HotelHomeFragment();
        Fragment hotelHistoryFragment = new HotelHistoryFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        if (bottomNavigationView.getSelectedItemId()== R.id.action_history){
            transaction.replace(R.id.hotel_fragment_layout, hotelHistoryFragment);
            //transaction.addToBackStack(null);
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.action_history);
        }else if(bottomNavigationView.getSelectedItemId()== R.id.action_home){
            //onPause();
            //onDestroy();
        }else{
            transaction.replace(R.id.hotel_fragment_layout, hotelHomeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        }
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_home);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.hotel_bottom_navigation);
        Fragment hotelHomeFragment = new HotelHomeFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.hotel_fragment_layout, hotelHomeFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Fragment hotelHomeFragment = new HotelHomeFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.hotel_fragment_layout, hotelHomeFragment);
                        //transaction.addToBackStack(null);
                        transaction.commit();

                        break;
                    case R.id.action_history:
                        Fragment hotelHistoryFragment = new HotelHistoryFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.hotel_fragment_layout, hotelHistoryFragment);
                        //transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case R.id.action_settings:
                        Fragment settingFragment = new SettingFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.hotel_fragment_layout, settingFragment);
                        //transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                }
                return true;
            }
        });
    }
}
