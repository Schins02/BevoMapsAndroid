package edu.utexas.cs.bevomaps;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/*** Think about adding callback functionality somehow to let the cache know the urls are available ***/

public class BuildingData {

    private static final String TAG = "*** BuildingData ***";
    private JSONObject mBuildingJSON;
    private boolean mImageMapsPopulated;
    private HashMap<String, HashMap<String, String>> mImageMaps;

    public BuildingData() {

        ParseQuery<BuildingJSON> query = ParseQuery.getQuery("BuildingJSON");
        query = query.whereEqualTo("pk", "jsonObj");

        query.getFirstInBackground(new GetCallback<BuildingJSON>() {
            @Override
            public void done(BuildingJSON buildingJSON, ParseException parseException) {
                mImageMaps = new HashMap<>();

                if (parseException == null) {
                    mBuildingJSON = buildingJSON.getJSONObject("Buildings");
                    Iterator<String> iter = mBuildingJSON.keys();
                    while (iter.hasNext()) {
                        String building = iter.next();
                        JSONObject buildingInfo;
                        try {
                            buildingInfo = mBuildingJSON.getJSONObject(building);
                            HashMap<String, String> buildingInfoMap = new HashMap<>();
                            Iterator<String> innerIter = buildingInfo.keys();
                            while (innerIter.hasNext()) {
                                String key = innerIter.next();
                                buildingInfoMap.put(key, buildingInfo.getString(key));
                            }
                            mImageMaps.put(building, buildingInfoMap);

                        } catch (JSONException jsonException) {
                            Log.d(TAG, "JSON exception => " + jsonException.toString());
                        }
                    }
                    Log.d(TAG, "loaded imageMap => " + mImageMaps.toString());
                    mImageMapsPopulated = true;
                } else {
                    Log.d(TAG, "ParseException => " + parseException);
                }
            }
        });
    }

    public boolean imageMapsPopulated(){
        return mImageMapsPopulated;
    }

    public HashMap<String, HashMap<String, String>> getImageMap(){
        return mImageMaps;
    }

}
