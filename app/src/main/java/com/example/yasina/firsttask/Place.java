package com.example.yasina.firsttask;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by yasina on 15.09.15.
 */
public class Place {

    private LatLng placePos;
    private String name;

    public Place() {
    }

    public Place(LatLng placePos, String name) {
        this.placePos = placePos;
        this.name = name;
    }

    public LatLng getPlacePos() {
        return placePos;
    }

    public void setPlacePos(LatLng placePos) {
        this.placePos = placePos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
