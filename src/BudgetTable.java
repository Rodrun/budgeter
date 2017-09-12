import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * The BudgetTable is a JTable subclass that displays the information of a
 * BudgetList.
 */
public class BudgetTable extends JTable {

    private BudgetList budgetList;

    /**
     * Create a BudgetTable with a given BudgetList.
     * @param b BudgetList to use.
     */
    public BudgetTable(BudgetList b, CategoryList cats) {
        setModel(b);

        // Disable reordering of headers
        getTableHeader().setReorderingAllowed(false);

        // Cell editors
        TableColumn dateColumn = getColumnModel().getColumn(0);
        TableColumn typeColumn = this.getColumnModel().getColumn(1);
        TableColumn moneyColumn = this.getColumnModel().getColumn(4);
        JComboBox<String> dateCombo = new JComboBox<>(
                FormattedDate.getDaysOfMonth());
        JComboBox<String> typeCombo = new JComboBox<>(cats.toArray());
        JCheckBox deleteButton = new JCheckBox("X");
        deleteButton.addActionListener(e ->
                budgetList.removeBudget(getSelectedRow())
        );
        dateColumn.setCellEditor(new DefaultCellEditor(dateCombo));
        typeColumn.setCellEditor(new DefaultCellEditor(typeCombo));
        moneyColumn.setCellEditor(new DefaultCellEditor(deleteButton));
    }

    /**
     * Repaint the table to update cells.
     */
    public void update() {
        repaint();
    }

    /**
     * Set the BudgetList model of the table.
     * @param model
     */
    public void setModel(BudgetList model) {
        super.setModel((TableModel) model);
        budgetList = model;
    }

}
