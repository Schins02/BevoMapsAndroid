package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * BuildingActivity.java
 *
 * Created by Eric on 3/28/15.
 */

public class BuildingActivity extends Activity {

  // Fields---------------------------------------------------------

  private ABHelper abHelper;
  private BGHelper bgHelper;
  private ImageHelper imageHelper;

  // Methods--------------------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_building);

    Intent intent = getIntent();

    abHelper = new ABHelper(this);
    abHelper.setBackOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hideKeyboard();
        finish();
      }
    });
    abHelper.setSearchOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        abHelper.expand();
        bgHelper.fadeIn();
        showKeyboard();
      }
    });
    abHelper.setTitle(intent.getStringExtra("name"));
    bgHelper = new BGHelper(findViewById(R.id.building_background));
    bgHelper.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (abHelper.isSearchVisible()) {
          abHelper.collapse();
          bgHelper.fadeOut();
          hideKeyboard();
        }

        return false;
      }
    });
  }

  private void showKeyboard() {
    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(abHelper.getEditText(), 0);
  }

  private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(abHelper.getEditText().getWindowToken(), 0);
  }
}
