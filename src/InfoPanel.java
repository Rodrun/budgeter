import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Information panel.
 */
public class InfoPanel extends JPanel {

    public static final String POSITIVE = "+";
    public static final String NEGATIVE = "-";

    private NumberTextField budgetField;
    private JLabel spentLabel;
    private JLabel moneyLeftLabel;
    private String sign;

    public InfoPanel(BudgetHandler budgetHandler) {
        budgetField = new NumberTextField(budgetHandler.getBudget(), 2);
        spentLabel = new JLabel("- $0.00"); // after budget
        moneyLeftLabel = new JLabel(" = $0.00");

        budgetField.setColumns(8);
        budgetField.setToolTipText("Your monthly net income");
        spentLabel.setToolTipText("Expenses");
        moneyLeftLabel.setToolTipText("Money left after expenses");

        budgetHandler.addBudgetEventListener(() -> {
            // Set positive sign if spent is negative
            setSign((budgetHandler.getMoneySpent() < 0) ?
                POSITIVE : NEGATIVE);

            setMoneySpent(Math.abs(budgetHandler.getMoneySpent()));
            setMoneyLeft(budgetHandler.getRemainingBudget());
        });

        // Update budget when user enters value
        addActionListener(e -> {
            double bud = budgetHandler.getBudget();
            try {
                bud = getBudget();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error in budget: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                budgetHandler.setBudget(bud);
                setBudgetField(budgetHandler.getBudget());
            }
        });

        this.setLayout(new FlowLayout());
        this.add(new JLabel("BudgetHandler: $"));
        this.add(budgetField);
        this.add(spentLabel);
        this.add(moneyLeftLabel);
    }

    /**
     * Set the sign of the budget "math" (The sign shown before the expenses).
     * @param sign POSITIVE or NEGATIVE sign.
     */
    public void setSign(String sign) {
        if (sign == POSITIVE || sign == NEGATIVE) {
            this.sign = sign;
        }
    }

    /**
     * Set the number of the budget text field.
     * @param n Number value.
     */
    public void setBudgetField(double n) {
        budgetField.setNumber(n);
    }

    /**
     * Get budget value inputted.
     * @return BudgetHandler value.
     */
    public double getBudget() throws NumberFormatException{
        return budgetField.getNumber();
    }

    /**
     * Get the sign.
     * @return The sign shown after the budget amount.
     */
    public String getSign() {
        return sign;
    }

    /**
     * Update the money left label in the info panel.
     * @param money Money left
     */
    public void setMoneyLeft(double money) {
        moneyLeftLabel.setText(" = $" + String.format("%.02f", money));
    }

    /**
     * Update the money spent label in the info panel appropriately.
     * @param spent
     */
    public void setMoneySpent(double spent) {
        spentLabel.setText(sign + " $" + String.format("%.02f", spent));
    }

    /**
     * Add an ActionListener to the text field that handles budget input.
     * @param l ActionListener.
     */
    public void addActionListener(ActionListener l) {
        budgetField.addActionListener(l);
    }

    /**
     * Reset all fields.
     */
    public void clear() {
        budgetField.setNumber(0);
        setMoneySpent(0);
        setMoneyLeft(0);
    }

}
