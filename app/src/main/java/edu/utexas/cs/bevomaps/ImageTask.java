package edu.utexas.cs.bevomaps;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * DownloadHelper.java
 *
 * Created by Eric on 3/29/15.
 */

class ImageTask extends AsyncTask <Void, Integer, Uri[]> {

  // Fields---------------------------------------------------------

  private static final int BUFFER_SIZE = 102400; //100KB

  private final File cacheDir;
  private final ImageHelper imageHelper;
  private final Map<String, String> infoMap;
  private final String floor;
  private final ProgressBar progressBar;

  private static final long FADE_DURATION = 500; //500ms
  private static final String TAG = ImageTask.class.getSimpleName();

  // Constructors---------------------------------------------------

  ImageTask(ImageHelper imageHelper, ProgressBar progressBar,
            Map<String, String> infoMap, String floor, File cacheDir) {
    this.cacheDir = cacheDir;
    this.imageHelper = imageHelper;
    this.infoMap = infoMap;
    this.floor = floor;
    this.progressBar = progressBar;
  }

  // Methods--------------------------------------------------------

  @Override
  protected void onPreExecute() {
    progressBar.setAlpha(1);
    progressBar.setProgress(10);
    freeCache();
  }

  @Override
  protected Uri[] doInBackground(Void... params) {
    String imageUrl = infoMap.get(floor),
        previewUrl = infoMap.get(floor + DataLayer.PREVIEW_POSTFIX);
    HttpURLConnection connection = null;

    try {
      File imageCache = new File(cacheDir, CacheLayer.getImageName(imageUrl));
      FileOutputStream out = new FileOutputStream(imageCache);
      connection = (HttpURLConnection)new URL(imageUrl).openConnection();
      InputStream in = connection.getInputStream();

      copyStream(in, out);
      publishProgress(50);

      File previewCache = new File(cacheDir, CacheLayer.getImageName(previewUrl));
      out = new FileOutputStream(previewCache);
      connection = (HttpURLConnection)new URL(previewUrl).openConnection();
      in = connection.getInputStream();

      copyStream(in, out);
      publishProgress(80);

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
  protected void onProgressUpdate(Integer... values) {
    progressBar.setProgress(values[0]);
  }

  @Override
  protected void onPostExecute(Uri[] uri) {
    progressBar.setProgress(100);
    progressBar.animate().alpha(0).setDuration(FADE_DURATION);

    if (uri != null) {
      imageHelper.setImage(uri[0], uri[1]);

      for (String key : infoMap.keySet()) {
        String url = infoMap.get(key);

        if (!key.equals(DataLayer.DEFAULT_FLOOR) &&
            !key.equals(DataLayer.NUM_FLOORS) &&
            !key.equals(floor) &&
            !key.equals(floor + DataLayer.PREVIEW_POSTFIX)) {
          new CacheTask().execute(url);
        }
      }
    }
  }

  private void freeCache() {
    File[] files = cacheDir.listFiles();
    long length = 0;
    for (File file : files) {
      length += file.length();
    }

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
      }
    }
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

  private class CacheTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
      HttpURLConnection connection = null;

      try {
        FileOutputStream out = new FileOutputStream(new File(cacheDir,
            CacheLayer.getImageName(params[0])));
        connection = (HttpURLConnection)new URL(params[0]).openConnection();
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
  }
}
