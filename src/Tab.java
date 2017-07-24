import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Create a tab with required information for the main tabbed pane.
 * Made for organizational purposes (holds information only).
 */
public class Tab {

    public String name;
    public JPanel panel;
    public boolean selected = false;

    protected Tab() {
        this("", null);
    }

    public Tab(String name, JPanel panel) {
        this.name = name;
        this.panel = panel;
    }

    public boolean isSelected() {
        return selected;
    }

    /**
     * Add a tab to a tabbed pane.
     * @param tabbedPane The tabbed pane to add a tab to.
     * @param tab The tab to add to the tabbed pane.
     */
    public static void addTo(JTabbedPane tabbedPane, Tab tab) {
        tabbedPane.addTab(tab.name, tab.panel);
    }

    /**
     * Iterate through an array of Tabs to set each 'selected' value to false,
     * while the Tab in the given index will be set to true.
     * @param list Array of Tabs.
     * @param index Index of the Tab to set selected.
     */
    public static void setSelectedTab(Tab[] list, int index) {
        if (index < 0 || index >= list.length) {
            return; // Possibly set all tabs to not selected?
        } else {
            for (int i = 0; i < list.length; i++) {
                list[i].selected = (i == index);
            }
        }
    }

}
