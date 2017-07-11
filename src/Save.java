import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Create and read a save.
 */
public class Save {

    public double budget;
    public String[] types;
    public BudgetRow[] rows;

    private File saveFile;

    /**
     * String that indicates the beginning of a budget chunk.
     */
    private static final String BUDGETROW_TOGGLE = "@";
    private static final char SEPARATOR = '\t';

    /**
     * Initialize a save with given path.
     * @param path Location to file (does not need to exist if writing).
     */
    public Save(String path) {
        saveFile = new File(path);
    }

    /**
     * Write save to file.
     * @throws IOException Thrown by FileWriter.
     */
    public void writeSave() throws IOException {
        StringBuilder b = new StringBuilder();
        // SEE: SaveFormat.txt
        // First section: $budget
        writeLine(b, Double.toString(budget));
        // Second section: types
        for (String type : types) {
            b.append(type + SEPARATOR);
        }
        b.append(System.lineSeparator());
        // Third section: budget rows
        writeLine(b, BUDGETROW_TOGGLE);
        for (BudgetRow row : rows) {
            writeLine(b, row.toString());
        }
        writeLine(b, BUDGETROW_TOGGLE);

        FileWriter writer = new FileWriter(saveFile);
        writer.write(b.toString());
        writer.close();
    }

    /**
     * Read from save file.
     */
    public void readSave() {
        // SEE: SaveFormat.txt

    }

    public void setFilePath(String path) {
        saveFile = new File(path);
    }

    private void writeLine(StringBuilder b, String out) {
        b.append(out + System.lineSeparator());
    }
}
