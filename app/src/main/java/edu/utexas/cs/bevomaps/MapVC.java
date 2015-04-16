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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * MapVC.java
 *
 * Created by Eric on 4/2/15.
 */

class MapVC implements GoogleApiClient.ConnectionCallbacks, LocationListener {

  // Fields---------------------------------------------------------

  private static final float DEFAULT_ZOOM = 17;
  private static final long UPDATE_FREQ = 5000; //5s

  private boolean curFollow;
  private Circle curCircle;
  private Marker curMarker;

  private GoogleMap googleMap;

  private final GoogleApiClient apiClient;
  private final LocationRequest locRequest;
  private final View mapView;

  // Constructors---------------------------------------------------

  MapVC(Context context, MapFragment mapFragment, final CameraPosition cameraPos,
        final CacheLayer cacheLayer, final int[] circleColors) {
    locRequest = new LocationRequest();
    locRequest.setInterval(UPDATE_FREQ);
    locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    apiClient = new GoogleApiClient.Builder(context)
        .addConnectionCallbacks(this)
        .addApi(LocationServices.API)
        .build();

    mapView = mapFragment.getView();
    final LatLng utTower = new LatLng(30.2861, -97.739321);

    mapFragment.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap map) {
        googleMap = map;
        configureMap();

        curCircle = map.addCircle(new CircleOptions()
            .center(utTower)
            .fillColor(circleColors[0])
            .strokeColor(circleColors[1])
            .strokeWidth(2));

        curMarker = map.addMarker(new MarkerOptions()
            .position(utTower)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
            .anchor(0.5f, 1.0f));

        map.moveCamera(cameraPos != null ?
            CameraUpdateFactory.newCameraPosition(cameraPos) :
            CameraUpdateFactory.newLatLngZoom(utTower, DEFAULT_ZOOM));

        cacheLayer.loadMarkers(map);
      }
    });
  }

  // Methods--------------------------------------------------------

  @Override
  public void onConnected(Bundle bundle) {
    setLocation();
    LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locRequest, this);
  }

  @Override
  public void onLocationChanged(Location location) {
    if (googleMap == null) {
      return;
    }

    LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
    curCircle.setCenter(position);
    curCircle.setRadius(location.getAccuracy() * 0.75);
    curMarker.setPosition(position);

    if (curFollow) {
      googleMap.animateCamera(CameraUpdateFactory
          .newCameraPosition(new CameraPosition(position, DEFAULT_ZOOM, 0, 0)));
    }
  }

  @Override
  public void onConnectionSuspended(int i) {}

  void connectLocations() {
    apiClient.connect();
  }

  void disconnectLocations() {
    if (apiClient.isConnected()) {
      LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
      apiClient.disconnect();
    }
  }

  CameraPosition getCameraPosition() {
    return googleMap != null ? googleMap.getCameraPosition() : null;
  }

  void invalidate() {
    mapView.invalidate();
  }

  void setCurFollow(boolean curFollow) {
    this.curFollow = curFollow;
    if (curFollow) {
      setLocation();
    }
  }

  private void configureMap() {
    UiSettings settings = googleMap.getUiSettings();
    settings.setCompassEnabled(false);
    settings.setMapToolbarEnabled(false);
    settings.setZoomControlsEnabled(false);
    googleMap.setBuildingsEnabled(true);
    googleMap.setIndoorEnabled(false);
  }

  private void setLocation() {
    Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
    if (location != null) {
      onLocationChanged(location);
    }
  }
}
