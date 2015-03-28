package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import java.util.Map;

public class TestView extends Activity {

  private static String TAG = "*** TestView ***";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ParseObject.registerSubclass(BuildingJSON.class);
    Parse.initialize(this, "xTzPEGb9UXNKHH6lEphikPyDpfXeSinJ9HoIqODU", "tmEVaWNvPic1VQd2c69Zn0u6gieingOJcMIF6zrD");

    // Privacy settings
    ParseACL defaultACL = new ParseACL();
    defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);

    setContentView(R.layout.test);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      WindowManager.LayoutParams params = window.getAttributes();
      params.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
      window.setAttributes(params);
    }

//    Log.d(TAG, DataLayer.getBuildingMap().toString());
    final CacheLayer cache = new CacheLayer(this);

    final ImageView view = (ImageView)findViewById(R.id.image);
    final Button button = (Button)findViewById(R.id.button);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        cache.loadImage(view, "GDC", "06");
      }
    });
  }
}
