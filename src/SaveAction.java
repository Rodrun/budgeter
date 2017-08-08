import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Handles saving to a file properly (includes handling save dialog).
 */
class SaveAction implements ActionListener {

    private BudgetHandler budgetHandler;
    private Save saveFile;

    /**
     * Creates a SaveAction.
     * @param budgetHandler The BudgetHandler to write to a save.
     */
    public SaveAction(BudgetHandler budgetHandler) {
        this.budgetHandler = budgetHandler;
        saveFile = new Save((File) null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (saveFile.isNull()) { // Show dialog to set save file
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
                // The Save has been written and is currently in use now
                // Therefore, saveFile.isNull() == false
            }
        }
        // Write save
        try {
            saveFile.writeSave(budgetHandler);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Get the BudgetHandler handler in use.
     * @return BudgetHandler.
     */
    public BudgetHandler getBudgetHandler() {
        return budgetHandler;
    }

    /**
     * Set the BudgetHandler.
     * @param budgetHandler BudgetHandler to set to.
     */
    public void setBudgetHandler(BudgetHandler budgetHandler) {
        this.budgetHandler = budgetHandler;
    }

    /**
     * Set the file used by the Save.
     * @param f File to use.
     */
    public void setSaveFile(File f) {
        saveFile.setFile(f);
    }

    /**
     * Get the Save used.
     * @return The Save.
     */
    public Save getSave() {
        return saveFile;
    }

}