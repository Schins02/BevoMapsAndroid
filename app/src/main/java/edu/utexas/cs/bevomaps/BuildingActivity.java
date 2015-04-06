package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import java.util.Map;

/**
 * BuildingActivity.java
 *
 * Created by Eric on 3/28/15.
 */

public class BuildingActivity extends Activity {

  // Fields---------------------------------------------------------

  private CacheLayer cacheLayer;

  private ABHelper abHelper;
  private BGHelper bgHelper;
  private ImageHelper imageHelper;

  private ProgressBar progressBar;

  // Methods--------------------------------------------------------

  @Override
  protected void onCreate(Bundle in) {
    super.onCreate(in);
    setContentView(R.layout.activity_building);

    Intent intent = getIntent();
    cacheLayer = intent.getParcelableExtra("cache");

    String text = "";
    if (in != null) {
      text = in.getString("searchText");
    }

    abHelper = new ABHelper(this);
    abHelper.setTitle(intent.getStringExtra("name"));
    abHelper.getEditText().setText(text);
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
        showKeyboard();
      }
    });
    abHelper.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        hideKeyboard();
        prepareForSegue(SearchLayer.parseInputText(cacheLayer,
            abHelper.getEditText().getText().toString()));
        return true;
      }
    });
    bgHelper = new BGHelper(findViewById(R.id.building_background));
    bgHelper.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (abHelper.isSearchVisible()) {
          hideKeyboard();
        }

        return false;
      }
    });
    imageHelper = new ImageHelper((SubsamplingScaleImageView)findViewById(R.id.building_image));

    progressBar = (ProgressBar)findViewById(R.id.ab_progress);

    cacheLayer.loadImage(imageHelper, progressBar,
        intent.getStringExtra(SearchLayer.BUILDING), intent.getStringExtra(SearchLayer.FLOOR));
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle out) {
    out.putString("searchText", abHelper.getEditText().getText().toString());
  }

  private void prepareForSegue(Map<String, String> info) {
    String building = info.get(SearchLayer.BUILDING);
    if (building != null && cacheLayer.isBuilding(building)) {
      abHelper.setTitle(cacheLayer.getBuildingName(building));
      cacheLayer.loadImage(imageHelper, progressBar, building, info.get(SearchLayer.FLOOR));
    }
    else {
      Toast.makeText(this, R.string.toast_invalid, Toast.LENGTH_SHORT).show();
    }
  }

  private void showKeyboard() {
    abHelper.expand();
    bgHelper.fadeIn();

    abHelper.getEditText().requestFocus();
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(abHelper.getEditText(), 0);
  }

  private void hideKeyboard() {
    abHelper.collapse();
    bgHelper.fadeOut();

    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(abHelper.getEditText().getWindowToken(), 0);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (abHelper.isSearchVisible()) {
      hideKeyboard();
    }
  }
}
