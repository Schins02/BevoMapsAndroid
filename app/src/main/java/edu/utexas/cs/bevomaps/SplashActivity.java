package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

/**
 * SplashActivity.java
 *
 * Created by Eric on 4/16/15.
 */

public class SplashActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    configureStatusBar();
    configureParse();

    final Context context = this;
    final CacheLayer cacheLayer = new CacheLayer(context);
    final Handler handler = new Handler();

    new Runnable() {
      @Override
      public void run() {
        if (cacheLayer.isReady()) {
          handler.removeCallbacks(this);

          Intent intent = new Intent(context, MapActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
              .putExtra(CacheLayer.class.getSimpleName(), cacheLayer);

          startActivity(intent);
          finish();
          return;
        }

        handler.postDelayed(this, 500);
      }
    }.run();
  }

  private void configureParse() {
    ParseObject.registerSubclass(BuildingJSON.class);
    Parse.initialize(this,
        DataLayer.PARSE_APP_ID,
        DataLayer.PARSE_CLIENT_ID);
    ParseACL acl = new ParseACL();
    acl.setPublicReadAccess(true);
    ParseACL.setDefaultACL(acl, true);
  }

  private void configureStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      int id = getResources().getIdentifier("status_bar_height", "dimen", "android");
      if (id > 0) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        View searchBox = findViewById(R.id.sb);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) searchBox.getLayoutParams();
        params.topMargin = getResources().getDimensionPixelSize(id);
        searchBox.setLayoutParams(params);
      }
    }
  }
}
