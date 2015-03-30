package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

public class MapActivity extends Activity implements OnMapReadyCallback {

  // Fields---------------------------------------------------------

  private static final String TAG = "MapActivity";

  private CacheLayer cacheLayer;

  // Methods--------------------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);

    MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    getParseConfig();
    getStatusBarConfig();
    cacheLayer = new CacheLayer();
  }

  private void getParseConfig() {
    ParseObject.registerSubclass(BuildingJSON.class);
    Parse.initialize(this, "xTzPEGb9UXNKHH6lEphikPyDpfXeSinJ9HoIqODU",
        "tmEVaWNvPic1VQd2c69Zn0u6gieingOJcMIF6zrD");

    // Privacy settings
    ParseACL defaultACL = new ParseACL();
    defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);
  }

  private void getStatusBarConfig() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      WindowManager.LayoutParams params = window.getAttributes();
      params.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
      window.setAttributes(params);
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    //TODO Add markers
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return false;
  }
}
