package org.tensorflow.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ShopHomeActivity extends AppCompatActivity implements ShopRenewFragment.OnFragmentInteractionListener,ShopHomeFragment.OnFragmentInteractionListener,ShopHistoryFragment.OnFragmentInteractionListener, SettingFragment.OnFragmentInteractionListener{

    FragmentTransaction transaction;
    BottomNavigationView bottomNavigationView;

    @Override
    public void onFragmentInteraction(Uri uri){

    }

    @Override
    public void onBackPressed(){

        Fragment shopHomeFragment = new ShopHomeFragment();
        Fragment shopHistoryFragment = new ShopHistoryFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        if (bottomNavigationView.getSelectedItemId()== R.id.action_history){
            transaction.replace(R.id.shop_fragment_layout, shopHistoryFragment);
            //transaction.addToBackStack(null);
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.action_history);
        }else if(bottomNavigationView.getSelectedItemId()== R.id.action_home){
            //onPause();
            //onDestroy();
        }else{
            transaction.replace(R.id.shop_fragment_layout, shopHomeFragment);
            //transaction.addToBackStack(null);
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        }
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_home);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.shop_bottom_navigation);
        Fragment shopHomeFragment = new ShopHomeFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.shop_fragment_layout, shopHomeFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Fragment shopHomeFragment = new ShopHomeFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.shop_fragment_layout, shopHomeFragment);
                        //transaction.addToBackStack(null);
                        transaction.commit();

                        break;
                    case R.id.action_history:
                        Fragment ShopHistoryFragment = new ShopHistoryFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.shop_fragment_layout, ShopHistoryFragment);
                        //transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case R.id.action_settings:
                        Fragment settingFragment = new SettingFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.shop_fragment_layout, settingFragment);
                        //transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                }
                return true;
            }
        });
    }
}
