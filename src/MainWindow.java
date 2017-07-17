import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
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

    private double budget = 0.00;
    private double moneySpent = 0.00;
    private double moneyLeft = 0.00;

    public MainWindow() {
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
        infoPanel = new InfoPanel(budget, moneySpent, moneyLeft);
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

        // Tabbed Pane
        for (Tab tab : tabbedPaneTabs) {
            Tab.addTo(tabbedPane, tab);
        }
        // Ensure that appropriate objects are known of tab selection changes
        tabbedPane.addChangeListener(e -> {
            // ONLY fixed and variable tabs have a budget list!
            if (tabbedPane.getSelectedIndex() < 2) {
                addPanel.setEnabled(true);
                Tab.setSelectedTab(tabbedPaneTabs,
                        tabbedPane.getSelectedIndex());
                //addPanel.setSelectedTab(tabbedPane.getSelectedIndex());
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
            infoPanel.setMoneyLeft(budgetHandler.getBudget(),
                    budgetHandler.getMoneySpent());
        });
        // Budget handler must know if the budget has changed
        infoPanel.addActionListener(e -> {
            budgetHandler.setBudget(infoPanel.getBudget());
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
