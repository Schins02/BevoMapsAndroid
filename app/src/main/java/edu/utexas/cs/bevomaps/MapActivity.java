package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
  private MapHelper mapHelper;

  private FloatingActionButton followButton;
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
        mapHelper.setFollowing(false);
        hideKeyboard();
        return false;
      }
    });

    mapHelper = new MapHelper(this,
        (MapFragment)getFragmentManager().findFragmentById(R.id.map), cacheLayer);

    followButton = (FloatingActionButton)findViewById(R.id.location);
    followButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mapHelper.setFollowing(true);
      }
    });

    textView = (EditText)findViewById(R.id.search);
    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showKeyboard();
      }
    });
    textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        prepareForSegue(SearchLayer.parseInputText(cacheLayer, textView.getText().toString()));
        hideKeyboard();
        return true;
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
      bgHelper.startAnimation(true);
    }
  }

  private void hideKeyboard() {
    if (textView.isCursorVisible()) {
      textView.setCursorVisible(false);
      bgHelper.startAnimation(false);

      InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

      mapHelper.invalidate();
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
      Window window = getWindow();
      WindowManager.LayoutParams params = window.getAttributes();
      params.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
      window.setAttributes(params);
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
