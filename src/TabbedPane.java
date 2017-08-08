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

    // Tab titles
    private static final String FIXED_TITLE = "Fixed";
    private static final String VARIABLE_TITLE = "Variable";

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
        // Add tabs
        tabs = new Tab[]{
                new Tab(FIXED_TITLE, new JPanel(new BorderLayout())),
                new Tab(VARIABLE_TITLE, new JPanel(new BorderLayout())),
                new OverviewTab(budgetHandler)
        };
        tabs[0].panel.add(new JScrollPane(
                budgetHandler.getBudgetList(BudgetHandler.Which.FIXED)),
                BorderLayout.CENTER);
        tabs[1].panel.add(new JScrollPane(
                budgetHandler.getBudgetList(BudgetHandler.Which.VARIABLE)),
                BorderLayout.CENTER);
        for (Tab tab : tabs) {
            addTab(tab.name, tab.panel);
        }
    }

}
