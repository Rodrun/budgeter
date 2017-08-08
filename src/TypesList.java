import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A TypesList holds a list of available "types," which are used to
 * categorize expenses.
 */
public class TypesList {

    private List<String> list;
    private List<TypesChangeListener> listeners = new ArrayList<>();

    /**
     * Create a new instance of TypesList with an initial list.
     * @param i Initial list of types.
     */
    public TypesList(String[] i) {
        setList(i);
    }

    /**
     * Create a new instance with an empty initial list.
     */
    public TypesList() {
        setList(new String[] { });
    }

    /**
     * Get the list of types.
     * @return List of types.
     */
    public final List<String> getList() {
        return list;
    }

    /**
     * Set the types list. Notifies change listeners.
     * @param o New list to set to.
     */
    public void setList(List<String> o) {
        list = o;
        fireTypesChanged();
    }

    /**
     * Set the types list. Notifies change listeners. Will not add empty
     * strings as types.
     * @param sl String array to set the list to.
     */
    public void setList(String[] sl) {
        list = new ArrayList<>(Arrays.asList(sl));
        // Remove any empty strings in the list
        for (String t : list) {
            if (t.isEmpty()) {
                list.remove(t);
            }
        }
        fireTypesChanged();
    }

    /**
     * Add a type to the list. Notifies change listeners.
     * @param t Type to be added.
     */
    public void add(String t) {
        list.add(t);
        fireTypesChanged();
    }

    /**
     * Remove a type from the list. Notifies change listeners.
     * @param t Type to remove.
     */
    public void remove(String t) {
        list.remove(t);
        fireTypesChanged();
    }

    /**
     * Remove all types from the list. Notifies change listeners.
     */
    public void clear() {
        list.clear();
        fireTypesChanged();
    }

    /**
     * Get the size of the list.
     * @return List size.
     */
    public int size() {
        return list.size();
    }

    /**
     * Get the array of the types list.
     * @return A String array of the list.
     */
    public String[] toArray() {
        return list.toArray(new String[size()]);
    }

    /**
     * Notify the listeners.
     */
    private void fireTypesChanged() {
        listeners.forEach(listener -> {
            listener.typesChanged();
        });
        Logger.getAnonymousLogger().log(Level.INFO, "Listeners informed.");
    }

    /**
     * Add a TypesChangeListener to listen for modifications to the list.
     * @param l TypesChangeListener to add.
     */
    public void addTypesChangeListener(TypesChangeListener l) {
        listeners.add(l);
    }

}
