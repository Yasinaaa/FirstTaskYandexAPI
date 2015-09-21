package com.example.yasina.firsttask;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

import com.example.yasina.firsttask.database.PlacesDAO;
import com.example.yasina.firsttask.marker.Markers;
import com.example.yasina.firsttask.place.Place;
import com.example.yasina.firsttask.place.PlacesBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;

import static android.view.View.*;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MapFragment
        extends
        com.google.android.gms.maps.MapFragment{

    private String TAG = "MapFragment";
    private static final int POPUP_POSITION_REFRESH_INTERVAL = 16;
    private static final int ANIMATION_DURATION = 500;
    private LatLng trackedPosition;
    private Handler handler;
    private Runnable positionUpdaterRunnable;
    private int popupXOffset;
    private int popupYOffset;
    private AbsoluteLayout.LayoutParams overlayLayoutParams;

    private ViewTreeObserver.OnGlobalLayoutListener infoWindowLayoutListener;
    public static View infoWindowContainer;

    private PlacesBuilder mPlaceBuilder;
    private LatLng mCurrentLatLng;
    private GoogleMap mMap;
    private int markerHeight;
    private ArrayList<Place> mBuildingsListPos;
    private PlacesDAO mPlacesDAO;
    private Markers mMarker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markerHeight = getResources().getDrawable(R.drawable.pin).getIntrinsicHeight();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment, null);

        FrameLayout containerMap = (FrameLayout) rootView.findViewById(R.id.container_map);
        View mapView = super.onCreateView(inflater, container, savedInstanceState);
        containerMap.addView(mapView, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        mMap = getMap();
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.clear();
        double currentLatitude = MainActivity.mCurrentLocation.getLatitude();
        double currentLongitude = MainActivity.mCurrentLocation.getLongitude();

        mCurrentLatLng = new LatLng(currentLatitude, currentLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 13));

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pin);
        mMap.addMarker(new MarkerOptions()
                .position(mCurrentLatLng)
                .icon(icon)
                .title(getResources().getString(R.string.title_current_location)));

        mPlacesDAO = new PlacesDAO(MainActivity.mContext);
        Place mPlace = new Place(mCurrentLatLng,
                MainActivity.mContext.getResources().getString(R.string.title_current_location),
                MainActivity.mContext.getResources().getString(R.string.description_current_location));
        mPlacesDAO.addPlace(mPlace);

        getMarkersFromDB();

        mPlaceBuilder = new PlacesBuilder(mMap, getActivity(), mBuildingsListPos);

        infoWindowContainer = rootView.findViewById(R.id.container_popup);

        return rootView;
    }


    private void getMarkersFromDB(){
        try {
            mBuildingsListPos = mPlacesDAO.getAllPlaces();
            mMarker = new Markers(getActivity(), mMap, mBuildingsListPos);
            mMarker.setMarkers(getActivity());
        }catch (Exception e){
            Log.e(TAG, "no such table");
            mBuildingsListPos = new ArrayList<Place>();
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handler = new Handler(Looper.getMainLooper());
        positionUpdaterRunnable = new PositionUpdaterRunnable();
        handler.post(positionUpdaterRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        infoWindowContainer.getViewTreeObserver().removeGlobalOnLayoutListener(infoWindowLayoutListener);
        handler.removeCallbacks(positionUpdaterRunnable);
        handler = null;
    }



    private class PositionUpdaterRunnable implements Runnable {
        private int lastXPosition = Integer.MIN_VALUE;
        private int lastYPosition = Integer.MIN_VALUE;

        @Override
        public void run() {

            handler.postDelayed(this, POPUP_POSITION_REFRESH_INTERVAL);

            if (trackedPosition != null && infoWindowContainer.getVisibility() == VISIBLE) {
                Point targetPosition = getMap().getProjection().toScreenLocation(trackedPosition);

                if (lastXPosition != targetPosition.x || lastYPosition != targetPosition.y) {
                    overlayLayoutParams.x = targetPosition.x - popupXOffset;
                    overlayLayoutParams.y = targetPosition.y - popupYOffset   - markerHeight - 30;
                    infoWindowContainer.setLayoutParams(overlayLayoutParams);
                    lastXPosition = targetPosition.x;
                    lastYPosition = targetPosition.y;
                }
            }
        }
    }
}
