package org.tensorflow.demo.adapter;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.demo.R;
import org.tensorflow.demo.model.DummyModel;
import com.nhaarman.listviewanimations.util.Swappable;

public class OfferAdapter extends BaseAdapter implements Swappable,
		OnClickListener {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<DummyModel> mDummyModelList;

	public OfferAdapter(Context context,
						ArrayList<DummyModel> dummyModelList,
						boolean shouldShowDragAndDropIcon) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDummyModelList = dummyModelList;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getCount() {
		return mDummyModelList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDummyModelList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mDummyModelList.get(position).getId();
	}

	@Override
	public void swapItems(int positionOne, int positionTwo) {
		Collections.swap(mDummyModelList, positionOne, positionTwo);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.list_item_offer, parent, false);
			holder = new ViewHolder();

			holder.name = (TextView) convertView.findViewById(R.id.list_item_parallax_offer_name);
			holder.text = (TextView) convertView
					.findViewById(R.id.list_item_parallax_offer_text);
			holder.favorite = (TextView) convertView.findViewById(R.id.list_item_parallax_offer_icon_favorite);
			holder.comment = (TextView) convertView.findViewById(R.id.list_item_parallax_offer_icon_comment);
			holder.share = (TextView) convertView.findViewById(R.id.list_item_parallax_offer_icon_share);
			holder.favorite.setOnClickListener(this);
			holder.comment.setOnClickListener(this);
			holder.share.setOnClickListener(this);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		DummyModel dm = mDummyModelList.get(position);

		holder.name.setText(dm.getText());
		holder.text.setText(R.string.lorem_ipsum_short);
		
		holder.favorite.setTag(position);
		holder.comment.setTag(position);
		holder.share.setTag(position);
		return convertView;
	}

	private static class ViewHolder {
		public TextView name;
		public TextView text;
		public TextView favorite;
		public TextView comment;
		public TextView share;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int position = (Integer) v.getTag();
		switch (v.getId()) {
		case R.id.list_item_parallax_offer_icon_favorite:
			Toast.makeText(mContext, "Favorite: " + position, Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.list_item_parallax_offer_icon_comment:
			Toast.makeText(mContext, "Comment: " + position, Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.list_item_parallax_offer_icon_share:
			Toast.makeText(mContext, "Share: " + position, Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}
}
