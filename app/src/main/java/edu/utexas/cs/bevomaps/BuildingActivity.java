package edu.utexas.cs.bevomaps;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

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
    private String mNameString;
    private String mBuildingString;
    private String mFloorString;
    private CacheLayer mCacheLayer;
    SubsamplingScaleImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        /*** Get info from MapActivity ***/
        mCacheLayer = getIntent().getParcelableExtra("cache");
        mNameString = getIntent().getStringExtra("name");
        mBuildingString = getIntent().getStringExtra(SearchLayer.BUILDING);
        mFloorString = getIntent().getStringExtra(SearchLayer.FLOOR);

        /*** Set and style action bar ***/
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.building_view_tool_bar);
        toolbar.setTitle(mNameString);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        /*** Hand cacheLayer the view and building info to set the image ***/
        mImageView = (SubsamplingScaleImageView)findViewById(R.id.imageView);
        mCacheLayer.loadImage(mImageView, mBuildingString, mFloorString);
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
                    mQueryMap = SearchLayer.parseInputText(mCacheLayer, query);  //TODO this should not be instance var
                    mCacheLayer.loadImage(mImageView, mQueryMap.get(SearchLayer.BUILDING) , mQueryMap.get(SearchLayer.FLOOR));
                    return true;
                }
                return false;
            }

        });

        return true;
    }


}
