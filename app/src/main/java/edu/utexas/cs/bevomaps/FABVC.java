package edu.utexas.cs.bevomaps;

import android.view.View;
import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * FABVC.java
 *
 * Created by Eric on 4/2/15.
 */

class FABVC {

  // Fields---------------------------------------------------------

  private static final float SLIDE_DISTANCE = 50; //50dp
  private static final long SLIDE_DURATION = 100; //100ms

  private final FloatingActionButton button;

  // Constructors---------------------------------------------------

  FABVC(FloatingActionButton button) {
    this.button = button;
  }

  // Methods--------------------------------------------------------

  void animateSlideIn() {
    button.animate()
          .alpha(1)
          .translationYBy(-SLIDE_DISTANCE)
          .setDuration(SLIDE_DURATION)
          .withStartAction(new Runnable() {
      @Override
      public void run() {
        button.setVisibility(View.VISIBLE);
      }
    });
  }

  void animateSlideOut() {
    button.animate()
          .alpha(0)
          .translationYBy(SLIDE_DISTANCE)
          .setDuration(SLIDE_DURATION)
          .withEndAction(new Runnable() {
      @Override
      public void run() {
        button.setVisibility(View.GONE);
      }
    });
  }

  void animateMoveUp() {

  }

  void animateMveDown() {

  }

  void setOnClickListener(View.OnClickListener listener) {
    button.setOnClickListener(listener);
  }
}
