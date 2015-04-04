package edu.utexas.cs.bevomaps;

import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import java.util.Map;

/**
 * MarkerTask.java
 * <p/>
 * Created by Eric on 4/3/15.
 */

class MarkerTask extends AsyncTask<Void, Void, List<Map<String, String>>> {

  // Fields---------------------------------------------------------

  private final GoogleMap map;

  // Constructors---------------------------------------------------

  MarkerTask(GoogleMap map) {
    this.map = map;
  }

  // Methods--------------------------------------------------------

  @Override
  protected List<Map<String, String>> doInBackground(Void... params) {
    return DataLayer.getMarkerList();
  }

  @Override
  protected void onPostExecute(List<Map<String, String>> list) {
    for (Map<String, String> buildingMarker : list) {
      map.addMarker(new MarkerOptions()
         .position(new LatLng(Double.parseDouble(buildingMarker.get("latitude")),
             Double.parseDouble(buildingMarker.get("longitude")))));
    }
  }
}
