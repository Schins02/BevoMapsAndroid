package edu.utexas.cs.bevomaps;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
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

/**
 * MapHelper.java
 *
 * Created by Eric on 4/2/15.
 */

class MapHelper implements GoogleApiClient.ConnectionCallbacks, LocationListener {

  // Fields---------------------------------------------------------

  private boolean following;
  private GoogleMap map;
  private Marker marker;

  private final GoogleApiClient client;
  private final View view;

  private static final long INTERVAL = 6000;   //6s
  private static final LatLng UT_TOWER = new LatLng(30.2861, -97.739321);
  private static final LocationRequest REQUEST = new LocationRequest();

  // Constructors---------------------------------------------------

  MapHelper(Context context, MapFragment fragment, final CacheLayer cache) {
    REQUEST.setInterval(INTERVAL);
    REQUEST.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    view = fragment.getView();
    client = new GoogleApiClient.Builder(context)
        .addConnectionCallbacks(this)
        .addApi(LocationServices.API)
        .build();

    fragment.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {
        UiSettings settings = googleMap.getUiSettings();
        settings.setCompassEnabled(false);
        settings.setMapToolbarEnabled(false);
        settings.setZoomControlsEnabled(false);

        googleMap.setBuildingsEnabled(true);
        googleMap.setIndoorEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UT_TOWER, 17f));

        marker = googleMap.addMarker(new MarkerOptions()
            .position(UT_TOWER)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
            .anchor(0.5f, 1.0f));
        cache.loadMarkers(googleMap);
        map = googleMap;
      }
    });
  }

  // Methods--------------------------------------------------------

  @Override
  public void onConnected(Bundle bundle) {
    setLocation();
    LocationServices.FusedLocationApi.requestLocationUpdates(client, REQUEST, this);
  }

  @Override
  public void onLocationChanged(Location location) {
    LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
    marker.setPosition(position);

    if (following) {
      map.animateCamera(CameraUpdateFactory.newLatLng(position));
    }
  }

  @Override
  public void onConnectionSuspended(int i) {}

  void connect() {
    client.connect();
  }

  void disconnect() {
    if (client.isConnected()) {
      LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
      client.disconnect();
    }
  }

  void redraw() {
    view.invalidate();
  }

  void setFollowing(boolean following) {
    this.following = following;
    if (following) {
      setLocation();
    }
  }

  private void setLocation() {
    Location location = LocationServices.FusedLocationApi.getLastLocation(client);
    if (location != null) {
      onLocationChanged(location);
    }
  }
}