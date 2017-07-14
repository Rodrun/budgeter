import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The add panel handles the addition of a new field to the budget list.
 */
public class AddPanel extends JPanel {

    /**
     * Default selection of plus minus combo box.
     */
    private static final String DEFAULT_PLUSMINUS = "-";
    /**
     * Default text value for the money text field.
     */
    private static final String DEFAULT_MONEYFIELD = "0.00";

    private int day;
    private String plusMinus;
    private String type;
    private String name;
    private double money;
    private JComboBox<Integer> daysBox;
    private JComboBox<String> plusMinusBox;
    private JComboBox<String> typeBox;
    private JTextField nameField;
    private JTextField moneyField;
    private JButton addButton;
    private Tab[] tabs;
    private int tabIndex = 0;

    /**
     * Initialize the add panel, which handles adding a row to a BudgetList.
     * @param tabs The tabs of the main tabbed pane.
     */
    public AddPanel(Tab[] tabs, Vector<String> types) {
        // Item init
        daysBox = new JComboBox<>(FormattedDate.getDaysOfMonthInt());
        daysBox.setSelectedIndex(FormattedDate.getDay() - 1);
        plusMinusBox = new JComboBox<>(new String[]{ "-", "+" });
        plusMinusBox.setSelectedItem(DEFAULT_PLUSMINUS);
        plusMinusBox.setToolTipText("Subtracting/adding from budget");
        typeBox = new JComboBox<>(types);
        typeBox.setToolTipText("Categorize");
        nameField = new JTextField();
        nameField.setColumns(16);
        moneyField = new JTextField(DEFAULT_MONEYFIELD);
        moneyField.setColumns(6);

        this.tabs = tabs;
        addButton = new JButton("Add");
        addButton.addActionListener(event -> {
            day = daysBox.getSelectedIndex() + 1;
            plusMinus = plusMinusBox.getSelectedItem().toString();
            type = getSelectedType();
            name = getNameFromField();
            money = getMoneyFromField();
            tabs[tabIndex].budgetList.addBudget(new BudgetRow(
                    FormattedDate.dateFormat(FormattedDate.getMonth(), day),
                    type,
                    name,
                    String.valueOf(money)));
            // Lastly, reset fields
            //setDayToToday();
            typeBox.setSelectedIndex(0);
            nameField.setText("");
            plusMinusBox.setSelectedItem(DEFAULT_PLUSMINUS);
            moneyField.setText(DEFAULT_MONEYFIELD);
        });

        // Layout manager
        this.setLayout(new FlowLayout());
        this.add(new JLabel("Day:"));
        this.add(daysBox);
        this.add(new JLabel("Type:"));
        this.add(typeBox);
        this.add(new JLabel("Name:"));
        this.add(nameField);
        this.add(new JLabel("-/+:"));
        this.add(plusMinusBox);
        this.add(new JLabel("$:"));
        this.add(moneyField);
        this.add(addButton);
    }

    /*public void addButtonListener(ActionListener al) {
        addButton.addActionListener(al);
    }*/

    @Override
    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
        addButton.setEnabled(flag);
    }

    /**
     * Set the active tab. If value is < 0, will set to 0, or if value is
     * >= tabs.length, will set to tabs.length-1.
     * @param index Index of tab.
     */
    public void setSelectedTab(int index) {
        if (index >= tabs.length) {
            tabIndex = tabs.length - 1;
        } else if (index < 0) {
            tabIndex = 0;
        } else {
            tabIndex = index;
        }
        Logger.getAnonymousLogger().log(Level.INFO,
                "AddPanel: Tab index = " + index);
    }

    /**
     * Add a type to the list.
     * @param name Name of type.
     */
    public void addType(String name) {
        BudgetRow.types.add(name);
        typeBox.addItem(name);
    }

    /**
     * Replace the type list.
     * @param t Vector of types.
     */
    public void setTypes(Vector<String> t) {
        typeBox.removeAllItems();
        for (String item : t) {
            addType(item);
        }
    }

    /**
     * Get last selected plus/minus.
     * @return Plus or minus string.
     */
    private String getPlusMinus() {
        return plusMinus;
    }

    /**
     * Get the double value from the money text field.
     * @return 0.0 If no valid value is available, otherwise the input value.
     */
    private double getMoneyFromField() {
        double val = 0.0;
        try {
            val = Double.valueOf(moneyField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return (getPlusMinus() == "+") ? val : -val;
    }

    /**
     * Get the input from the name text field.
     * @return Name string.
     */
    private String getNameFromField() {
        return nameField.getText();
    }

    /**
     * Get the selected string from the type combo box.
     * @return Type string.
     */
    private String getSelectedType() {
        return (String) typeBox.getSelectedItem();
    }

    /**
     * Set the selection of the days combo box to today.
     */
    private void setDayToToday() {
        daysBox.setSelectedIndex(FormattedDate.getDay() - 1);
    }

}
