package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.google.android.gms.location.LocationListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

public class MapActivity extends Activity implements OnMapReadyCallback, View.OnClickListener,
    GoogleApiClient.ConnectionCallbacks, LocationListener{

  // Fields---------------------------------------------------------

  private static final boolean EMULATOR = false; //TODO Remove
  private static final String TAG = "MapActivity";

  private CacheLayer cacheLayer;
  private FloatingActionButton fabView;
  private GoogleMap mapView;

  private static final long LOC_INTERVAL = 5000; //5s
  private static final LatLng LOC_TOWER = new LatLng(30.2861, -97.739321);
  private static final LocationRequest LOC_REQUEST = new LocationRequest();

  private boolean locationFollow;
  private GoogleApiClient locationClient;
  private Marker locationMarker;

  // Methods--------------------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);

    MapFragment fragment =
        (MapFragment)getFragmentManager().findFragmentById(R.id.map);
    fragment.getMapAsync(this);

    fabView = (FloatingActionButton)findViewById(R.id.location);
    fabView.setOnClickListener(this);

    getLocationConfig();
    getParseConfig();
    getStatusBarConfig();

    cacheLayer = new CacheLayer(this);
  }

  private void getLocationConfig() {
    LOC_REQUEST.setInterval(LOC_INTERVAL);
    LOC_REQUEST.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    locationClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addApi(LocationServices.API)
        .build();
  }

  private void getParseConfig() {
    ParseObject.registerSubclass(BuildingJSON.class);
    Parse.initialize(this, "xTzPEGb9UXNKHH6lEphikPyDpfXeSinJ9HoIqODU",
        "tmEVaWNvPic1VQd2c69Zn0u6gieingOJcMIF6zrD");

    // Privacy settings
    ParseACL acl = new ParseACL();
    acl.setPublicReadAccess(true);
    ParseACL.setDefaultACL(acl, true);
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
  public void onResume() {
    super.onResume();
    locationClient.connect();
  }

  @Override
  public void onPause() {
    super.onPause();
    if (locationClient.isConnected()) {
      LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, this);
      locationClient.disconnect();
    }
  }

  @Override
  public void onMapReady(GoogleMap map) {
    mapView = map;
    mapView.setBuildingsEnabled(true);
    mapView.setIndoorEnabled(false);

    //TODO Change following line for device/emulator
    mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(LOC_TOWER, EMULATOR ? 13.9f : 17f));

    locationMarker = mapView.addMarker(new MarkerOptions()
        .position(LOC_TOWER)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
    cacheLayer.loadMarkers(mapView);
  }

  @Override
  public void onClick(View v) {
    if (v == fabView) {
      locationFollow = !locationFollow;

      if (locationFollow) {
        fabView.setIcon(R.drawable.ic_crosshair);
        mapView.animateCamera(CameraUpdateFactory.newLatLng(locationMarker.getPosition()));
      }
      else {
        fabView.setIcon(R.drawable.ic_crosshair_disabled);
      }
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    Location location = LocationServices.FusedLocationApi.getLastLocation(locationClient);
    if (location != null) {
      onLocationChanged(location);
    }

    LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, LOC_REQUEST, this);
  }

  @Override
  public void onLocationChanged(Location location) {
    LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
    locationMarker.setPosition(coordinate);

    if (locationFollow) {
      mapView.animateCamera(CameraUpdateFactory.newLatLng(coordinate));
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return false;
  }

  @Override
  public void onConnectionSuspended(int i) {}
}
