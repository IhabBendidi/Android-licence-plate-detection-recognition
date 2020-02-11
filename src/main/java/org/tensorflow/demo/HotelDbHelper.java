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
        db.execSQL(PlateContract.SQL_DELETE_ENTRIES);
        db.execSQL(PlateContract.SQL_CREATE_ENTRIES);
        Hotel hotel = new Hotel("Novotel Paris Les Halles","","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel);
        hotel = new Hotel("Paris Gare de Lyon","","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel);
        hotel = new Hotel("Astotel","","John Doe","+47 6 65 54 43 32","France","Luxury","Valid");
        addHotel(hotel);
        hotel = new Hotel("Hotel la Manufacture","","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel);
        hotel = new Hotel("Pullman Paris Tour Eiffel","","John Doe","+47 6 65 54 43 32","France","Luxury","Valid");
        addHotel(hotel);
        hotel = new Hotel("Paris Gare de Lyon","","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel);
        hotel = new Hotel("Hotel Eiffel Blomet","","John Doe","+47 6 65 54 43 32","France","Luxury","Valid");
        addHotel(hotel);
        hotel = new Hotel("Hotel Gare Montparnasse","","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel);
        hotel = new Hotel("Hyatt Regency Paris Etoile","","John Doe","+47 6 65 54 43 32","France","Luxury","Valid");
        addHotel(hotel);
        hotel = new Hotel("Mercure Paris Hotel","","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel);
        hotel = new Hotel("Hotel Saint Germain de Pres","","John Doe","+47 6 65 54 43 32","France","Luxury","Valid");
        addHotel(hotel);
        hotel = new Hotel("Hotel Malte","","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel);
        hotel = new Hotel("Hotel Darcis","","John Doe","+47 6 65 54 43 32","France","Luxury","Valid");
        addHotel(hotel);
        hotel = new Hotel("Hotel Saint Petersbourg","","John Doe","+47 6 65 54 43 32","France","Luxury","Valid");
        addHotel(hotel);
        hotel = new Hotel("Hotel Saint Severin","","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel);
        hotel = new Hotel("Hotel Molière","","John Doe","+47 6 65 54 43 32","France","Luxury","Valid");
        addHotel(hotel);
        hotel = new Hotel("Le Mareuil","","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel);
        hotel = new Hotel("Hotel la Nouvelle République","","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel);
        hotel = new Hotel("Hotel Fabric","","John Doe","+47 6 65 54 43 32","France","Luxury","Valid");
        addHotel(hotel);
        hotel = new Hotel("Hotel Relais de Bousquet de Paris","","John Doe","+47 6 65 54 43 32","France","Luxury","Valid");
        addHotel(hotel);
        hotel = new Hotel("Hotel de Bercy","","John Doe","+47 6 65 54 43 32","France","Luxury","Expired");
        addHotel(hotel);
        hotel = new Hotel("Hotel Marais Home","","John Doe","+47 6 65 54 43 32","France","Luxury","Valid");
        addHotel(hotel);


    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(PlateContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long addHotel(Hotel hotel){
        SQLiteDatabase writableDb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_TYPE, hotel.getType());
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_MANAGER, hotel.getManager());
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_IMAGE, hotel.getImagePath());
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_LOCATION, hotel.getLocation());
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_PHONE, hotel.getPhone());
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_NAME, hotel.getName());
        contentValues.put(HotelContract.HotelEntry.COLUMN_NAME_LICENSE, hotel.getLicense());
        long newRowId = writableDb.insert(HotelContract.HotelEntry.TABLE_NAME, null, contentValues);
        return newRowId;
    }
}
