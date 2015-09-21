package com.example.yasina.firsttask.marker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.yasina.firsttask.MainActivity;
import com.example.yasina.firsttask.MapFragment;
import com.example.yasina.firsttask.R;
import com.example.yasina.firsttask.database.PlacesDAO;
import com.example.yasina.firsttask.place.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by yasina on 19.09.15.
 */
public class Markers implements RoutingListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener{

    private final String TAG = "Markers";

    private ArrayList<Place> mBuildingsListPos;
    private GoogleMap mGoogleMap;
    private LatLng mCurrentLatLng;
    private HashMap<Marker, Place> mMarkersHashMap;
    private Context mContext;
    private Activity mActivity;

    private static final int POPUP_POSITION_REFRESH_INTERVAL = 16;
    private static final int ANIMATION_DURATION = 500;
    private LatLng trackedPosition;
    private Handler handler;
    private Runnable positionUpdaterRunnable;
    private int popupXOffset;
    private int popupYOffset;
    private AbsoluteLayout.LayoutParams overlayLayoutParams;
    private int markerHeight;

    private ViewTreeObserver.OnGlobalLayoutListener infoWindowLayoutListener;
    private View infoWindowContainer;

    public Markers(Activity activity, GoogleMap map, ArrayList<Place> mBuildingsListPos) {
        this.mGoogleMap = map;
        this.mBuildingsListPos = mBuildingsListPos;
        this.mActivity = activity;
        mContext = activity.getBaseContext();
        mCurrentLatLng = new LatLng(MainActivity.mCurrentLocation.getLatitude(),
                MainActivity.mCurrentLocation.getLongitude());
        mMarkersHashMap = new HashMap<Marker, Place>();
        infoWindowContainer = MapFragment.infoWindowContainer;

    }

    private PlacesDAO mPlacesDAO;


    private class ViewHolder{
        TextView tvMarkerLabel;
        EditText etMarkerDescription;
        ImageView ivDeleteMarker, ivChangeDescription;
    }

    private ViewHolder vh;

    private void init(View v){
        vh = new ViewHolder();
        vh.tvMarkerLabel = (TextView) v.findViewById(R.id.tv_name_marker_item);
        vh.etMarkerDescription = (EditText) v.findViewById(R.id.et_description_marker_item);
        vh.ivChangeDescription = (ImageView) v.findViewById(R.id.iv_change_description_maker_item);
        vh.ivDeleteMarker = (ImageView) v.findViewById(R.id.iv_delete_marker_item);
    }

    @Override
    public void onRoutingFailure() {

    }

    @Override
    public void onRoutingStart() {

    }

    public void routeToPlaces() {

        for (int i = 0; i < mBuildingsListPos.size() - 1; i++) {
            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.WALKING)
                    .withListener(this)
                    .waypoints(mCurrentLatLng, mBuildingsListPos.get(i).getPlacePos())
                    .build();
            routing.execute();

        }
    }

    @Override
    public void onRoutingSuccess(PolylineOptions polylineOptions, Route route) {
        PolylineOptions polyoptions = new PolylineOptions();
        polyoptions.addAll(polylineOptions.getPoints());
        if (mGoogleMap != null) mGoogleMap.addPolyline(polyoptions);
    }

    @Override
    public void onRoutingCancelled() {

    }

    public void changeCamera() {
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(mCurrentLatLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);

        mGoogleMap.moveCamera(center);
        mGoogleMap.animateCamera(zoom);
    }


    public void setMarkers(Activity activity) {

        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnMarkerClickListener(this);
        mPlacesDAO = new PlacesDAO(MainActivity.mContext);

        for (int i = 0; i < mBuildingsListPos.size(); i++) {
            Place place = mBuildingsListPos.get(i);
            MarkerOptions markerOptions = new MarkerOptions().position(place.getPlacePos()).
                    title(place.getName());
            Marker mCurrentMarket = mGoogleMap.addMarker(markerOptions);
            mMarkersHashMap.put(mCurrentMarket,place);
            mPlacesDAO.addPlace(place);
        }

        if (mBuildingsListPos != null) AlertDialogMarker.setAlertDialogSuccessfulAddedNewMarkers(mActivity);
        else
            AlertDialogMarker.setAlertDialogError(mActivity, new NullPointerException());


        infoWindowLayoutListener = new InfoWindowLayoutListener();
        infoWindowContainer.getViewTreeObserver().addOnGlobalLayoutListener(infoWindowLayoutListener);
        overlayLayoutParams = (AbsoluteLayout.LayoutParams) infoWindowContainer.getLayoutParams();


        init(infoWindowContainer);

    }


    @Override
    public void onMapClick(LatLng latLng) {
        infoWindowContainer.setVisibility(INVISIBLE);
    }

    private void set(final Place place, final Marker marker){
        vh.tvMarkerLabel.setText(place.getName());
        vh.etMarkerDescription.setText(place.getDescription());
        vh.ivDeleteMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "remove marker");
                marker.remove();
                mPlacesDAO.deletePlace(place);
            }
        });
        vh.ivChangeDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "change marker");
                String newDescriptionForPlace = vh.etMarkerDescription.getText().toString();
                place.setDescription(newDescriptionForPlace);
                mPlacesDAO.updatePlace(place);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {


        Projection projection = mGoogleMap.getProjection();
        trackedPosition = marker.getPosition();
        Point trackedPoint = projection.toScreenLocation(trackedPosition);
        trackedPoint.y -= popupYOffset / 2;

        LatLng newCameraLocation = projection.fromScreenLocation(trackedPoint);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(newCameraLocation), ANIMATION_DURATION, null);

        Place mPlace = mMarkersHashMap.get(marker);
        set(mPlace, marker);

        infoWindowContainer.setVisibility(VISIBLE);

        return true;
    }

    private class InfoWindowLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            popupXOffset = infoWindowContainer.getWidth() / 2;
            popupYOffset = infoWindowContainer.getHeight();
        }
    }



}
