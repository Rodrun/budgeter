import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The options tab. Displays visual representation of the budgetHandler and allows
 * user to set other available budgeting options.
 */
public class OverviewTab extends Tab {

    private static final String[] expTypeStrings = {
            "Both Expense CategoryList",
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
    private JFreeChart chart; // Data
    private ChartPanel chartPanel; // Display
    // Char type, expense type, category
    private JComboBox<String> chartBox, expTypeBox, catBox;
    /**
     * The title of the chart.
     */
    private String title = "";

    private OverviewTab() { }

    public OverviewTab(BudgetHandler budgetHandler) {
        super("Overview", new JPanel());
        init(budgetHandler);
    }

    /**
     * Set the title of the chart.
     * @param t Chart title.
     */
    public void setChartTitle(String t) {
        title = t;
    }

    /***
     * Get the title of the chart.
     * @return
     */
    public String getChartTitle() {
        return title;
    }

    private void init(BudgetHandler budgetHandler) {
        internalPanel = new JPanel();
        internalPanel.setLayout(new BoxLayout(internalPanel,
                BoxLayout.PAGE_AXIS));
        chartBox = new JComboBox<>(chartStrings);
        expTypeBox = new JComboBox<>(expTypeStrings);
        catBox = new JComboBox<>(budgetHandler.getCategories().toArray());

        // Add ActionListeners
        class Updater implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                update(budgetHandler);
            }
        }
        Updater listener = new Updater();
        chartBox.addActionListener(listener);
        chartBox.addActionListener(e -> {
            // CategoryList box should be disabled unless XY Chart is selected
            catBox.setEnabled(chartBox.getSelectedIndex() == 0);
        });
        expTypeBox.addActionListener(listener);
        catBox.addActionListener(listener); // Enabled when XY is selected
        catBox.setEnabled(false);

        // Update types if they're changed
        budgetHandler.getCategories().addTypesChangeListener(() -> {
            updateTypes(budgetHandler.getCategories().toArray());
        });
        budgetHandler.addBudgetEventListener(() -> {
            update(budgetHandler);
        });


