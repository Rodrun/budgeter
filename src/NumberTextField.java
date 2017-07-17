import javax.swing.*;

/**
 * A JTextField that reads numerical values.
 */
public class NumberTextField extends JTextField {

    /**
     * The regular expression used to exclude non-numerical characters.
     */
    private static final String REGEX = "[^0-9+.-]";

    private String numberFormat = "%.02f";

    /**
     * Initialize a NumberTextField with the text set to a numerical value and
     * set the amount of column
     * @param initialValue The value to set the text to.
     * @param trailing Number of zeros to have after the decimal point. Ex:
     *                 2 would result in 0.00, and 4 would result in 0.0000
     */
    public NumberTextField(double initialValue, int trailing) {
        setNumber(initialValue);
        setTrailingZeroes(trailing);
    }

    /**
     * Initialize a NumberTextField with the text set to a numerical value.
     * @param initialValue The value to set the text to.
     */
    public NumberTextField(double initialValue) {
        setNumber(initialValue);
    }

    /**
     * Get the value of the text field.
     * @return Value in the text field.
     * @throws NumberFormatException If text does not have a readable number.
     */
    public double getNumber() throws NumberFormatException {
        return Double.valueOf(getText().trim().replaceAll(REGEX, ""));
    }

    /**
     * Set the text to the given value.
     * @param number Value to set text.
     */
    public void setNumber(double number) {
        setText(String.format("%.02f", number));
    }

    /**
     * Remove all non-numerical (excluding '+', '-', and '.') characters from
     * the text field.
     */
    public void clean() {
        // TODO: Find out if trim() even necessary?
        setText(getText().trim().replaceAll(REGEX, ""));
    }

    /**
     * Set the amount of zeroes to use after the decimal point.
     * @param amount The number of trailing zeroes, will use absolute value.
     */
    public void setTrailingZeroes(int amount) {
        numberFormat = "%.0xf".replaceAll("x",
                Integer.toString(Math.abs(amount)));
    }
}
