package org.tensorflow.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PlateActivity extends AppCompatActivity {

    static LinearLayout layout;
    PlateDbHelper dbHelper;
    Plate plate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plate);
        layout = this.findViewById(R.id.the_layout);
        dbHelper = new PlateDbHelper(this);
        String _ID = getIntent().getStringExtra("ID");
        createPlateActivity(_ID,dbHelper);

    }


    private void createPlateActivity(String _ID,PlateDbHelper dbHelper){


        plate = new Plate(_ID);
        plate = dbHelper.readPlate(plate);

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);



        LinearLayout linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.VERTICAL);



        //ID handling


        TextView idtext = new TextView(this);
        idtext.setLayoutParams(lparams);
        idtext.setText(plate.get_ID());
        idtext.setTextColor(Color.parseColor("#FFFFFF"));
        idtext.setVisibility(View.GONE);


        //Image handling
        Bitmap b = loadImageFromStorage(plate.getImagePath());
        ImageView img = new ImageView(this);
        img.setLayoutParams(lparams);
        img.setImageBitmap(b);





        //Text handling
        TextView recon_text = new TextView(this);
        recon_text.setLayoutParams(lparams);
        recon_text.setText(plate.getText());
        recon_text.setTextColor(Color.parseColor("#FFFFFF"));



        //Location handling
        TextView locationholder = new TextView(this);
        locationholder.setLayoutParams(lparams);
        locationholder.setText("Country of detection : ");
        locationholder.setTextColor(Color.parseColor("#FFFFFF"));


        TextView locationText = new TextView(this);
        locationText.setLayoutParams(lparams);
        locationText.setText(plate.getLocation());
        locationText.setTextColor(Color.parseColor("#FFFFFF"));






        //Time handling

        TextView timeholder = new TextView(this);
        timeholder.setLayoutParams(lparams);
        timeholder.setText("Time of detection : ");
        timeholder.setTextColor(Color.parseColor("#FFFFFF"));

        TextView timeText = new TextView(this);
        timeText.setLayoutParams(lparams);
        timeText.setText(plate.getDate());
        timeText.setTextColor(Color.parseColor("#FFFFFF"));


        //Owner handling

        TextView ownerholder = new TextView(this);
        ownerholder.setLayoutParams(lparams);
        ownerholder.setText("Owner of the vehicule : ");
        ownerholder.setTextColor(Color.parseColor("#FFFFFF"));

        TextView ownertext = new TextView(this);
        ownertext.setLayoutParams(lparams);
        ownertext.setText(plate.getOwner());
        ownertext.setTextColor(Color.parseColor("#FFFFFF"));


        //Type handling

        TextView typeholder = new TextView(this);
        typeholder.setLayoutParams(lparams);
        typeholder.setText("Type of the vehicule : ");
        typeholder.setTextColor(Color.parseColor("#FFFFFF"));

        TextView typetext = new TextView(this);
        typetext.setLayoutParams(lparams);
        typetext.setText(plate.getType());
        typetext.setTextColor(Color.parseColor("#FFFFFF"));


        //validity handling

        TextView validityholder = new TextView(this);
        validityholder.setLayoutParams(lparams);
        validityholder.setText("Validity :  ");
        validityholder.setTextColor(Color.parseColor("#FFFFFF"));

        TextView validitytext = new TextView(this);
        validitytext.setLayoutParams(lparams);
        validitytext.setText(plate.getValidity());
        validitytext.setTextColor(Color.parseColor("#FFFFFF"));






        linear.addView(idtext);
        linear.addView(img);
        linear.addView(recon_text);
        linear.addView(locationholder);
        linear.addView(locationText);
        linear.addView(timeholder);
        linear.addView(timeText);
        linear.addView(typeholder);
        linear.addView(typetext);
        linear.addView(validityholder);
        linear.addView(validitytext);
        linear.addView(ownerholder);
        linear.addView(ownertext);





        /*
        card.addView(img);
        card.addView(recon_text);
        card.addView(locationholder);
        card.addView(locationText);
        card.addView(timeholder);
        card.addView(timeText);

         */


        this.layout.addView(linear);







    }

    private Bitmap loadImageFromStorage(String path)
    {
        Bitmap rotated = null;
        try {
            File f=new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            rotated = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                    matrix, true);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return rotated;
    }

    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

}
