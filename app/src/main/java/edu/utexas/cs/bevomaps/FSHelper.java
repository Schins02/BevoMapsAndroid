package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * FSHelper.java
 *
 * Created by Eric on 4/6/15.
 */

class FSHelper {

  // Fields---------------------------------------------------------

  private final ArrayAdapter<String> itemAdapter;
  private final LinearLayout listLayout;
  private final int normalColor, selectedColor;

  private OnItemSelectedListener listener;
  private TextView current;

  // Constructors---------------------------------------------------

  FSHelper (Activity activity) {
    itemAdapter = new ArrayAdapter<>(activity, R.layout.floor_selector_item);
    listLayout = (LinearLayout)activity.findViewById(R.id.fs_list);
    normalColor = activity.getResources().getColor(R.color.secondary_blue_75);
    selectedColor = activity.getResources().getColor(R.color.secondary_black);
  }

  // Methods--------------------------------------------------------

  void addItems(String[] items, String selected) {
    itemAdapter.clear();
    itemAdapter.addAll(items);

    for (int i = 0; i < itemAdapter.getCount(); i++) {
      TextView view = (TextView) itemAdapter.getView(i, null, null);
      if (itemAdapter.getItem(i).equals(selected)) {
        view.setTextColor(selectedColor);
        current = view;
      }

      final int position = i;
      view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          TextView chosen = (TextView)v;
          setViewSelected(chosen);

          if (listener != null) {
            listener.onItemClicked(chosen, position);
          }
        }
      });

      listLayout.addView(view);
    }
  }

  void setOnItemClickListener(OnItemSelectedListener listener) {
    this.listener = listener;
  }

  private void setViewSelected(TextView view) {
    current.setTextColor(normalColor);
    view.setTextColor(selectedColor);
    current = view;
  }

  static interface OnItemSelectedListener {
    void onItemClicked(TextView view, int position);
  }
}
