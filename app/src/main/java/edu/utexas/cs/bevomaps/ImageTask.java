package edu.utexas.cs.bevomaps;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * DownloadHelper.java
 *
 * Created by Eric on 3/29/15.
 */

class ImageTask extends AsyncTask <Void, Double, Uri[]> {

  // Fields---------------------------------------------------------

  private static final int BUFFER_SIZE = 102400; //100KB
  private static final String TAG = ImageTask.class.getSimpleName();

  private final File cacheDir;
  private final ImageVC imageVC;
  private final OnProgressUpdateListener updateListener;

  private final Map<String, String> buildingInfo;
  private final String selectedFloor;

  private double curProgress, maxProgress;
  private List<CacheTask> cacheTasks;

  // Constructors---------------------------------------------------

  ImageTask(ImageVC image, Map<String, String> info, String floor,
            File dir, OnProgressUpdateListener listener) {
    imageVC = image;
    buildingInfo = info;
    selectedFloor = floor;
    cacheDir = dir;
    updateListener = listener;
    cacheTasks = new LinkedList<>();
  }

  // Methods--------------------------------------------------------

  @Override
  protected void onPreExecute() {
    if (updateListener != null) {
      updateListener.onProgressBegin();
    }
    maxProgress += 200;
    publishProgress(50.0);

    freeCache();

    for (String key : buildingInfo.keySet()) {
      if (!key.equals(DataLayer.DEFAULT_FLOOR) &&
          !key.equals(DataLayer.FLOOR_NAMES) &&
          !key.equals(selectedFloor) &&
          !key.equals(selectedFloor + DataLayer.PREVIEW_POSTFIX)) {
        maxProgress += 100;

        CacheTask task = new CacheTask();
        task.listener = updateListener;
        task.url = buildingInfo.get(key);
        cacheTasks.add(task);
      }
    }
  }

  @Override
  protected Uri[] doInBackground(Void... params) {
    String imageUrl = buildingInfo.get(selectedFloor),
        previewUrl = buildingInfo.get(selectedFloor + DataLayer.PREVIEW_POSTFIX);
    HttpURLConnection connection = null;

    try {
      File imageCache = new File(cacheDir, CacheLayer.getImageName(imageUrl));
      FileOutputStream out = new FileOutputStream(imageCache);
      connection = (HttpURLConnection)new URL(imageUrl).openConnection();
      InputStream in = connection.getInputStream();

      copyStream(in, out);
      publishProgress(80.0);

      File previewCache = new File(cacheDir, CacheLayer.getImageName(previewUrl));
      out = new FileOutputStream(previewCache);
      connection = (HttpURLConnection)new URL(previewUrl).openConnection();
      in = connection.getInputStream();

      copyStream(in, out);
      publishProgress(50.0);

      return new Uri[]{Uri.fromFile(imageCache), Uri.fromFile(previewCache)};
    }
    catch (Exception e) {
      Log.e(TAG, e.toString());
    }
    finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    return null;
  }

  @Override
  protected void onProgressUpdate(Double... values) {
    curProgress += values[0];
    if (updateListener != null) {
      updateListener.onProgressUpdate(curProgress / maxProgress);
    }
  }

  @Override
  protected void onPostExecute(Uri[] uri) {
    publishProgress(20.0);

    if (uri != null) {
      imageVC.setImage(uri[0], uri[1]);
      for (CacheTask task : cacheTasks) {
        task.execute();
      }
    }
  }

  private void freeCache() {
    File[] files = cacheDir.listFiles();
    long length = 0;
    for (File file : files) {
      length += file.length();
    }

    StringBuilder builder = new StringBuilder("Deleted:  ");
    while (length > CacheLayer.CACHE_SIZE) {
      int oldest = 0;
      for (int i = 1; i < files.length; i++) {
        if (files[i].lastModified() < files[oldest].lastModified()) {
          oldest = i;
        }
      }

      long deleted = files[oldest].length();
      if (files[oldest].delete()) {
        length -= deleted;
        builder.append(files[oldest].getName()).append(", ");
      }
    }

    Log.d(TAG, builder.substring(0, builder.length() - 2));
  }

  private static void copyStream(InputStream in, OutputStream out)
      throws IOException {
    byte[] buffer = new byte[BUFFER_SIZE];
    int length;
    while ((length = in.read(buffer)) > 0) {
      out.write(buffer, 0, length);
    }

    out.flush();
    out.close();
    in.close();
  }

  static interface OnProgressUpdateListener {
    void onProgressBegin();
    void onProgressUpdate(double progress);
  }

  private class CacheTask extends AsyncTask<Void, Void, Void> {
    private OnProgressUpdateListener listener;
    private String url;

    @Override
    protected Void doInBackground(Void... params) {
      HttpURLConnection connection = null;

      try {
        FileOutputStream out = new FileOutputStream(new File(cacheDir,
            CacheLayer.getImageName(url)));
        connection = (HttpURLConnection)new URL(url).openConnection();
        InputStream in = connection.getInputStream();

        copyStream(in, out);
      }
      catch (Exception e) {
        Log.e(TAG, e.toString());
      }
      finally {
        if (connection != null) {
          connection.disconnect();
        }
      }

      return null;
    }

    @Override
    protected void onPostExecute(Void params) {
      curProgress += 100;
      if (listener != null) {
        listener.onProgressUpdate(curProgress / maxProgress);
      }
    }
  }
}
