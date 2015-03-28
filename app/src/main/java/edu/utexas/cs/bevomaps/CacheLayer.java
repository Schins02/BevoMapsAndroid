package edu.utexas.cs.bevomaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;

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

  private static final String TAG = "*** CacheLayer ***";

  private File cacheFile;
  private HashMap<String, HashMap<String, String>> buildingMap;
  private LinkedHashMap<String, File> cacheMap;

  // Methods--------------------------------------------------------

  CacheLayer (Context context) {
    cacheFile = new File(context.getDir("data", Context.MODE_PRIVATE), "cache");
    readCacheFile();
    new DownloadBuildingMapTask().execute();
  }

  private CacheLayer (Parcel in) {
    Bundle bundle = in.readBundle();
    cacheFile = (File)bundle.getSerializable("cacheFile");
    buildingMap = (HashMap<String, HashMap<String, String>>)bundle.getSerializable("buildingMap");
    cacheMap = (LinkedHashMap<String, File>)bundle.getSerializable("cacheMap");
  }

  void loadImage(ImageView view, String building, String floor) {
    new DownloadImageTask(view).execute(buildingMap.get(building).get(floor));
  }

  private void readCacheFile() {
    if (!cacheFile.isFile()) {
      return;
    }
    try {
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile));
      cacheMap = (LinkedHashMap<String, File>)ois.readObject();
      ois.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void writeCacheFile() {
    try {
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
      oos.writeObject(cacheMap);
      oos.flush();
      oos.close();
    }
    catch (Exception e) {
      Log.e(TAG, "Write Exception => " + e);
    }
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    Bundle bundle = new Bundle(2);
    bundle.putSerializable("cacheFile", cacheFile);
    bundle.putSerializable("buildingMap", buildingMap);
    bundle.putSerializable("cacheMap", cacheMap);
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
    protected void onPostExecute(HashMap<String, HashMap<String, String>>  result) {
      buildingMap = result;
      Log.d(TAG, buildingMap.toString());
    }
  }

  private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView view;

    private DownloadImageTask(ImageView view) {
      this.view = view;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
      HttpURLConnection url = null;

      try {
        url = (HttpURLConnection)new URL(params[0]).openConnection();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        return BitmapFactory.decodeStream(new BufferedInputStream(url.getInputStream()), null, options);
      }
      catch (Exception e) {
        Log.e(TAG, "URL Exception => " + e);
      }
      finally {
        if (url != null) {
          url.disconnect();
        }
      }

      return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
      if (result != null) {
        view.setImageBitmap(result);
      }
    }
  }
}
