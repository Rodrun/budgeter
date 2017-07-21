import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sun.rmi.runtime.Log;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handle save file reading and writing. All public fields are used by save to
 * either modify (read) or to be used (write).
 */
public class Save {

    public double budget = 0;
    public String[] types = BudgetRow.types.toArray(
            new String[BudgetRow.types.size()]);
    public Vector<BudgetRow> fixedRows;
    public Vector<BudgetRow> variableRows;

    private File saveFile;

    private static final String EXPENSETYPE_FIXED = "fix";
    private static final String EXPENSETYPE_VARIABLE = "var";
    private static final String SEPARATOR = "\t";

    public static final String EXTENSION = "mbf";
    public static final FileNameExtensionFilter FILTER =
            new FileNameExtensionFilter("Monthly Budget File",
                    EXTENSION);

    /**
     * Initialize a save with given path.
     * @param path Location to file (does not need to exist if writing).
     * @throws NullPointerException If path is null.
     */
    public Save(String path) throws NullPointerException {
        setFilePath(path);
    }

    /**
     * Initialize a save with a given file.
     * @param f Save file.
     * @throws NullPointerException If file is null.
     */
    public Save(File f) throws NullPointerException {
        setFile(f);
    }

    /**
     * Initialize a save with no definite path. Highly recommended to call
     * setFile() or setFilePath() afterwards before reading or writing in order
     * to have a valid save file.
     */
    public Save() {
        setFilePath("");
    }

    /**
     * Write save to file.
     * @throws IOException Thrown by FileWriter.
     */
    public void writeSave() throws IOException {
        Logger.getAnonymousLogger().log(Level.INFO, "Writing save...");
        // SEE: SaveFormat.txt
        StringBuilder b = new StringBuilder();
        // First section: $budget
        writeLine(b, Double.toString(budget));
        // Second section: types
        for (String type : types) {
            // Ensure that there are no separator characters in each type
            b.append(type.replaceAll(SEPARATOR, "") + SEPARATOR);
        }
        b.append(System.lineSeparator());

        // Third section: budget rows
        Logger.getAnonymousLogger().log(Level.INFO, "Writing budget rows...");
        Vector<BudgetRow>[] twoLists = new Vector[]{ fixedRows, variableRows };
        String[] twoTypes = { EXPENSETYPE_FIXED, EXPENSETYPE_VARIABLE };
        for (int v = 0; v < twoLists.length; v++) {
            // Iterate through the appropriate list
            for (int row = 0; row < twoLists[v].size(); row++) {
                //System.out.println(twoLists[v].get(row).toString());
                writeLine(b, twoTypes[v] + BudgetRow.DELIMITER +
                        twoLists[v].get(row).toString());
            }
        }

        // Finally write to file
        Logger.getAnonymousLogger().log(Level.INFO, "Writing to file...");
        try (FileWriter writer = new FileWriter(saveFile)) {
            writer.write(b.toString());
        } // Will call close() after success or fail
    }

    /**
     * Read from save file.
     */
    public void readSave() throws FileNotFoundException {
        Logger.getAnonymousLogger().log(Level.INFO, "Reading save...");
        // SEE: SaveFormat.txt
        try (Scanner scanner = new Scanner(saveFile)) {
            Vector<BudgetRow>[] rows = new Vector[]{ fixedRows, variableRows};
            String[] expTypes = new String[]{ EXPENSETYPE_FIXED,
                    EXPENSETYPE_VARIABLE };
            int section = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Empty lines should not increment the section number
                if (line.isEmpty()) {
                    continue;
                }

                switch (section) {
                    case 1: // budget amount
                        try {
                            budget = Double.valueOf(line);
                        } catch (NumberFormatException e) {
                            Logger.getAnonymousLogger().log(Level.WARNING,
                                    "'budget' could not be parsed because: " +
                                            e.getMessage());
                            // Budget must have a value! Default to 0
                            budget = 0;
                            Logger.getAnonymousLogger().log(Level.WARNING,
                                    "'budget' is set to" + budget);
                        }
                        break;
                    case 2: // types...
                        types = line.split(SEPARATOR);
                        break;
                    case 3: // budget rows...
                        String[] split = line.split(BudgetRow.DELIMITER);
                        if (split.length == 5) {
                            // Determine budget type
                            for (int et = 0; et < expTypes.length; et++) {
                                if (split[0] == expTypes[et]) {
                                    // Remove 1st token and add row
                                    rows[et].add(BudgetRow.readLine(
                                            line.replaceFirst(expTypes[et] +
                                                    BudgetRow.DELIMITER, "")
                                    ));
                                }
                            }
                        }
                        break;
                }
            }
        } catch (FileNotFoundException fex){
            throw fex;
        }

        // Ensure that there are no null budget rows
        if (fixedRows == null) {
            Logger.getAnonymousLogger().log(Level.INFO, "fixedRows = null.");
            fixedRows = new Vector<>();
        }
        if (variableRows == null) {
            Logger.getAnonymousLogger().log(Level.INFO, "variableRows = null.");
            variableRows = new Vector<>();
        }
    }

    /**
     * Set the file path of the save file.
     * @param path File path.
     * @throws NullPointerException If path is null.
     */
    public void setFilePath(String path) throws NullPointerException{
        if (path == null) {
            throw new NullPointerException("Save path cannot be null.");
        }
        setFile(new File(path));
    }

    /**
     * Set the save file.
     * @param f Save file.
     * @throws NullPointerException If file is null.
     */
    public void setFile(File f) throws NullPointerException {
        if (f == null) {
            throw new NullPointerException("Save file cannot be null.");
        }
        saveFile = f;
        Logger.getAnonymousLogger().log(Level.INFO, "Save path set to " +
            f.getPath());
    }

    /**
     * Write a line with a line separator at the end.
     * @param b
     * @param out
     */
    private void writeLine(StringBuilder b, String out) {
        b.append(out + System.lineSeparator());
    }

    /**
     * Get the file path of the save file.
     * @return File path.
     */
    public String getPath() {
        return saveFile.getPath();
    }

    /**
     * Get the extension of the file. This gets the substring from after the
     * last occurrence of '.' to the end of the file name.
     * @param f File.
     * @return The file extension, will return "" if file is null or no
     * extension is found.
     */
    public static String getExtension(File f) {
        if (f != null) {
            String name = f.getName();
            int index = name.lastIndexOf('.');
            if (index != -1 && index != 0) {
                return name.substring(index + 1);
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

}
