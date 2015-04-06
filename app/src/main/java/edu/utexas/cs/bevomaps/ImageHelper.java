package edu.utexas.cs.bevomaps;

import android.graphics.BitmapFactory;
import android.net.Uri;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

/**
 * ImageHelper.java
 *
 * Created by Eric on 4/3/15.
 */

class ImageHelper {

  // Fields---------------------------------------------------------

  private final SubsamplingScaleImageView view;

  // Constructors---------------------------------------------------

  ImageHelper(SubsamplingScaleImageView view) {
    view.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_CENTER);
    this.view = view;
  }

  // Methods--------------------------------------------------------

  void setImage(Uri image, Uri preview) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(image.getPath(), options);

    view.setImage(ImageSource.uri(image).dimensions(options.outWidth, options.outHeight),
        ImageSource.uri(preview));
  }
}
