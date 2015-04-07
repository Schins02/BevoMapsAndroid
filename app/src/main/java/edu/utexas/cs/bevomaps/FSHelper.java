package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * FSHelper.java
 *
 * Created by Eric on 4/6/15.
 */

class FSHelper {

  // Fields---------------------------------------------------------

  private final int pixelSize;
  private final ArrayAdapter<String> itemAdapter;
  private final LinearLayout listLayout;
  private final ScrollView scrollView;

  private OnItemSelectedListener itemListener;

  private static final float FADE_DISTANCE = 50; //50dp
  private static final long FADE_DURATION = 100; //100ms

  // Constructors---------------------------------------------------

  FSHelper (Activity activity) {
    pixelSize = activity.getResources().getDimensionPixelSize(R.dimen.fs_item_size);
    itemAdapter = new ArrayAdapter<>(activity, R.layout.floor_selector_item);
    listLayout = (LinearLayout)activity.findViewById(R.id.fs_list);
    scrollView = (ScrollView)activity.findViewById(R.id.fs);
  }

  // Methods--------------------------------------------------------

  void addItems(String[] items) {
    itemAdapter.addAll(items);

    for (int i = 0; i < itemAdapter.getCount(); i++) {
      TextView view = (TextView)itemAdapter.getView(i, null, null);
      view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (itemListener != null) {
            itemListener.onItemClicked((TextView)v);
          }
        }
      });
      listLayout.addView(view, pixelSize, pixelSize);
    }
  }

  void fadeIn() {
    scrollView.animate()
        .alpha(1)
        .translationYBy(-FADE_DISTANCE)
        .setDuration(FADE_DURATION);

  }

  void fadeOut() {
    scrollView.animate()
        .alpha(0)
        .translationYBy(FADE_DISTANCE)
        .setDuration(FADE_DURATION);
  }

  void clear() {
    itemAdapter.clear();
    listLayout.removeAllViews();
  }

  void setOnItemClickListener(OnItemSelectedListener listener) {
    this.itemListener = listener;
  }

  static interface OnItemSelectedListener {
    void onItemClicked(TextView view);
  }
}
