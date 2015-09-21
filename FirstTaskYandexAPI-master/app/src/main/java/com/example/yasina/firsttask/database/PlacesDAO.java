package com.example.yasina.firsttask.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.yasina.firsttask.place.Place;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

/**
 * Created by yasina on 19.09.15.
 */
public class PlacesDAO {

    public static final String TAG = "PlacesDAO";

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;
    private String[] mAllColumns = {DBHelper.COLUMN_NAME_ID, DBHelper.COLUMN_NAME,
            DBHelper.COLUMN_DESCRIPTION, DBHelper.COLUMN_LAT, DBHelper.COLUMN_LON};


    public PlacesDAO(Context context) {
        mDbHelper = new DBHelper(context);
        this.mContext = context;
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public void addPlace(Place place) {
        open();
        try{
            getPlaceById(place.getId());
            Log.d(TAG, place.getId() + " this mark already exists");
        }catch (android.database.CursorIndexOutOfBoundsException e){
            ContentValues values = putValues(place);
            mDatabase.insert(DBHelper.TABLE_MARKERS, null, values);
        }
        close();
    }

    public ContentValues putValues(Place place){
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, place.getName());
        LatLng placeLatLng = place.getPlacePos();
        values.put(DBHelper.COLUMN_DESCRIPTION, place.getDescription());
        values.put(DBHelper.COLUMN_LAT, placeLatLng.latitude);
        values.put(DBHelper.COLUMN_LON, placeLatLng.longitude);
        return values;
    }

    public void addPlaces(ArrayList<Place> mAllPlaces) {
        for (int i=0; i< mAllPlaces.size(); i++){
            addPlace(mAllPlaces.get(i));
        }
    }


    public void deletePlace(Place place) {
        open();
        long id = place.getId();
        Log.d(TAG,"the deleted dictionary has the id: " + id);
        mDatabase.delete(DBHelper.TABLE_MARKERS, DBHelper.COLUMN_NAME_ID + " = " + id, null);
        close();
    }

  /*  public void deletePlace(String placeName) {
        open();
        mDatabase.delete(DBHelper.TABLE_MARKERS, DBHelper.COLUMN_NAME + " = " + placeName, null);
        close();
    }*/

    public void updatePlace(Place place){
        open();
        ContentValues values = putValues(place);
        long id = place.getId();
        mDatabase.update(DBHelper.TABLE_MARKERS, values, "id" + " =?" , new String[] { String.valueOf(id) });
        close();
    }


    public ArrayList<Place> getAllPlaces() {
        open();
        ArrayList<Place> mListPlaces = new ArrayList<Place>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_MARKERS,
                mAllColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Place place = cursorToPlace(cursor);
            mListPlaces.add(place);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return mListPlaces;
    }


    public Place getPlaceById(long id) {
        open();
        Cursor cursor = mDatabase.query(DBHelper.TABLE_MARKERS,mAllColumns,
                DBHelper.COLUMN_NAME_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Place place = cursorToPlace(cursor);
        cursor.close();
        close();
        return place;
    }

    private Place cursorToPlace(Cursor cursor) {
        Place marker = new Place();
        marker.setId(cursor.getLong(0));
        marker.setName(cursor.getString(1));
        marker.setDescription(cursor.getString(2));
        LatLng position = new LatLng(cursor.getDouble(3),cursor.getDouble(4));
        marker.setPlacePos(position);
        return marker;
    }

}


