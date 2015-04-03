package edu.utexas.cs.bevomaps;

import android.view.View;

/**
 * BGHelper.java
 *
 * Created by Eric on 4/2/15.
 */

class BGHelper {

  // Fields---------------------------------------------------------

  private final View view;

  private static final long FADE_DURATION = 250;

  // Constructors---------------------------------------------------

  BGHelper(View view) {
    this.view = view;
  }

  // Methods--------------------------------------------------------

  void fadeIn() {
    view.animate()
        .alpha(0.3f)
        .setDuration(FADE_DURATION);
  }

  void fadeOut() {
    view.animate()
        .alpha(0)
        .setDuration(FADE_DURATION);
  }

  void setOnTouchListener(View.OnTouchListener listener) {
    view.setOnTouchListener(listener);
  }
}
