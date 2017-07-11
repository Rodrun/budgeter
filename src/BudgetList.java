import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

/**
 * The GUI budget table.
 */
public class BudgetList extends JTable {

    private Vector<BudgetRow> budget;
    private DefaultTableModel model;

    public enum SortType {
        ALPHABETICAL, // Sort alphabetically
        BY_DATE, // Sort by date
        BY_MONEY, // Sort by money amount (ignores +/-)
        BY_MONEY_NO_ABS // Sort by money amount (including if it is negative)
    }

    public BudgetList() {
        model = (DefaultTableModel) this.getModel();
    }

    /**
     * Clear the budget list.
     * @return A copy of Vector of Budgets that will be cleared.
     */
    public Vector<BudgetRow> clear() {
        Vector<BudgetRow> copy = new Vector<>(budget);
        budget.clear();
        return copy;
    }

    /**
     * Add a budget row.
     * @param b A BudgetRow.
     */
    public void addBudget(BudgetRow b) {
        budget.add(b);
        model.addRow(b.getRowData());
    }

    public void setBudget(Vector<BudgetRow> bud) {
        budget = bud;
    }

    /**
     * Read a string chunk containing a budget list.
     * @param chunk String data of a budget list.
     * @throws IllegalStateException - Thrown by Scanner.nextLine().
     * @throws NoSuchElementException - Thrown by Scanner.nextLine().
     */
    public void readBudget(String chunk) throws IllegalStateException,
            NoSuchElementException {
        try {
            Scanner scanner = new Scanner(chunk);
            while (scanner.hasNext()) {
                budget.add(BudgetRow.readLine(scanner.nextLine()));
            }
        } catch (NoSuchElementException|IllegalStateException e) {
            throw e;
        }
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (BudgetRow bud : budget) {
            string.append(bud.toString());
        }
        return string.toString();
    }

    /**
     * Sort the budget list.
     */
    public void sort(SortType type) {
        budget.sort((BudgetRow o1, BudgetRow o2) -> {
            // 1 = >, -1 = <, 0 = ==
            if (type == SortType.BY_MONEY_NO_ABS ||
                    type == SortType.BY_MONEY) {
                double o1money_real = (type == SortType.BY_MONEY) ?
                        o1.money : Math.abs(o1.money);
                double o2money_real = (type == SortType.BY_MONEY) ?
                        o2.money : Math.abs(o2.money);
                if (o1money_real != o2money_real) {
                    return (o1money_real > o2money_real) ? 1 : -1;
                } // else its equal
            } else if (type == SortType.BY_DATE) {
                if (BudgetRow.convertDateToInt(o1.date) !=
                        BudgetRow.convertDateToInt(o2.date)) {
                    return (BudgetRow.convertDateToInt(o1.date) >
                            BudgetRow.convertDateToInt(o2.date)) ? 1 : -1;
                } // else its equal
            } else {
                // TODO: implement alphabetical sort (possibly)
                return 0;
            }
            return 0;
        });
    }

}
