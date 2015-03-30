package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class TestActivity extends Activity implements View.OnClickListener{

  // Fields---------------------------------------------------------

  private static final String TAG = "TestActivity";

  private CacheLayer cacheLayer;
  private ImageView imageView;

  private Button button1, button2;

  // Methods--------------------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);

    cacheLayer = new CacheLayer();
    imageView = (ImageView)findViewById(R.id.image);

    button1 = (Button)findViewById(R.id.button1);
    button2 = (Button)findViewById(R.id.button2);
    button1.setOnClickListener(this);
    button2.setOnClickListener(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return false;
  }

  @Override
  public void onClick(View v) {
    if (v == button1) {
      cacheLayer.loadImage(this, imageView, "GDC", "01");
    }
    else if (v == button2) {
      cacheLayer.loadImage(this, imageView, "GDC", "03");
    }
  }
}
