import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The add panel handles the addition of a new field to the budget list.
 */
public class AddPanel extends JPanel {

    /**
     * Default selection of plus minus combo box.
     */
    private static final String DEFAULT_PLUSMINUS = "-";
    /**
     * Default value for the money text field.
     */
    private static final double DEFAULT_MONEYFIELD = 0;

    private TypesList types;
    private JComboBox<Integer> daysBox;
    private JComboBox<String> plusMinusBox;
    private JComboBox<String> typeBox;
    private JTextField nameField;
    private NumberTextField moneyField;
    private JButton addButton;

    /**
     * Create an AddPanel.
     * @param types TypesList list.
     */
    public AddPanel(TypesList types) {
        this.types = types;
        // Item init
        daysBox = new JComboBox<>(FormattedDate.getDaysOfMonthInt());
        daysBox.setSelectedIndex(FormattedDate.getDay() - 1);
        plusMinusBox = new JComboBox<>(new String[]{ "-", "+" });
        plusMinusBox.setSelectedItem(DEFAULT_PLUSMINUS);
        plusMinusBox.setToolTipText("Subtracting/adding from budget");
        typeBox = new JComboBox<>(types.toArray());
        typeBox.setToolTipText("Categorize");
        nameField = new JTextField();
        nameField.setColumns(16);
        moneyField = new NumberTextField(DEFAULT_MONEYFIELD);
        moneyField.setColumns(6);
        // Clear text field on click
        moneyField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                moneyField.setText("");
            }
        });

        Budget.types.addTypesChangeListener(() -> {
            typeBox.setModel(new DefaultComboBoxModel<>(
                    Budget.types.toArray()));
        });

        //this.tabs = tabs;
        addButton = new JButton("Add");

        // Layout manager
        this.setLayout(new FlowLayout());
        this.add(new JLabel("Day:"));
        this.add(daysBox);
        this.add(new JLabel("TypesList:"));
        this.add(typeBox);
        this.add(new JLabel("Name:"));
        this.add(nameField);
        this.add(new JLabel("-/+:"));
        this.add(plusMinusBox);
        this.add(new JLabel("$:"));
        this.add(moneyField);
        this.add(addButton);
    }

    @Override
    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
        moneyField.setEnabled(flag);
        addButton.setEnabled(flag);
    }

    /**
     * Reset all the input fields to their original states.
     */
    public void resetFields() {
        setDayToToday();
        nameField.setText("");
        plusMinusBox.setSelectedItem(DEFAULT_PLUSMINUS);
        moneyField.setNumber(DEFAULT_MONEYFIELD);
    }

    /**
     * Get selected plus/minus.
     * @return Plus or minus string.
     */
    public String getPlusMinus() {
        return (plusMinusBox.getSelectedItem() == "-") ? "-" : "+";
    }

    /**
     * Get selected day.
     * @return Selected day string.
     */
    public String getSelectedDay() {
        return String.valueOf(daysBox.getSelectedItem());
    }

    /**
     * Get the double value from the money text field.
     * @return 0.0 If no valid value is available, otherwise the input value.
     */
    public String getMoneyFromField() {
        return moneyField.getText();
    }

    /**
     * Get ACTUAL money value inputted (give negative or positive values).
     * @return Real money value submitted.
     */
    public double getMoney() {
        double money = Double.valueOf(getMoneyFromField());
        return (getPlusMinus() == "-") ? -money : money;
    }

    /**
     * Get the input from the name text field.
     * @return Name string.
     */
    public String getNameFromField() {
        return nameField.getText();
    }

    /**
     * Get the selected string from the type combo box.
     * @return TypesList string.
     */
    public String getSelectedType() {
        return (String) typeBox.getSelectedItem();
    }

    /**
     * Set the selection of the days combo box to today.
     */
    public void setDayToToday() {
        daysBox.setSelectedIndex(FormattedDate.getDay() - 1);
    }

    /**
     * Add an action listener to the add button and to the money text field.
     * @param l ActionListener.
     */
    public void addActionListener(ActionListener l) {
        addButton.addActionListener(l);
        moneyField.addActionListener(l);
    }

}
