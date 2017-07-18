import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Information panel.
 */
public class InfoPanel extends JPanel {

    private NumberTextField budgetField;
    private JLabel spentLabel;
    private JLabel moneyLeftLabel;

    public InfoPanel(double budget, double moneySpent, double moneyLeft) {
        budgetField = new NumberTextField(budget, 2);
        spentLabel = new JLabel("- $0.00"); // after budget
        moneyLeftLabel = new JLabel(" = $0.00");

        budgetField.setColumns(8);
        budgetField.setToolTipText("Your monthly net income");
        spentLabel.setToolTipText("Expenses");
        moneyLeftLabel.setToolTipText("Money left after expenses");

        setMoneySpent(moneySpent);
        setMoneyLeft(moneyLeft);

        this.setLayout(new FlowLayout());
        this.add(new JLabel("Budget: $"));
        this.add(budgetField);
        this.add(spentLabel);
        this.add(moneyLeftLabel);
    }

    /**
     * Get budget value inputted.
     * @return Budget value.
     */
    public double getBudget() throws NumberFormatException{
        return budgetField.getNumber();
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
        spentLabel.setText("- $" + String.format("%.02f", spent));
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
