package edu.utexas.cs.bevomaps;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;
import java.io.File;
import java.util.HashMap;

/**
 * Created by Eric on 3/28/15.
 */

class CacheLayer implements Parcelable {

  // Fields---------------------------------------------------------

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

  static final long CACHE_SIZE = 1048576; // 1MB

  private static final BitmapFactory.Options OPTIONS = new BitmapFactory.Options();
  private static final String TAG = "CacheLayer",
                              CACHE_DIR = "ImageCache",
                              DEFAULT_FLOOR = "defaultFloor";

  private HashMap<String, HashMap<String, String>> buildingMap;

  // Constructors---------------------------------------------------

  CacheLayer() {
    new DownloadBuildingMapTask().execute();
  }

  // Methods--------------------------------------------------------

  boolean checkBulding (String building) {
    if (buildingMap == null) {
      Log.d(TAG, "Building map not loaded.");
      return false;
    }

    return buildingMap.containsKey(building);
  }

  void loadImage(Context context, ImageView imageView, String building, String floor) {
    if (!checkBulding(building)) {
      return;
    }

    String imageUrl = buildingMap.get(building).get(floor);
    if (imageUrl == null) {
      imageUrl = buildingMap.get(building).get(DEFAULT_FLOOR);
    }

    File cacheDir = new File (context.getCacheDir(), CACHE_DIR);
    if (cacheDir.mkdir()) {
      Log.d(TAG, "Creating image cache.");
    }

    File cacheFile = new File(cacheDir, getImageName(imageUrl));
    if (cacheFile.isFile()) {
      Log.d(TAG, "Loading from cache.");
      new LoadImageTask(imageView).execute(cacheFile);
    }
    else {
      Log.d(TAG, "Loading from network.");
      new DownloadImageTask(cacheFile, imageView).execute(imageUrl);
    }
  }

  static BitmapFactory.Options getImageOptions() {
    OPTIONS.inSampleSize = 4;
    return OPTIONS;
  }

  private static String getImageName(String url) {
    return url.substring(url.lastIndexOf('/'));
  }

  @SuppressWarnings("unchecked")
  private CacheLayer (Parcel in) {
    Bundle bundle = in.readBundle();
    buildingMap = (HashMap<String, HashMap<String, String>>)bundle.getSerializable("buildingMap");
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    Bundle bundle = new Bundle();
    bundle.putSerializable("buildingMap", buildingMap);
    out.writeBundle(bundle);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  private class DownloadBuildingMapTask extends AsyncTask<Void, Void, HashMap<String, HashMap<String, String>>> {
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
