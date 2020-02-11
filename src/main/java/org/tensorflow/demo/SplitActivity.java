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

        final Intent carIntent = new Intent(this, SplashActivity.class);
        final Intent hotelIntent = new Intent(this, HotelHomeActivity.class);

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



    }
}
