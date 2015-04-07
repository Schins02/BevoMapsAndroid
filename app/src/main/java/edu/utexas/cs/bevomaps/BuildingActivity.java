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
  private String building, floor;

  private ABHelper abHelper;
  private BGHelper bgHelper;
  private FSHelper fsHelper;
  private ImageHelper imageHelper;

  private ProgressBar progressBar;

  // Methods--------------------------------------------------------

  @Override
  protected void onCreate(Bundle in) {
    super.onCreate(in);
    setContentView(R.layout.activity_building);

    Intent intent = getIntent();
    cacheLayer = intent.getParcelableExtra("cache");
    building = intent.getStringExtra(SearchLayer.BUILDING);
    floor = intent.getStringExtra(SearchLayer.FLOOR);

    String text = "";
    if (in != null) {
      building = in.getString(SearchLayer.BUILDING);
      floor = in.getString(SearchLayer.FLOOR);
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
        Map<String, String> infoMap =
            SearchLayer.parseInputText(cacheLayer, abHelper.getEditText().getText().toString());
        prepareForSegue(infoMap.get(SearchLayer.BUILDING), infoMap.get(SearchLayer.FLOOR));
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
    fsHelper = new FSHelper(this);
    fsHelper.setOnItemClickListener(new FSHelper.OnItemSelectedListener() {
      @Override
      public void onItemClicked(TextView view) {
        prepareForSegue(building, view.getText().toString());
      }
    });
    imageHelper = new ImageHelper((SubsamplingScaleImageView)findViewById(R.id.building_image));

    progressBar = (ProgressBar)findViewById(R.id.ab_progress);

    cacheLayer.loadFloors(fsHelper, building);
    cacheLayer.loadImage(imageHelper, progressBar, building, floor);
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle out) {
    out.putString(SearchLayer.BUILDING, building);
    out.putString(SearchLayer.FLOOR, floor);
    out.putString("searchText", abHelper.getEditText().getText().toString());
  }

  private void prepareForSegue(String building, String floor) {
    if (this.building.equals(building)) {
      this.floor = floor;

      cacheLayer.loadImage(imageHelper, progressBar, building, floor);
    }
    else if (building != null && cacheLayer.isBuilding(building)) {
      this.building = building;
      this.floor = floor;

      abHelper.setTitle(cacheLayer.getBuildingName(building));
      fsHelper.clear();

      cacheLayer.loadFloors(fsHelper, building);
      cacheLayer.loadImage(imageHelper, progressBar, building, floor);
    }
    else {
      Toast.makeText(this, R.string.toast_invalid, Toast.LENGTH_SHORT).show();
    }
  }

  private void showKeyboard() {
    abHelper.expand();
    bgHelper.fadeIn();
    fsHelper.fadeOut();

    abHelper.getEditText().requestFocus();
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(abHelper.getEditText(), 0);
  }

  private void hideKeyboard() {
    abHelper.collapse();
    bgHelper.fadeOut();
    fsHelper.fadeIn();

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
