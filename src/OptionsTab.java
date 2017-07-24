import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultXYDataset;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * The options tab. Displays visual representation of the budget and allows
 * user to set other available budgeting options.
 */
public class OptionsTab extends Tab {

    private static final String[] expTypeStrings = {
            "Both Expense TypesList",
            "Fixed Expenses",
            "Variable Expenses"
    };
    private static final String[] chartStrings = {
            "Pie Chart",
            "XY Chart"
    };

    /**
     * The JPanel that will be inside the scroll pane
     */
    private JPanel internalPanel;
    private Budget budget;
    private JLabel chartLabel;
    private JComboBox<String> chartBox, expTypeBox, typeBox;

    private OptionsTab() { }

    public OptionsTab(Budget budget) {
        super("Options", new JPanel());
        this.budget = budget;
        init();
    }

    private void init() {
        internalPanel = new JPanel();
        internalPanel.setLayout(new BoxLayout(internalPanel,
                BoxLayout.PAGE_AXIS));
        chartLabel = new JLabel();
        chartBox = new JComboBox<>(chartStrings);
        expTypeBox = new JComboBox<>(expTypeStrings);
        typeBox = new JComboBox<>(Budget.types.toArray());

        // Add ActionListeners
        class Updater implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        }
        Updater listener = new Updater();
        chartBox.addActionListener(listener);
        chartBox.addActionListener(e -> {
            // TypesList box should be disabled unless XY Chart is selected
            typeBox.setEnabled(chartBox.getSelectedItem() == chartStrings[1]);
        });
        expTypeBox.addActionListener(listener);
        typeBox.addActionListener(listener); // Enabled when XY is selected

        // Update types if they're changed
        Budget.types.addTypesChangeListener(() -> {
            typeBox.setModel(new DefaultComboBoxModel<>(
                    Budget.types.toArray()));
        });

        // The default selection is the pie chart, so disable initially
        chartLabel.setEnabled(false);

        panel.add(new JScrollPane(internalPanel));
        internalPanel.add(chartLabel);
        internalPanel.add(chartBox);
        internalPanel.add(expTypeBox);
        internalPanel.add(typeBox);
    }

    /**
     * Generate the proper pie chart.
     * @return The pie chart.
     */
    private JFreeChart generatePieChart() {
        JFreeChart chart = null;
        DefaultPieDataset set = new DefaultPieDataset();
        // Pie chart cannot show single budget type, but CAN show 1 expense type
        switch (expTypeBox.getSelectedIndex()) {
            case 0: // Both
                setPieDataset(set, budget.getExpenseByType());
                break;
            default: // Fixed or Variable
                setPieDataset(set, budget.getExpenseByType(
                        Budget.Which.get(expTypeBox.getSelectedIndex() - 1)));
                break;
        }
        return chart;
    }

    /**
     * Generate the appropriate XY chart.
     * @return The XY chart.
     */
    private JFreeChart generateXYChart() {
        JFreeChart chart = null;
        return chart;
    }

    /**
     * Set/add the contents of a map into a Pie dataset. Will add contents if
     * map contains keys that the dataset does not have.
     * @param set DefaultPieDataset to modify.
     * @param m Map of values to set contents to.
     */
    private void setPieDataset(DefaultPieDataset set, Map<String, Double> m) {
        for (Map.Entry<String, Double> entry : m.entrySet()) {
            set.setValue(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Set/add the contents of a map into a XY dataset. Will add contents if
     * map contains keys that the dataset does not have.
     * @param set
     * @param m
     */
    private void setXYDataset(DefaultXYDataset set, Map<String, Double> m) {
        for (Map.Entry<String, Double> entry : m.entrySet()) {
            // TODO: Series stuff...
        }
    }

    /**
     * Update the chart.
     */
    public void update() {
        // TODO: Add a cache(?)-type function to prevent redundant updates
        JFreeChart chart;
        if (chartBox.getSelectedIndex() == 0) { // Pie
            chart = generatePieChart();
        } else { // 1, XY
            chart = generateXYChart();
        }
        chartLabel.setIcon(new ImageIcon(chart.createBufferedImage(
                internalPanel.getWidth(), internalPanel.getHeight() / 30
        )));
    }

}
