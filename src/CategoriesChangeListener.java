import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.EventListener;

/**
 * Listener for a modification in the CategoryList list.
 */
public interface CategoriesChangeListener {
    void typesChanged();
}
