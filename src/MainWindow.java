import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainWindow extends JFrame {

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;
    private JTabbedPane tabbedPane;
    private JPanel fixedPane;
    private JPanel variablePane;
    private JPanel optionsPane;
    private JScrollPane fixedScroll;
    private JScrollPane variableScroll;
    private JScrollPane optionsScroll;
    /**
     * Fixed budget list.
     */
    private BudgetList fBudgetList;
    /**
     * Variable budget list.
     */
    private BudgetList vBudgetList;
    private Budget budgetHandler;
    private AddPanel addPanel;
    private InfoPanel infoPanel;

    public MainWindow() {
        init();
    }

    private void init() {
        // Init
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        newMenuItem = new JMenuItem("New");
        openMenuItem = new JMenuItem("Open");
        saveMenuItem = new JMenuItem("Save");
        tabbedPane = new JTabbedPane();
        fixedPane = new JPanel();
        variablePane = new JPanel();
        optionsPane = new JPanel();
        fBudgetList = new BudgetList();
        vBudgetList = new BudgetList();
        budgetHandler = new Budget(fBudgetList, vBudgetList);
        infoPanel = new InfoPanel(budgetHandler.getBudget(),
                budgetHandler.getMoneySpent(),
                budgetHandler.getRemainingBudget());
        fixedScroll = new JScrollPane(fBudgetList);
        variableScroll = new JScrollPane(vBudgetList);
        Tab[] tabbedPaneTabs = {
                new Tab("Fixed", fixedPane, fBudgetList),
                new Tab("Variable", variablePane, vBudgetList),
                new Tab("Options", optionsPane, null)};
        addPanel = new AddPanel(BudgetRow.types);

        // Shortcuts
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                KeyEvent.CTRL_MASK));
        openMenuItem.setAccelerator (KeyStroke.getKeyStroke(KeyEvent.VK_O,
                KeyEvent.CTRL_MASK));
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.CTRL_MASK));


        // Menu
        this.setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        // File -> New
        newMenuItem.addActionListener(e -> {
            // Warn the user before clearing anything
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to start a new budget? Any unsaved" +
                            " changes will be lost!", "New",
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                // Clear budget and update info
                budgetHandler.clear();
                infoPanel.clear();
            }
        });
        // File -> Open
        openMenuItem.addActionListener(e -> {
            // Warn user for any unsaved changes
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Continue? Any unsaved changes will be lost!", "Open",
                    JOptionPane.YES_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Get file
                final JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new MBFFilter());
                int chooserValue = chooser.showOpenDialog(null);
                File file;
                if (chooserValue == JFileChooser.APPROVE_OPTION) {
                    file = chooser.getSelectedFile();
                } else {
                    return; // Cancel
                }
                // Clear budget
                budgetHandler.clear();
                // Read file
                // TODO: Change save to copy a file instead of creating
                Save save = new Save(file.getPath());
                try {
                    save.readSave();
                } catch (FileNotFoundException x) {
                    JOptionPane.showMessageDialog(null, "Error: " +
                            x.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Set budget
                // TODO: More efficient way for this:
                for (BudgetRow row : save.fixedRows) {
                    budgetHandler.addBudget(Budget.FIXED, row);
                }
                for (BudgetRow row : save.variableRows) {
                    budgetHandler.addBudget(Budget.VARIABLE, row);
                }
                budgetHandler.setBudget(save.budget);
                // Update types and clear info
                addPanel.setTypes(new Vector<>(Arrays.asList(save.types)));
                infoPanel.clear();
            }
        });
        // File -> Save
        saveMenuItem.addActionListener(e -> {
            final JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new MBFFilter());
            int confirm = chooser.showSaveDialog(null);
            if (confirm == JFileChooser.APPROVE_OPTION) {
                // Get chosen path
                Save save = new Save(chooser.getSelectedFile().getPath());
                // Set values
                save.fixedRows = budgetHandler.getBudgetRows(Budget.FIXED);
                save.variableRows = budgetHandler.getBudgetRows(
                        Budget.VARIABLE);
                save.budget = budgetHandler.getBudget();
                save.types = (String[])BudgetRow.types.toArray();
                try {
                    save.writeSave();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Error: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        // Tabbed Pane
        // Setup tabs
        for (Tab tab : tabbedPaneTabs) {
            Tab.addTo(tabbedPane, tab);
        }
        // Have default tab selected
        tabbedPaneTabs[tabbedPane.getSelectedIndex()].selected = true;
        // Ensure that appropriate objects are known of tab selection changes
        tabbedPane.addChangeListener(e -> {
            // ONLY fixed and variable tabs have a budget list!
            if (tabbedPane.getSelectedIndex() < 2) {
                addPanel.setEnabled(true);
                Tab.setSelectedTab(tabbedPaneTabs,
                        tabbedPane.getSelectedIndex());
            } else {
                addPanel.setEnabled(false); // Don't want to blindly add
            }
        });
        fixedPane.setLayout(new BorderLayout());
        variablePane.setLayout(new BorderLayout());
        fixedPane.add(fixedScroll);
        variablePane.add(variableScroll);

        // Ensure that appropriate info display changes occur
        budgetHandler.addBudgetEventListener(() -> {
            infoPanel.setMoneySpent(budgetHandler.getMoneySpent());
            infoPanel.setMoneyLeft(budgetHandler.getRemainingBudget());
        });
        // Budget handler must know if the budget has changed
        infoPanel.addActionListener(e -> {
            double bud = budgetHandler.getBudget();
            try {
                bud = infoPanel.getBudget();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error in budget: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            budgetHandler.setBudget(bud);
        });
        addPanel.addActionListener(e -> {
            int which = (tabbedPaneTabs[0].isSelected()) ?
                    Budget.FIXED : Budget.VARIABLE;
            budgetHandler.addBudget(which, new BudgetRow(
                    FormattedDate.dateFormat(FormattedDate.getMonth(),
                            Integer.valueOf(addPanel.getSelectedDay())),
                    addPanel.getSelectedType(),
                    addPanel.getNameFromField(),
                    addPanel.getMoneyFromField()
            ));

            budgetHandler.update();
            addPanel.resetFields();
        });

        this.setLayout(new BorderLayout());
        this.add(infoPanel, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(addPanel, BorderLayout.SOUTH);
    }

}
