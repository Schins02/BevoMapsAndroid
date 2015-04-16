package edu.utexas.cs.bevomaps;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SearchLayer.java
 *
 * Created by Steven on 3/22/2015.
 */

class SearchLayer {

  static final String BUILDING = "building", FLOOR = "floor";

  //Input: a string that the user searched for
  //Output: returns a map of "building" : building, and "floor" : floor
  public static Map<String, String> parseInputText(CacheLayer c, String s){
    s = s.toLowerCase();
    String[] strings = s.split("\\s+");

    //Map of common searches to the building or floor
    Map<String,String> cache_map = c.loadSearchMap();
    for (String t : strings) {
      if (cache_map.containsKey(t)) {
        s = s.replaceAll(t, cache_map.get(t).toLowerCase());
      }
    }

    Map<String, String> result = new TreeMap<>();
    String building = null;
    String floor = null;

    //Re split the string in case anything changed
    strings = s.split("\\s+");

    //Check the strings array for a building name "ABC"
    for(String t: strings){
        if(t.length() == 3 && t.matches("[a-zA-Z]{3}")){
            building = t.toUpperCase();
            break;
        }
    }

    // Find the floor in the string string
    Pattern roomPattern = Pattern.compile("(\\d\\.\\d*)");
    Matcher match = roomPattern.matcher(s);
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

    result.put(BUILDING, building);
    result.put(FLOOR, floor);

	  return result;
  }
}
