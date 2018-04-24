package org.tensorflow.demo.util;

import java.util.ArrayList;

import org.tensorflow.demo.R;
import org.tensorflow.demo.model.DummyModel;

public class DummyContent {
	
	/* This method gives us just a dummy content - array list
	 * of ImageGalleryCategoryModels. Every model has id that is
	 * need for some classes (e.g. DefaultAdapter.java).
	 * Favourites are randomly chosen to be true or false.
	 * Last category is randomly added to the list so you could
	 * see when there are even or odd numbers of categories in
	 * ImageGalleryActivity.
	 */

	public static ArrayList<DummyModel> getDummyModelDragAndDropTravelList() {
		ArrayList<DummyModel> list = new ArrayList<>();

		list.add(new DummyModel(0, "https://www.positivepromotions.com/images/175/NT-4939.jpg", "Volunteers Celebration Pack", R.string.fontello_heart_empty));
		list.add(new DummyModel(1, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/1.jpg", "River walk tour", R.string.fontello_heart_empty));
		list.add(new DummyModel(2, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/2.jpg", "City walk tour", R.string.fontello_heart_empty));
		/*list.add(new DummyModel(3, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/3.jpg", "Park walk tour", R.string.fontello_heart_empty));
		list.add(new DummyModel(4, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/4.jpg", "Vilage walk tour", R.string.fontello_heart_empty));
		list.add(new DummyModel(5, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/5.jpg", "Lake walk tour", R.string.fontello_heart_empty));
		list.add(new DummyModel(6, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/6.jpg", "Castle walk tour", R.string.fontello_heart_empty));
		list.add(new DummyModel(7, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/7.jpg", "Beach walk tour", R.string.fontello_heart_empty));*/
		
		return list;
	}
	
	public static ArrayList<DummyModel> getDummyModelListTravel() {
		ArrayList<DummyModel> list = new ArrayList<>();

		list.add(new DummyModel(0, "", "Joe's restaurant", R.string.fontello_heart_empty));
		list.add(new DummyModel(1, "", "Good restaurant", R.string.fontello_heart_empty));
		list.add(new DummyModel(2, "", "Express restaurant", R.string.fontello_heart_empty));
		list.add(new DummyModel(3, "", "Mine restaurant", R.string.fontello_heart_empty));
		list.add(new DummyModel(4, "", "Love restaurant", R.string.fontello_heart_empty));
		list.add(new DummyModel(5, "", "Story restaurant", R.string.fontello_heart_empty));
		
		return list;
	}

}
