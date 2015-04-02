package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.location.LocationListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

/**
 * MapActivity.java
 *
 * Created by Eric on 3/28/15.
 */

public class MapActivity extends Activity
    implements GoogleApiClient.ConnectionCallbacks, LocationListener{

  // Fields---------------------------------------------------------

  private static final boolean EMULATOR = false; //TODO Change immediately
  private static final String TAG = "MapActivity";

  private CacheLayer cacheLayer;
  private GoogleMap mapView;

  private boolean keyboardOpen;
  private View backgroundView;
  private static final AlphaAnimation FADE_IN = new AlphaAnimation(0, 1),
                                      FADE_OUT = new AlphaAnimation(1, 0);

  private boolean locationFollow;
  private GoogleApiClient locationClient;
  private Marker locationMarker;

  private static final long LOC_INTERVAL = 6000; //6s
  private static final LatLng LOC_TOWER = new LatLng(30.2861, -97.739321);
  private static final LocationRequest LOC_REQUEST = new LocationRequest();

  // Methods--------------------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);

    getAnimationConfig();
    getLocationConfig();
    getParseConfig();
    getStatusBarConfig();

    cacheLayer = new CacheLayer(this);

    final MapFragment fragment =
        (MapFragment)getFragmentManager().findFragmentById(R.id.map);
    fragment.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap map) {
        mapView = map;
        getMapConfig();
      }
    });

    final FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.location);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        locationFollow = true;
        updateLocation();
      }
    });

    final EditText text = (EditText)findViewById(R.id.search);
    text.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showKeyboard();
      }
    });
    text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        hideKeyboard(text, fragment.getView());
        return true;
      }
    });

    backgroundView = findViewById(R.id.background);
    backgroundView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (keyboardOpen) {
          hideKeyboard(text, fragment.getView());
        }

        return locationFollow = false;
      }
    });
  }

  private void showKeyboard() {
    keyboardOpen = true;
    backgroundView.startAnimation(FADE_IN);
  }

  private void hideKeyboard(View text, View redraw) {
    keyboardOpen = false;
    backgroundView.startAnimation(FADE_OUT);

    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(text.getWindowToken(), 0);

    redraw.invalidate();
  }

  @Override
  public void onLocationChanged(Location location) {
    LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
    locationMarker.setPosition(coordinate);

    if (locationFollow) {
      mapView.animateCamera(CameraUpdateFactory.newLatLng(locationMarker.getPosition()));
    }
  }

  private void updateLocation() {
    Location location = LocationServices.FusedLocationApi.getLastLocation(locationClient);
    if (location != null) {
      onLocationChanged(location);
    }
  }

  private void getAnimationConfig() {
    FADE_IN.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
        backgroundView.setAlpha(0.3f);
      }

      @Override
      public void onAnimationEnd(Animation animation) {}

      @Override
      public void onAnimationRepeat(Animation animation) {}
    });
    FADE_IN.setDuration(300);
    FADE_OUT.setFillAfter(true);
    FADE_OUT.setDuration(300);
  }

  private void getLocationConfig() {
    LOC_REQUEST.setInterval(LOC_INTERVAL);
    LOC_REQUEST.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    locationClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addApi(LocationServices.API)
        .build();
  }

  private void getMapConfig() {
    UiSettings ui = mapView.getUiSettings();
    ui.setCompassEnabled(false);
    ui.setMapToolbarEnabled(false);
    ui.setZoomControlsEnabled(false);

    mapView.setBuildingsEnabled(true);
    mapView.setIndoorEnabled(false);
    mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(LOC_TOWER, EMULATOR ? 13.9f : 17f));

    locationMarker = mapView.addMarker(new MarkerOptions()
        .position(LOC_TOWER)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
        .anchor(0.5f, 1.0f));
    cacheLayer.loadMarkers(mapView);
  }

  private void getParseConfig() {
    ParseObject.registerSubclass(BuildingJSON.class);
    Parse.initialize(this,
        "xTzPEGb9UXNKHH6lEphikPyDpfXeSinJ9HoIqODU",
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
  public void onPause() {
    super.onPause();
    if (locationClient.isConnected()) {
      LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, this);
      locationClient.disconnect();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    locationClient.connect();
  }

  @Override
  public void onConnected(Bundle bundle) {
    updateLocation();
    LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, LOC_REQUEST, this);
  }

  @Override
  public void onConnectionSuspended(int i) {}

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return false;
  }
}
