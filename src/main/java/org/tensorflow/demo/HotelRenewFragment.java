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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HotelRenewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HotelRenewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotelRenewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    HotelDbHelper dbHelper;
    Hotel hotel;
    PopupWindow popUp;
    ConstraintLayout popupLayout;
    boolean click = true;

    private OnFragmentInteractionListener mListener;

    public HotelRenewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HotelRenewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HotelRenewFragment newInstance(String param1, String param2) {
        HotelRenewFragment fragment = new HotelRenewFragment();
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
        dbHelper = new HotelDbHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        // Inflate the layout for this fragment
        final LayoutInflater inf = inflater;
        final View renewView = inflater.inflate(R.layout.fragment_hotel_renew, container, false);

        final Button paymentButton = renewView.findViewById(R.id.hotelPaymentButton);
        final TextView nameValue = renewView.findViewById(R.id.NameValue);
        final TextView managerValue = renewView.findViewById(R.id.managerValue);
        final TextView phoneValue = renewView.findViewById(R.id.phoneValue);
        final TextView locationValue = renewView.findViewById(R.id.hotelValue);
        final TextView typeValue = renewView.findViewById(R.id.typeValue);
        final TextView licenseValue = renewView.findViewById(R.id.LicenseValue);
        final CardView paymentCard = renewView.findViewById(R.id.hotelPayment);

        if(bundle != null){
            // handle your code here.

            Log.e("The size of bundle : ","not null");
            // Setting up all values of data

            String id = bundle.get("ID").toString();
            hotel = new Hotel(id);
            hotel = dbHelper.readHotel(hotel);

            if (hotel.getLicense().equals("Valid")){
                paymentCard.setVisibility(View.GONE);
            }
            String[] words = hotel.getName().split(" ");
            String name;
            if (words.length > 3){
                name = words[0] + " " + words[1] +"\n";
                for (int i=2;i<words.length;i++){
                    name += words[i] + " ";
                }
            }else{
                name = hotel.getName();
            }
            nameValue.setText(name);
            managerValue.setText(hotel.getManager());
            phoneValue.setText(hotel.getPhone());
            locationValue.setText(hotel.getLocation());
            typeValue.setText(hotel.getType());
            licenseValue.setText(hotel.getLicense());
            setHotelImageView(hotel,renewView);



            // getting metrics of the screen :
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenheight = displayMetrics.heightPixels;
            int screenwidth = displayMetrics.widthPixels;
            final int popupWidth =  (2*screenwidth)/3;
            final int popupHeight =  (2*screenheight)/5;


            paymentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    final View popUpLayout = inf.inflate(R.layout.hotel_payment_popup,
                            null);
                    final RadioGroup priceGroup = popUpLayout.findViewById(R.id.priceGroup);
                    final Button paymentHotelButton = popUpLayout.findViewById(R.id.buttonHotelPayment);
                    final CardView paymentValueCard = popUpLayout.findViewById(R.id.paymentValueCard);
                    final TextView paymentFixedValue = popUpLayout.findViewById(R.id.textView3);
                    final RadioButton checkedbutton = popUpLayout.findViewById(priceGroup.getCheckedRadioButtonId());
                    if (checkedbutton.getText().toString().split(" ")[0].equals("Fixed")){
                        paymentFixedValue.setVisibility(View.VISIBLE);
                        paymentValueCard.setVisibility(View.GONE);
                    }else{
                        paymentFixedValue.setVisibility(View.GONE);
                        paymentValueCard.setVisibility(View.VISIBLE);
                    }
                    priceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            RadioButton newlyCheckedbutton = popUpLayout.findViewById(checkedId);
                            if (newlyCheckedbutton.getText().toString().split(" ")[0].equals("Fixed")){
                                paymentFixedValue.setVisibility(View.VISIBLE);
                                paymentValueCard.setVisibility(View.GONE);
                            }else{
                                paymentFixedValue.setVisibility(View.GONE);
                                paymentValueCard.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    paymentHotelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hotel.setLicense("Valid");
                            licenseValue.setText("Valid");
                            paymentCard.setVisibility(View.GONE);
                            dbHelper.updateHotelLicense(hotel);
                            dialog.dismiss();
                        }
                    });

                    dialog.setContentView(popUpLayout);
                    dialog.getWindow().setLayout(popupWidth, popupHeight);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();
                }
            });

        }
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

    public void setHotelImageView(Hotel hotel, View view){
        Log.e("loading image :",hotel.getImagePath());
        Bitmap b = loadImageFromStorage(hotel.getImagePath());
        Log.e("Image loaded :",hotel.getImagePath());
        ImageView img = view.findViewById(R.id.HotelImageView);

        img.setImageBitmap(b);
    }

    private Bitmap loadImageFromStorage(String path)
    {
        Bitmap rotated = null;

        try {
            //File f=new File(path);
            //Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            InputStream is = getResources().getAssets().open("imghotel.jpg");
            Bitmap b = BitmapFactory.decodeStream(is);
            //Matrix matrix = new Matrix();
            //matrix.postRotate(90);
            //rotated = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
            rotated = b;
        }
        catch (Exception e)
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
}
