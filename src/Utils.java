import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

/**
 * Useful helper methods.
 */
public class Utils {

    /**
     * Convert a JsonArray of String to a String array.
     * @param arr JsonArray containing the Strings.
     * @return String array from given JsonArray.
     */
    public static String[] jsonArrayToString(JsonArray arr) {
        String[] strVals = new String[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            strVals[i] = arr.get(i).asString();
        }
        return strVals;
    }

}
