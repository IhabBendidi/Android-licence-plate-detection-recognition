package org.tensorflow.demo;

import android.provider.BaseColumns;

public final class ShopContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ShopContract() {}


    /* Inner class that defines the table contents */
    public static class ShopEntry implements BaseColumns {
        public static final String TABLE_NAME = "shop";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_MANAGER = "manager";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_LICENSE = "license";
        public static final String COLUMN_NAME_IMAGE = "image";
    }


    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ShopEntry.TABLE_NAME + " (" +
                    ShopEntry._ID + " INTEGER PRIMARY KEY," +
                    ShopEntry.COLUMN_NAME_NAME + " TEXT," +
                    ShopEntry.COLUMN_NAME_MANAGER + " TEXT," +
                    ShopEntry.COLUMN_NAME_PHONE + " TEXT," +
                    ShopEntry.COLUMN_NAME_LOCATION + " TEXT," +
                    ShopEntry.COLUMN_NAME_TYPE + " TEXT," +
                    ShopEntry.COLUMN_NAME_LICENSE + " TEXT," +
                    ShopEntry.COLUMN_NAME_IMAGE + " TEXT)";



    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ShopEntry.TABLE_NAME;

}
