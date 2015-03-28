package edu.utexas.cs.bevomaps;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Steven on 3/22/2015.
 */

class SearchLayer {

    //Input: a string that the user searched for
    //Output: returns a map of "building" : building, and "floor" : floor
    public static Map<String, String> getInputText(String s){
    	Map<String, String> result = new HashMap<String,String>();
    	
    	// Find building name in the search string
    	Pattern pattern = Pattern.compile("([a-zA-Z]{3})");    	
    	Matcher match = pattern.matcher(s);
    	String building = "";
    	if(match.find()){
    		building = match.group(1);
    		result.put("building", building);
    	}    	
    	
    	if(building == ""){
    		result.put("building",  null);
    	}   
    	
    	// Find the floor in the string string
    	Pattern roomPattern = Pattern.compile("(\\d\\.\\d*)");
    	match = roomPattern.matcher(s);    	
    	if(match.find()){
    		String floor = match.group(1);
    		result.put("floor", Character.toString(floor.charAt(0)));
    	}    	
    	
    	// Look for a floor number with no decimal
    	else{
    		roomPattern = Pattern.compile("(\\d+)");
    		match = roomPattern.matcher(s);
    		if(match.find()){
    			String floor = match.group(1);
    			result.put("floor", Character.toString(floor.charAt(0)));
    		}    		
    	}    	
    	
		return result;  	
    }
}
