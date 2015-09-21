package com.example.yasina.firsttask.database;

/**
 * Created by yasina on 19.03.15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "DBHelper";

    public static final String TABLE_MARKERS = "markers";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LON = "longitude";
    private static final String DATABASE_NAME = "places.db";
    private static final int DATABASE_VERSION = 1;

    public static String SQL_CREATE_TABLE_PLACES = "CREATE TABLE " + TABLE_MARKERS + "("
            + COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
            + COLUMN_LAT + " TEXT NOT NULL,"
            + COLUMN_LON + " TEXT NOT NULL)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_TABLE_PLACES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading the database from version " + oldVersion + " to " + newVersion);
       db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS);
       onCreate(db);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

}

