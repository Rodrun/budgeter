import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Vector;

/**
 * The add panel handles the addition of a new field to the budget list.
 */
public class AddPanel extends JPanel {

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
    public AddPanel() {
        // Item init
        Vector<Integer> daysVec = new Vector<>();
        for (int i = 1; i <= 31; i++) {
            daysVec.add(i);
        }
        daysBox = new JComboBox<>(daysVec);
        daysBox.setSelectedIndex(Calendar.getInstance().get(
                Calendar.DAY_OF_MONTH) - 1);
        plusMinusBox = new JComboBox<>(new String[]{ "-", "+" });
        plusMinusBox.setToolTipText("Specify whether subtracting/adding money");
        typeBox = new JComboBox<>();
        nameField = new JTextField();
        nameField.setColumns(16);
        moneyField = new JTextField("0.00");
        moneyField.setColumns(6);
        addButton = new JButton("Add");
        addButtonListener(event -> {
            day = daysBox.getSelectedIndex() + 1;
            plusMinus = plusMinusBox.getSelectedItem().toString();
            type = getSelectedType();
            name = getNameFromField();
            money = getMoneyFromField();
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

    public void addButtonListener(ActionListener al) {
        addButton.addActionListener(al);
    }

    /**
     * Get the last selected day.
     * @return Day selected.
     */
    public int getDay() {
        return day;
    }

    /**
     * Get last selected plus/minus.
     * @return Plus or minus string.
     */
    public String getPlusMinus() {
        return plusMinus;
    }

    /**
     * Get last selected type.
     * @return Type string.
     */
    public String getType() {
        return type;
    }

    /**
     * Get last inputted name.
     * @return Name string.
     */
    public String getBudgetName() {
        return name;
    }

    /**
     * Get last inputted money.
     * @return Money.
     */
    public double getMoney() {
        return money;
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
        return val;
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
}
