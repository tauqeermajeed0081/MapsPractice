package com.example.tauqeer.mapspractice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tauqeer on 1/25/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Maps.db";
    public static final String TABLE_NAME = "map_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "Latitude";
    public static final String COL_3 = "Longitude";
    public static final String COL_4 = "Address";
    public static final String COL_5 = "City";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, Latitude TEXT, Longitude TEXT, Address TEXT , City TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS"+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String lat,String lng,String address,String city)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,lat);
        contentValues.put(COL_3,lng);
        contentValues.put(COL_4,address);
        contentValues.put(COL_5,city);
        long l = db.insert(TABLE_NAME,null,contentValues);
        if(l==-1)
        {
            return false;
        }
        else
            return true;
    }
    public Cursor getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor rawData = db.rawQuery("select * from "+TABLE_NAME,null);
        return rawData;
    }
    public boolean updataData(String id,String name,String surname,String marks)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,surname);
        contentValues.put(COL_4,marks);
        db.update(TABLE_NAME,contentValues,"ID = ?",new String[] { id });
        return true;
    }
    public Integer deleteData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,null,null);
    }
}
