package edu.utexas.cs.bevomaps;

import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.HashMap;
import java.util.List;

/**
 * MarkerTask.java
 * <p/>
 * Created by Eric on 4/3/15.
 */

class MarkerTask extends AsyncTask<Void, Void, List<HashMap<String, String>>> {

    // Fields---------------------------------------------------------

    private final GoogleMap map;

    // Constructors---------------------------------------------------

    MarkerTask(GoogleMap map) {
        this.map = map;
    }

    // Methods--------------------------------------------------------

    @Override
    protected List<HashMap<String, String>> doInBackground(Void... params) {
        return DataLayer.getMarkerList();
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> markerList) {
        for (HashMap<String, String> buildingMarker : markerList) {
                    map.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(buildingMarker.get("latitude")), Double.parseDouble(buildingMarker.get("longitude"))))
                            .title(buildingMarker.get("shortName"))
                            .snippet(buildingMarker.get("longName"))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );
        }
    }

}
