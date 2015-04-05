package edu.utexas.cs.bevomaps;

import android.graphics.BitmapFactory;
import android.net.Uri;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

/**
 * ImageHelper.java
 *
 * Created by Eric on 4/3/15.
 */

class ImageHelper {

  // Fields---------------------------------------------------------

  private ImageViewState state;
  private final SubsamplingScaleImageView view;

  // Constructors---------------------------------------------------

  ImageHelper(SubsamplingScaleImageView view) {
    this.view = view;
    view.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_CENTER);
  }

  // Methods--------------------------------------------------------

  void reset() {
    state = null;
  }

  void setImage(Uri image, Uri preview) {
    int width, height;
    state = view.getState();

    if (state == null) {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(image.getPath(), options);

      width = options.outWidth;
      height = options.outHeight;
    }
    else {
      width = view.getSWidth();
      height = view.getSHeight();
    }

    view.setImage(ImageSource.uri(image).dimensions(width, height), ImageSource.uri(preview));
  }
}
