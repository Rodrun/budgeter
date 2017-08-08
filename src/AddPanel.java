import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The AddPanel is the bottom part of the window that holds the add button
 * to allow the addition of a row to the budget list.
 */
public class AddPanel extends JPanel {

    private JButton addButton;

    /**
     * Creates an AddPanel with an add button.
     * that holds the add button.
     */
    public AddPanel() {
        // Item init
        addButton = new JButton("Add");

        // Layout manager
        setLayout(new FlowLayout());
        add(addButton);
    }

    @Override
    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
        addButton.setEnabled(flag);
    }

    /**
     * Add an ActionListener to the add button.
     * @param l ActionListener to add.
     */
    public void addActionListener(ActionListener l) {
        addButton.addActionListener(l);
    }

}
