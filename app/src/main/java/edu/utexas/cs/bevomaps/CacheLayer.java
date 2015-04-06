package edu.utexas.cs.bevomaps;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ProgressBar;
import com.google.android.gms.maps.GoogleMap;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * CacheLayer.java
 *
 * Created by Eric on 3/28/15.
 */

class CacheLayer implements Parcelable {

  // Fields---------------------------------------------------------

  static final long CACHE_SIZE = 10485760; //10MB

  private final File cacheDir;
  private final HashMap<String, HashMap<String, String>> markerMap;
  private HashMap<String, HashMap<String, String>> buildingMap;
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
  private static final String TAG = CacheLayer.class.getSimpleName();

  // Constructors---------------------------------------------------

  CacheLayer(Context context) {
    cacheDir = new File(context.getCacheDir(), "ImageCache");
    if (cacheDir.mkdir()) {
      Log.d(TAG, "Creating image cache.");
    }

    markerMap = new HashMap<>();
    new BuildingTask().execute();
    new SearchTask().execute();
  }

  // Methods--------------------------------------------------------

  void loadImage(ImageHelper imageHelper, ProgressBar progressBar,
                 String building, String floor) {
    Map <String, String> infoMap = buildingMap.get(building);
    if (floor == null) {
      floor = infoMap.get(DataLayer.DEFAULT_FLOOR);
    }

    String imageUrl = infoMap.get(floor),
        previewUrl = infoMap.get(floor + DataLayer.PREVIEW_POSTFIX);
    File imageCache = new File(cacheDir, getImageName(imageUrl)),
        previewCache = new File(cacheDir, getImageName(previewUrl));

    if (imageCache.isFile() && previewCache.isFile()) {
      imageHelper.setImage(Uri.fromFile(imageCache), Uri.fromFile(previewCache));
    }
    else {
      new ImageTask(imageHelper, progressBar, infoMap, floor, cacheDir).execute();
    }
  }

  void loadMarkers(GoogleMap map) {
    new MarkerTask(markerMap, map).execute();
  }

  void clearCache() {
    StringBuilder builder = new StringBuilder("Deleted:  ");
    for (File file : cacheDir.listFiles()) {
      String name = file.getName();
      if (file.delete()) {
        builder.append(name).append(", ");
      }
      else {
        Log.d(TAG, name + " cannot be deleted.");
      }
    }

    Log.d(TAG, builder.substring(0, builder.length() - 2));
  }

  String getBuildingName(String building) {
    if (markerMap == null) {
      Log.d(TAG, "Marker list not loaded.");
      return building;
    }

    return markerMap.get(building).get(DataLayer.LONG_NAME);
  }

  Map<String, String> getSearchMap() {
    if (searchMap == null) {
      Log.d(TAG, "Search map not loaded.");
    }

    return searchMap;
  }

  boolean isBuilding(String building) {
    if (buildingMap == null) {
      Log.d(TAG, "Building map not loaded.");
      return false;
    }

    return buildingMap.containsKey(building);
  }

  static String getImageName(String url) {
    return url.substring(url.lastIndexOf('/'));
  }

  @SuppressWarnings("unchecked")
  private CacheLayer (Parcel in) {
    Bundle bundle = in.readBundle();
    cacheDir = (File)bundle.getSerializable("cacheDir");
    markerMap = (HashMap<String, HashMap<String, String>>)bundle.getSerializable("markerMap");
    buildingMap = (HashMap<String, HashMap<String, String>>)bundle.getSerializable("buildingMap");
    searchMap = (HashMap<String, String>)bundle.getSerializable("searchMap");
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    Bundle bundle = new Bundle();
    bundle.putSerializable("cacheDir", cacheDir);
    bundle.putSerializable("markerMap", markerMap);
    bundle.putSerializable("buildingMap", buildingMap);
    bundle.putSerializable("searchMap", searchMap);
    out.writeBundle(bundle);
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
