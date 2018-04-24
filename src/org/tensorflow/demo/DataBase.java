package org.tensorflow.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME= "angeleyes.db";
    public static final String TABLE_VO= "volontaire";
    public static final String TABLE_MV= "mal_voyant";


    private Context context;
    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists " + TABLE_VO +" (email VARCHAR(50),points INTEGER)");
        sqLiteDatabase.execSQL("create table if not exists " + TABLE_MV +" (id INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
        onCreate(sqLiteDatabase);
    }

    public void init_table(SQLiteDatabase sqLiteDatabase, String user)
    {


    }


        public boolean insertData_vo(String email, String points){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("email",email);
            contentValues.put("points",points);
            long result = db.insert(TABLE_VO,null ,contentValues);
            if(result==1)
                return true;
            else
                return false;
        }
        public boolean insertData_mv(String id){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("id",id);
            long result = db.insert(TABLE_MV,null ,contentValues);
            if(result==1)
                return true;
            else
                return false;
        }

        public void delet_vo(){
            SQLiteDatabase db=this.getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_VO);
            db.execSQL("create table " + TABLE_VO +" (email VARCHAR(50),points INTEGER)");
        }

        public void delet_mv(){
            SQLiteDatabase db=this.getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_MV);
            db.execSQL("create table " + TABLE_MV +" (id INTEGER)");
        }

        public Cursor getvo() {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res = db.rawQuery("select * from "+TABLE_VO,null);
            return res;
        }
        public Cursor getmv() {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res = db.rawQuery("select * from "+TABLE_MV,null);
            return res;
        }

}
