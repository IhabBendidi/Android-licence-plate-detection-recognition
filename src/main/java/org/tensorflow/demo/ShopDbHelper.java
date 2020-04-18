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

public class ShopDbHelper extends SQLiteOpenHelper{

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ShopReader.db";

    public ShopDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ShopContract.SQL_DELETE_ENTRIES);
        db.execSQL(ShopContract.SQL_CREATE_ENTRIES);
        Log.e("1", "gets to here");
        Shop shop = new Shop("Asante bookshop","19.jpeg","Thomas Doe","0772773458","Uganda","Bookstore","Expired");
        addShop(shop,db);
        shop = new Shop("Tororo Cement LTD","20.jpeg","Thomas Doe","0772773458","Uganda","Cement Store","Expired");
        addShop(shop,db);
        shop = new Shop("Stevery Telecom","21.jpeg","Thomas Doe","0772773458","Uganda","Telecom Company","Expired");
        addShop(shop,db);
        shop = new Shop("KK Agencies","22.jpeg","Thomas Doe","0772773458","Uganda","Agency","Expired");
        addShop(shop,db);
        shop = new Shop("Mukasa and sons electronics","23.jpeg","Thomas Doe","0772773458","Uganda","Electronics","Expired");
        addShop(shop,db);
        shop = new Shop("Sakwa Salon","24.jpeg","Thomas Doe","0772773458","Uganda","Beauty Salon","Expired");
        addShop(shop,db);
        shop = new Shop("Frank Electronics","25.jpeg","Thomas Doe","0772773458","Uganda","Electronics","Expired");
        addShop(shop,db);
        shop = new Shop("New life pharmacy","26.jpeg","Thomas Doe","0772773458","Uganda","Pharmacy","Expired");
        addShop(shop,db);
        shop = new Shop("Oketcho and Sons","27.jpeg","Thomas Doe","0772773458","Uganda","Agency","Expired");
        addShop(shop,db);
        shop = new Shop("Sege Hardware","28.jpeg","Thomas Doe","0772773458","Uganda","Hardware","Expired");
        addShop(shop,db);
        shop = new Shop("Abu conjunction Network","29.jpeg","Thomas Doe","0772773458","Uganda","Telecom","Expired");
        addShop(shop,db);
        shop = new Shop("Youth coins ford","30.jpeg","Thomas Doe","0772773458","Uganda","Finance","Expired");
        addShop(shop,db);
        shop = new Shop("Deluxe mattresses","31.jpeg","Thomas Doe","0772773458","Uganda","Luxury","Expired");
        addShop(shop,db);
        shop = new Shop("Mark Five tiles","32.jpeg","Thomas Doe","0772773458","Uganda","Building Agency","Expired");
        addShop(shop,db);
        shop = new Shop("Chicken Daddy","19.jpeg","Thomas Doe","0772773458","Uganda","Food","Expired");
        addShop(shop,db);
        shop = new Shop("Mustar Classic","20.jpeg","Thomas Doe","0772773458","Uganda","Food","Expired");
        addShop(shop,db);
        Log.e("2", "gets to here");


    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(ShopContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addShop(Shop shop,SQLiteDatabase db){
        //SQLiteDatabase writableDb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ShopContract.ShopEntry.COLUMN_NAME_TYPE, shop.getType());
        contentValues.put(ShopContract.ShopEntry.COLUMN_NAME_MANAGER, shop.getManager());
        contentValues.put(ShopContract.ShopEntry.COLUMN_NAME_IMAGE, shop.getImagePath());
        contentValues.put(ShopContract.ShopEntry.COLUMN_NAME_LOCATION, shop.getLocation());
        contentValues.put(ShopContract.ShopEntry.COLUMN_NAME_PHONE, shop.getPhone());
        contentValues.put(ShopContract.ShopEntry.COLUMN_NAME_NAME, shop.getName());
        contentValues.put(ShopContract.ShopEntry.COLUMN_NAME_LICENSE, shop.getLicense());
        db.insert(ShopContract.ShopEntry.TABLE_NAME, null, contentValues);
        //return newRowId;
    }


    public ArrayList<Shop> readShopsNames(){
        SQLiteDatabase readableDb = getReadableDatabase();
        ArrayList<Shop> shops = new ArrayList<>();


// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                ShopContract.ShopEntry.COLUMN_NAME_NAME
        };



// How you want the results sorted in the resulting Cursor
        //String sortOrder =
        //PlateContract.PlateEntry.COLUMN_NAME_DATE + " DESC";
        String sortOrder =
                BaseColumns._ID + " DESC";

        Cursor cursor = readableDb.query(
                ShopContract.ShopEntry.TABLE_NAME,   // The table to query
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
                Shop s = readItem(cursor);
                shops.add(s);

            }
        }
        return shops;

    }

    public void updateShopLicense(Shop shop) {
        SQLiteDatabase writableDb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ShopContract.ShopEntry.COLUMN_NAME_LICENSE, shop.getLicense());
        String whereClause = "_id=?";
        String whereArgs[] = {shop.get_ID()};
        writableDb.update(ShopContract.ShopEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
    }

    private Shop readItem(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry._ID));
        String type = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_NAME_TYPE));
        String manager = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_NAME_MANAGER));
        String imagePath = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_NAME_IMAGE));
        String location = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_NAME_LOCATION));
        String phone = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_NAME_PHONE));
        String name = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_NAME_NAME));
        String license = cursor.getString(cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_NAME_LICENSE));
        Shop shop = new Shop(id,name,imagePath,manager,phone,location,type,license);
        return shop;
    }


    public Shop readShop(Shop shop){
        SQLiteDatabase readableDb = getReadableDatabase();
        Shop s = new Shop();



        // Filter results WHERE "title" = 'My Title'
        String selection = "_id" + " = ?";
        String[] selectionArgs = {shop.get_ID()};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                ShopContract.ShopEntry.COLUMN_NAME_NAME + " DESC";

        Cursor cursor = readableDb.query(
                ShopContract.ShopEntry.TABLE_NAME,   // The table to query
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
                s = readItem(cursor);

            }
        }
        return s;


    }
}
