package edu.utexas.cs.bevomaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * DownloadImageTask.java
 *
 * Created by Eric on 3/29/15.
 */

class DownloadImageTask extends AsyncTask <Void, Void, Bitmap> {

  // Fields---------------------------------------------------------

  private static final String TAG = "DownloadImageTask";

  private final File cacheDir;
  private final ImageView imageView;
  private final Map<String, String> infoMap;
  private final String imageUrl;

  // Constructors---------------------------------------------------

  DownloadImageTask(File cacheDir, ImageView imageView,
                    Map<String, String> infoMap, String imageUrl) {
    this.cacheDir = cacheDir;
    this.imageView = imageView;
    this.infoMap = infoMap;
    this.imageUrl = imageUrl;
  }

  // Methods--------------------------------------------------------

  @Override
  protected void onPreExecute() {
    freeCache();
  }

  @Override
  protected Bitmap doInBackground(Void... params) {
    HttpURLConnection connection = null;

    try {
      File file = new File(cacheDir, CacheLayer.getImageName(imageUrl));
      FileOutputStream out = new FileOutputStream(file);
      connection = (HttpURLConnection)new URL(imageUrl).openConnection();
      InputStream in = connection.getInputStream();

      copyStream(in, out);
      return BitmapFactory.decodeFile(file.getPath(),
          CacheLayer.getImageOptions());
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
  protected void onPostExecute(Bitmap image) {
    if (imageView != null && image != null) {
      imageView.setImageBitmap(image);

      for (String key : infoMap.keySet()) {
        String url = infoMap.get(key);

        if (!key.equals(CacheLayer.DEFAULT_FLOOR) &&
            !key.equals(CacheLayer.NUM_FLOORS) &&
            !url.equals(imageUrl)) {
          new CacheImageTask().execute(url);
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
    byte[] buffer = new byte[102400];
    int length;
    while ((length = in.read(buffer)) > 0) {
      out.write(buffer, 0, length);
    }

    in.close();
    out.close();
  }

  private class CacheImageTask extends AsyncTask<String, Void, Void> {

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
