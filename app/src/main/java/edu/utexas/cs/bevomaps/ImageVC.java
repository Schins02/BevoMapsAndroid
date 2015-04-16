package edu.utexas.cs.bevomaps;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

/**
 * ImageVC.java
 *
 * Created by Eric on 4/3/15.
 */

class ImageVC {

  // Fields---------------------------------------------------------

  private final SubsamplingScaleImageView imageView;

  // Constructors---------------------------------------------------

  ImageVC(View view) {
    imageView = (SubsamplingScaleImageView)view;
    imageView.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_CENTER);
  }

  // Methods--------------------------------------------------------

  void setImage(Uri image, Uri preview) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(image.getPath(), options);

    imageView.setImage(ImageSource.uri(image).dimensions(options.outWidth, options.outHeight),
        ImageSource.uri(preview));
  }
}
