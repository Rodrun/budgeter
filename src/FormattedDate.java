import java.util.Calendar;
import java.util.Vector;
import java.util.stream.IntStream;

/**
 * Get relevant calendar information.
 */
public final class FormattedDate {

    /**
     * For String.format(...).
     * Date should be MONTH-DAY.
     */
    public static final String FORMAT_STRING = "%d-%d";

    private FormattedDate() { }

    /**
     * Properly format a date.
     * @param month
     * @param day
     * @return Formatted date.
     */
    public static String dateFormat(int month, int day) {
        return String.format(FORMAT_STRING, month, day);
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
     * Get the current day of the month.
     * @return Day of month.
     */
    public static int getDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
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
}
