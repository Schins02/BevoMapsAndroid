package edu.utexas.cs.bevomaps;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * ABHelper.java
 *
 * Created by Eric on 4/4/15.
 */

class ABHelper {

  // Fields---------------------------------------------------------

  private final int distance;
  private final EditText text;
  private final ImageButton back, search;
  private final TextView title;
  private final View bar;

  private static final long BAR_DURATION = 200; //200ms

  // Constructors---------------------------------------------------

  ABHelper(BuildingActivity activity) {
    distance = activity.getResources().getDisplayMetrics().widthPixels / 2;
    text = (EditText)activity.findViewById(R.id.ab_text);
    back = (ImageButton)activity.findViewById(R.id.ab_back);
    search = (ImageButton)activity.findViewById(R.id.ab_search);
    title = (TextView)activity.findViewById(R.id.ab_title);
    bar = activity.findViewById(R.id.ab_search_bar);

    bar.setTranslationX(distance);
  }

  // Methods--------------------------------------------------------

  void collapse() {
    back.setEnabled(true);
    search.setEnabled(true);
    bar.animate()
       .alpha(0)
       .scaleX(0)
       .setDuration(BAR_DURATION)
       .translationX(distance)
       .withEndAction(new Runnable() {
         @Override
         public void run() {
           bar.setVisibility(View.INVISIBLE);
         }
       });
  }

  void expand() {
    back.setEnabled(false);
    search.setEnabled(false);
    bar.animate()
       .alpha(1)
       .scaleX(1)
       .setDuration(BAR_DURATION)
       .translationX(0)
       .withStartAction(new Runnable() {
         @Override
         public void run() {
           bar.setVisibility(View.VISIBLE);
         }
       });
  }

  EditText getEditText() {
    return text;
  }

  boolean isSearchVisible() {
    return bar.getVisibility() == View.VISIBLE;
  }

  void setBackOnClickListener(View.OnClickListener listener) {
    back.setOnClickListener(listener);
  }

  void setSearchOnClickListener(View.OnClickListener listener) {
    search.setOnClickListener(listener);
  }

  void setOnEditorActionListener(TextView.OnEditorActionListener listener) {
    text.setOnEditorActionListener(listener);
  }

  void setTitle(String title) {
    this.title.setText(title);
  }
}
