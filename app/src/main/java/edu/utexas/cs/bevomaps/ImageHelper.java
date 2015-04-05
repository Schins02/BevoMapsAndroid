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
    this.view = view;
  }

  // Methods--------------------------------------------------------

  void setImage(Uri imageUri, Uri previewUri) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(imageUri.getPath(), options);

    ImageSource image = ImageSource.uri(imageUri).dimensions(options.outWidth, options.outHeight),
        preview = ImageSource.uri(previewUri);

    view.setImage(image, preview);
  }
}
