package edu.utexas.cs.bevomaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
  protected Bitmap doInBackground(String... params) {
    HttpURLConnection connection = null;

    try {
      FileOutputStream out = new FileOutputStream(cacheFile);
      connection = (HttpURLConnection)new URL(params[0]).openConnection();
      InputStream in = connection.getInputStream();

      byte[] buffer = new byte[10240];
      int length;
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }

      in.close();
      out.close();
      return BitmapFactory.decodeFile(cacheFile.getPath(),
          CacheLayer.getImageOptions());
    }
    catch (Exception e) {
      Log.e(TAG, "Download Exception => " + e);
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
}
