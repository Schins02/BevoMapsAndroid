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
  }

  // Methods--------------------------------------------------------

  Map<String, String> getSearchMap() {
    return new HashMap<>();
  }

  boolean isBuilding(String building) {
    if (buildingMap == null) {
      Log.d(TAG, "Building map not loaded.");
      return false;
    }

    return buildingMap.containsKey(building);
  }

  void loadImage(ImageHelper imageHelper, String building, String floor) {
    String imageUrl = buildingMap.get(building).get(floor);
    if (imageUrl == null) {
      imageUrl = buildingMap.get(building).get(DataLayer.DEFAULT_FLOOR);
    }

    File cacheFile = new File(cacheDir, getImageName(imageUrl));
    if (cacheFile.isFile()) {
      imageHelper.setImage(Uri.fromFile(cacheFile));
    }
    else {
      new ImageTask(cacheDir, imageHelper, buildingMap.get(building), imageUrl).execute();
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
    buildingMap = (HashMap<String, HashMap<String, String>>)bundle.getSerializable("buildingMap");
    cacheDir = (File)bundle.getSerializable("cacheDir");
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    Bundle bundle = new Bundle();
    bundle.putSerializable("buildingMap", buildingMap);
    bundle.putSerializable("cacheDir", cacheDir);
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
}
