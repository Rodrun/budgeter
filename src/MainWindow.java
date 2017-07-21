import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

public class MainWindow extends JFrame {

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem saveAsMenuItem;
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
    /**
     * If a save file has been opened/is in use, this enables the 'Save' menu.
     */
    private boolean hasSaveFile = false;

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
        saveMenuItem.setEnabled(false);
        saveAsMenuItem = new JMenuItem("Save As");
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
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.CTRL_MASK));


        // Menu
        this.setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveAsMenuItem);
        // File -> New
        newMenuItem.addActionListener(e -> {
            // Warn the user before clearing anything
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to start a new budget? Any unsaved" +
                            " changes will be lost!", "New",
                    JOptionPane.OK_OPTION);
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
                chooser.setFileFilter(Save.FILTER);
                int chooserValue = chooser.showOpenDialog(this);
                if (chooserValue == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    // Clear budget
                    budgetHandler.clear();
                    infoPanel.clear();
                    // Read file
                    Save save = new Save(file);
                    try {
                        save.readSave();
                    } catch (FileNotFoundException x) {
                        JOptionPane.showMessageDialog(null, "Error: " +
                                x.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
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

                    budgetHandler.setBudget(save.budget); // Triggers listeners
                    infoPanel.setBudgetField(budgetHandler.getBudget());
                    // Update types
                    addPanel.setTypes(new Vector<>(Arrays.asList(save.types)));
                } // Else cancel
            }
        });
        // File -> Save
        saveAsMenuItem.addActionListener(e -> {
            final JFileChooser chooser = new JFileChooser();
            int confirm = chooser.showSaveDialog(this);
            if (confirm == JFileChooser.APPROVE_OPTION) {
                // Setup save
                // Add .mbf extensions if file does not have it
                String ext = (Save.getExtension(chooser.getSelectedFile())
                        .compareToIgnoreCase(Save.EXTENSION) == 0)
                        ? "" : "." + Save.EXTENSION;
                Save save = new Save(
                        chooser.getCurrentDirectory().getPath() +
                        System.getProperty("file.separator") +
                        chooser.getSelectedFile().getName() +
                        ext);
                // Set values
                save.fixedRows = budgetHandler.getBudgetRows(Budget.FIXED);
                save.variableRows = budgetHandler.getBudgetRows(
                        Budget.VARIABLE);
                save.budget = budgetHandler.getBudget();
                save.types = BudgetRow.types.toArray(
                        new String[BudgetRow.types.size()]);
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
            double moneySpent = budgetHandler.getMoneySpent();
            // Show positive sign if spent is negative
            if (moneySpent < 0) {
                infoPanel.setSign(InfoPanel.POSITIVE);
            } else {
                infoPanel.setSign(InfoPanel.NEGATIVE);
            }

            // Update information
            infoPanel.setMoneySpent(Math.abs(moneySpent));
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
                    String.valueOf(addPanel.getMoney())
            ));

            budgetHandler.update();
            addPanel.resetFields();
        });

        this.setLayout(new BorderLayout());
        this.add(infoPanel, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(addPanel, BorderLayout.SOUTH);
    }

    /**
     * Set whether save file is in use.
     * @param flag
     */
    private void setHasSaveFile(boolean flag) {
        hasSaveFile = flag;
    }

}
