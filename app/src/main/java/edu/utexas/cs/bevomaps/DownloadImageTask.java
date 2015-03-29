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

/**
 * Created by Eric on 3/29/15.
 */

class DownloadImageTask extends AsyncTask <String, Void, Bitmap> {

  // Fields---------------------------------------------------------

  private static final String TAG = "DownloadImageTask";
  private final File cacheFile;
  private final ImageView imageView;

  // Constructors---------------------------------------------------

  DownloadImageTask(File cacheFile, ImageView imageView) {
    this.cacheFile = cacheFile;
    this.imageView = imageView;
  }

  @Override
  protected void onPreExecute() {
    File cacheDir = cacheFile.getParentFile();
    if (cacheDir == null) {
      return;
    }

    long cacheLength = getDirSpace(cacheDir);
    while (cacheLength > CacheLayer.CACHE_SIZE) {
      cacheLength -= freeDirSpace(cacheDir);
    }
  }

  @Override
  protected Bitmap doInBackground(String... params) {
    HttpURLConnection connection = null;

    try {
      FileOutputStream out = new FileOutputStream(cacheFile);
      connection = (HttpURLConnection)new URL(params[0]).openConnection();
      InputStream in = connection.getInputStream();

      copyStream(in, out);
      in.close();
      out.close();

      return BitmapFactory.decodeFile(cacheFile.getPath(),
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
    if (image != null) {
      imageView.setImageBitmap(image);
    }
  }

  private static void copyStream (InputStream in, OutputStream out)
      throws IOException {
    byte[] buffer = new byte[102400];
    int length;
    while ((length = in.read(buffer)) > 0) {
      out.write(buffer, 0, length);
    }
  }

  private static long getDirSpace(File dir) {
    long length = 0;
    if (dir != null && dir.isDirectory()) {
      for (File file : dir.listFiles()) {
        length += file.length();
      }
    }

    return length;
  }

  private static long freeDirSpace(File dir) {
    long length = 0;
    if (dir != null && dir.isDirectory()) {
      File[] files = dir.listFiles();
      int oldest = 0;
      for (int i = 1; i < files.length; i++) {
        if (files[i].lastModified() < files[oldest].lastModified()) {
          oldest = i;
        }
      }

      length = files[oldest].length();
      if (!files[oldest].delete()) {
        length = 0;
      }
    }

    return length;
  }
}
