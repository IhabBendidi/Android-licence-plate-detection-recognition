package org.tensorflow.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static java.lang.Thread.sleep;


public class HomeActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,HistoryFragment.OnFragmentInteractionListener, SettingFragment.OnFragmentInteractionListener, RenewFragment.OnFragmentInteractionListener {

    FragmentTransaction transaction;
    BottomNavigationView bottomNavigationView;


    @Override
    public void onFragmentInteraction(Uri uri){

    }

    @Override
    public void onBackPressed(){

        Fragment homeFragment = new HomeFragment();
        Fragment historyFragment = new HistoryFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        if (bottomNavigationView.getSelectedItemId()== R.id.action_renew){
            transaction.replace(R.id.fragment_layout, historyFragment);
            //transaction.addToBackStack(null);
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.action_history);
        }else if(bottomNavigationView.getSelectedItemId()== R.id.action_home){
            //onPause();
            //onDestroy();
        }else{
            transaction.replace(R.id.fragment_layout, homeFragment);
            //transaction.addToBackStack(null);
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        }
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        final Context currentContext = this;
        Fragment homeFragment = new HomeFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_layout, homeFragment);
        transaction.addToBackStack(null);
        transaction.commit();



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Fragment homeFragment = new HomeFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_layout, homeFragment);
                        //transaction.addToBackStack(null);
                        transaction.commit();

                        break;
                    case R.id.action_history:
                        Fragment historyFragment = new HistoryFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_layout, historyFragment);
                        //transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case R.id.action_settings:
                        Fragment settingFragment = new SettingFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_layout, settingFragment);
                        //transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case R.id.action_renew:
                        Fragment renewFragment = new RenewFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_layout, renewFragment);
                        //transaction.addToBackStack(null);
                        transaction.commit();
                        break;          }
                return true;
            }
        });


    }





}
