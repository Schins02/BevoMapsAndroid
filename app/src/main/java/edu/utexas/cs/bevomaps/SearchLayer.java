package edu.utexas.cs.bevomaps;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SearchLayer.java
 *
 * Created by Steven on 3/22/2015.
 */

class SearchLayer {

  //Input: a string that the user searched for
  //Output: returns a map of "building" : building, and "floor" : floor
  public static Map<String, String> parseInputText(CacheLayer c, String s){
    s = s.toLowerCase();
    String[] strings = s.split("\\s+");

    //Map of common searches to the building or floor
    Map<String,String> cache_map = c.getSearchMap();
    for(String t: strings){
      if(cache_map.containsKey(t.toLowerCase())){
        s = s.replaceAll(t, cache_map.get(t).toLowerCase());
      }
    }

    Map<String, String> result = new HashMap<>();

    // Find building name in the search string
    Pattern pattern = Pattern.compile("([a-zA-Z]{3})");
    Matcher match = pattern.matcher(s);
    String building = null;
    String floor = null;

    if(match.find()){
      building = match.group(1).toUpperCase();
    }

    // Find the floor in the string string
    Pattern roomPattern = Pattern.compile("(\\d\\.\\d*)");
    match = roomPattern.matcher(s);
    if(match.find()){
      floor = match.group(1);
      floor = Character.toString(floor.charAt(0));
    }

    // Look for a floor number with no decimal
    else{
      roomPattern = Pattern.compile("(\\d+)");
      match = roomPattern.matcher(s);
      if(match.find()){
        floor = match.group(1);
        floor = Character.toString(floor.charAt(0));
      }
    }

    result.put("building", building);
    result.put("floor", floor);

	  return result;
  }
}
