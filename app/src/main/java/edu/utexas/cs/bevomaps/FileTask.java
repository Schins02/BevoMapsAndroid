package edu.utexas.cs.bevomaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.File;

/**
 * LoadHelper.java
 *
 * Created by Eric on 3/28/15.
 */

class FileTask extends AsyncTask<Void, Void, Bitmap> {

  // Fields---------------------------------------------------------

  private final File cacheFile;
  private final ImageView imageView;

  // Constructors---------------------------------------------------

  FileTask(File cacheFile, ImageView imageView) {
    this.cacheFile = cacheFile;
    this.imageView = imageView;
  }

  // Methods--------------------------------------------------------

  @Override
  protected Bitmap doInBackground(Void... params) {
    return BitmapFactory.decodeFile(cacheFile.getPath(),
        CacheLayer.getImageOptions());
  }

  @Override
  protected void onPostExecute(Bitmap image) {
    if (image != null) {
      imageView.setImageBitmap(image);
    }
  }
}
