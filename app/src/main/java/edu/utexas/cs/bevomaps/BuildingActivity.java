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
  private String curBuilding, curFloor;

  private ActionBarVC actionBarVC;
  private BackgroundVC backgroundVC;
  private FloorSelectorVC floorSelectorVC;
  private ImageVC imageVC;

  private static final long FADE_DURATION = 500; //500ms
  private ProgressBar progressBar;
  private ImageTask.OnProgressUpdateListener progressListener;

  // Methods--------------------------------------------------------

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.activity_building);

    Intent intent = getIntent();
    cacheLayer = intent.getParcelableExtra(CacheLayer.class.getSimpleName());

    if (bundle != null) {
      curBuilding = bundle.getString(SearchLayer.BUILDING);
      curFloor = bundle.getString(SearchLayer.FLOOR);
    }
    else {
      curBuilding = intent.getStringExtra(SearchLayer.BUILDING);
      curFloor = intent.getStringExtra(SearchLayer.FLOOR);
    }

    CharSequence text = bundle != null ? bundle.getCharSequence("text") : null;
    String title = intent.getStringExtra("title");
    actionBarVC = new ActionBarVC(this);
    actionBarVC.setTitle(title);
    actionBarVC.getEditText().setText(text);
    actionBarVC.setOnBackClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hideKeyboard();
        finish();
      }
    });
    actionBarVC.setOnSearchClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showKeyboard();
      }
    });
    actionBarVC.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        hideKeyboard();

        String input = actionBarVC.getEditText().getText().toString();
        Map<String, String> info = SearchLayer.parseInputText(cacheLayer, input);

        prepareForSegue(info.get(SearchLayer.BUILDING), info.get(SearchLayer.FLOOR));
        return true;
      }
    });

    backgroundVC = new BackgroundVC(findViewById(R.id.building_background));
    backgroundVC.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (actionBarVC.isSearchVisible()) {
          hideKeyboard();
          return true;
        }
        return false;
      }
    });

    floorSelectorVC = new FloorSelectorVC(this);
    floorSelectorVC.setOnItemClickListener(new FloorSelectorVC.OnItemSelectedListener() {
      @Override
      public void onItemClicked(TextView view) {
        prepareForSegue(curBuilding, view.getText().toString());
      }
    });

    SubsamplingScaleImageView imageView =
        (SubsamplingScaleImageView)findViewById(R.id.building_image);
    imageVC = new ImageVC(imageView);

    progressBar = (ProgressBar)findViewById(R.id.ab_progress);

    progressListener = new ImageTask.OnProgressUpdateListener() {
      @Override
      public void onProgressBegin() {
        progressBar.setProgress(0);
        progressBar.setAlpha(1);
      }
      @Override
      public void onProgressUpdate(double progress) {
        progressBar.setProgress((int)(progress * progressBar.getMax()));
        if (progress == 1) {
          progressBar.animate().alpha(0).setDuration(FADE_DURATION);
        }
      }
    };

    cacheLayer.loadFloors(floorSelectorVC, curBuilding);
    curFloor = cacheLayer.loadImage(imageVC, curBuilding, curFloor, progressListener);
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle out) {
    out.putString(SearchLayer.BUILDING, curBuilding);
    out.putString(SearchLayer.FLOOR, curFloor);
    out.putString("text", actionBarVC.getEditText().getText().toString());
  }

  private void prepareForSegue(String building, String floor) {
    if (curBuilding.equals(building)) {
      curFloor = cacheLayer.loadImage(imageVC, building, floor, progressListener);
      return;
    }

    String title = cacheLayer.getBuildingName(building);
    if (building != null && title != null) {
      actionBarVC.setTitle(title);
      floorSelectorVC.clearItems();
      floorSelectorVC.addItems(cacheLayer.getFloorNames(building));

      curBuilding = building;
      curFloor = cacheLayer.loadImage(imageVC, building, floor, progressListener);
    }
    else {
      Toast.makeText(this, R.string.toast_invalid, Toast.LENGTH_SHORT).show();
    }
  }

  private void showKeyboard() {
    actionBarVC.expandBar();
    backgroundVC.animateFadeIn();
    floorSelectorVC.animateFadeOut();

    actionBarVC.getEditText().requestFocus();
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(actionBarVC.getEditText(), 0);
  }

  private void hideKeyboard() {
    actionBarVC.collapseBar();
    backgroundVC.animateFadeOut();
    floorSelectorVC.animateFadeIn();

    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(actionBarVC.getEditText().getWindowToken(), 0);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (actionBarVC.isSearchVisible()) {
      hideKeyboard();
    }
  }
}
