package org.tensorflow.demo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class HotelDbHelper extends SQLiteOpenHelper{
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HotelReader.db";

    public HotelDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HotelContract.SQL_DELETE_ENTRIES);
        db.execSQL(HotelContract.SQL_CREATE_ENTRIES);
        Log.e("1", "gets to here");
        Hotel hotel = new Hotel("Novotel Paris Les Halles","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Paris Gare de Lyon","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Astotel","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel la Manufacture","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Pullman Paris Tour Eiffel","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Paris Gare de Lyon","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel Eiffel Blomet","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel Gare Montparnasse","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hyatt Regency Paris Etoile","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Mercure Paris Hotel","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel Saint Germain de Pres","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel Malte","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel Darcis","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel Saint Petersbourg","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel Saint Severin","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel Molière","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Le Mareuil","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel la Nouvelle République","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel Fabric","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel Relais de Bousquet de Paris","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel de Bercy","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        hotel = new Hotel("Hotel Marais Home","file:///android_asset/imghotel.jpg","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel,db);
        Log.e("2", "gets to here");


    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(HotelContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addHotel(Hotel hotel,SQLiteDatabase db){
        //SQLiteDatabase writableDb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_TYPE, hotel.getType());
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_MANAGER, hotel.getManager());
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_IMAGE, hotel.getImagePath());
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_LOCATION, hotel.getLocation());
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_PHONE, hotel.getPhone());
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_NAME, hotel.getName());
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_LICENSE, hotel.getLicense());
        db.insert(HotelContract.HotelEntry.TABLE_NAME, null, contentValues);
        //return newRowId;
    }


    public ArrayList<Hotel> readHotelsNames(){
        SQLiteDatabase readableDb = getReadableDatabase();
        ArrayList<Hotel> hotels = new ArrayList<>();


// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                HotelContract.HotelEntry.COLUMN_NAME_NAME
        };



// How you want the results sorted in the resulting Cursor
        //String sortOrder =
        //PlateContract.PlateEntry.COLUMN_NAME_DATE + " DESC";
        String sortOrder =
                BaseColumns._ID + " DESC";

        Cursor cursor = readableDb.query(
                HotelContract.HotelEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // move the cursor to next row if there is any to read it's data
                Hotel h = readItem(cursor);
                hotels.add(h);

            }
        }
        return hotels;

    }

    public void updateHotelLicense(Hotel hotel) {
        SQLiteDatabase writableDb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_LICENSE, hotel.getLicense());
        String whereClause = "_id=?";
        String whereArgs[] = {hotel.get_ID()};
        writableDb.update(HotelContract.HotelEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
    }

    private Hotel readItem(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(HotelContract.HotelEntry._ID));
        String type = cursor.getString(cursor.getColumnIndex(HotelContract.HotelEntry.COLUMN_NAME_TYPE));
        String manager = cursor.getString(cursor.getColumnIndex(HotelContract.HotelEntry.COLUMN_NAME_MANAGER));
        String imagePath = cursor.getString(cursor.getColumnIndex(HotelContract.HotelEntry.COLUMN_NAME_IMAGE));
        String location = cursor.getString(cursor.getColumnIndex(HotelContract.HotelEntry.COLUMN_NAME_LOCATION));
        String phone = cursor.getString(cursor.getColumnIndex(HotelContract.HotelEntry.COLUMN_NAME_PHONE));
        String name = cursor.getString(cursor.getColumnIndex(HotelContract.HotelEntry.COLUMN_NAME_NAME));
        String license = cursor.getString(cursor.getColumnIndex(HotelContract.HotelEntry.COLUMN_NAME_LICENSE));
        Hotel hotel = new Hotel(id,name,imagePath,manager,phone,location,type,license);
        return hotel;
    }


    public Hotel readHotel(Hotel hotel){
        SQLiteDatabase readableDb = getReadableDatabase();
        Hotel h = new Hotel();



        // Filter results WHERE "title" = 'My Title'
        String selection = "_id" + " = ?";
        String[] selectionArgs = {hotel.get_ID()};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                HotelContract.HotelEntry.COLUMN_NAME_NAME + " DESC";

        Cursor cursor = readableDb.query(
                HotelContract.HotelEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // move the cursor to next row if there is any to read it's data
                h = readItem(cursor);

            }
        }
        return h;


    }
}
