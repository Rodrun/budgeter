import javax.swing.event.TableModelEvent;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handle all budgeting functions and the tables.
 */
public class BudgetHandler {

    /**
     * Index of which BudgetList to use.
     */
    public enum Which {
        FIXED(0, "Fixed"),
        VARIABLE(1, "Variable");

        private int index;
        private String name;

        Which(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        /**
         * Get appropriate enum based on the given index.
         * @param index
         * @return Which enum.
         */
        public static Which get(int index) {
            return (index == VARIABLE.index) ? VARIABLE : FIXED;
        }
    }

    public static final String[] DEFAULT_TYPE_ARRAY = new String[]{
            "Rent",
            "Insurance",
            "Utility",
            "Groceries",
            "Loan",
            "Clothing",
            "Internet/phone",
            "Misc."
    };
    /**
     * The budget types available. Types allow the user to categorize
     * their expenses. The default value is DEFAULT_TYPE_ARRAY.
     */
    public static CategoryList types = new CategoryList(DEFAULT_TYPE_ARRAY);

    private BudgetList[] lists;
    private List<BudgetEventListener> listeners = new ArrayList<>(2);
    private double budget = 0;

    /**
     * Creates a BudgetHandler with given BudgetLists.
     *
     * @param fixed    Fixed BudgetList.
     * @param variable Variable BudgetList.
     */
    public BudgetHandler(BudgetList fixed, BudgetList variable) {
        lists = new BudgetList[]{fixed, variable};
        addListeners();
        update();
    }

    /**
     * Creates a BudgetHandler with empty BudgetLists.
     */
    public BudgetHandler() {
        lists = new BudgetList[]{ new BudgetList(), new BudgetList() };
        addListeners();
        update();
    }

    /**
     * Set to a new BudgetHandler.
     */
    public void copy(BudgetHandler other) {
        lists = other.lists;
        listeners = other.listeners;
        budget = other.budget;
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
                    //notifyListeners();
                    update();
                }
            });
        }
    }

    /**
     * Notify all listeners that a budget event occurred.
     */
    private void notifyListeners() {
        for (BudgetEventListener listener : listeners) {
            listener.eventOccurred();
        }
    }

    /**
     * Add a BudgetEventListener to listen for changes.
     *
     * @param listener BudgetEventListener.
     */
    public void addBudgetEventListener(BudgetEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Add a row to a list.
     *
     * @param which BudgetList to add to.
     * @param row   Row information.
     */
    public void addBudget(Which which, BudgetRow row) {
        lists[which.index].addBudget(row);
        update();
    }

    /**
     * Remove a row from a budget list.
     *
     * @param which The budget list to remove a row.
     * @param row
     */
    public void removeRow(Which which, int row) {
        lists[which.index].removeBudget(row);
        update();
    }

    /**
     * Set the budget amount.
     *
     * @param budget The budget amount, will use absolute value.
     */
    public void setBudget(double budget) {
        this.budget = Math.abs(budget);
        update();
    }

    /**
     * Get the budget amount.
     *
     * @return The budget amount.
     */
    public double getBudget() {
        return budget;
    }

    /**
     * Get the total of money spent.
     * <p>
     * This will not update the money spent, update() must be called to update
     * the amount.
     *
     * @return Last available known money spent.
     */
    public double getMoneySpent() {
        double spent = 0;
        for (BudgetList list : lists) {
            for (BudgetRow row : list.getRowsVector()) {
                spent -= row.getMoneyValue();
            }
        }
        return spent;
    }

    /**
     * Get the remaining amount of money left in the budget.
     *
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
        notifyListeners();
    }

    /**
     * Completely reset everything.
     */
    public void clear() {
        Logger.getAnonymousLogger().log(Level.INFO, "Clearing lists.");
        for (BudgetList list : lists) {
            list.clear();
        }
        setBudget(0); // will call update()
    }

    /**
     * Get the BudgetRows of a BudgetList.
     *
     * @param which Which BudgetList.
     * @return Array of BudgetRows.
     */
    public BudgetRow[] getBudgetRows(Which which) {
        return lists[which.index].getRows();
    }

    /**
     * Get the BudgetRows of a BudgetList, but only of one category.
     * @param category Category of rows to return.
     * @param which Which BudgetList.
     * @return Array of BudgetRows from one category.
     */
    public BudgetRow[] getBudgetRows(String category, Which which) {
        ArrayList<BudgetRow> list =
                new ArrayList<>(lists[which.index].getRowCount());
        for (BudgetRow row : getBudgetRows(which)) {
            if (row.getCategory() == category) {

            }
        }
        return list.toArray(new BudgetRow[list.size()]);
    }

    /**
     * Get the amount of expenses by type for BOTH BudgetLists.
     * @return Map of expenses by type.
     */
    public HashMap<String, Double> getExpenseByType() {
        HashMap<String, Double> map = new HashMap<>(types.size());
        for (BudgetList list : lists) {
            map.putAll(list.getExpenseByCategory());
        }
        return map;
    }

    /**
     * Get the amount of expenses by type of a BudgetList.
     * @param which Which BudgetList.
     * @return HashMap of expenses by type, or null
     * @throws IndexOutOfBoundsException If which is invalid.
     */
    public HashMap<String, Double> getExpenseByType(Which which) {
        return lists[which.index].getExpenseByCategory();
    }

    /**
     * Get the amount of expenses of a type from a BudgetList. Each expense
     * will be paired to its day of the month (Integer value).
     * @param which Which BudgetList.
     * @return HashMap of expenses of every month day from a given type.
     */
    public HashMap<Integer, Double> getDatedExpenses(String type, Which which) {
        HashMap<Integer, Double> map =
                new HashMap<>(lists[which.index].getRowCount());
        boolean[] daysSet = new boolean[FormattedDate.getMaxMonthDays()];
        for (BudgetRow row : getBudgetRows(which)) {
            map.put(row.getDay(), map.getOrDefault(row.getDay(), 0d) +
                row.getMoneyValue());
            daysSet[row.getDay()] = true;
        }
        // Ensure that days without any expenses are set to 0
        for (int i = 0; i < daysSet.length; i++) {
            if (!daysSet[i]) {
                map.put(i, 0d);
            }
        }
        return map;
    }

    /**
     * Get a BudgetList.
     * @param which Which BudgetList.
     * @return Appropriate BudgetList.
     */
    public BudgetList getBudgetList(Which which) {
        return lists[which.index];
    }

}
