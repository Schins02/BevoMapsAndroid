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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import java.util.Map;

/**
 * MapActivity.java
 *
 * Created by Eric on 3/28/15.
 */

public class MapActivity extends Activity {

  // Fields---------------------------------------------------------

  private CacheLayer cacheLayer;

  private BGHelper bgHelper;
  private FABHelper fabHelper;
  private MapHelper mapHelper;

  private EditText textView;

  // Methods--------------------------------------------------------

  @Override
  protected void onCreate(Bundle in) {
    super.onCreate(in);
    setContentView(R.layout.activity_map);

    configParse();
    configStatusBar();


    CameraPosition position = null;
    String text = "";
    if (in != null) {
      cacheLayer = in.getParcelable("cacheLayer");
      position = in.getParcelable("cameraPosition");
      text = in.getString("searchText");
    }
    else {
      cacheLayer = new CacheLayer(this);
    }

    bgHelper = new BGHelper(findViewById(R.id.map_background));
    bgHelper.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (textView.isCursorVisible()) {
          hideKeyboard();
        } else {
          mapHelper.setFollowing(false);
        }

        return false;
      }
    });
    mapHelper = new MapHelper(this,
        (MapFragment) getFragmentManager().findFragmentById(R.id.map_map),
        position,cacheLayer, new GoogleMap.OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        prepareForSegue(cacheLayer.getBuildingName(marker.getPosition()), null);
        return true;
      }
    });
    fabHelper = new FABHelper((FloatingActionButton) findViewById(R.id.map_location));
    fabHelper.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mapHelper.setFollowing(true);
      }
    });

    textView = (EditText) findViewById(R.id.sb_text);
    textView.setText(text);
    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!textView.isCursorVisible()) {
          showKeyboard();
        }
      }
    });
    textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        hideKeyboard();
        Map<String, String> infoMap =
            SearchLayer.parseInputText(cacheLayer, textView.getText().toString());
        prepareForSegue(infoMap.get(SearchLayer.BUILDING), infoMap.get(SearchLayer.FLOOR));
        return true;
      }
    });

    ImageButton menuButton = (ImageButton) findViewById(R.id.sb_menu);
    menuButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (textView.isCursorVisible()) {
          hideKeyboard();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.map_drawer);
        drawer.openDrawer(GravityCompat.START);
      }
    });
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle out) {
    out.putParcelable("cacheLayer", cacheLayer);
    out.putParcelable("cameraPosition", mapHelper.getCameraPosition());
    out.putString("searchText", textView.getText().toString());
  }

  private void prepareForSegue(String building, String floor) {
    if (building != null && cacheLayer.isBuilding(building)) {
      Intent intent = new Intent(this, BuildingActivity.class);
      intent.putExtra("cache", cacheLayer)
            .putExtra("name", cacheLayer.getBuildingName(building))
            .putExtra(SearchLayer.BUILDING, building)
            .putExtra(SearchLayer.FLOOR, floor);
      startActivity(intent);
    }
    else {
      Toast.makeText(this, R.string.toast_invalid, Toast.LENGTH_SHORT).show();
    }
  }

  private void showKeyboard() {
    textView.setCursorVisible(true);
    bgHelper.fadeIn();
    fabHelper.fadeOut();
  }

  private void hideKeyboard() {
    textView.setCursorVisible(false);
    bgHelper.fadeOut();
    fabHelper.fadeIn();

    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

    mapHelper.redraw();
  }

  private void configParse() {
    ParseObject.registerSubclass(BuildingJSON.class);
    Parse.initialize(this,
        "xTzPEGb9UXNKHH6lEphikPyDpfXeSinJ9HoIqODU",
        "tmEVaWNvPic1VQd2c69Zn0u6gieingOJcMIF6zrD");
    ParseACL acl = new ParseACL();
    acl.setPublicReadAccess(true);
    ParseACL.setDefaultACL(acl, true);
  }

  private void configStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      int id = getResources().getIdentifier("status_bar_height", "dimen", "android");
      if (id > 0) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        View fsb = findViewById(R.id.sb);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fsb.getLayoutParams();
        params.topMargin = getResources().getDimensionPixelSize(id);
        fsb.setLayoutParams(params);
      }
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    mapHelper.disconnect();
  }

  @Override
  public void onResume() {
    super.onResume();
    mapHelper.connect();
    if (textView.isCursorVisible()) {
      hideKeyboard();
    }
    else {
      mapHelper.redraw();
    }
  }
}
