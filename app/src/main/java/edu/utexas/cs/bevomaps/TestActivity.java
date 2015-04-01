package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

import java.io.File;
import java.util.Arrays;

/**
 * TestActivity.java
 *
 * Created by Eric on 3/28/15.
 */

public class TestActivity extends Activity implements View.OnClickListener{

  // Fields---------------------------------------------------------

  private static final String TAG = "TestActivity";

  private CacheLayer cacheLayer;
  private ImageView imageView;

  private Button button1, button2, button3;

  // Methods--------------------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);

    getParseConfig();
    cacheLayer = new CacheLayer(this);
    imageView = (ImageView)findViewById(R.id.image);

    button1 = (Button)findViewById(R.id.button1);
    button2 = (Button)findViewById(R.id.button2);
    button3 = (Button)findViewById(R.id.button3);
    button1.setOnClickListener(this);
    button2.setOnClickListener(this);
    button3.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (v == button1) {
      cacheLayer.loadImage(imageView, "GDC", "01");
    }
    else if (v == button2) {
      cacheLayer.loadImage(imageView, "GDC", "03");
    }
    else if (v == button3) {
      File cacheDir = new File(getCacheDir(), "ImageCache");
      Log.d(TAG, Arrays.toString(cacheDir.listFiles()));
    }
  }

  private void getParseConfig() {
    ParseObject.registerSubclass(BuildingJSON.class);
    Parse.initialize(this, "xTzPEGb9UXNKHH6lEphikPyDpfXeSinJ9HoIqODU",
        "tmEVaWNvPic1VQd2c69Zn0u6gieingOJcMIF6zrD");

    // Privacy settings
    ParseACL acl = new ParseACL();
    acl.setPublicReadAccess(true);
    ParseACL.setDefaultACL(acl, true);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return false;
  }
}
