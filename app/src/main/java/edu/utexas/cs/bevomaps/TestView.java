package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

public class TestView extends Activity implements View.OnClickListener{

  // Fields---------------------------------------------------------

  private static final String TAG = "TestView";

  private CacheLayer cacheLayer;
  private ImageView imageView;

  // Methods--------------------------------------------------------

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

    cacheLayer = new CacheLayer();
    imageView = (ImageView)findViewById(R.id.image);

    final Button button = (Button)findViewById(R.id.button);
    button.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    cacheLayer.loadImage(this, imageView, "GDC", "06");
  }
}
