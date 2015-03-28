import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Steven on 3/22/2015.
 */
public class Search {

    //Input: a string that the user searched for
    //Output: returns a map of "building" : building, and "floor" : floor
    public static Map<String, String> getInputText(String s){

        s = s.toLowerCase();
        Map<String, String> result = new HashMap<String,String>();

        //Regex for ABC 1.234
        //Character,charcter,character, 0 or more blank space, a digit followed by a period, followed by one or more digits
        if(s.matches("\\w{3}\\s*\\d\\.\\d+")){
            String building_name = s.substring(0,3);
            result.put("building", building_name.toUpperCase());

            int period_index = s.indexOf('.');
            char floor = s.charAt(period_index-1);
            String floor_number = Character.toString(floor);
            result.put("floor", floor_number);
        }

        //Regex for ABC 1234
        //No decimal in the string
        //Starting with 3 chars in a row, 0 or more blank space, then one or more digits
        else if(s.matches("\\w{3}\\s*\\d+")){
            String[] strings = s.split("\\s+");

            String building_name = strings[0];
            result.put("building", building_name.toUpperCase());

            String floor_number = Character.toString(strings[1].charAt(0));
            result.put("floor", floor_number);
        }

        //Regex for ABC
        else if(s.matches("\\w{3}")){
            String building_name = s.substring(0,3);
            String floor_number = null; //No floor given in the search
            result.put("building", building_name.toUpperCase());
            result.put("floor", floor_number);
        }

        else{
            return null;
        }

        return result;
    }
}
