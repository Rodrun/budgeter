import java.time.Month;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

/**
 * Get relevant calendar information.
 */
public final class FormattedDate {

    /**
     * For String.format(...).
     * Date should be in 'MONTH DAY' format, Ex: "February 18".
     */
    public static final String FORMAT_STRING = "%s %d";
    /**
     * Index of the month value from <code>getFormattedDateValues()</code>.
     */
    public static final int MONTH_INDEX = 0;
    /**
     * Index of the day value from <code>getFormattedDateValues()</code>.
     */
    public static final int DAY_INDEX = 1;

    private FormattedDate() { }

    /**
     * Properly format a date.
     * @param month
     * @param day
     * @return Formatted date.
     */
    public static String dateFormat(int month, int day) {
        return String.format(FORMAT_STRING, getMonthName(month), day);
    }

    /**
     * Get the properly formatted (MONTH-DAY) date of today.
     * @return String containing the month and day.
     */
    public static String getFormattedToday() {
        return dateFormat(getMonth(), getDay());
    }

    /**
     * Get the current month.
     * @return Current month.
     */
    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    /**
     * Get month value from string.
     * @param month Month string.
     * @return Integer value of the month.
     * @throws IllegalArgumentException If no value is found for the string.
     */
    public static int getMonthValue(String month)
            throws IllegalArgumentException {
        return Month.valueOf(month.trim().toUpperCase()).getValue();
    }

    /**
     * Get the name of the given month.
     * @param i Month number.
     * @return Full name of the current month.
     */
    public static String getMonthName(int i) {
        Month m = Month.of(i);
        return m.getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    /**
     * Get the current day of the month.
     * @return Day of month.
     */
    public static int getDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get the current year.
     * @return Current year.
     */
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * Get the last day of the current month.
     * @return Last day of the month.
     */
    public static int getMaxMonthDays() {
        return Calendar.getInstance().getMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * List every day in the current month, expressed as integers.
     * @return Vector of days of the month, as integers.
     */
    public static Vector<Integer> getDaysOfMonthInt() {
        Vector<Integer> ret = new Vector<>();
        for (int day = 0; day < getMaxMonthDays(); day++) {
            ret.add(day + 1);
        }
        return ret;
    }

    /**
     * List every day in the current month, formatted.
     * @return Vector of days of the month, formatted.
     */
    public static Vector<String> getDaysOfMonth() {
        Vector<String> ret = new Vector<>();
        for (Integer day : getDaysOfMonthInt()) {
            ret.add(dateFormat(getMonth(), day));
        }
        return ret;
    }

    /**
     * Get the values from a formatted date string.
     * @param fdate Formatted date string.
     * @return Array containing [month, day] expressed as integers.
     * @throws IllegalArgumentException If fdate is not properly formatted.
     */
    public static int[] getFormattedDateValues(String fdate)
        throws IllegalArgumentException {
        String[] split = fdate.split(" ");

        // Check if meets criteria of formatted date
        if (split.length < 2) {
            throw new IllegalArgumentException("fdate length < 2.");
        } else { // Meets format criteria so far...
            int day, month;
            try {
                day = Integer.valueOf(split[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "fdate not formatted correctly: could not parse day.");
            }

            month = getMonthValue(split[0]); // Throws if invalid
            return new int[]{ month, day };
        }
    }

}
