package edu.utexas.cs.bevomaps;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import org.json.JSONObject;

/**
 * BuildingJSON.java
 *
 * Created by John on 3/18/2015.
 */

@ParseClassName("BuildingJSON")
public class BuildingJSON extends ParseObject {
    public JSONObject getBuildingJSON() {
      return getJSONObject("Buildings");
    }
}
