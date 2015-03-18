package edu.utexas.cs.bevomaps;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONObject;

@ParseClassName("BuildingJSON")
public class BuildingJSON extends ParseObject {

    public BuildingJSON() {
    }

    public JSONObject getBuildingJSON() {return getJSONObject("Buildings");}

}
