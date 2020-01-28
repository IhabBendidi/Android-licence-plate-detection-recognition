package org.tensorflow.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    PopupWindow popUp;
    ConstraintLayout popupLayout;
    boolean click = true;

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        //TextView text = new TextView(getActivity());textView3
        final View renewView = inflater.inflate(R.layout.fragment_renew, container, false);


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
            final TextView responseOwner = renewView.findViewById(R.id.responseOwner);
            final TextView responseLicense = renewView.findViewById(R.id.responseLicense);
            final EditText ownerRegistration = renewView.findViewById(R.id.OwnerRegistration);
            final EditText validityRegistration = renewView.findViewById(R.id.validityRegistration);
            TextView registrationTitleValidityDate = renewView.findViewById(R.id.registrationtitleValiditydate);
            CardView registrationButtonCard = renewView.findViewById(R.id.registrationButtonCard);
            CardView renewalButtonCard = renewView.findViewById(R.id.renewalButtonCard);
            TextView titleLicense = renewView.findViewById(R.id.titleLicense);
            Button renewalButton = renewView.findViewById(R.id.renewalButton);
            Button registrationButton = renewView.findViewById(R.id.registrationButton);



            // Setting up all values of data

            String id = bundle.get("ID").toString();
            plate = new Plate(id);
            plate = dbHelper.readPlate(plate);

            int existence = plate.getExistence();
            if (existence == 0){
                responseOwner.setVisibility(View.GONE);
                renewalButtonCard.setVisibility(View.GONE);
                responseLicense.setVisibility(View.GONE);
                titleLicense.setVisibility(View.GONE);


                registrationButtonCard.setVisibility(View.VISIBLE);
                registrationTitleValidityDate.setVisibility(View.VISIBLE);
                validityRegistration.setVisibility(View.VISIBLE);
                ownerRegistration.setVisibility(View.VISIBLE);

            } else if (existence == 1){ //// Add case for when existence got a value of 2, that happens when we cant succesfully check the existence of the plate in the server because of some sort of error of any kind
                //Sending the API request to the server
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("plateID", plate.getMongoid());

                Toast toast = Toast.makeText(
                        getContext(), "Updating", Toast.LENGTH_SHORT);
                toast.show();


                client.get("http://15.188.76.142:5000/revolution/get", params, new AsyncHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        Log.e("TAG", "Success");
                        try{
                            String str = new String(responseBody, "UTF-8");

                            JSONParser parser = new JSONParser();
                            JSONObject json = (JSONObject) parser.parse(str);
                            String plateType = json.get("plateType").toString();
                            String plateText = json.get("plateText").toString();
                            String plateValidity = json.get("plateValidity").toString();
                            String plateOwner = json.get("plateOwner").toString();
                            String mongoid = json.get("mongoid").toString();
                            plate.setMongoid(mongoid);
                            plate.setOwner(plateOwner);
                            plate.setValidity(plateValidity);
                            plate.setType(plateType);
                            plate.setText(plateText);
                            dbHelper.updatePlateText(plate);
                            dbHelper.updatePlateValidity(plate);
                            dbHelper.updatePlateMongo(plate,mongoid);
                            dbHelper.updatePlateText(plate);
                            text.setText(plate.getText());
                            editPlateText.setText(plate.getText());
                            responseOwner.setText(plate.getOwner());
                            responseLicense.setText(processValidity(plate.getValidity()));
                            Toast toast = Toast.makeText(
                                    getContext(), "Update Complete", Toast.LENGTH_SHORT);
                            toast.show();


                        }catch (Exception e){
                            Log.e("TAG",e.toString());
                            Toast toast = Toast.makeText(
                                    getContext(), "Internet Connection Error", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    }
                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e("TAG", "Failure");
                        Toast toast = Toast.makeText(
                                getContext(), "Internet Sychronization Error", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }else {
                // this is the case of having a value of 2 (when we still dont know whether it exists or not in the database because of internet errors)
            }
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
                    if (plate.getExistence()==1){
                        //Sending the API request to the server
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("plateID", plate.getMongoid());
                        Log.e("MONGOID",plate.getMongoid());
                        params.put("plateText", modificationResult);

                        client.get("http://15.188.76.142:5000/revolution/update", params, new AsyncHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                                Log.e("TAG", "Success");
                            }
                            @Override
                            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                                Log.e("TAG", "Failure");
                                Toast toast = Toast.makeText(
                                        getContext(), "Internet Sychronization Error", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                    plate.setText(modificationResult);
                    dbHelper.updatePlateText(plate);
                    text.setText(modificationResult);
                    editPlateText.setVisibility(View.GONE);
                    checkingValidationButton.setVisibility(View.GONE);
                    editingButton.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                }
            });






            // getting metrics of the screen :
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenheight = displayMetrics.heightPixels;
            int screenwidth = displayMetrics.widthPixels;
            final int popupWidth =  (2*screenwidth)/3;
            final int popupHeight =  (2*screenheight)/7;


            renewalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    final View popUpLayout = inflater.inflate(R.layout.renewal_popup,
                            null);
                    Button paymentButton = popUpLayout.findViewById(R.id.button4);
                    paymentButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RadioGroup priceGroup = popUpLayout.findViewById(R.id.priceGroup);
                            RadioButton checkedbutton = popUpLayout.findViewById(priceGroup.getCheckedRadioButtonId());
                            String numberMonthsPaid = checkedbutton.getText().toString().split(" ")[0];
                            String newValidity = addToValidity(plate.getValidity(),Integer.parseInt(numberMonthsPaid));
                            plate.setValidity(newValidity);
                            //Sending the API request to the server
                            AsyncHttpClient client = new AsyncHttpClient();
                            RequestParams params = new RequestParams();
                            params.put("plateID", plate.getMongoid());
                            params.put("plateValidity", newValidity);

                            client.get("http://15.188.76.142:5000/revolution/update", params, new AsyncHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                                    Log.e("TAG", "Success");
                                }
                                @Override
                                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                                    Log.e("TAG", "Failure");
                                    Toast toast = Toast.makeText(
                                            getContext(), "Internet Sychronization Error", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                            dbHelper.updatePlateValidity(plate);
                            responseLicense.setText(processValidity(plate.getValidity()));
                            dialog.dismiss();
                        }
                    });
                    dialog.setContentView(popUpLayout);
                    dialog.getWindow().setLayout(popupWidth, popupHeight);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();




                    /*String newValidity = addToValidity(plate.getValidity());
                    plate.setValidity(newValidity);
                    //Sending the API request to the server
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("plateID", plate.getMongoid());
                    params.put("plateValidity", newValidity);

                    client.get("http://15.188.76.142:5000/revolution/update", params, new AsyncHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            Log.e("TAG", "Success");
                        }
                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            Log.e("TAG", "Failure");
                            Toast toast = Toast.makeText(
                                    getContext(), "Internet Sychronization Error", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                    dbHelper.updatePlateValidity(plate);
                    responseLicense.setText(processValidity(plate.getValidity()));*/
                }


            });


            registrationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String owner = ownerRegistration.getText().toString();
                    String expiration = validityRegistration.getText().toString();
                    if (checkTyping(expiration)){
                        plate.setValidity(expiration);
                        plate.setOwner(owner);
                        //Sending the API request to the server
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("plateText", plate.getText());
                        params.put("plateType", plate.getType());
                        params.put("plateValidity", expiration);
                        params.put("plateOwner", owner);

                        Log.e("TAG", owner);
                        Log.e("TAG", expiration);
                        Log.e("TAG", plate.getType());
                        Log.e("TAG", plate.getText());

                        client.get("http://15.188.76.142:5000/revolution/register", params, new AsyncHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                                Log.e("TAG", "Success");
                                Toast toast = Toast.makeText(
                                        getContext(), "Saved in the system", Toast.LENGTH_SHORT);
                                toast.show();
                                // If success, don't forget to save plate with new id also in sqlite, and make existence = 1
                                try{
                                    String plateID = new String(responseBody, "UTF-8");

                                    plate.setExistence(1);
                                    plate.setMongoid(plateID);


                                    dbHelper.updatePlateExistence(plate);
                                    dbHelper.updatePlateValidity(plate);
                                    dbHelper.updatePlateOwner(plate);
                                    dbHelper.updatePlateMongo(plate,plateID);



                                    Log.e("MONGOID REGISTRATION",plate.getMongoid());




                                    RenewFragment renewFragment= new RenewFragment();

                                    Bundle bundle = new Bundle();
                                    bundle.putString("ID",plate.get_ID());

                                    renewFragment.setArguments(bundle);


                                    BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                                    bottomNavigationView.setSelectedItemId(R.id.action_renew);
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_layout, renewFragment, "findThisFragment")
                                            .commit();




                                }catch(Exception e){
                                    Log.e("TAG",e.toString());
                                }



                            }

                            @Override
                            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                                Log.e("TAG", "Failure");
                                Toast toast = Toast.makeText(
                                        getContext(), "Please check your internet connection and try again", Toast.LENGTH_SHORT);
                                toast.show();

                            }




                        });

                    }else{
                        Toast toast = Toast.makeText(
                                getContext(), "Expiration Date should be in the DD-MM-YYYY format", Toast.LENGTH_SHORT);
                        toast.show();
                    }

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



    private String addToValidity(String validity,Integer number){
        String[] dates = validity.split("-");
        long month = Long.parseLong(dates[1]);
        long year = Long.parseLong(dates[2]);
        if (month + number > 12){
            year += 1;
            month = (month + number )- 12;
        } else {
            month += number;
        }
        String date = dates[0] + "-" + month + "-" + year;
        return date;

    }

    private boolean checkTyping(String expiration){
        String[] dates = expiration.split("-");
        if(dates.length==3){
            try{
                long month = Long.parseLong(dates[1]);
                long year = Long.parseLong(dates[2]);
                long days = Long.parseLong(dates[0]);
                if (days>0 && month >0 && year > 0){
                    return true;
                }else{
                    return false;
                }
            }catch(Exception e){
                Log.e("Expiration Typing", e.toString());
                return false;
            }

        }else{
            return false;
        }
    }

}
