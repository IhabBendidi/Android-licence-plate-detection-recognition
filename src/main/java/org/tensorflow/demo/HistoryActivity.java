package org.tensorflow.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    String inputFileName = "PATHS.txt";

    FileInputStream is;
    BufferedReader reader;

    static LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        layout = this.findViewById(R.id.the_layout);
        final File file = new File(getApplicationContext().getFilesDir() + "/" + inputFileName);
        if (file.exists()) {
            try{
                is = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(is));
                String line;
                do{
                    line = reader.readLine();
                    createNewHistoryObject(line);
                } while(line != null);
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
        String[] values = line.split(" ");
        String imagePath = values[0];
        String location = values[1];
        String time = values[2];
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //Image handling
        Bitmap b = loadImageFromStorage(imagePath);
        ImageView img = new ImageView(this);
        img.setLayoutParams(lparams);
        img.setImageBitmap(b);


        //Location handling
        TextView locationText = new TextView(this);
        locationText.setLayoutParams(lparams);
        locationText.setText(location);



        //Time handling
        TextView timeText = new TextView(this);
        timeText.setLayoutParams(lparams);
        locationText.setText(time);

        this.layout.addView(img);
        this.layout.addView(locationText);
        this.layout.addView(timeText);
    }

    private Bitmap loadImageFromStorage(String path)
    {
        Bitmap b = null;
        try {
            File f=new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
            //ImageView img=(ImageView)findViewById(R.id.imgPicker);
            //img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
    }
}
