package edu.utexas.cs.bevomaps;

import android.util.Log;
import com.parse.ParseException;
import com.parse.ParseQuery;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * DataLayer.java
 *
 * Created by John on 3/18/2015.
 */

class DataLayer {

    private static final String TAG = "** DataLayer **";
    static final String DEFAULT_FLOOR = "defaultFloor",
                        NUM_FLOORS = "numFloors",
                        PREVIEW_POSTFIX = "p",

                        LATITUDE = "latitude",
                        LONGITUDE = "longitude",
                        LONG_NAME = "longName",
                        THUMBNAIL = "thumbnail";

    /**
     * Method to get HashMap of HashMaps containing data for each building
     *
     * @return This will return null if it is not able to get the data from Parse
     */
    static HashMap<String, HashMap<String, String>> getBuildingMap() {

        ParseQuery<BuildingJSON> query = ParseQuery.getQuery("BuildingJSON");
        query = query.whereEqualTo("pk", "jsonObj");

        try {
            BuildingJSON parseBuildingJSON = query.getFirst();
            if (parseBuildingJSON != null)
                return extractImageMap(parseBuildingJSON);

        } catch (ParseException e) {
            Log.e(TAG, e.toString());
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
        JSONObject json = buildingJSON.getBuildingJSON();
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
                Log.e(TAG, jsonException.toString());
            }
        }

        if (imageMaps.size() > 0) {
            return imageMaps;
        } else
            return null;
    }

    /**
     * Method to get list of Marker data from Parse
     *
     * @return List of HashMaps containing marker data
     */
    static List<Map<String, String>> getMarkerList() {

        ParseQuery<BuildingJSON> query = ParseQuery.getQuery("BuildingJSON");
        query = query.whereEqualTo("pk", "jsonObj");

        try {
            BuildingJSON parseBuildingJSON = query.getFirst();
            if (parseBuildingJSON != null){
                return parseBuildingJSON.getMarkerList();
            }

        } catch (ParseException e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    static HashMap<String, String> getSearchMap() {
        return null;
    }
}
