package org.tensorflow.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SplitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);

        CardView carCard = findViewById(R.id.carcard);
        CardView hotelCard = findViewById(R.id.hotelcard);

        CardView shopCard = findViewById(R.id.shopcard);


        final Intent carIntent = new Intent(this, SplashActivity.class);
        final Intent hotelIntent = new Intent(this, HotelHomeActivity.class);
        final Intent shopIntent = new Intent(this, ShopHomeActivity.class);


        carCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(carIntent);
            }
        });

        hotelCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(hotelIntent);
            }
        });

        shopCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(shopIntent);
            }
        });



    }
}
