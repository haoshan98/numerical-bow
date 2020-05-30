package graphic_drawing;

import java.awt.BorderLayout;
import javax.swing.*;

public class MainFrame {

    public static void main(String args[]) {
        
        JFrame frame = new JFrame("Graphic Drawing");
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        
        Bow ok = new Bow(new int[]{300,300},1);
        //frame.getContentPane().add(new Bow(), BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
