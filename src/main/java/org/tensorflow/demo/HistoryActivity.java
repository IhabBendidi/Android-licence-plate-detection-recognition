package org.tensorflow.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HistoryActivity extends AppCompatActivity {

    FileReader fReader;
    static final String TOG = "HistoryActivity";
    String inputFileName = "resul.txt";

    FileInputStream is;
    BufferedReader reader;

    static LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        layout = this.findViewById(R.id.the_layout);
        final File file = new File("/data/user/0/org.tensorflow.demo/app_assets/resul.txt");
        Log.e(TOG,file.toString());

        if (file.exists()) {
            Log.e(TOG,"1");
            try{
                is = new FileInputStream(file);
                Log.e(TOG,"2");
                reader = new BufferedReader(new InputStreamReader(is));
                Log.e(TOG,"3");
                String line = "";
                Log.e(TOG,"4");
                while(line != null){
                    Log.e(TOG,"5");
                    line = reader.readLine();
                    if (line != null){
                        Log.e(TOG,"6, line is : " + line);
                        Log.e(TOG,"7");
                        createNewHistoryObject(line);
                        Log.e(TOG,"fin one loop");
                    }

                }
            } catch (FileNotFoundException e){
                Log.e(TOG,e.toString());
            } catch(IOException e){
                Log.e(TOG,e.toString());
            }
        }
    }


    protected void onDestroy() {

        super.onDestroy();
    }

    protected void onPause() {

        super.onPause();
    }

    protected void onResume() {
        super.onResume();

    }




    private void createNewHistoryObject(String line){
        Log.e(TOG,"8");
        String[] values = line.split(" ");
        Log.e(TOG,"9");
        String imagePath = values[0];
        String result_text = values[1];
        Log.e(TOG,"10");
        String location = values[2];
        Log.e(TOG,"11");
        String time = values[3];
        Log.e(TOG,"12");
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Log.e(TOG,"13");
        //Image handling
        Bitmap b = loadImageFromStorage(imagePath);
        ImageView img = new ImageView(this);
        img.setLayoutParams(lparams);
        img.setImageBitmap(b);


        Log.e(TOG,"14");


        //Text handling
        TextView recon_text = new TextView(this);
        recon_text.setLayoutParams(lparams);
        recon_text.setText(result_text);
        recon_text.setTextColor(Color.parseColor("#FFFFFF"));



        //Location handling
        TextView locationholder = new TextView(this);
        locationholder.setLayoutParams(lparams);
        locationholder.setText("Country of detection : ");
        locationholder.setTextColor(Color.parseColor("#FFFFFF"));


        TextView locationText = new TextView(this);
        locationText.setLayoutParams(lparams);
        locationText.setText(location);
        locationText.setTextColor(Color.parseColor("#FFFFFF"));


        Log.e(TOG,"15");



        //Time handling

        TextView timeholder = new TextView(this);
        timeholder.setLayoutParams(lparams);
        timeholder.setText("Time of detection : ");
        timeholder.setTextColor(Color.parseColor("#FFFFFF"));

        TextView timeText = new TextView(this);
        timeText.setLayoutParams(lparams);
        timeText.setText(time);
        timeText.setTextColor(Color.parseColor("#FFFFFF"));

        Log.e(TOG,"16");

        this.layout.addView(img);
        this.layout.addView(recon_text);
        this.layout.addView(locationholder);
        this.layout.addView(locationText);
        this.layout.addView(timeholder);
        this.layout.addView(timeText);




        Log.e(TOG,"17");
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
}
