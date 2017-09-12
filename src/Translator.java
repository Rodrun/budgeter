import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Translator translates specific strings into the given available language.
 * This is done by reading a JSON file containing keys of the english string
 * paired with its translated string value. Notice that it doesn't magically
 * google translate given strings...
 * For example, "Spanish.json" would look like:
 * <p>
 * <code>
 *     {
 *         "Hello":"Hola",
 *         "Fifth of May":"Cinco de Mayo"
 *     }
 * </code>
 */
public class Translator {

    private HashMap<String, String> words;

    /**
     * Creates a Translator that uses the given file.
     * @param file Path of the language file.
     */
    public Translator(String file) throws IOException {
        words = readFile(file);
    }

    /**
     * Creates a Translator without a language. This will make all values of
     * <code>translate(english)</code> return the value of <code>english</code>.
     */
    public Translator() {
        // Initialize an empty HashMap. It will stay empty.
        words = new HashMap<>(0);
    }

    /**
     * Get the proper translated string.
     * @param english Original english string to translate.
     * @return Translated string if english is found, otherwise return given
     * english string.
     */
    public String translate(String english) {
        return words.getOrDefault(english, english);
    }

    /**
     * Translate every element of a String array. This method iterates
     * through all the elements and translates each one, storing its
     * translated value into a separate array that is returned.
     * @param array String array to translate.
     * @return Array
     */
    public String[] translate(String[] array) {
        if (array == null) {
            return null;
        }
        // Translate every element of the array
        String[] trans = new String[array.length];
        for (int i = 0; i < trans.length; i++) {
            trans[i] = translate(array[i]);
        }
        return trans;
    }

    /**
     * Read a language JSON file and put all values into the map.
     * @param fn Path of the file.
     * @throws IOException If file does not exist.
     */
    private HashMap<String, String> readFile(String fn) throws IOException {
        File file = new File(fn);
        if (!file.exists()) {
            throw new FileNotFoundException("Language file not found!");
        }

        // Parse JSON
        FileReader r = new FileReader(file);
        JsonObject obj = Json.parse(r).asObject();
        // Put parsed into map
        return createMap(obj);
    }

    /**
     * Create a map from the values found in a JSON object.
     * @param object JsonObject to map values.
     * @return A HashMap containing the values of object.
     */
    private HashMap<String, String> createMap(JsonObject object) {
        HashMap<String, String> map = new HashMap<>(
                (object == null) ? 0 : object.size()
        );
        // Assign
        if (object != null) {
            object.forEach(member ->
                    map.putIfAbsent(
                            member.getName(),
                            member.getValue().asString()
                    )
            );
        }
        return map;
    }

}
