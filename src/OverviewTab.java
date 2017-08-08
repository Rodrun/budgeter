import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultXYDataset;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * The options tab. Displays visual representation of the budgetHandler and allows
 * user to set other available budgeting options.
 */
public class OverviewTab extends Tab {

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
    private BudgetHandler budgetHandler;
    private JLabel chartLabel;
    private JComboBox<String> chartBox, expTypeBox, typeBox;
    private String title;

    private OverviewTab() { }

    public OverviewTab(BudgetHandler budgetHandler) {
        super("Overview", new JPanel());
        this.budgetHandler = budgetHandler;
        title = "";
        init();
    }

    /**
     * Set the title of the chart.
     * @param t Chart title.
     */
    public void setChartTitle(String t) {
        title = t;
    }

    public String getChartTitle() {
        return title;
    }

    private void init() {
        internalPanel = new JPanel();
        internalPanel.setLayout(new BoxLayout(internalPanel,
                BoxLayout.PAGE_AXIS));
        chartLabel = new JLabel();
        chartBox = new JComboBox<>(chartStrings);
        expTypeBox = new JComboBox<>(expTypeStrings);
        typeBox = new JComboBox<>(BudgetHandler.types.toArray());

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
        BudgetHandler.types.addTypesChangeListener(() -> {
            typeBox.setModel(new DefaultComboBoxModel<>(
                    BudgetHandler.types.toArray()));
        });
        budgetHandler.addBudgetEventListener(() -> {
            update();
        });

        // The default selection is the pie chart, so disable initially
        chartLabel.setEnabled(false);

        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(internalPanel), BorderLayout.CENTER);
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
        // Pie chart cannot show single budgetHandler type, but CAN show 1 expense type
        switch (expTypeBox.getSelectedIndex()) {
            case 0: // Both
                setPieDataset(set, budgetHandler.getExpenseByType());
                break;
            default: // Fixed or Variable
                setPieDataset(set, budgetHandler.getExpenseByType(
                        BudgetHandler.Which.get(expTypeBox.getSelectedIndex() - 1)));
                break;
        }
        return ChartFactory.createPieChart(title, set,
                true, // legend
                true, // tooltips
                false); // urls
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
        int width = internalPanel.getWidth() - 100;
        int height = internalPanel.getHeight() - 100;
        if (width <= 0) {
            width = 400;
        }
        if (height <= 0) {
            height = 400;
        }
        chartLabel.setIcon(new ImageIcon(chart.createBufferedImage(width,
                height)));
    }

}
