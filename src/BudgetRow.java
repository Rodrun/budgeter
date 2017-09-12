import com.sun.istack.internal.NotNull;

import java.text.MessageFormat;

/**
 * A single row in a budget list.
 * NOTE: Every object in the row is a String!
 */
public class BudgetRow {

    private String date;
    private String category;
    private String name;
    private String money;


    /**
     * Amount of tokens a line should have. This is how many parameters a
     * BudgetRow takes.
     */
    private static final int SPLIT_COUNT = 4;

    /**
     * Line delimiter (used to split separate tokens).
     */
    public static final String DELIMITER = "\t";

    /**
     * Create a new budget row.
     * @param date Date in 'Month-Day' format.
     * @param category BudgetRow category.
     * @param name Given name.
     * @param money Money added/subtracted from available.
     */
    public BudgetRow(String date, String category, String name, String money) {
        this.date = date;
        this.category = category;
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
        return new Object[]{ date, category, name, money };
    }

    @Override
    public String toString() {
        // Create a formatted string that has all objects separated by delimiter
        return new MessageFormat(
                "{1}{0}{2}{0}{3}{0}{4}").format(
                        new Object[]{ DELIMITER, date, category, name, money}
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
            case 1: // String category
                category = String.valueOf(val);
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

    public int getDay() {
        return FormattedDate.getFormattedDateValues(
                getDate())[FormattedDate.DAY_INDEX];
    }

    public String getCategory() {
        return category;
    }

    public String getMoney() {
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
     * @throws IllegalArgumentException If line split count is invalid.
     */
    @NotNull
    public static BudgetRow readLine(String line)
            throws NullPointerException, IllegalArgumentException {
        if (line == null) {
            throw new NullPointerException("Line may not be null.");
        }
        String[] split = line.split(DELIMITER);
        if (split.length != 4) { // CHANGE VALUE IF TOKEN SIZE CHANGES!!!
            throw new IllegalArgumentException("Line has insufficient tokens");
        }
        return new BudgetRow(split);
    }

    /**
     * Get the day of the formatted date string.
     * @deprecated Use <code>getDay()</code> from a BudgetRow object.
     * @param fdate Formatted date string.
     * @return Integer of the day. Will return 1 if fdate is not in proper
     * format.
     */
    @Deprecated
    public static int getDay(String fdate) throws
            NumberFormatException {
        String[] split = fdate.split(" ");
        return (split.length != 2) ? 1 : Integer.valueOf(split[1]);
    }

}
