import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * FileFilter for *.mbf files.
 */
public class MBFFilter extends FileFilter {
    public static final String EXTENSION = "mbf";

    @Override
    public boolean accept(File f) {
        String[] split = f.getName().split(".");
        String last = split[split.length - 1];
        if (last.toLowerCase() == EXTENSION) {
            return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Monthly Budget File";
    }

}
