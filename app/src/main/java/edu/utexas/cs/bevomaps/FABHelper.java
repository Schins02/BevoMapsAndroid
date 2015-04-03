package edu.utexas.cs.bevomaps;

import android.view.View;
import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * FABHelper.java
 *
 * Created by Eric on 4/2/15.
 */

class FABHelper {

  // Fields---------------------------------------------------------

  private final FloatingActionButton button;

  private static final float FADE_DISTANCE = 50;
  private static final long FADE_DURATION = 100;

  // Constructors---------------------------------------------------

  FABHelper(FloatingActionButton button) {
    this.button = button;
  }

  // Methods--------------------------------------------------------

  void fadeIn() {
    button.animate()
          .alpha(1)
          .translationYBy(-FADE_DISTANCE)
          .setDuration(FADE_DURATION);

  }

  void fadeOut() {
    button.animate()
          .alpha(0)
          .translationYBy(FADE_DISTANCE)
          .setDuration(FADE_DURATION);
  }

  void moveUp() {

  }

  void moveDown() {

  }

  void setOnClickListener(View.OnClickListener listener) {
    button.setOnClickListener(listener);
  }
}
