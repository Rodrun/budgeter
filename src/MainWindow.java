import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainWindow extends JFrame {

    private JMenuItem saveMenuItem;
    private JTabbedPane tabbedPane;
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
    private boolean hasSaveFile;
    private Save saveFile;

    public MainWindow() {
        init();
    }

    private void init() {
        // Init
        /* Menu items */
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem openMenuItem = new JMenuItem("Open");
        saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setEnabled(false);
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        /* Budget */
        fBudgetList = new BudgetList();
        vBudgetList = new BudgetList();
        budgetHandler = new Budget(fBudgetList, vBudgetList);
        /* Tabbed pane */
        tabbedPane = new JTabbedPane();
        JPanel fixedPane = new JPanel();
        JPanel variablePane = new JPanel();
        Tab[] tabbedPaneTabs = {
                new Tab("Fixed", fixedPane),
                new Tab("Variable", variablePane),
                new OptionsTab(budgetHandler)
        };
        /* North & South panels */
        infoPanel = new InfoPanel(budgetHandler.getBudget(),
                budgetHandler.getMoneySpent(),
                budgetHandler.getRemainingBudget());
        JScrollPane fixedScroll = new JScrollPane(fBudgetList);
        JScrollPane variableScroll = new JScrollPane(vBudgetList);
        addPanel = new AddPanel(Budget.types);
        /* Save */
        hasSaveFile = false;
        saveFile = new Save();


        // Shortcuts
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                KeyEvent.CTRL_DOWN_MASK));
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                KeyEvent.CTRL_DOWN_MASK));
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.CTRL_DOWN_MASK));
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.CTRL_DOWN_MASK| KeyEvent.SHIFT_DOWN_MASK));


        /* Menu */
        this.setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
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
                    saveFile = new Save(file);
                    try {
                        saveFile.readSave();
                    } catch (FileNotFoundException x) {
                        JOptionPane.showMessageDialog(null, "Error: " +
                                x.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Set budget
                    // TODO: More efficient way for this:
                    for (BudgetRow row : saveFile.fixedRows) {
                        budgetHandler.addBudget(Budget.Which.FIXED, row);
                    }
                    for (BudgetRow row : saveFile.variableRows) {
                        budgetHandler.addBudget(Budget.Which.VARIABLE, row);
                    }

                    budgetHandler.setBudget(saveFile.budget); // Call listeners
                    infoPanel.setBudgetField(budgetHandler.getBudget());
                    // Update types
                    Budget.types.setList(saveFile.types);
                } // Else cancel
            } // Else NO_OPTION
        });

        /**
         * The action to save to a new file.
         */
        class SaveAsAction implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser chooser = new JFileChooser();
                int confirm = chooser.showSaveDialog(null);
                if (confirm == JFileChooser.APPROVE_OPTION) {
                    // Setup save
                    // Add .mbf extensions if file does not have it
                    String ext = (Save.getExtension(chooser.getSelectedFile())
                            .compareToIgnoreCase(Save.EXTENSION) == 0)
                            ? "" : "." + Save.EXTENSION;
                    saveFile = new Save(
                            chooser.getCurrentDirectory().getPath() +
                                    System.getProperty("file.separator") +
                                    chooser.getSelectedFile().getName() +
                                    ext);
                    // Set values
                    saveFile.fixedRows = budgetHandler.getBudgetRows(
                            Budget.Which.FIXED);
                    saveFile.variableRows = budgetHandler.getBudgetRows(
                            Budget.Which.VARIABLE);
                    saveFile.budget = budgetHandler.getBudget();
                    saveFile.types = Budget.types.toArray();
                    try {
                        saveFile.writeSave();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
        // File -> Save
        saveMenuItem.addActionListener(e -> {
            // Save to last used file
            if (hasSaveFile) {
                try {
                    saveFile.writeSave();
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(this, "IO Error: " +
                            e1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else { // No save file! Instead save as.
                saveAsMenuItem.doClick();
            }
        });
        // File -> Save As
        saveAsMenuItem.addActionListener(new SaveAsAction());


        /* Tabbed Pane */
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
            } finally {
                budgetHandler.setBudget(bud);
                Logger.getAnonymousLogger().log(Level.INFO, "After exception," +
                        " budget is set to " + budgetHandler.getBudget());
            }
        });
        // AddPanel listener to add a field
        addPanel.addActionListener(e -> {
            budgetHandler.addBudget(
                    // which
                    (tabbedPaneTabs[0].isSelected()) ?
                    Budget.Which.FIXED : Budget.Which.VARIABLE,
                    // BudgetRow
                    new BudgetRow(
                        FormattedDate.dateFormat(FormattedDate.getMonth(),
                            Integer.valueOf(addPanel.getSelectedDay())),
                        addPanel.getSelectedType(),
                        addPanel.getNameFromField(),
                        String.valueOf(addPanel.getMoney()))
            );

            budgetHandler.update();
            addPanel.resetFields();
        });

        /* Add to container */
        this.setLayout(new BorderLayout());
        this.add(infoPanel, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(addPanel, BorderLayout.SOUTH);
    }

    /**
     * Set the Save.
     * @param s New Save to set to.
     */
    private void setSaveFile(Save s) {
        saveFile = s;
    }

    /**
     * Set whether save file is in use.
     * @param flag
     */
    private void setHasSaveFile(boolean flag) {
        hasSaveFile = flag;
        saveMenuItem.setEnabled(hasSaveFile);
    }

    /**
     * Revert back to an empty save file
     */
    private void resetSave() {
        setHasSaveFile(false);
        saveFile = new Save();
    }

}
