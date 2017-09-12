import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;

import static java.util.logging.Logger.getAnonymousLogger;

/**
 * Handle save file reading and writing. All public fields are used by save to
 * either modify (read) or to be used (write).
 */
public class Save {

    private File saveFile;

    private static final String DELIMITER = BudgetRow.DELIMITER;

    public static final String EXTENSION = "mbf";
    public static final FileNameExtensionFilter FILTER =
            new FileNameExtensionFilter("Monthly BudgetHandler File",
                    EXTENSION);

    /**
     * Initialize a save with given path.
     * @param path Location to file (does not need to exist if writing).
     * @throws NullPointerException If path is null.
     */
    public Save(String path) throws NullPointerException {
        setPath(path);
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
     * setFile() or setPath() afterwards before reading or writing in order
     * to have a valid save file.
     */
    public Save() {
        this("");
    }

    /**
     * Write save to file.
     * @throws IOException Thrown by FileWriter.
     */
    public void writeSave(BudgetHandler budgetHandler) throws IOException {
        getAnonymousLogger().log(Level.INFO, "Writing save...");
        // SEE: SaveFormat.md
        StringBuilder b = new StringBuilder();
        // First section: $budgetHandler
        writeLine(b, Double.toString(budgetHandler.getBudget()));
        // Second section: types
        for (String type : budgetHandler.getCategories().getList()) {
            // Ensure that there are no separator characters in each type
            b.append(type.replaceAll(DELIMITER, "") + DELIMITER);
        }
        b.append(System.lineSeparator());

        // Third section: budgetHandler rows
        getAnonymousLogger().log(Level.INFO, "Writing budgetHandler rows...");
        for (BudgetHandler.Which which : BudgetHandler.Which.values()) {
            // Iterate through the appropriate list
            for (BudgetRow row : budgetHandler.getBudgetRows(which)) {
                writeLine(b, which.getName() + DELIMITER +
                        row.toString());
            }
        }

        // Finally write to file
        getAnonymousLogger().log(Level.INFO, "Writing to file...");
        try (FileWriter writer = new FileWriter(saveFile)) {
            writer.write(b.toString());
        } // Will call close() after success or fail
        getAnonymousLogger().log(Level.INFO, "Writing done.");
    }

    /**
     * Read from save file.
     * @param defaultCats Default category list.
     * @param t Translator to use.
     * @return Parsed budget, however, will return null if save file is null.
     */
    public BudgetHandler readSave(String[] defaultCats, Translator t) throws
            FileNotFoundException {
        if (saveFile == null) {
            return null;
        }

        BudgetHandler budgetHandler = new BudgetHandler(defaultCats, t);
        getAnonymousLogger().info("Reading save...");
        // SEE: SaveFormat.md
        try (Scanner scanner = new Scanner(saveFile)) {
            /**
             * Determine what to look for depending on the section.
             */
            int section = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Empty lines should not increment the section number
                System.out.println(line);
                if (line.trim().isEmpty()) {
                    continue;
                } else if (section != 3) {
                    // Section 3 may consist of multiple lines
                    section++;
                }

                switch (section) {
                    case 1: // budget amount
                        try {
                            budgetHandler.setBudget(Double.valueOf(line));
                        } catch (NumberFormatException e) {
                            getAnonymousLogger().log( Level.WARNING,
                                    "BudgetHandler couldn't be parsed because "
                                            + e.getMessage());
                            // Must have a value! Default to 0
                            budgetHandler.setBudget(0);
                        }
                        break;
                    case 2: // types...
                        budgetHandler.getCategories().setList(
                                line.split(DELIMITER)
                        );
                        break;
                    case 3: // budgetHandler rows...
                        String[] split = line.split(DELIMITER);
                        if (split.length >= 5) {
                            // Determine budgetHandler type
                            for (BudgetHandler.Which which :
                                    BudgetHandler.Which.values()) {
                                if (split[0].compareTo(which.getName()) == 0) {
                                    // Remove 1st token and add row
                                    budgetHandler.addBudget(which,
                                            BudgetRow.readLine(
                                                    line.replaceFirst(
                                                            which.getName() +
                                                                    DELIMITER,
                                                            "")
                                            )
                                    );
                                    System.out.println("ADDED");
                                    break; // Done with the line
                                }
                            }
                        }
                        break;
                }
            }
        } catch (FileNotFoundException fex) {
            throw fex;
        }
        return budgetHandler;
    }

    /**
     * Set the file path of the save file.
     * @param path File path.
     * @throws NullPointerException If path is null.
     */
    public void setPath(String path) throws NullPointerException{
        setFile(new File(path));
    }

    /**
     * Set the save file.
     * @param f Save file.
     * @throws NullPointerException If file is null.
     */
    public void setFile(File f) throws NullPointerException {
        saveFile = f;
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
     * Get the save file.
     * @return The save file to get.
     */
    public File getFile() {
        return saveFile;
    }

    /**
     * Check if save file is null.
     * @return True if save file is null, false otherwise.
     */
    public boolean isNull() {
        return saveFile == null;
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
