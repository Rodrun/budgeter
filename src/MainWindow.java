import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainWindow extends JFrame {

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;
    private JTabbedPane tabbedPane;
    private JPanel fixedPane;
    private JPanel variablePane;
    private JPanel optionsPanel;
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
        optionsPanel = new JPanel();
        addPanel = new AddPanel();
        infoPanel = new InfoPanel(budget, moneySpent, moneyLeft);

        // Shortcuts
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                KeyEvent.CTRL_MASK));
        openMenuItem.setAccelerator (KeyStroke.getKeyStroke(KeyEvent.VK_O,
                KeyEvent.CTRL_MASK));
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.CTRL_MASK));

        this.setJMenuBar(menuBar);
        this.setLayout(new BorderLayout());
        menuBar.add(fileMenu);
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        tabbedPane.addTab("Fixed", fixedPane);
        tabbedPane.addTab("Variable", variablePane);
        tabbedPane.addTab("Options", optionsPanel);
        this.add(infoPanel, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(addPanel, BorderLayout.SOUTH);
    }

}
