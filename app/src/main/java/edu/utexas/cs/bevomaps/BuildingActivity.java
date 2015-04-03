package edu.utexas.cs.bevomaps;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.Map;

/**
 * BuildingActivity.java
 * <p/>
 * Created by Eric on 3/28/15.
 */

public class BuildingActivity extends ActionBarActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private Map<String, String> mQueryMap;
    private static final String TAG = "BuildingView";
    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        /*** Set and style action bar ***/
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.building_view_tool_bar);
        toolbar.setTitle("Dell Gates Center");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.building_view_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }


            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() != 0) {
                    System.out.println("--->" + query);
                    CacheLayer c = getIntent().getParcelableExtra("cache");
                    mQueryMap = SearchLayer.parseInputText(c, query.toString());
                    //c.loadImage(mContext, mBuildingView, "GDC", "01");
                    Log.d(TAG, mQueryMap.toString() + "THE QUERY");

                    return true;
                }
                return false;
            }

        });

        return true;
    }


}