        chart = ChartFactory.createPieChart("Start budgeting for visual",
                new DefaultPieDataset());
        chartPanel = new ChartPanel(chart,
                true, // properties
                true, // save
                true, // print
                false, // zoom
                true // tooltips
        );

        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(internalPanel), BorderLayout.CENTER);
        internalPanel.add(chartPanel);
        internalPanel.add(chartBox);
        internalPanel.add(expTypeBox);
        internalPanel.add(catBox);
    }

    /**
     * Update the typesBox ComboBoxModel.
     * @param categories Category list to use.
     */
    private void updateTypes(String[] categories) {
        catBox.setModel(new DefaultComboBoxModel<>(categories));
        // Add required items
        catBox.addItem("((No types; Spending))");
        catBox.addItem("((All types))");
        // Set the default selection to the last item
        catBox.setSelectedIndex(catBox.getItemCount() - 1);
    }

    /**
     * Generate the proper pie chart.
     * @return The pie chart.
     */
    private JFreeChart generatePieChart(BudgetHandler budgetHandler) {
        DefaultPieDataset set = new DefaultPieDataset();
        // Cannot show specific (category) type, but can show expense types
        switch (expTypeBox.getSelectedIndex()) {
            case 0: // Both
                setPieDataset(
                        budgetHandler,
                        set,
                        budgetHandler.getExpenseByCategory()
                );
                break;
            default: // Fixed or Variable
                setPieDataset(
                        budgetHandler,
                        set,
                        budgetHandler.getExpenseByCategory(
                            BudgetHandler.Which.get(
                                    expTypeBox.getSelectedIndex() - 1))
                );
                break;
        }
        return ChartFactory.createPieChart(title, set,
                true, // legend
                true, // tooltips
                false); // urls
    }

    /**
     * Set/add the contents of a map into a Pie dataset. Will add contents if
     * map contains keys that the dataset does not have.
     * @param budgetHandler BudgetHandler to retrieve information from.
     * @param set DefaultPieDataset to modify.
     * @param m Map of values to set contents to.
     */
    private void setPieDataset(BudgetHandler budgetHandler,
            DefaultPieDataset set, Map<String, Double> m) {
        for (Map.Entry<String, Double> entry : m.entrySet()) {
            if (entry.getValue() < 0) {
                set.setValue(entry.getKey(), Math.abs(entry.getValue()));
            }
        }
        set.setValue("Remaining",
                new Double(budgetHandler.getRemainingBudget()));
    }

    /**
     * Add HashMap values.
     * @param m List of HashMaps.
     * @return Sum of all maps.
     */
    private HashMap<Integer, Double> addMaps(HashMap<Integer, Double>... m) {
        if (m == null) {
            throw new NullPointerException();
        }
        HashMap<Integer, Double> result = new HashMap<>(m[0].size());
        for (HashMap<Integer, Double> map : m) {
            for (Integer i = 0; i < map.size(); i++) {
                result.compute(i, (key, value) -> {
                    if (result.get(key) == null) {
                        return (value == null) ? 0d : value;
                    }
                    return result.get(key) + value;
                });
            }
        }

        // Finalize all values
        //finalizeMap(result);
        return result;
    }

    /**
     * Finalize all values of a map properly.
     * @param result HashMap to modify.
     */
    private void finalizeMap(HashMap<Integer, Double> result) {
        result.forEach((key, val) ->
                result.compute(key, (k, v) -> {
                    // If gained, show as 0: that is not SPENT money!
                    // TODO: Add 'show gained' feature or something similar
                    if (v.doubleValue() >= 0) {
                        return 0d;
                    }
                    // Make each value positive
                    return Math.abs(v.doubleValue());
                })
        );
    }

    /**
     * Create an XYSeries object from the values of a given HashMap.
     * @param map HashMap to use.
     * @param name Name of the series.
     * @return XYSeries containing the HashMap values.
     */
    private XYSeries mapToSeries(HashMap<Integer, Double> map, String name) {
        // Finalize all values
        finalizeMap(map);

        final XYSeries xy = new XYSeries(name);
        for (Map.Entry<Integer, Double> entry : map.entrySet()) {
            xy.add(entry.getKey(), entry.getValue());
        }
        return xy;
    }

    /**
     * Generate the appropriate XY chart.
     * @param budgetHandler BudgetHandler to use.
     * @return The XY chart.
     */
    private JFreeChart generateXYChart(BudgetHandler budgetHandler) {
        DefaultXYDataset set = new DefaultXYDataset();
        // Generate series:
        ArrayList<XYSeries> seriesList;
        // All types: show all individual type plots
        if (catBox.getSelectedIndex() == catBox.getItemCount() - 1) {
            seriesList = new ArrayList<>(catBox.getItemCount() - 2);

            // Iterate through all the types (excluding last 2 non-type items)
            for (int i = 0; i < catBox.getItemCount() - 2; i++) {
                String currentItem = catBox.getItemAt(i);

                final XYSeries series = new XYSeries(catBox.getItemAt(i));
                HashMap<Integer, Double> datedMap =
                        (expTypeBox.getSelectedIndex() == 0) ?
                        // If 0, add both dated expenses
                        addMaps(
                                budgetHandler.getDatedExpenses(
                                        currentItem,
                                        BudgetHandler.Which.FIXED
                                ),
                                budgetHandler.getDatedExpenses(
                                        currentItem,
                                        BudgetHandler.Which.VARIABLE
                                )
                        )
                        : // OR != 0
                        budgetHandler.getDatedExpenses(
                                currentItem,
                                BudgetHandler.Which.get(
                                        expTypeBox.getSelectedIndex() - 1)
                        );

                // Add the map to the series
                if (datedMap != null) {
                    datedMap.forEach((k, v) -> {
                        series.add(k, v);
                    });
                }
                // Add the series to the list
                seriesList.add(series);
            }
        } else if (catBox.getSelectedIndex() == catBox.getItemCount() - 2) {
            // Show: no specific category; just overall daily spending
            seriesList = new ArrayList<>(1);
            // Accumulate spending of all categories and types of every day
            HashMap<Integer, Double> sum =
                    new HashMap<>(FormattedDate.getDay());
            for (int t = 0; t < catBox.getItemCount() - 2; t++) {
                if (expTypeBox.getSelectedIndex() != 0) { // Fixed or variable
                    // Put sum of 'sum' and the dated expenses of a category
                    sum.putAll(addMaps(
                            sum,
                            budgetHandler.getDatedExpenses(
                            catBox.getItemAt(t),
                            BudgetHandler.Which.get(
                                    expTypeBox.getSelectedIndex() - 1))
                    ));
                } else { // Both is selected as expense type to show
                    String category = catBox.getItemAt(t);
                    sum.putAll(addMaps(
                            sum,
                            budgetHandler.getDatedExpenses(
                                    category,
                                    BudgetHandler.Which.FIXED
                            ),
                            budgetHandler.getDatedExpenses(
                                    category,
                                    BudgetHandler.Which.VARIABLE
                            )
                    ));
                }
            }

            // Put all info into series
            seriesList.add(mapToSeries(sum, "Total Spending"));
        } else { // Show an individual plot of a chosen category
            seriesList = new ArrayList<>(1);
            String cat = catBox.getItemAt(catBox.getSelectedIndex());
            seriesList.add(mapToSeries(
                    (expTypeBox.getSelectedIndex() == 0) ?
                            // Selected index = both
                    addMaps(
                            budgetHandler.getDatedExpenses(
                                    cat,
                                    BudgetHandler.Which.FIXED
                            ),
                            budgetHandler.getDatedExpenses(
                                    cat,
                                    BudgetHandler.Which.VARIABLE
                            )
                    )
                            : // OR if != 0
                    budgetHandler.getDatedExpenses(
                            cat,
                            BudgetHandler.Which.get(
                                    expTypeBox.getSelectedIndex() - 1
                            )
                    ),
                    cat // Name
            ));
        }
        // Add all series to collection
        final XYSeriesCollection collection = new XYSeriesCollection();
        seriesList.forEach(xySeries-> collection.addSeries(xySeries));
        return ChartFactory.createXYLineChart(
                title, // title
                "Day of Month", // x axis
                "Spent", // y axis
                collection, // data
                PlotOrientation.VERTICAL, // orientation
                true, // legend
                true, // tooltips
                false // urls
        );
    }

    /**
     * Update the chart.
     * @param budgetHandler BudgetHandler to use.
     */
    public void update(BudgetHandler budgetHandler) {
        if (chartBox.getSelectedIndex() == 0) { // Pie
            chart = generatePieChart(budgetHandler);
        } else { // 1, XY
            chart = generateXYChart(budgetHandler);
        }
        chart.setTitle("Expenses: " +
                FormattedDate.getMonthName(FormattedDate.getMonth()) +
                " " + FormattedDate.getYear());
        chartPanel.setChart(chart);
    }

}
