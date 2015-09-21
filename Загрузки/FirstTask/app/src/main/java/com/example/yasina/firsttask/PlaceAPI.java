package com.example.yasina.firsttask;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yasina on 15.09.15.
 */
public class PlaceAPI {

    private static final String TAG = "PlaceAPI";

    public static ArrayList<Place> getPLaces(String rs) {
        ArrayList<Place> mBuildingsListPos = new ArrayList<Place>();
        JSONObject jObj = null;
        JSONArray jArray = null;
        try {
            jObj = new JSONObject(rs);

            jArray = jObj.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jArray.length(); i++) {
            try {
                JSONObject oneObject = jArray.getJSONObject(i);

                String positions = oneObject.getJSONObject("GeoObject").getJSONObject("Point").getString("pos");
                Log.d(TAG, positions);
                String[] locSplit = positions.split(" ");

                LatLng placeLocation = new LatLng(Double.parseDouble(locSplit[1]), Double.parseDouble(locSplit[0]));
                String placeName = oneObject.getJSONObject("GeoObject").getString("name");

                mBuildingsListPos.add(new Place(placeLocation, placeName));

            } catch (JSONException e) {
                Log.d("json", e.getMessage());
            }
        }
        return mBuildingsListPos;
    }


}
