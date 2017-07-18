import javax.swing.event.TableModelEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handle all budgeting functions and the tables.
 */
public class Budget {

    public static final int FIXED = 0;
    public static final int VARIABLE = 1;

    private BudgetList[] lists;
    private List<BudgetEventListener> listeners = new ArrayList<>();
    private double budget = 0;

    /**
     * Initialize a budget handler.
     * @param fixed Fixed BudgetList.
     * @param variable Variable BudgetList.
     */
    public Budget(BudgetList fixed, BudgetList variable) {
        lists = new BudgetList[]{ fixed, variable };
        addListeners();
        update();
    }

    /**
     * Add listeners to the budget lists.
     */
    private void addListeners() {
        for (BudgetList list : lists) {
            list.addTableModelListener(e -> {
                // TODO: Add more budget-related stuff
                if (e.getType() == TableModelEvent.UPDATE ||
                        e.getType() == TableModelEvent.DELETE) {
                    //fireBudgetEvent();
                    update();
                }
            });
        }
    }

    /**
     * Notify all listeners that a budget event occurred.
     */
    private void fireBudgetEvent() {
        for (BudgetEventListener listener : listeners) {
            listener.eventOccurred();
        }
    }

    /**
     * Add a BudgetEventListener to listen for changes.
     * @param listener BudgetEventListener.
     */
    public void addBudgetEventListener(BudgetEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Add a row to a list.
     * @param which BudgetList to add to.
     * @param row Row information.
     */
    public void addBudget(int which, BudgetRow row) {
        lists[which].addBudget(row);
        update();
    }

    /**
     * Remove a row from a budget list.
     * @param which The budget list to remove a row.
     * @param row
     */
    public void removeRow(int which, int row) {
        lists[which].removeBudget(row);
        update();
    }

    /**
     * Set the budget amount.
     * @param budget The budget amount, will use absolute value.
     */
    public void setBudget(double budget) {
        this.budget = Math.abs(budget);
        update();
    }

    /**
     * Get the budget amount.
     * @return The budget amount.
     */
    public double getBudget() {
        return budget;
    }

    /**
     * Get the total of money spent.
     *
     * This will not update the money spent, update() must be called to update
     * the amount.
     * @return Last available known money spent.
     */
    public double getMoneySpent() {
        double spent = 0;
        for (BudgetList list : lists) {
            for (BudgetRow row : list.getRowsVector()) {
                spent += row.getMoneyValue();
            }
        }
        return spent;
    }

    /**
     * Get the remaining amount of money left in the budget.
     * @return Remaining budget.
     */
    public double getRemainingBudget() {
        return getBudget() - getMoneySpent();
    }

    /**
     * Update budget information. Preferably to be called after changes made
     * to any of the budget lists or any other budget-related value changes.
     */
    public void update() {
        fireBudgetEvent();
    }

    /**
     * Completely reset everything.
     */
    public void clear() {
        for (BudgetList list : lists) {
            list.clear();
        }
        setBudget(0); // will call update()
    }

    /**
     * Get the BudgetRows of a BudgetList.
     * @param which Which BudgetList.
     * @return Vector of BudgetRows.
     */
    public final Vector<BudgetRow> getBudgetRows(int which) {
        return lists[which].getRowsVector();
    }

}
