package edu.utexas.cs.bevomaps;

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
    this.view = view;
  }

  // Methods--------------------------------------------------------

  void setImage(Uri uri) {
    view.setImage(ImageSource.uri(uri));
  }
}
