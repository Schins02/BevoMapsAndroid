package edu.utexas.cs.bevomaps;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CacheLayer.java
 *
 * Created by Eric on 3/28/15.
 */

class CacheLayer implements Parcelable {

  // Fields---------------------------------------------------------

  static final long CACHE_SIZE = 10485760; //10MB
  private static final String TAG = CacheLayer.class.getSimpleName();

  private final File cacheDir;

  private HashMap<String, HashMap<String, String>> buildingMap;
  private HashMap<String, HashMap<String, String>> markerMap;
  private HashMap<String, String> searchMap;

  public static final Creator<CacheLayer> CREATOR = new Creator<CacheLayer>() {
    @Override
    public CacheLayer createFromParcel(Parcel in) {
      return new CacheLayer(in);
    }
    @Override
    public CacheLayer[] newArray(int size) {
      return new CacheLayer[size];
    }
  };

  // Constructors---------------------------------------------------

  CacheLayer(Context context) {
    cacheDir = new File(context.getCacheDir(), "ImageCache");
    if (cacheDir.mkdir()) {
      Log.d(TAG, "Creating image cache.");
    }

    new BuildingTask().execute();
    new MarkerTask().execute();
    new SearchTask().execute();
  }

  // Methods--------------------------------------------------------

  void loadFloors(FloorSelectorVC floorSelectorVC, String building) {
    floorSelectorVC.addItems(buildingMap.get(building).get(DataLayer.FLOOR_NAMES).split("\\s+"));
  }

  void loadImage(ImageVC imageVC, String building, String floor,
                 ImageTask.OnProgressUpdateListener listener) {
    Map <String, String> info = buildingMap.get(building);
    if (!info.containsKey(floor)) {
      floor = info.get(DataLayer.DEFAULT_FLOOR);
    }

    String imageUrl = info.get(floor),
        previewUrl = info.get(floor + DataLayer.PREVIEW_POSTFIX);
    File imageCache = new File(cacheDir, getImageName(imageUrl)),
        previewCache = new File(cacheDir, getImageName(previewUrl));

    if (imageCache.isFile() && previewCache.isFile()) {
      imageVC.setImage(Uri.fromFile(imageCache), Uri.fromFile(previewCache));
    }
    else {
      new ImageTask(imageVC, info, floor, cacheDir, listener).execute();
    }
  }

  void loadMarkers(GoogleMap map) {
    Map<String, HashMap<String, String>> temp = new HashMap<>();

    for (HashMap<String, String> info : markerMap.values()) {
      LatLng position = new LatLng(Double.parseDouble(info.get(DataLayer.LATITUDE)),
          Double.parseDouble(info.get(DataLayer.LONGITUDE)));

      map.addMarker(new MarkerOptions()
          .position(position)
          .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building))
          .anchor(0.5f, 0.5f));

      temp.put(position.toString(), info);
    }

    markerMap.putAll(temp);
  }

  Map<String, String> loadSearchMap() {
    return searchMap;
  }

  String getBuildingName(String building) {
    Map<String, String> info = markerMap.get(building);
    return info != null ? info.get(DataLayer.LONG_NAME) : null;
  }

  boolean isReady() {
    return buildingMap != null && markerMap != null && searchMap != null;
  }

  static String getImageName(String url) {
    return url.substring(url.lastIndexOf('/'));
  }

  @SuppressWarnings("unchecked")
  private CacheLayer (Parcel parcel) {
    Bundle bundle = parcel.readBundle();
    cacheDir = (File)bundle.getSerializable("cache");
    markerMap = (HashMap<String, HashMap<String, String>>)bundle.getSerializable("markers");
    buildingMap = (HashMap<String, HashMap<String, String>>)bundle.getSerializable("buildings");
    searchMap = (HashMap<String, String>)bundle.getSerializable("search");
  }

  @Override
  public void writeToParcel(Parcel parcel, int flags) {
    Bundle bundle = new Bundle();
    bundle.putSerializable("cache", cacheDir);
    bundle.putSerializable("markers", markerMap);
    bundle.putSerializable("buildings", buildingMap);
    bundle.putSerializable("search", searchMap);
    parcel.writeBundle(bundle);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  private class BuildingTask
      extends AsyncTask<Void, Void, HashMap<String, HashMap<String, String>>> {
    @Override
    protected HashMap<String, HashMap<String, String>> doInBackground(Void... params) {
      return DataLayer.getBuildingMap();
    }
    @Override
    protected void onPostExecute(HashMap<String, HashMap<String, String>> map) {
      buildingMap = map;
    }
  }

  private class MarkerTask
      extends AsyncTask<Void, Void, List<HashMap<String, String>>> {
    @Override
    protected void onPreExecute() {
      markerMap = new HashMap<>();
    }
    @Override
    protected List<HashMap<String, String>> doInBackground(Void... params) {
      return DataLayer.getMarkerList();
    }
    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
      for (HashMap<String, String> map : list) {
        markerMap.put(map.get(DataLayer.SHORT_NAME), map);
      }
    }
  }

  private class SearchTask
      extends AsyncTask<Void, Void, HashMap<String, String>> {
    @Override
    protected HashMap<String, String> doInBackground(Void... params) {
      return DataLayer.getSearchMap();
    }
    @Override
    protected void onPostExecute(HashMap<String, String> map) {
      searchMap = map;
    }
  }
}
