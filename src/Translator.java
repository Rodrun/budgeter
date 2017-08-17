import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Translator translates specific strings into the given available language.
 * This is done by reading a JSON file containing keys of the english string
 * paired with its translated string value.
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

    private Map<String, String> words;

    /**
     * Creates a Translator that uses the given file.
     * @param file Path of the language file.
     */
    public Translator(String file) throws IOException {
        readFile(file);
    }

    /**
     * Creates a Translator, defaulting to "English" as its language.
     */
    public Translator() throws IOException {
        this("English.json");
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
     * Read a language file and put all values into the map.
     * @param fn Path of the file.
     * @throws IOException If file does not exist.
     */
    public void readFile(String fn) throws IOException {
        File file = new File(fn);
        if (!file.exists()) {
            throw new FileNotFoundException("Language file not found!");
        }

        // Parse JSON
        FileReader r = new FileReader(file);
        JsonObject obj = Json.parse(r).asObject();
        // Put parsed into map
        words = new HashMap<>(obj.size());
        obj.forEach(member ->
                words.putIfAbsent(
                        member.getName(),
                        member.getValue().asString()
                )
        );
    }

}
