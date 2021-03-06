import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Logger.*;

/**
 * Created by Juan on 6/28/17.
 *
 * Budgeter: A basic budgeting tool.
 *
 * Purpose: A tool that tracks your spending and earnings throughout
 * a month and ensure that it is within your budget.
 */


public class MainWindow extends JFrame {

    private BudgetHandler budgetHandler;
    private TabbedPane tabbedPane;
    private AddPanel addPanel;
    private InfoPanel infoPanel;

    /**
     * Create an instance of the main window and the program.
     * @param p Preferences to use.
     * @param t Translations to use.
     */
    public MainWindow(Preferences p, Translator t) {
        init(p, t);
    }

    // No emtpy constructor allowed
    private MainWindow() { }

    /**
     * Initialize the main window, along with its children and other major
     * components of the program.
     * @param prefs Preferences.
     * @param trans Translator.
     */
    private void init(Preferences prefs, Translator trans) {
        // Init
        /* Menu */
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        /* Tabbed pane */
        // Create the BudgetHandler
        JsonArray catsArray = prefs.get("categories").asArray();
        budgetHandler = new BudgetHandler(
                Utils.jsonArrayToString(catsArray),
                trans
        );
        tabbedPane = new TabbedPane(budgetHandler);
        /* North & South panels */
        infoPanel = new InfoPanel(budgetHandler);
        addPanel = new AddPanel();
        /* Save */
        SaveAction saveAction = new SaveAction(budgetHandler);


        // Shortcuts
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                KeyEvent.CTRL_DOWN_MASK));
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                KeyEvent.CTRL_DOWN_MASK));
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.CTRL_DOWN_MASK));
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));


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
                            " changes will be lost!",
                    "New",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                // Clear budget and update info
                budgetHandler.clear();
                infoPanel.clear();
                saveAction.setSaveFile(null);
            }
        });
        // File -> Open
        openMenuItem.addActionListener(e -> {
            // Warn user for any unsaved changes
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Continue? Any unsaved changes will be lost!",
                    "Open",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Get file
                final JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(Save.FILTER);
                int chooserValue = chooser.showOpenDialog(this);
                if (chooserValue == JFileChooser.APPROVE_OPTION) {
                    // Clear budget
                    budgetHandler.clear();
                    infoPanel.clear();
                    // Read file
                    saveAction.setSaveFile(chooser.getSelectedFile());
                    try {
                        budgetHandler.copy(saveAction.getSave().readSave(
                                budgetHandler.getCategories().toArray(),
                                trans
                        ));
                    } catch (FileNotFoundException x) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Error: " + x.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                    // Set budget and update
                    infoPanel.setBudgetField(budgetHandler.getBudget());
                    budgetHandler.update();
                    tabbedPane.updateTables();
                } // Else cancel
            } // Else NO_OPTION
        });
        // File -> Save
        saveMenuItem.addActionListener(saveAction);
        // File -> Save As
        saveAsMenuItem.addActionListener(saveAction);


        /* Tabbed Pane */
        // Ensure that appropriate objects are known of tab selection changes
        tabbedPane.addChangeListener(e -> {
            // ONLY fixed and variable tabs have a budget list!
            addPanel.setEnabled(tabbedPane.getSelectedIndex() < 2);
        });

        // AddPanel listener to add a field
        addPanel.addActionListener(e -> {
            budgetHandler.addBudget(
                    BudgetHandler.Which.get(tabbedPane.getSelectedIndex()),
                    new BudgetRow(
                            FormattedDate.getFormattedToday(),
                            budgetHandler.getCategories().get(0),
                            "New",
                            "0"
                    )
            );
            budgetHandler.update();
        });

        /* Add to container */
        this.setLayout(new BorderLayout());
        this.add(infoPanel, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(addPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        /*
         * Attempt to stream any output to file.
         */
        PrintStream outstream = System.out;
        try {
            outstream = new PrintStream(
                    new BufferedOutputStream(
                            new FileOutputStream("log.txt")),
                    true // auto flush
            );
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        System.setOut(outstream);
        System.setErr(System.out);

        EventQueue.invokeLater(() -> {
            Preferences preferences = null;
            Translator translator = null;
            // Read preferences and language
            getAnonymousLogger().info("Reading preferences...");
            try {
                preferences = new Preferences(
                        "resource/preferences.json"
                );
                getAnonymousLogger().info("Initializing translator...");
                String languageDir = preferences.get("language").asString();
                translator = new Translator(languageDir);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        "An error occurred: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (NullPointerException ex) {
                ex.printStackTrace();
                // No specific language
                translator = new Translator();
            }
            MainWindow window = new MainWindow(preferences, translator);
            window.setTitle("Budgeter");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }

}
