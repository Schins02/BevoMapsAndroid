package edu.utexas.cs.bevomaps;

import android.view.View;

/**
 * BackgroundVC.java
 *
 * Created by Eric on 4/2/15.
 */

class BackgroundVC {

  // Fields---------------------------------------------------------

  private static final long FADE_DURATION = 200; //200ms

  private final View background;

  // Constructors---------------------------------------------------

  BackgroundVC(View view) {
    background = view;
  }

  // Methods--------------------------------------------------------

  void animateFadeIn() {
    background.animate()
        .alpha(1)
        .setDuration(FADE_DURATION);
  }

  void animateFadeOut() {
    background.animate()
        .alpha(0)
        .setDuration(FADE_DURATION);
  }

  void setOnTouchListener(View.OnTouchListener listener) {
    background.setOnTouchListener(listener);
  }
}
