package edu.utexas.cs.bevomaps;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
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

  private static final String TAG = "CacheLayer";
  static final long CACHE_SIZE = 10485760; //10MB

  private final File cacheDir;
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

  // Constructors---------------------------------------------------

  CacheLayer(Context context) {
    cacheDir = new File(context.getCacheDir(), "ImageCache");
    if (cacheDir.mkdir()) {
      Log.d(TAG, "Creating image cache.");
    }

    new BuildingHelper().execute();
    new SearchHelper().execute();
  }

  // Methods--------------------------------------------------------

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

  void loadImage(ImageHelper imageHelper, String building, String floor) {
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
      new ImageTask(imageHelper, infoMap, floor, cacheDir).execute();
    }
  }

  static String getImageName(String url) {
    return url.substring(url.lastIndexOf('/'));
  }

  void loadMarkers(GoogleMap map) {
    new MarkerTask(map).execute();
  }

  @SuppressWarnings("unchecked")
  private CacheLayer (Parcel in) {
    Bundle bundle = in.readBundle();
    cacheDir = (File)bundle.getSerializable("cacheDir");
    buildingMap = (HashMap<String, HashMap<String, String>>)bundle.getSerializable("buildingMap");
    searchMap = (HashMap<String, String>)bundle.getSerializable("searchMap");
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    Bundle bundle = new Bundle();
    bundle.putSerializable("cacheDir", cacheDir);
    bundle.putSerializable("buildingMap", buildingMap);
    bundle.putSerializable("searchMap", searchMap);
    out.writeBundle(bundle);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  private class BuildingHelper
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

  private class SearchHelper
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
