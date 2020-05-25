package archieved;

import numerical_bow.*;
import java.awt.BorderLayout;
import javax.swing.*;

public class MainFrame {

    public static void main(String args[]) {
        
        JFrame frame = new JFrame("Bow Shooting Game");
        BorderLayout layout = new BorderLayout();
        NumericalBow bowPanel = new NumericalBow();
        frame.setLayout(layout);

        JScrollPane gameboard = new JScrollPane(bowPanel);
//        gameboard.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//        gameboard.setHorizontalScrollBar(new JScrollBar(JScrollBar.HORIZONTAL));

        frame.getContentPane().add(gameboard, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
