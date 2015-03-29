package edu.utexas.cs.bevomaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.File;

/**
 * Created by Eric on 3/28/15.
 */

class LoadImageTask extends AsyncTask<File, Void, Bitmap> {

  // Fields---------------------------------------------------------

  private static final String TAG = "LoadImageTask";
  private final ImageView imageView;

  // Constructors---------------------------------------------------

  LoadImageTask(ImageView imageView) {
    this.imageView = imageView;
  }

  // Methods--------------------------------------------------------

  @Override
  protected Bitmap doInBackground(File... params) {
    return BitmapFactory.decodeFile(params[0].getPath(),
        CacheLayer.getImageOptions());
  }

  @Override
  protected void onPostExecute(Bitmap image) {
    if (image != null) {
      imageView.setImageBitmap(image);
    }
  }
}
