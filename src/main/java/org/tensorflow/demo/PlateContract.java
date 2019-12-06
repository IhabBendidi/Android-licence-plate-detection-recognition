package org.tensorflow.demo;

import android.provider.BaseColumns;

public final class PlateContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PlateContract() {}

    /* Inner class that defines the table contents */
    public static class PlateEntry implements BaseColumns {
        public static final String TABLE_NAME = "plate";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_VALIDITY = "validity";
        public static final String COLUMN_NAME_OWNER = "owner";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_IMAGE = "image";

    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PlateEntry.TABLE_NAME + " (" +
                    PlateEntry._ID + " INTEGER PRIMARY KEY," +
                    PlateEntry.COLUMN_NAME_TYPE + " TEXT," +
                    PlateEntry.COLUMN_NAME_VALIDITY + " TEXT," +
                    PlateEntry.COLUMN_NAME_OWNER + " TEXT," +
                    PlateEntry.COLUMN_NAME_LOCATION + " TEXT," +
                    PlateEntry.COLUMN_NAME_DATE + " TEXT," +
                    PlateEntry.COLUMN_NAME_TEXT + " TEXT," +
                    PlateEntry.COLUMN_NAME_IMAGE + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PlateEntry.TABLE_NAME;
}
