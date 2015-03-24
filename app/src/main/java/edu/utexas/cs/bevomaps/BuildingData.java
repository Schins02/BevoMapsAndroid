package edu.utexas.cs.bevomaps;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BuildingData {

    private static final String TAG = "*** BuildingData ***";

    /**
     * Method to get HashMap of HashMaps containing data for each building
     *
     * @return This will return null if it is not able to get the data from Parse
     */
    public static HashMap<String, HashMap<String, String>> getBuildingMap() {

        ParseQuery<BuildingJSON> query = ParseQuery.getQuery("BuildingJSON");
        query = query.whereEqualTo("pk", "jsonObj");

        try {
            BuildingJSON parseBuildingJSON = query.getFirst();
            if (parseBuildingJSON != null)
                return extractImageMap(parseBuildingJSON);

        } catch (ParseException e) {
            Log.d(TAG, "ParseException => " + e);
        }

        return null;
    }

    /**
     * Helper method to parse the JSON and insert into HashMaps
     *
     * @param buildingJSON subclass of ParseObject, stores building data in JSON format
     * @return This will return the HashMap of HashMaps or null if there is a problem
     */
    private static HashMap<String, HashMap<String, String>> extractImageMap(BuildingJSON buildingJSON) {

        HashMap<String, HashMap<String, String>> imageMaps = new HashMap<>();
        JSONObject json = buildingJSON.getJSONObject("Buildings");
        Iterator<String> iter = json.keys();
        while (iter.hasNext()) {
            String building = iter.next();
            JSONObject buildingInfo;
            try {
                buildingInfo = json.getJSONObject(building);
                HashMap<String, String> buildingInfoMap = new HashMap<>();
                Iterator<String> innerIter = buildingInfo.keys();
                while (innerIter.hasNext()) {
                    String key = innerIter.next();
                    buildingInfoMap.put(key, buildingInfo.getString(key));
                }
                imageMaps.put(building, buildingInfoMap);

            } catch (JSONException jsonException) {
                Log.d(TAG, "JSON exception => " + jsonException.toString());
            }
        }

        if (imageMaps.size() > 0) {
            Log.d(TAG, "loaded imageMap => " + imageMaps.toString());
            return imageMaps;
        } else
            return null;
    }

    public static ArrayList<String> getMarkerList() {
        return null;
    }

    public static HashMap<String, String> getSearchMap() {
        return null;
    }

}
