package org.tensorflow.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RenewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RenewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RenewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    PlateDbHelper dbHelper;
    Plate plate;

    private OnFragmentInteractionListener mListener;

    public RenewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RenewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RenewFragment newInstance(String param1, String param2) {
        RenewFragment fragment = new RenewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        dbHelper = new PlateDbHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        //TextView text = new TextView(getActivity());textView3
        View renewView = inflater.inflate(R.layout.fragment_renew, container, false);


        //Log.e("The size of bundle : ",bundle.toString());
        Log.e("Renew CreateView : ","created");




        if(bundle != null){
            // handle your code here.

            Log.e("The size of bundle : ","not null");

            final TextView text = renewView.findViewById(R.id.PlateText);
            final EditText editPlateText = renewView.findViewById(R.id.editPlateText);
            TextView responseCountry = renewView.findViewById(R.id.responseCountry);
            TextView responseTime = renewView.findViewById(R.id.responseTime);
            TextView responseVehicule = renewView.findViewById(R.id.responseVehicule);
            TextView responseOwner = renewView.findViewById(R.id.responseOwner);
            final TextView responseLicense = renewView.findViewById(R.id.responseLicense);

            // Setting up all values of data

            String id = bundle.get("ID").toString();
            plate = new Plate(id);
            plate = dbHelper.readPlate(plate);
            setPlateImageView(plate,renewView);
            text.setText(plate.getText());
            editPlateText.setText(plate.getText());
            responseCountry.setText(plate.getLocation());
            responseTime.setText(plate.getDate());
            responseVehicule.setText(plate.getType());
            responseOwner.setText(plate.getOwner());
            responseLicense.setText(processValidity(plate.getValidity()));


            // Setting up logic of modification of plate text

            final ImageView editingButton = renewView.findViewById(R.id.edit_icon);
            final ImageView checkingValidationButton = renewView.findViewById(R.id.PlateValidationIcon);
            Button renewalButton = renewView.findViewById(R.id.renewalButton);

            editingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editPlateText.setVisibility(View.VISIBLE);
                    checkingValidationButton.setVisibility(View.VISIBLE);
                    editingButton.setVisibility(View.GONE);
                    text.setVisibility(View.GONE);
                }
            });

            checkingValidationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String modificationResult = editPlateText.getText().toString();
                    plate.setText(modificationResult);
                    dbHelper.updatePlateText(plate);
                    text.setText(modificationResult);
                    editPlateText.setVisibility(View.GONE);
                    checkingValidationButton.setVisibility(View.GONE);
                    editingButton.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                }
            });

            renewalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    plate.setValidity(addToValidity(plate.getValidity()));
                    dbHelper.updatePlateValidity(plate);

                    responseLicense.setText(processValidity(plate.getValidity()));
                }
            });




        }
        //return inflater.inflate(R.layout.fragment_renew, container, false);
        return renewView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setPlateImageView(Plate plate, View view){
        Bitmap b = loadImageFromStorage(plate.getImagePath());
        ImageView img = view.findViewById(R.id.plateImageView);
        img.setImageBitmap(b);
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

    @Override
    public void onDestroy() {
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
            validitySentence = "Expires on " + validity + "\n" + difference + " days left";
        }else {
            difference = - difference;
            validitySentence = "Expired on " + validity + "\n" + difference + " days ago";
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
