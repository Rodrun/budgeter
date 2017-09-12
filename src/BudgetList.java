import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Vector;

/**
 * The table model for a BudgetTable.
 */
public class BudgetList extends DefaultTableModel {

    private static final String[] HEADERS = {
            "Date",
            "CategoryList",
            "Name",
            "$",
            "Remove"
    };

    public enum SortType {
        ALPHABETICAL, // Sort alphabetically
        BY_DATE, // Sort by date
        BY_MONEY, // Sort by money amount (including if it is negative)
        BY_MONEY_ABS // Sort by absolute money (ignores if positive or negative)
    }


    /**
     * Create a BudgetList with translations and starting CategoryList.
     * @param translator Translator to use.
     */
    public BudgetList(Translator translator) {
        setColumnIdentifiers(translator.translate(HEADERS));
    }

    // No empty constructor
    private BudgetList() { }

    /**
     * Clear the budget list.
     */
    public void clear() {
        setRowCount(0);
    }

    /**
     * Add a budget row.
     * @param b A BudgetRow.
     */
    public void addBudget(BudgetRow b) {
        addRow(b.getRowData());
    }

    /**
     * Remove a row.
     * @param index Index of the row too remove.
     */
    public void removeBudget(int index) {
        removeRow(index);
    }

    /**
     * Sort the budget list.
     * @param type Method to sort.
     */
    public void sort(SortType type) {
        getRowsVector().sort((BudgetRow o1, BudgetRow o2) -> {
            // 1 = >, -1 = <, 0 = EQUAL
            if (type == SortType.BY_DATE) {
                // Get 'values' of dates (concatenated month and day)
                if (o1.getDay() != o2.getDay()) {
                    return (o1.getDay() >
                            o2.getDay()) ? 1 : -1;
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
     * Get the BudgetRows in the list in the form of a Vector object.
     * @return Vector of BudgetRows.
     */
    public Vector<BudgetRow> getRowsVector() {
        Vector<BudgetRow> vec = new Vector<>(getRowCount());
        for (int r = 0; r < getRowCount(); r++) {
            BudgetRow budgetRow = new BudgetRow();
            for (int col = 0; col < getColumnCount(); col++) {
                budgetRow.setProperField(col, getValueAt(r, col));
            }
            vec.add(budgetRow);
        }
        return vec;
    }

    /**
     * Get the BudgetRows in the list.
     * @return Array of BudgetRows.
     */
    public BudgetRow[] getRows() {
        return getRowsVector().toArray(new BudgetRow[getRowCount()]);
    }

    /**
     * Get the total amount of expenses by category. If expenses are negative,
     * this means $X has been spent, otherwise if positive, $X have been
     * gained.
     * @param c The CategoryList to use.
     * @return HashMap of expenses by type.
     */
    public HashMap<String, Double> getExpenseByCategory(CategoryList c) {
        HashMap<String, Double> map = new HashMap<>(c.size());
        for (BudgetRow row : getRowsVector()) {
            map.put(row.getCategory(), map.getOrDefault(row.getCategory(), 0d) +
                    row.getMoneyValue());
        }
        return map;
    }

    @Override
    public String toString() {
        return getRows().toString();
    }

}
