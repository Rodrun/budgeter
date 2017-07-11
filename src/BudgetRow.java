import com.sun.istack.internal.NotNull;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.Calendar;

/**
 * A single row in a budget list.
 */
public class BudgetRow {

    public String date;
    public String type;
    public String name;
    public double money;

    public static JComboBox<String> types;
    public static final String DELIMITER = "\t";

    /**
     * Create a new budget row.
     * @param date Date in 'Month-Day' format.
     * @param type BudgetRow type.
     * @param name Given name.
     * @param money Money added/subtracted from available.
     */
    public BudgetRow(String date, String type, String name, double money) {
        this.date = date;
        this.type = type;
        this.name = name;
        this.money = money;
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
     * Create an empty budget row.
     */
    public BudgetRow() {
        date = BudgetRow.getFormattedDate(
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                Calendar.getInstance().get(Calendar.MONTH));
        money = 0.00;
    }

    public Object[] getRowData() {
        types.setSelectedItem(type);
        return new Object[]{ date, types, name, money};
    }

    @Override
    public String toString() {
        // Create a formatted string that has all objects separated by delimiter
        return new MessageFormat("{1}{0}{2}{0}{3}{0}{4}").format(
                new Object[]{ DELIMITER, date, type, name, money});
    }

    /**
     * When reading an array of String values for a budget, set each value
     * properly.
     * @param index
     * @param val
     */
    private void setProperField(int index, String val) {
        switch (index) {
            case 0: // String date
                date = val;
                break;
            case 1: // String type
                type = val;
                break;
            case 2: // String name
                name = val;
                break;
            case 3: // double money
                money = Double.parseDouble(val);
                break;
        }
    }

    /**
     * Parse a string line containing budget data.
     * @param line String containing one budget row.
     * @return BudgetRow created fro
     */
    @NotNull
    public static BudgetRow readLine(String line) {
        return new BudgetRow(line.split(BudgetRow.DELIMITER));
    }

    /**
     * Format a date with only day and month.
     * @param day Day of month.
     * @param month Month.
     * @return Formatted date string in "DAY-MONTH" format.
     */
    public static String getFormattedDate(int day, int month) {
        return "" + day + "-" + month;
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
        return 0;
    }
}
