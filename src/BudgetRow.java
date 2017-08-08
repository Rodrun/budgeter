import com.sun.istack.internal.NotNull;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.TableView;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Vector;

/**
 * A single row in a budget list.
 * NOTE: Every object in the row is a String!
 */
public class BudgetRow {

    private String date;
    private String type;
    private String name;
    private String money;

    /**
     * Line delimiter (used to split separate tokens).
     */
    public static final String DELIMITER = "\t";

    /**
     * Create a new budget row.
     * @param date Date in 'Month-Day' format.
     * @param type BudgetRow type.
     * @param name Given name.
     * @param money Money added/subtracted from available.
     */
    public BudgetRow(String date, String type, String name, String money) {
        this.date = date;
        this.type = type;
        this.name = name;
        this.money = String.valueOf(money);
    }

    /**
     * Create an empty budget row.
     */
    public BudgetRow() {
        date = FormattedDate.getFormattedToday();
        name = "New";
        money = "0";
    }

    /**
     * Create a new budget row using an array of strings with data.
     * @param fields Array of strings with data, will use first 4 strings.
     */
    public BudgetRow(String[] fields) {
        for (int i = 0; i < fields.length; i++) {
            setProperField(i, fields[i]);
        }
    }

    /**
     * Get the objects of the row.
     * @return Row Objects, every object is a String.
     */
    public Object[] getRowData() {
        return new Object[]{ date, type, name, money };
    }

    @Override
    public String toString() {
        // Create a formatted string that has all objects separated by delimiter
        return new MessageFormat(
                "{1}{0}{2}{0}{3}{0}{4}").format(
                        new Object[]{ DELIMITER, date, type, name, money}
                        );
    }

    /**
     * When reading an array of String values for a budget, set each value
     * properly.
     * @param index
     * @param val
     */
    public void setProperField(int index, Object val) {
        switch (index) {
            case 0: // String date
                date = String.valueOf(val);
                break;
            case 1: // String type
                type = String.valueOf(val);
                break;
            case 2: // String name
                name = String.valueOf(val);
                break;
            case 3: // double money
                money = String.valueOf(val);
                break;
        }
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getMoneyString() {
        return money;
    }

    public double getMoneyValue() throws NumberFormatException {
        return Double.valueOf(money);
    }

    /**
     * Parse a string line containing budget data.
     * @param line String containing one budget row.
     * @return A new BudgetRow.
     * @throws NullPointerException If line is null.
     */
    @NotNull
    public static BudgetRow readLine(String line) throws NullPointerException {
        if (line == null) {
            throw new NullPointerException("Line may not be null.");
        }
        return new BudgetRow(line.split(BudgetRow.DELIMITER));
    }

    /**
     * For use of sorting purposes. Will remove the '-' from the formatted
     * date string and will return the month and day as an integer.
     * @param fdate Formatted date string.
     * @return Integer of month and day together.
     */
    public static int convertDateToInt(String fdate) {
        String[] split = fdate.split("-");
        if (!(split.length < 2)) { // day and month
            return Integer.valueOf(split[0] +  split[1]);
        }
        return 11;
    }

}
