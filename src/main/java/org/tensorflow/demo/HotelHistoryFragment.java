package org.tensorflow.demo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HotelHistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HotelHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotelHistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    HotelDbHelper dbHelper;


    //public TextView plateID;
    ArrayList<Hotel> hotels;

    private OnFragmentInteractionListener mListener;

    public HotelHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HotelHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HotelHistoryFragment newInstance(String param1, String param2) {
        HotelHistoryFragment fragment = new HotelHistoryFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_hotel_history, container, false);
        dbHelper = new HotelDbHelper(getActivity());
        hotels = dbHelper.readHotelsNames();
        return createHotelLayout(hotels);
    }
    public LinearLayout createSearchHotelLayout(ArrayList<Hotel> hotels, String newText){
        // Setting up the linear layout used for the multitude of plate texts
        //ScrollView scroller = new ScrollView(getActivity());
        //scroller.setId(View.generateViewId());


        ArrayList<Hotel> filteredHotels = filterHotels(hotels,newText);


        LinearLayout innerLayout= createAllHotelEntries(filteredHotels);
        innerLayout.setId(R.id.triangle);
        //scroller.addView(innerLayout);


        return innerLayout;
    }

    public ArrayList<Hotel> filterHotels(ArrayList<Hotel> hotels, String newText){
        ArrayList<Hotel> filteredPlates = new ArrayList<Hotel>();

        for (Hotel hotel : hotels){
            String existingLicense = hotel.getName();
            String[] results = existingLicense.split(newText);
            if (results.length>1){
                filteredPlates.add(hotel);
            }
        }

        return filteredPlates;
    }

    public CardView createSearchView(){
        // Setting up the search view
        CardView card = new CardView(getActivity());
        final SearchView search = new SearchView(getActivity());
        search.setBackgroundColor(getResources().getColor(R.color.colorSearch));
        search.setIconifiedByDefault(false);
        search.setQueryHint("Search for a license plate");

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {



                LinearLayout initialConstraint = getView().findViewById(R.id.withText);
                LinearLayout wrapper = getView().findViewById(R.id.sawtooth);



                if (newText.length() > 0) {
                    wrapper.removeView(getView().findViewById(R.id.triangle));
                    initialConstraint.setVisibility(View.GONE);
                    LinearLayout searchLinearLayout = createSearchHotelLayout(hotels,newText);
                    wrapper.addView(searchLinearLayout);

                } else {
                    wrapper.removeView(getView().findViewById(R.id.triangle));
                    initialConstraint.setVisibility(View.VISIBLE);

                }

                return false;
            }
        });
        card.setId(View.generateViewId());
        //search.setElevation(0.3f);
        card.addView(search);
        card.setRadius(10f);
        card.setCardElevation(8f);





        return card;
    }


    public ConstraintLayout createHotelLayout(ArrayList<Hotel> hotels){

        // Setting up the parent constraint layout parameters
        ConstraintLayout constraint = new ConstraintLayout(getActivity());
        constraint.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ConstraintLayout.LayoutParams lparams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        constraint.setLayoutParams(lparams);



        // Setting up the constraints of the components of the layout
        ConstraintSet set = new ConstraintSet();


        // Setting up the search view
        CardView search = createSearchView();
        constraint.addView(search);





        // Setting up the linear layout used for the multitude of plate texts
        ScrollView scroller = new ScrollView(getActivity());
        scroller.setId(View.generateViewId());







        LinearLayout innerConstraint = createAllHotelEntries(hotels);
        innerConstraint.setId(R.id.withText);

        LinearLayout wrapperLayout = new LinearLayout(getActivity());
        wrapperLayout.setOrientation(LinearLayout.VERTICAL);
        wrapperLayout.setId(R.id.sawtooth);
        wrapperLayout.addView(innerConstraint);



        // Setting up the constraints of the scrollview
        scroller.addView(wrapperLayout);
        constraint.addView(scroller);
        set.constrainHeight(scroller.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(scroller.getId(), ConstraintSet.MATCH_CONSTRAINT);

        int px60 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, this.getResources().getDisplayMetrics());
        set.connect(scroller.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, px60);
        set.connect(scroller.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, this.getResources().getDisplayMetrics());
        set.connect(scroller.getId(), ConstraintSet.TOP, search.getId(), ConstraintSet.BOTTOM, px);





        // Setting up the constraints for the search view
        int px50 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, this.getResources().getDisplayMetrics());
        set.constrainHeight(search.getId(), px50);
        set.constrainWidth(search.getId(), ConstraintSet.MATCH_CONSTRAINT);
        int px25 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, this.getResources().getDisplayMetrics());
        set.connect(search.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, px25);
        int px30 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, this.getResources().getDisplayMetrics());
        set.connect(search.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, px30);
        int px100 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, this.getResources().getDisplayMetrics());
        set.connect(search.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, px100);



        // Applying the constraints to the constraintlayout
        set.applyTo(constraint);
        return constraint;

    }

    public LinearLayout createAllHotelEntries(ArrayList<Hotel> hotels){

        // Creating the constraint layout that will hold all historical entries
        LinearLayout innerConstraint = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        innerConstraint.setLayoutParams(lparams);
        innerConstraint.setOrientation(LinearLayout.VERTICAL);
        //innerConstraint.setId(View.generateViewId());
        // Generating the multiple linear layouts each holding the hotel names (in the case of having only one entry, or no entry at all)
        for (Hotel hotel : hotels){
            if (hotel.getLicense().equals("Valid")){
                ConstraintLayout linear = createHotelEntry(hotel);
                innerConstraint.addView(linear);
            }
        }
        return innerConstraint;
    }

    public TextView createHotelIDEntry(Hotel hotel){
        LinearLayout.LayoutParams linearparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        String id = hotel.get_ID();
        final TextView id_text = new TextView(getActivity());
        id_text.setLayoutParams(linearparams);
        id_text.setText(id);
        id_text.setVisibility(View.GONE);
        id_text.setId(View.generateViewId());
        return id_text;
    }

    public ConstraintLayout createHotelEntry(Hotel hotel){
        // Text handling
        ConstraintLayout.LayoutParams linearparams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);


        final TextView hotelName = new TextView(getActivity());
        //plateText.setLayoutParams(linearparams);
        hotelName.setText("      " + hotel.getName());



        // Designing the shape of the text
        hotelName.setTextColor(getResources().getColor(R.color.black));
        //plateText.setTextColor(Color.BLACK);
        int px15 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, this.getResources().getDisplayMetrics());
        hotelName.setTextSize(px15);
        int px80 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, this.getResources().getDisplayMetrics());
        hotelName.setHeight(px80);
        int px300 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, this.getResources().getDisplayMetrics());
        hotelName.setWidth(px300);

        hotelName.setGravity(Gravity.CENTER_VERTICAL);





        // Create the invisible Text View of the plate ID
        TextView hotelID = createHotelIDEntry(hotel);
        final String id = hotelID.getText().toString();
        hotelName.setId(Integer.parseInt(hotelID.getText().toString()));



        // TODO : ELEVATION AND DESIGN SHOULD BE DONE
        //Setting the onclicklistener to go to the renewal fragment using the id of each entry
        hotelName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Renewal for this plate", Toast.LENGTH_SHORT).show();
                //String id = plateID.getText().toString();

                hotelName.setBackgroundColor(getResources().getColor(R.color.history_clicked_background));
                Log.e("The id is : ", id);
                RenewFragment renewFragment= new RenewFragment();

                Bundle bundle = new Bundle();
                bundle.putString("ID",id);

                renewFragment.setArguments(bundle);


                //BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                //bottomNavigationView.setSelectedItemId(R.id.action_renew);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.hotel_fragment_layout, renewFragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();

            }
        });




        // Adding the linear layout wrapping each entry
        ConstraintLayout plateLayout = new ConstraintLayout(getActivity());
        plateLayout.setLayoutParams(linearparams);
        plateLayout.setId(View.generateViewId());






        //plateLayout.addView(plateID);
        plateLayout.addView(hotelName);







        return plateLayout;
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

    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
