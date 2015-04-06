package edu.utexas.cs.bevomaps;

import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.HashMap;
import java.util.List;

/**
 * MarkerTask.java
 *
 * Created by Eric on 4/3/15.
 */

class MarkerTask extends AsyncTask<Void, Void, List<HashMap<String, String>>> {

  // Fields---------------------------------------------------------

  private final GoogleMap googleMap;
  private final HashMap<String, HashMap<String, String>> markerMap;

  // Constructors---------------------------------------------------

  MarkerTask(HashMap<String, HashMap<String, String>> markerMap, GoogleMap googleMap) {
    this.googleMap = googleMap;
    this.markerMap = markerMap;
  }

  // Methods--------------------------------------------------------

  @Override
  protected List<HashMap<String, String>> doInBackground(Void... params) {
    return DataLayer.getMarkerList();
  }

  @Override
  protected void onPostExecute(List<HashMap<String, String>> list) {
    for (HashMap<String, String> map : list) {
      LatLng position = new LatLng(Double.parseDouble(map.get(DataLayer.LATITUDE)),
          Double.parseDouble(map.get(DataLayer.LONGITUDE)));

      googleMap.addMarker(new MarkerOptions()
          .position(position)
          .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building))
          .anchor(0.5f, 0.5f));

      markerMap.put(map.get(DataLayer.SHORT_NAME), map);
      markerMap.put(position.toString(), map);
    }
  }
}
