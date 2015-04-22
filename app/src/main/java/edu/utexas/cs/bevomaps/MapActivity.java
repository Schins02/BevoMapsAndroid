package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;

import java.util.Map;

/**
 * MapActivity.java
 * <p/>
 * Created by Eric on 3/28/15.
 */

public class MapActivity extends Activity {

  // Fields---------------------------------------------------------

  private CacheLayer cacheLayer;

  private BackgroundVC backgroundVC;
  private FABVC fabVC;
  private MapVC mapVC;

  private DrawerLayout drawerLayout;
  private EditText textView;

  private Context context;

  // Methods--------------------------------------------------------

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.activity_map);
    context = this;

    cacheLayer = getIntent().getParcelableExtra(CacheLayer.class.getSimpleName());
    configureStatusBar();

    backgroundVC = new BackgroundVC(findViewById(R.id.map_background));
    backgroundVC.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent event) {
        if (textView.isCursorVisible()) {
          hideKeyboard();
          return true;
        }
        mapVC.setCurFollow(false);
        return false;
      }
    });

    MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
    int[] circleColors = {getResources().getColor(R.color.burnt_orange_10),
            getResources().getColor(R.color.burnt_orange_20)};
    if (bundle != null) {
      mapVC = new MapVC(this, mapFragment, (CameraPosition) bundle.getParcelable("camera"),
              bundle.getBoolean("hybrid"), cacheLayer, circleColors);
      mapVC.setCurFollow(bundle.getBoolean("follow"));
    } else {
      mapVC = new MapVC(this, mapFragment, null, false, cacheLayer, circleColors);
    }

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.map_location);
    fabVC = new FABVC(fab);
    fabVC.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mapVC.setCurFollow(true);
      }
    });

    textView = (EditText) findViewById(R.id.sb_text);
    if (bundle != null) {
      textView.setText(bundle.getCharSequence("text"));
    }
    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!textView.isCursorVisible()) {
          showKeyboard();
        }
      }
    });
    textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        hideKeyboard();

        String input = textView.getText().toString();
        Map<String, String> info = SearchLayer.parseInputText(cacheLayer, input);

        prepareForSegue(info.get(SearchLayer.BUILDING), info.get(SearchLayer.FLOOR));
        return true;
      }
    });

    drawerLayout = (DrawerLayout) findViewById(R.id.map_drawer);

    ImageButton menuButton = (ImageButton) findViewById(R.id.sb_menu);
    menuButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (textView.isCursorVisible()) {
          hideKeyboard();
        }
        drawerLayout.openDrawer(GravityCompat.START);
      }
    });

    View satelliteItem = findViewById(R.id.drawer_satellite);
    satelliteItem.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mapVC.setCurSatellite(!mapVC.getCurSatellite());
        drawerLayout.closeDrawer(GravityCompat.START);
      }
    });

    View aboutItem = findViewById(R.id.drawer_about);
    aboutItem.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(context);
        builder.setTitle("About").
                setMessage("Bevo Maps is powered by the Google Maps API").
                show();
      }
    });
  }

  private void prepareForSegue(String building, String floor) {
    String title = cacheLayer.getBuildingName(building);

    if (building != null && title != null) {
      Intent intent = new Intent(this, BuildingActivity.class);
      intent.putExtra(CacheLayer.class.getSimpleName(), cacheLayer)
              .putExtra("title", title)
              .putExtra(SearchLayer.BUILDING, building)
              .putExtra(SearchLayer.FLOOR, floor);

      startActivity(intent);
    } else {
      Toast.makeText(this, R.string.toast_invalid, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle bundle) {
    bundle.putParcelable("camera", mapVC.getCameraPosition());
    bundle.putBoolean("follow", mapVC.getCurFollow());
    bundle.putBoolean("hybrid", mapVC.getCurSatellite());
    bundle.putCharSequence("text", textView.getText());
  }

  private void showKeyboard() {
    backgroundVC.animateFadeIn();
    fabVC.animateSlideOut();
    textView.setCursorVisible(true);
  }

  private void hideKeyboard() {
    backgroundVC.animateFadeOut();
    fabVC.animateSlideIn();
    textView.setCursorVisible(false);

    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

    mapVC.invalidate();
  }

  private void configureStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      int id = getResources().getIdentifier("status_bar_height", "dimen", "android");
      if (id > 0) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int height = getResources().getDimensionPixelSize(id);

        View topView = findViewById(R.id.sb);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) topView.getLayoutParams();
        params.topMargin = height;
        topView.setLayoutParams(params);

        topView = findViewById(R.id.drawer_options);
        params = (ViewGroup.MarginLayoutParams) topView.getLayoutParams();
        params.topMargin = height;
        topView.setLayoutParams(params);
      }
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    mapVC.disconnectLocations();
  }

  @Override
  public void onResume() {
    super.onResume();
    mapVC.connectLocations();
    if (textView.isCursorVisible()) {
      hideKeyboard();
    } else {
      mapVC.invalidate();
    }
  }
}
