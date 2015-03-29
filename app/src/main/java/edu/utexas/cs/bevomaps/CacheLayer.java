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
  private static final BitmapFactory.Options OPTIONS = new BitmapFactory.Options();
  private static final String TAG = "CacheLayer";
  private HashMap<String, HashMap<String, String>> buildingMap;

  // Constructors---------------------------------------------------

  CacheLayer() {
    new DownloadBuildingsTask().execute();
  }

  // Methods--------------------------------------------------------

  void loadImage(Context context, ImageView imageView, String building, String floor) {
    if (buildingMap == null) {
      Log.d(TAG, "Building map not loaded.");
      return;
    }

    if (buildingMap.get(building) == null) {
      Log.d(TAG, "Bad building.");
      return;
    }

    String imageUrl = buildingMap.get(building).get(floor);
    if (imageUrl == null) {
      Log.d(TAG, "Bad floor.");
      return;
    }

    File cacheFile = new File(context.getCacheDir(), getImageName(imageUrl));
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

  private class DownloadBuildingsTask extends AsyncTask<Void, Void, HashMap<String, HashMap<String, String>>> {
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
