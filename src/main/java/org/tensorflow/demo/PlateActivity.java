package org.tensorflow.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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


    private void createPlateActivity(String _ID, final PlateDbHelper dbHelper){


        plate = new Plate(_ID);
        plate = dbHelper.readPlate(plate);

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);



        LinearLayout linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.VERTICAL);


        final LinearLayout renewalLayout = new LinearLayout(this);

        final Button receipt= new Button(this);
        final Button expirationbutton = new Button(this);

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
        final TextView recon_text = new TextView(this);
        //recon_text.setLayoutParams(lparams);
        recon_text.setText(plate.getText());
        recon_text.setTextColor(Color.parseColor("#FFFFFF"));

        final EditText modifiable = new EditText(this);
        //modifiable.setLayoutParams(lparams);
        modifiable.setText(plate.getText());
        modifiable.setTextColor(Color.parseColor("#FFFFFF"));
        modifiable.setVisibility(View.GONE);


        final Button button= new Button(this);

        //Button for modifying the text
        final Button update= new Button(this);
        update.setText("Update");
        //update.setLayoutParams(lparams);
        update.setTextColor(Color.parseColor("#FFFFFF"));
        update.setVisibility(View.GONE);
        update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                plate.setText(modifiable.getText().toString());
                dbHelper.updatePlateText(plate);
                recon_text.setText(plate.getText());
                recon_text.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                modifiable.setVisibility(View.GONE);
                modifiable.setText(plate.getText());
                update.setVisibility(View.GONE);

            }
        });

        //Button for modifying the text

        button.setText("Modify");
        //button.setLayoutParams(lparams);
        button.setTextColor(Color.parseColor("#FFFFFF"));
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                modifiable.setVisibility(View.VISIBLE);
                update.setVisibility(View.VISIBLE);
                recon_text.setVisibility(View.GONE);
                button.setVisibility(View.GONE);

            }
        });
        LinearLayout horizontal = new LinearLayout(this);
        horizontal.setOrientation(LinearLayout.HORIZONTAL);

        horizontal.addView(recon_text);
        horizontal.addView(modifiable);
        horizontal.addView(update);
        horizontal.addView(button);



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
        validityholder.setText("License :  ");
        validityholder.setTextColor(Color.parseColor("#FFFFFF"));


        LinearLayout horizontalValidity = new LinearLayout(this);
        horizontalValidity.setOrientation(LinearLayout.HORIZONTAL);


        final TextView validitytext = new TextView(this);
        validitytext.setLayoutParams(lparams);
        String validity = plate.getValidity();
        String validitySentence = processValidity(validity);
        validitytext.setText(validitySentence);
        validitytext.setTextColor(Color.parseColor("#FFFFFF"));

        /*final CheckedTextView carCheckBox = new CheckedTextView(this);
        carCheckBox.setCheckMarkDrawable(R.drawable.checkpoint);
        carCheckBox.setChecked(false);
        carCheckBox.setText("Car          ");
        carCheckBox.setTextColor(Color.parseColor("#FFFFFF"));
        carCheckBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                carCheckBox.setChecked(true);
            }
        });
        //carCheckBox.setVisibility(View.GONE);

        final CheckedTextView motorCheckBox = new CheckedTextView(this);
        motorCheckBox.setCheckMarkDrawable(R.drawable.checkpoint);
        motorCheckBox.setChecked(false);
        motorCheckBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                motorCheckBox.setChecked(true);
            }
        });
        motorCheckBox.setText("Motorcycle          ");
        motorCheckBox.setTextColor(Color.parseColor("#FFFFFF"));
        //motorCheckBox.setVisibility(View.GONE);*/


        Spinner spinner = new Spinner(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vehicule_types, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setBackgroundColor(Color.LTGRAY);






        LinearLayout verticalcheckboxes = new LinearLayout(this);
        verticalcheckboxes.setOrientation(LinearLayout.VERTICAL);
        verticalcheckboxes.addView(spinner);
        //verticalcheckboxes.addView(carCheckBox);
        //verticalcheckboxes.addView(motorCheckBox);
        //verticalcheckboxes.setVisibility(View.GONE);




        final TextView feeholder = new TextView(this);
        feeholder.setLayoutParams(lparams);
        feeholder.setText("Item price : ");
        feeholder.setTextColor(Color.parseColor("#FFFFFF"));
        final TextView fee = new TextView(this);
        fee.setLayoutParams(lparams);
        fee.setText(getFee());
        fee.setTextColor(Color.parseColor("#FFFFFF"));






        LinearLayout feeLayout = new LinearLayout(this);
        feeLayout.setOrientation(LinearLayout.VERTICAL);
        feeLayout.addView(feeholder);
        feeLayout.addView(fee);

        LinearLayout temp = new LinearLayout(this);
        temp.setOrientation(LinearLayout.HORIZONTAL);
        temp.addView(verticalcheckboxes);
        temp.addView(feeLayout);





        final Button save= new Button(this);
        save.setText("Save");
        //update.setLayoutParams(lparams);
        save.setTextColor(Color.parseColor("#FFFFFF"));

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                renewalLayout.setVisibility(View.GONE);

                plate.setValidity(addToValidity(plate.getValidity()));
                dbHelper.updatePlateValidity(plate);
                String validity = plate.getValidity();
                String validitySentence = processValidity(validity);
                validitytext.setText(validitySentence);
                String[] sentence = validitytext.getText().toString().split(" ");
                if (sentence[0].equals("Expired")){
                    expirationbutton.setVisibility(View.VISIBLE);
                } else {
                    expirationbutton.setVisibility(View.GONE);
                }
                receipt.setVisibility(View.VISIBLE);
                Toast toast = Toast.makeText(
                        getApplicationContext(), "Licence renewed", Toast.LENGTH_SHORT);
                toast.show();




            }
        });


        final Button annul= new Button(this);
        annul.setText("Go back");
        //update.setLayoutParams(lparams);
        annul.setTextColor(Color.parseColor("#FFFFFF"));

        annul.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                renewalLayout.setVisibility(View.GONE);
                String[] sentence = validitytext.getText().toString().split(" ");
                if (sentence[0].equals("Expired")){
                    expirationbutton.setVisibility(View.VISIBLE);
                } else {
                    expirationbutton.setVisibility(View.GONE);
                }


            }
        });


        LinearLayout savebuttons = new LinearLayout(this);
        savebuttons.setOrientation(LinearLayout.HORIZONTAL);
        savebuttons.addView(save);
        savebuttons.addView(annul);



        renewalLayout.setOrientation(LinearLayout.VERTICAL);
        renewalLayout.addView(temp);
        renewalLayout.addView(savebuttons);
        renewalLayout.setVisibility(View.GONE);







        expirationbutton.setText("Renew License");
        expirationbutton.setLayoutParams(lparams);
        expirationbutton.setTextColor(Color.parseColor("#FFFFFF"));
        String[] words = validitySentence.split(" ");
        Log.e(words[0]," is the detected word");
        if (words[0].equals("Expired") ){
            expirationbutton.setVisibility(View.VISIBLE);
        } else {
            expirationbutton.setVisibility(View.GONE);
        }
        expirationbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                expirationbutton.setVisibility(View.GONE);
                renewalLayout.setVisibility(View.VISIBLE);


            }
        });


        receipt.setText("Get Receipt");
        //update.setLayoutParams(lparams);
        receipt.setTextColor(Color.parseColor("#FFFFFF"));
        receipt.setVisibility(View.GONE);


        horizontalValidity.addView(validitytext);
        horizontalValidity.addView(expirationbutton);
        horizontalValidity.addView(receipt);

        LinearLayout validityFinal = new LinearLayout(this);
        validityFinal.setOrientation(LinearLayout.VERTICAL);
        //validityFinal.addView(horizontalValidity);
        //validityFinal.addView(renewalLayout);






        linear.addView(idtext);
        linear.addView(img);
        linear.addView(horizontal);
        linear.addView(locationholder);
        linear.addView(locationText);
        linear.addView(timeholder);
        linear.addView(timeText);
        linear.addView(typeholder);
        linear.addView(typetext);
        linear.addView(ownerholder);
        linear.addView(ownertext);
        /// here the layout of validity
        linear.addView(validityholder);
        //linear.addView(validityFinal);
        linear.addView(horizontalValidity);
        linear.addView(renewalLayout);





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

    private String processValidity(String validity){
        long validitydays = dateToDays(validity);
        String validitySentence;
        String time = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        long todayDays = dateToDays(time);
        long difference = validitydays - todayDays;
        if (difference >= 0){
            validitySentence = "Expires on " + validity + ", " + difference + " days left";
        }else {
            difference = - difference;
            validitySentence = "Expired on " + validity + ", " + difference + " days ago";
        }
        return validitySentence;

    }

    private long dateToDays(String date){
        String[] dates = date.split("-");
        long monthdays = monthToDays(Long.parseLong(dates[1]));
        long daydays = Long.parseLong(dates[0]);
        long yeardays = (Long.parseLong(dates[2]) - 1)*365;
        long days = monthdays + daydays + yeardays;
        return days;

    }

    private long monthToDays(long month){
        long days = 0;
        for (int i = 1;i<month;i++){
            if (i == 2){
                days += 28;
            } else if (i%2==1 && i<=7){
                days += 31;
            } else if (i%2 == 0 && i<=7){
                days += 30;
            } else if (i%2 == 0 && i>7){
                days += 31;
            } else {
                days += 30;
            }
        }
        return days;
    }

    private String getFee(){
        return "100 USD";
    }

    private String addToValidity(String validity){
        String[] dates = validity.split("-");
        long month = Long.parseLong(dates[1]);
        long year = Long.parseLong(dates[2]);
        if (month == 12){
            year += 1;
            month = 1;
        } else {
            month += 1;
        }
        String date = dates[0] + "-" + month + "-" + year;
        return date;

    }

}
