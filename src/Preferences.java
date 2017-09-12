import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.*;
import java.nio.file.NoSuchFileException;

import static java.util.logging.Logger.getAnonymousLogger;

/**
 * Read a preference file and store its values.
 */
public final class Preferences {

    /**
     * Object that stores all preference values.
     */
    private JsonObject object;
    private String path;
    private boolean hasRead = false;

    /**
     * Creates a Preference file. If a preferences file does not exist, will
     * create a default file and attempt to read it.
     * @param path Path of the preferences.json file.
     * @throws IOException If file fails to open.
     * @throws NoSuchFileException If preferences file does not exist.
     */
    public Preferences(String path) throws IOException, NullPointerException {
        this.path = path;
        getAnonymousLogger().info("Reading preference file at " + path);
        object = readJSON(path);
        if (object == null) {
            throw new NullPointerException("JsonObject is null after read.");
        }
    }

    private Preferences() { }

    /**
     * Read a preference file and parse the JSON data.
     * @param path Path of the JSON file.
     * @return JsonObject of parsed JSON, or null if file does not exist.
     * @throws IOException If I/O error occurs when opening the file.
     */
    private JsonObject readJSON(String path) throws IOException {
        // Open file
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        // Parse JSON
        return Json.parse(new FileReader(file)).asObject();
    }

    /**
     * Get a preference value.
     * @param name Name of value.
     * @return Retrieved JsonValue, or null if value not found.
     */
    public JsonValue get(String name) {
        return object.get(name);
    }

    /**
     * Set a member value for an EXISTING member.
     * @param name Name of member.
     * @param val Value to set.
     * @return Given value if member exists, null otherwise.
     *
    public JsonValue set(String name, JsonValue val) {
        if (object.get(name) != null) {
            object.set(name, val);
            getAnonymousLogger().info(
                    "Set member '" + name + "' to '" + val.toString() + "'");
            return val;
        }
        return null;
    }*/

    /**
     * Create a default preferences file
     * @param path Path to write file.
     * @throws IOException If file cannot be written to.
     */
    public static void createPreferencesFile(String path) throws IOException {
        getAnonymousLogger().info("Writing new preferences file to " + path);
        File file = new File(path);
        JsonObject toWrite = new JsonObject();
        toWrite.add("language dir", "resource/lang");
        toWrite.add("language", "English");
        toWrite.add(
                "default categories",
                Json.array(
                        "Misc.",
                        "Rent",
                        "Utility",
                        "Clothing",
                        "Food",
                        "Loan",
                        "Insurance",
                        "Internet/phone"
                )
        );
        // Finally, just write it all
        toWrite.writeTo(new FileWriter(file));
    }

    @Override
    public String toString() {
        return object.toString();
    }

    /**
     * Get the path of the preferences file.
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Convert JsonValue array to String array if the values are Strings.
     * @param arr
     * @return
     */
    public static String[] getStringArray(JsonArray arr) {
        String[] ret = new String[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            ret[i] = arr.get(i).asString();
        }
        return ret;
    }

    /**
     * Add a file separator between every given string.
     * @param parts Strings to use.
     * @return A String with file separators to make a file path. If
     * <code>parts.length = 0</code>, will return <code>parts[0]</code>.
     *
    public static String separated(String... parts) {
        StringBuilder string = new StringBuilder();
        if (parts.length == 1) {
            return parts[0];
        }
        for (int index = 0; index < parts.length - 1; index++) {
            string.append(parts[index]);
            string.append(File.separator);
        }
        return string.toString();
    }*/

}
