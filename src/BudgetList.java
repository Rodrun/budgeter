import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The GUI budget table.
 */
public class BudgetList extends JTable {

    private DefaultTableModel model;
    private TableColumn dateColumn;
    private TableColumn typeColumn;

    public static final String[] HEADERS = { "Date", "Type", "Name", "$" };

    public enum SortType {
        ALPHABETICAL, // Sort alphabetically
        BY_DATE, // Sort by date
        BY_MONEY, // Sort by money amount (including if it is negative)
        BY_MONEY_ABS // Sort by absolute money (ignores if positive or negative)
    }

    public BudgetList() {
        model = new DefaultTableModel();
        model.setColumnIdentifiers(HEADERS);
        this.setModel(model);
        this.getTableHeader().setReorderingAllowed(false);

        // Cell editors
        dateColumn = this.getColumnModel().getColumn(0);
        typeColumn = this.getColumnModel().getColumn(1);
        JComboBox<String> dateCombo = new JComboBox<>(
                FormattedDate.getDaysOfMonth());
        JComboBox<String> typeCombo = new JComboBox<>(BudgetRow.types);
        dateColumn.setCellEditor(new DefaultCellEditor(dateCombo));
        typeColumn.setCellEditor(new DefaultCellEditor(typeCombo));
    }

    /**
     * Update all table cells
     */
    private void update() {
        // re render all cells
        this.repaint();
    }

    /**
     * Clear the budget list.
     * @return A copy of Vector of Budgets that will be cleared.
     */
    public Vector<BudgetRow> clear() {
        Vector<BudgetRow> copy = getRowsVector();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.removeRow(i);
        }
        update();
        return copy;
    }

    /**
     * Add a budget row.
     * @param b A BudgetRow.
     */
    public void addBudget(BudgetRow b) {
        //getRowsVector().add(b);
        model.addRow(b.getRowData());
        update();
        Logger.getAnonymousLogger().log(Level.INFO, "Add Budget: " +
                b.toString());
    }

    /**
     * Read a string chunk containing a budget list.
     * @param chunk String data of a budget list.
     */
    public void readBudget(String chunk) {
        Scanner scanner = new Scanner(chunk);
        while (scanner.hasNext()) {
            getRowsVector().add(BudgetRow.readLine(scanner.nextLine()));
        }
    }

    /**
     * Return a savable string chunk.
     * @return A string chunk meant to be saved into a save file.
     */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (BudgetRow bud : getRowsVector()) {
            string.append(bud.toString());
        }
        return string.toString();
    }

    /**
     * Sort the budget list.
     */
    public void sort(SortType type) {
        getRowsVector().sort((BudgetRow o1, BudgetRow o2) -> {
            // 1 = >, -1 = <, 0 = EQUAL
            if (type == SortType.BY_DATE) {
                // Get 'values' of dates (concatenated month and day)
                if (BudgetRow.convertDateToInt(o1.getDate()) !=
                        BudgetRow.convertDateToInt(o2.getDate())) {
                    return (BudgetRow.convertDateToInt(o1.getDate()) >
                            BudgetRow.convertDateToInt(o2.getDate())) ? 1 : -1;
                } // else its equal
            } else if (type == SortType.ALPHABETICAL){
                return o1.getName().compareTo(o2.getName());
            } else if (type == SortType.BY_MONEY ||
                    type == SortType.BY_MONEY_ABS) {
                // Get desired values, BY_MONEY_ABS = Absolute value of money
                double o1money = (type == SortType.BY_MONEY) ?
                        o1.getMoneyValue(): Math.abs(o1.getMoneyValue());
                double o2money = (type == SortType.BY_MONEY) ?
                        o2.getMoneyValue() : Math.abs(o2.getMoneyValue());
                if (o1money != o2money) {
                    return (o1money > o2money) ? 1 : -1;
                } // else its equal
            }
            return 0;
        });
    }

    /**
     * Scan all rows to get a vector of BudgetRows.
     * @return Vector of BudgetRows.
     */
    public Vector<BudgetRow> getRowsVector() {
        Vector<BudgetRow> vec = new Vector<>();
        // TODO: Find a more efficient way to do this process
        for (int r = 0; r < model.getRowCount(); r++) {
            BudgetRow budgetRow = new BudgetRow();
            for (int col = 0; col < model.getColumnCount(); col++) {
                budgetRow.setProperField(col,
                        (String) model.getValueAt(r, col));
            }
            vec.add(budgetRow);
        }
        return vec;
    }
}
