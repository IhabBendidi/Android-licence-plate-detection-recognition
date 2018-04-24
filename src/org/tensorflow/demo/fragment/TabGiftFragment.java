package org.tensorflow.demo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tensorflow.demo.R;
import org.tensorflow.demo.adapter.GiftAdapter;
import org.tensorflow.demo.util.DummyContent;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

public class TabGiftFragment extends Fragment {

	private static final String ARG_POSITION = "position";


	private int position;
    private DynamicListView mDynamicListView;
	
	public static TabGiftFragment newInstance(int position) {
		TabGiftFragment f = new TabGiftFragment();
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
		View rootView = inflater.inflate(R.layout.fragment_tab_gift,
				container, false);

		mDynamicListView = (DynamicListView) rootView.findViewById(R.id.dynamic_listview);
		mDynamicListView.setDividerHeight(0);
		mDynamicListView.setAdapter(new GiftAdapter(getActivity(), DummyContent.getDummyModelDragAndDropTravelList()));
		ViewCompat.setElevation(rootView, 50);
		return rootView;
	}
}