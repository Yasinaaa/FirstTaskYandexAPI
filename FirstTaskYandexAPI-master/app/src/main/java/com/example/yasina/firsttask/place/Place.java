package com.example.yasina.firsttask.place;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by yasina on 15.09.15.
 */
public class Place {

    private long mId;
    private LatLng mPlacePos;
    private String mName, mDescription;

    public Place() {
    }

    public Place(LatLng placePos, String name, String description) {
        this.mPlacePos = placePos;
        this.mName = name;
        this.mDescription = description;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public LatLng getPlacePos() {
        return mPlacePos;
    }

    public void setPlacePos(LatLng mPlacePos) {
        this.mPlacePos = mPlacePos;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }
}
