package com.example.yasina.firsttask.place;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.yasina.firsttask.MainActivity;
import com.example.yasina.firsttask.database.PlacesDAO;
import com.example.yasina.firsttask.marker.AlertDialogMarker;
import com.example.yasina.firsttask.marker.Markers;
import com.example.yasina.firsttask.server.ServerBuilder;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yasina on 19.09.15.
 */
public class PlacesBuilder {

    private final String TAG = "PlacesBuilder";
    private Context mContext;
    private Activity mActivity;
    private ArrayList<Place> mBuildingsListPos;
    private GoogleMap mGoogleMap;
    private LatLng mCurrentLatLng;
    private Markers mMarker;
    private PlacesDAO placesDAO;

    public PlacesBuilder(GoogleMap map, Activity activity, ArrayList<Place> mBuildingsListPos) {
        this.mGoogleMap = map;
        this.mActivity = activity;
        this.mContext = activity.getBaseContext();
        this.mBuildingsListPos = mBuildingsListPos;
        mCurrentLatLng = new LatLng(MainActivity.mCurrentLocation.getLatitude(),
                MainActivity.mCurrentLocation.getLongitude());
        Log.d(TAG, mCurrentLatLng.toString());
        placesDAO = new PlacesDAO(mContext);
        getNearestPlace(MainActivity.mCurrentLocation);
    }

    private void getNearestPlace(Location location){
        String url = ServerBuilder.getFullYandexURL(location, 10);
        ServerBuilder.get(url, new PlaceCallback());
    }

    private class PlaceCallback implements Callback{
        @Override
        public void onFailure (Request request,final IOException e){
        Log.d(TAG, "on Failure = " + e.toString());
        android.os.Handler handler = new android.os.Handler(mContext.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                int mReturnNum = AlertDialogMarker.setAlertDialogError(mActivity, e);
                if (mReturnNum == 1)
                    getNearestPlace(MainActivity.mCurrentLocation);
            }
        });
    }

        @Override
        public void onResponse (Response response)throws IOException {

        String rs = response.body().string();
        Log.d(TAG, rs);

        mBuildingsListPos = PlaceAPI.getPlaces(rs);
        mMarker = new Markers(mActivity,mGoogleMap, mBuildingsListPos);
        placesDAO.addPlaces(mBuildingsListPos);

        android.os.Handler handler = new android.os.Handler(mContext.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mMarker.setMarkers(mActivity);
                mMarker.routeToPlaces();
                mMarker.changeCamera();
            }
        });


    }
    }


}
