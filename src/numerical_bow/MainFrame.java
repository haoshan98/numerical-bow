package numerical_bow;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

// create branch commit jiaqi

public class MainFrame {

    public static void main(String args[]) {

        JFrame frame = new JFrame("Bow Shooting Game");
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);
        NumericalBow bowPanel = new NumericalBow();

        JScrollPane gameboard = new JScrollPane(bowPanel);

        gameboard.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        gameboard.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//        gameboard.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);  
//        gameboard.setViewportBorder(new LineBorder(Color.BLACK));  

        frame.getContentPane().add(gameboard, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

}
