package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.MapFragment;
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
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);

    configParse();
    configStatusBar();

    cacheLayer = new CacheLayer(this);

    bgHelper = new BGHelper(findViewById(R.id.background));
    bgHelper.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        hideKeyboard();
        mapHelper.setFollowing(false);
        return false;
      }
    });
    mapHelper = new MapHelper(this,
        (MapFragment)getFragmentManager().findFragmentById(R.id.map), cacheLayer);
    fabHelper = new FABHelper((FloatingActionButton)findViewById(R.id.location));
    fabHelper.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mapHelper.setFollowing(true);
      }
    });

    textView = (EditText)findViewById(R.id.text);
    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showKeyboard();
      }
    });
    textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        hideKeyboard();
        prepareForSegue(SearchLayer.parseInputText(cacheLayer, textView.getText().toString()));
        return true;
      }
    });

    ImageButton menuButton = (ImageButton)findViewById(R.id.menu);
    menuButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hideKeyboard();
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer);
        drawer.openDrawer(GravityCompat.START);
      }
    });
  }

  private void prepareForSegue(Map<String, String> info) {
    String building = info.get(SearchLayer.BUILDING);
    if (building == null || !cacheLayer.isBuilding(building)) {
      Toast.makeText(this, R.string.toast_invalid, Toast.LENGTH_SHORT).show();
    }
    else {
      Log.d("MapActivity", "Intent => " + info.toString());
    }
  }

  private void showKeyboard() {
    if (!textView.isCursorVisible()) {
      textView.setCursorVisible(true);
      bgHelper.fadeIn();
      fabHelper.fadeOut();
    }
  }

  private void hideKeyboard() {
    if (textView.isCursorVisible()) {
      textView.setCursorVisible(false);
      bgHelper.fadeOut();
      fabHelper.fadeIn();

      InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

      mapHelper.redraw();
    }
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
        Window window = getWindow();
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(attrs);

        View fsb = findViewById(R.id.fsb);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)fsb.getLayoutParams();
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
    hideKeyboard();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return false;
  }
}
