import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Create and read a save.
 */
public class Save {

    public double budget;
    public String[] types;
    public Vector<BudgetRow> fixedRows;
    public Vector<BudgetRow> variableRows;

    private File saveFile;

    /**
     * String that indicates the beginning of a budget chunk.
     */
    private static final String BUDGETROW_TOGGLE = "@";
    /**
     * String that indicates that the next lines are for variable budget rows.
     */
    private static final String BUDGETROW_SPLIT = "!";
    private static final String SEPARATOR = "\t";

    /**
     * Initialize a save with given path.
     * @param path Location to file (does not need to exist if writing).
     */
    public Save(String path) {
        setFilePath(path);
    }

    /**
     * Write save to file.
     * @throws IOException Thrown by FileWriter.
     */
    public void writeSave() throws IOException {
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
        writeLine(b, BUDGETROW_TOGGLE);
        // TODO: Possibly make this section smaller?
        // fixed
        for (BudgetRow row : fixedRows) {
            writeLine(b, row.toString());
        }
        writeLine(b, BUDGETROW_SPLIT);
        // variable
        for (BudgetRow row : variableRows) {
            writeLine(b, row.toString());
        }
        writeLine(b, BUDGETROW_TOGGLE);

        // Finally write to file
        FileWriter writer = new FileWriter(saveFile);
        writer.write(b.toString());
        writer.close();
    }

    /**
     * Read from save file.
     */
    public void readSave() throws FileNotFoundException {
        // SEE: SaveFormat.txt
        Scanner scanner = new Scanner(saveFile);
        boolean readingBudget = false;
        boolean readingFixedBudget = true;
        int section = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            section++;

            // Check for budget rows toggle
            if (line == Save.BUDGETROW_TOGGLE) {
                readingBudget = !readingBudget;
                section = 3;
            }

            if (readingBudget) { // Toggled true
                if (line == "!")
                if (readingFixedBudget) {
                    fixedRows.add(BudgetRow.readLine(line));
                } else {
                    variableRows.add(BudgetRow.readLine(line));
                }
            } else {
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
                }
            }
        }
        scanner.close();
    }

    /**
     * Set the file path of the save file.
     * @param path File path.
     */
    public void setFilePath(String path) {
        saveFile = new File(path);
    }

    /**
     * Write a line with a line separator at the end.
     * @param b
     * @param out
     */
    private void writeLine(StringBuilder b, String out) {
        b.append(out + System.lineSeparator());
    }
}
