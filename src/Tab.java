import javax.swing.*;

/**
 * Create a tab with required information for the main tabbed pane.
 * Made for organizational purposes (holds information only).
 */
public class Tab {

    public String name;
    public JPanel panel;
    public BudgetList budgetList;

    public Tab(String name, JPanel panel, BudgetList budgetList) {
        this.name = name;
        this.panel = panel;
        this.budgetList = budgetList;
    }

    /**
     * Add a tab to a tabbed pane.
     * @param tabbedPane The tabbed pane to add a tab to.
     * @param tab The tab to add to the tabbed pane.
     */
    public static void addTo(JTabbedPane tabbedPane, Tab tab) {
        tabbedPane.addTab(tab.name, tab.panel);
    }

}
