package graphic_drawing;

import java.awt.BorderLayout;
import javax.swing.*;

public class MainFrame {

    public static void main(String args[]) {
        
        JFrame frame = new JFrame("Graphic Drawing");
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);

        frame.getContentPane().add(new graphics(), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
