package edu.utexas.cs.bevomaps;

import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;
import java.util.List;

/**
 * MarkerTask.java
 *
 * Created by Eric on 4/3/15.
 */

class MarkerTask extends AsyncTask<Void, Void, List<String>> {

  // Fields---------------------------------------------------------

  private final GoogleMap map;

  // Constructors---------------------------------------------------

  MarkerTask(GoogleMap map) {
    this.map = map;
  }

  // Methods--------------------------------------------------------

  @Override
  protected List<String> doInBackground(Void... params) {
    return DataLayer.getMarkerList();
  }

  @Override
  protected void onPostExecute(List<String> list) {

  }
}
