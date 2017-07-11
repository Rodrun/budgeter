import javax.swing.*;
import java.awt.*;

/**
 * Information panel.
 */
public class InfoPanel extends JPanel {

    private double budget;
    private double moneySpent;
    private double moneyLeft;
    private JTextField budgetField;
    private JLabel spentLabel;
    private JLabel moneyLeftLabel;

    public InfoPanel(double budget, double moneySpent, double moneyLeft) {
        this.budget = budget;
        this.moneySpent = moneySpent;
        this.moneyLeft = moneyLeft;

        budgetField = new JTextField("0.00", 8);
        spentLabel = new JLabel("- $0.00"); // after budget
        moneyLeftLabel = new JLabel(" = $0.00");

        budgetField.setToolTipText("Your monthly net income");
        spentLabel.setToolTipText("Expenses");
        moneyLeftLabel.setToolTipText("Money left after expenses");
        budgetField.addActionListener(event -> {
            String val = budgetField.getText().trim().replaceAll("[^0-9+.-]",
                    "");
            try {
                this.budget = Double.valueOf(val);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Error in budget: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            budgetField.setText(val);
            setMoneyLeft(this.budget, this.moneySpent);
        });

        this.setLayout(new FlowLayout());
        this.add(new JLabel("Budget: $"));
        this.add(budgetField);
        this.add(spentLabel);
        this.add(moneyLeftLabel);
    }

    /**
     * Update the money left label in the info panel.
     * @param budget
     * @param spent
     */
    public void setMoneyLeft(double budget, double spent) {
        moneyLeft = budget - spent;
        moneyLeftLabel.setText(" = $" + String.format("%.02f", moneyLeft));
    }

    /**
     * Update the money spent label in the info panel appropriately.
     * @param spent
     */
    public void setMoneySpent(double spent) {
        moneySpent = spent;
        spentLabel.setText("- $" + String.format("%.02f", spent));
    }
}
