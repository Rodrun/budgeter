import javax.swing.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Juan on 6/28/17.
 *
 * Budgeter: A basic budgeting tool.
 *
 * Purpose: A tool that tracks your spending and earnings throughout
 * a month and ensure that it is within your budget.
 */

public class Main {

    public static void main(String[] args) {
        MainWindow window = new MainWindow();
        window.setTitle("Budgeter");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //window.setSize(600, 450);
        window.pack();
        //window.setMinimumSize(window.getSize());
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private static Vector<String> createStringVector(String[] s) {
        Vector<String> vec = new Vector<>();
        for (String item : s) {
            vec.add(item);
        }
        return vec;
    }
}
