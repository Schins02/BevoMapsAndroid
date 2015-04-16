package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * ActionBarVC.java
 *
 * Created by Eric on 4/4/15.
 */

class ActionBarVC {

  // Fields---------------------------------------------------------

  private final int animTranslationX;
  private final EditText searchText;
  private final ImageButton backButton, searchButton;
  private final TextView titleView;
  private final View searchBar;

  private static final long BAR_DURATION = 100; //100ms

  // Constructors---------------------------------------------------

  ActionBarVC(Activity activity) {
    animTranslationX = activity.getResources().getDisplayMetrics().widthPixels / 2;
    searchText = (EditText)activity.findViewById(R.id.ab_text);
    backButton = (ImageButton)activity.findViewById(R.id.ab_back);
    searchButton = (ImageButton)activity.findViewById(R.id.ab_search);
    titleView = (TextView)activity.findViewById(R.id.ab_title);
    searchBar = activity.findViewById(R.id.ab_search_bar);
    searchBar.setTranslationX(animTranslationX);
  }

  // Methods--------------------------------------------------------

  void collapseBar() {
    backButton.setEnabled(true);
    searchButton.setEnabled(true);
    searchBar.animate()
       .alpha(0)
       .scaleX(0)
       .setDuration(BAR_DURATION)
       .translationX(animTranslationX)
       .withEndAction(new Runnable() {
         @Override
         public void run() {
           searchBar.setVisibility(View.INVISIBLE);
         }
       });
  }

  void expandBar() {
    backButton.setEnabled(false);
    searchButton.setEnabled(false);
    searchBar.animate()
       .alpha(1)
       .scaleX(1)
       .setDuration(BAR_DURATION)
       .translationX(0)
       .withStartAction(new Runnable() {
         @Override
         public void run() {
           searchBar.setVisibility(View.VISIBLE);
         }
       });
  }

  EditText getEditText() {
    return searchText;
  }

  boolean isSearchVisible() {
    return searchBar.getVisibility() == View.VISIBLE;
  }

  void setOnBackClickListener(View.OnClickListener listener) {
    backButton.setOnClickListener(listener);
  }

  void setOnEditorActionListener(TextView.OnEditorActionListener listener) {
    searchText.setOnEditorActionListener(listener);
  }

  void setOnSearchClickListener(View.OnClickListener listener) {
    searchButton.setOnClickListener(listener);
  }

  void setTitle(String title) {
    this.titleView.setText(title);
  }
}
