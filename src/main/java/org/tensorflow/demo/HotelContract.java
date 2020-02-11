package org.tensorflow.demo;

import android.provider.BaseColumns;

public final class HotelContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private HotelContract() {}


    /* Inner class that defines the table contents */
    public static class HotelEntry implements BaseColumns {
        public static final String TABLE_NAME = "hotel";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_MANAGER = "manager";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_LICENSE = "license";
        public static final String COLUMN_NAME_IMAGE = "image";
    }


    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + HotelEntry.TABLE_NAME + " (" +
                    HotelEntry._ID + " INTEGER PRIMARY KEY," +
                    HotelEntry.COLUMN_NAME_NAME + " TEXT," +
                    HotelEntry.COLUMN_NAME_MANAGER + " TEXT," +
                    HotelEntry.COLUMN_NAME_PHONE + " TEXT," +
                    HotelEntry.COLUMN_NAME_LOCATION + " TEXT," +
                    HotelEntry.COLUMN_NAME_TYPE + " TEXT," +
                    HotelEntry.COLUMN_NAME_LICENSE + " TEXT," +
                    HotelEntry.COLUMN_NAME_IMAGE + " TEXT)";



    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HotelEntry.TABLE_NAME;

}
