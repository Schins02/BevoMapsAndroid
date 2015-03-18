package edu.utexas.cs.bevomaps;

import android.app.Activity;
import android.os.Bundle;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

public class Test extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseObject.registerSubclass(BuildingJSON.class);
        Parse.initialize(this, "xTzPEGb9UXNKHH6lEphikPyDpfXeSinJ9HoIqODU", "tmEVaWNvPic1VQd2c69Zn0u6gieingOJcMIF6zrD");

        //privacy settings
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        BuildingData bd = new BuildingData();
        setContentView(R.layout.test);
    }
}
