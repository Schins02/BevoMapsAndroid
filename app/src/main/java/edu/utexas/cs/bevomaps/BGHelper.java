package edu.utexas.cs.bevomaps;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * BGHelper.java
 *
 * Created by Eric on 4/2/15.
 */

class BGHelper implements Animation.AnimationListener {

  // Fields---------------------------------------------------------

  private final View view;

  private static final AlphaAnimation FADE_IN = new AlphaAnimation(0, 1),
                                      FADE_OUT = new AlphaAnimation(1, 0);

  // Constructors---------------------------------------------------

  BGHelper(View view) {
    FADE_IN.setAnimationListener(this);
    FADE_IN.setDuration(300);
    FADE_OUT.setFillAfter(true);
    FADE_OUT.setDuration(300);

    this.view = view;
  }

  // Methods--------------------------------------------------------

  @Override
  public void onAnimationStart(Animation animation) {
    view.setAlpha(0.3f);
  }

  @Override
  public void onAnimationEnd(Animation animation) {}

  @Override
  public void onAnimationRepeat(Animation animation) {}

  void startAnimation(boolean in) {
    view.startAnimation(in ? FADE_IN : FADE_OUT);
  }

  void setOnTouchListener(View.OnTouchListener listener) {
    view.setOnTouchListener(listener);
  }
}
