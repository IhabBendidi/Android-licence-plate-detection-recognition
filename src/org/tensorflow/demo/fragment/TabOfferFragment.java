package org.tensorflow.demo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tensorflow.demo.R;
import org.tensorflow.demo.adapter.OfferAdapter;
import org.tensorflow.demo.util.DummyContent;
import org.tensorflow.demo.view.pzv.PullToZoomListViewEx;

public class TabOfferFragment extends Fragment {

	private static final String ARG_POSITION = "position";


	private int position;

	public static TabOfferFragment newInstance(int position) {
		TabOfferFragment f = new TabOfferFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tab_offer,
				container, false);

		PullToZoomListViewEx listView = (PullToZoomListViewEx) rootView.findViewById(R.id.paralax_offer_list_view);
		listView.setShowDividers(0);
		listView.setAdapter(new OfferAdapter(getActivity(), DummyContent
				.getDummyModelListTravel(), false));
		ViewCompat.setElevation(rootView, 50);
		return rootView;
	}

}