import javax.swing.*;
import java.awt.*;

/**
 * The TabbedPane is the central area of interest in the main window. Here it
 * allows the user to view both the fixed and variable expenses, and the
 * other information available from the budget such as visualizations and
 * other features.
 */
public class TabbedPane extends JTabbedPane {

    private Tab[] tabs;
    private BudgetTable[] budgetTables;

    // Tab titles
    private static final String FIXED_TITLE = "Fixed";
    private static final String VARIABLE_TITLE = "Variable";
    // Amount of BudgetLists
    private static final int LIST_COUNT = 2;

    /**
     * Creates a BudgetHandled object. Calls <code>initialize(...)</code>.
     * @param budgetHandler BudgetHandler to use.
     */
    public TabbedPane(BudgetHandler budgetHandler) {
        initialize(budgetHandler);
    }

    /**
     * Initialize necessary objects for proper functioning.
     * @param budgetHandler BudgetHandler to use.
     */
    private void initialize(BudgetHandler budgetHandler) {
        tabs = new Tab[]{
                new Tab(FIXED_TITLE, new JPanel(new BorderLayout())),
                new Tab(VARIABLE_TITLE, new JPanel(new BorderLayout())),
                new OverviewTab(budgetHandler)
        };
        // Add the tables
        budgetTables = new BudgetTable[LIST_COUNT];
        for (int i = 0; i < LIST_COUNT; i++) {
            // Create tables
            budgetTables[i] = budgetHandler.createBudgetTable(
                    BudgetHandler.Which.get(i)
            );
            // Add to panel
            tabs[i].panel.add(
                    new JScrollPane(budgetTables[i]),
                    BorderLayout.CENTER
            );
        }
        // Add the tabs
        for (Tab tab : tabs) {
            addTab(tab.name, tab.panel);
        }
    }

    /**
     * Update the BudgetTables available.
     */
    public void updateTables() {
        for (BudgetTable table : budgetTables) {
            table.update();
        }
    }

}
