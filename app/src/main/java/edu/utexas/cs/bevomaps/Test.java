package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import java.util.Map;

public class Test extends Activity {

  private static String TAG = "Test";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ParseObject.registerSubclass(BuildingJSON.class);
    Parse.initialize(this, "xTzPEGb9UXNKHH6lEphikPyDpfXeSinJ9HoIqODU", "tmEVaWNvPic1VQd2c69Zn0u6gieingOJcMIF6zrD");

    // Privacy settings
    ParseACL defaultACL = new ParseACL();
    defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);

    Map<String, Map<String, String>> buildingMap = BuildingData.getBuildingMap();
    Log.d(TAG, buildingMap.toString());

    setContentView(R.layout.test);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      WindowManager.LayoutParams params = window.getAttributes();
      params.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
      window.setAttributes(params);
    }
  }
}
