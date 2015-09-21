package com.example.yasina.firsttask;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.yasina.firsttask.server.ServerBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yasina on 14.09.15.
 */
public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        RoutingListener {

    private static final String TAG = "MainActivity";

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";
    protected static final int REQUEST_CODE_RESOLUTION = 1;
    private GoogleApiClient mGoogleApiClient;

    private boolean mIsInResolution;
    private GoogleMap map;
    private LatLng currentLatLng;
    private ArrayList<Place> mBuildingsListPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        map = mapFragment.getMap();
        map.setMyLocationEnabled(true);

    }


    private void getNearestPlace(Location location){
        String url = ServerBuilder.getFullYandexURL(location, 10);
        ServerBuilder.get(url, new PlacesCallback());
    }

    private void changeCamera(){
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(currentLatLng);
        CameraUpdate zoom= CameraUpdateFactory.zoomTo(17);

        map.moveCamera(center);
        map.animateCamera(zoom);
    }

    @Override
    public void onRoutingFailure() {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(PolylineOptions polylineOptions, Route route) {
        PolylineOptions polyoptions = new PolylineOptions();
        polyoptions.addAll(polylineOptions.getPoints());
        if (map != null) map.addPolyline(polyoptions);
    }

    @Override
    public void onRoutingCancelled() {

    }

    private class PlacesCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException e) {
            Log.d(TAG, "on Failure = " + e.toString());
        }

        @Override
        public void onResponse(Response response) throws IOException {

            String rs = response.body().string();
            Log.d(TAG, rs);

            mBuildingsListPos = PlaceAPI.getPLaces(rs);

            android.os.Handler handler = new android.os.Handler(getBaseContext().getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setMarkers();
                    routeToPlaces();
                    changeCamera();
                }
            });


        }
    }

    private void setMarkers(){

        for (int i = 0; i < mBuildingsListPos.size() ; i++){
            Place place = mBuildingsListPos.get(i);
            map.addMarker(new MarkerOptions().position(place.getPlacePos()).
                    title(place.getName()));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");

        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null) {
            double currentLatitude = mCurrentLocation.getLatitude();
            double currentLongitude = mCurrentLocation.getLongitude();

            currentLatLng = new LatLng(currentLatitude, currentLongitude);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13));

            map.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title("I'm here"));

            getNearestPlace(mCurrentLocation);
            }
        }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        retryConnecting();
    }

    private void routeToPlaces() {
        for (int i = 0; i < mBuildingsListPos.size() - 1; i++) {
            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.WALKING)
                    .withListener(this)
                    .waypoints(currentLatLng, mBuildingsListPos.get(i).getPlacePos())
                    .build();
            routing.execute();

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }

        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }


}