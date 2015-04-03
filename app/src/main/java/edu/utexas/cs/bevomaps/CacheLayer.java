package edu.utexas.cs.bevomaps;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;
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

  private static final BitmapFactory.Options OPTIONS = new BitmapFactory.Options();
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

  void loadImage(ImageView imageView, String building, String floor) {
    String imageUrl = buildingMap.get(building).get(floor);
    if (imageUrl == null) {
      imageUrl = buildingMap.get(building).get(DataLayer.DEFAULT_FLOOR);
    }

    File cacheFile = new File(cacheDir, getImageName(imageUrl));
    if (cacheFile.isFile()) {
      new LoadHelper(cacheFile, imageView).execute();
    }
    else {
      new DownloadHelper(cacheDir, imageView, buildingMap.get(building), imageUrl).execute();
    }
  }

  static String getImageName(String url) {
    return url.substring(url.lastIndexOf('/'));
  }

  void loadMarkers(GoogleMap map) {

  }

  static BitmapFactory.Options getImageOptions() {
    OPTIONS.inSampleSize = 4;
    return OPTIONS;
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
