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


public class PlateDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "PlateReader.db";
    //private SQLiteDatabase writableDb = getWritableDatabase();
    //private SQLiteDatabase readableDb = getReadableDatabase();




    public PlateDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PlateContract.SQL_CREATE_ENTRIES);
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

    public long addPlate(Plate plate){
        SQLiteDatabase writableDb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_TYPE, plate.getType());
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_DATE, plate.getDate());
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_IMAGE, plate.getImagePath());
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_LOCATION, plate.getLocation());
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_OWNER, plate.getOwner());
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_TEXT, plate.getText());
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_VALIDITY, plate.getValidity());
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_MONGOID, plate.getMongoid());
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_EXISTENCE, plate.getExistence().toString());
        long newRowId = writableDb.insert(PlateContract.PlateEntry.TABLE_NAME, null, contentValues);
        return newRowId;
    }

    public void updatePlateText(Plate plate) {
        SQLiteDatabase writableDb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_TEXT, plate.getText());
        String whereClause = "_id=?";
        String whereArgs[] = {plate.get_ID()};
        writableDb.update(PlateContract.PlateEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
    }

    public void updatePlateMongo(Plate plate,String plateID) {
        SQLiteDatabase writableDb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Log.e("DBHELPER",plateID);
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_MONGOID, plateID);
        String whereClause = "_id=?";
        //Log.e("DBHELPER",plate.get_ID());
        String whereArgs[] = {plate.get_ID()};
        writableDb.update(PlateContract.PlateEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
    }

    public void updatePlateExistence(Plate plate) {
        SQLiteDatabase writableDb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_EXISTENCE, plate.getExistence().toString());
        String whereClause = "_id=?";
        String whereArgs[] = {plate.get_ID()};
        writableDb.update(PlateContract.PlateEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
    }

    public void updatePlateValidity(Plate plate) {
        SQLiteDatabase writableDb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_VALIDITY, plate.getValidity());
        String whereClause = "_id=?";
        String whereArgs[] = {plate.get_ID()};
        writableDb.update(PlateContract.PlateEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
    }

    public void updatePlateOwner(Plate plate) {
        SQLiteDatabase writableDb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlateContract.PlateEntry.COLUMN_NAME_OWNER, plate.getOwner());
        String whereClause = "_id=?";
        String whereArgs[] = {plate.get_ID()};
        writableDb.update(PlateContract.PlateEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
    }


    public Plate readPlate(Plate plate){
        SQLiteDatabase readableDb = getReadableDatabase();
        Plate p = new Plate();



        // Filter results WHERE "title" = 'My Title'
        String selection = "_id" + " = ?";
        String[] selectionArgs = {plate.get_ID()};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                PlateContract.PlateEntry.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = readableDb.query(
                PlateContract.PlateEntry.TABLE_NAME,   // The table to query
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
                p = readItem(cursor);

            }
        }
        return p;


    }


    public ArrayList<Plate> readPlateTexts(){
        SQLiteDatabase readableDb = getReadableDatabase();
        ArrayList<Plate> plates = new ArrayList<>();


// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                PlateContract.PlateEntry.COLUMN_NAME_TEXT
        };



// How you want the results sorted in the resulting Cursor
        //String sortOrder =
                //PlateContract.PlateEntry.COLUMN_NAME_DATE + " DESC";
        String sortOrder =
                BaseColumns._ID + " DESC";

        Cursor cursor = readableDb.query(
                PlateContract.PlateEntry.TABLE_NAME,   // The table to query
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
                Plate p = readItem(cursor);
                plates.add(p);

            }
        }
        return plates;

    }


    private Plate readItem(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(PlateContract.PlateEntry._ID));
        String type = cursor.getString(cursor.getColumnIndex(PlateContract.PlateEntry.COLUMN_NAME_TYPE));
        String validity = cursor.getString(cursor.getColumnIndex(PlateContract.PlateEntry.COLUMN_NAME_VALIDITY));
        String owner = cursor.getString(cursor.getColumnIndex(PlateContract.PlateEntry.COLUMN_NAME_OWNER));
        String location = cursor.getString(cursor.getColumnIndex(PlateContract.PlateEntry.COLUMN_NAME_LOCATION));
        String date = cursor.getString(cursor.getColumnIndex(PlateContract.PlateEntry.COLUMN_NAME_DATE));
        String text = cursor.getString(cursor.getColumnIndex(PlateContract.PlateEntry.COLUMN_NAME_TEXT));
        String imagePath = cursor.getString(cursor.getColumnIndex(PlateContract.PlateEntry.COLUMN_NAME_IMAGE));
        String mongoid = cursor.getString(cursor.getColumnIndex(PlateContract.PlateEntry.COLUMN_NAME_MONGOID));
        String existence = cursor.getString(cursor.getColumnIndex(PlateContract.PlateEntry.COLUMN_NAME_EXISTENCE));
        Plate plate = new Plate(id, location,  date,  text,  imagePath, owner, validity, type,mongoid,parseInt(existence));
        return plate;
    }
}
