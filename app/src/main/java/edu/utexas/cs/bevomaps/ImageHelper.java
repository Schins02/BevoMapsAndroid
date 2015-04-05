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

  void reset() {

  }

  void setImage(Uri imageUri, Uri previewUri) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(imageUri.getPath(), options);

    int width = options.outWidth, height = options.outHeight;

    ImageSource image = ImageSource.uri(imageUri).dimensions(width, height),
        preview = ImageSource.uri(previewUri);

    view.setImage(image, preview);
  }
}
