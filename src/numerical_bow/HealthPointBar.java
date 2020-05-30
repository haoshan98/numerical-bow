/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package numerical_bow;

import java.awt.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
/**
 *
 * @author ultra
 */
public class HealthPointBar {
    JProgressBar healthBar;
    
    public static void main(String args[]) {

        JFrame frame = new JFrame("JFrame Example");  
        JPanel panel = new JPanel();  
        panel.setLayout(new FlowLayout());  
        JLabel label = new JLabel("JFrame By Example");  
        JButton button = new JButton();  
        button.setText("Button");  
        panel.add(label);  
        panel.add(button);  
        frame.setSize(500, 500);  
        frame.setLocationRelativeTo(null);  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        
        
        
        JPanel healthBarPanel;
        healthBarPanel= new JPanel();
        //input
        int Xpoint=100;int Ypoint= 100;int width=300;int height=30;
        healthBarPanel.setBounds(Xpoint, Ypoint, width, height);
        System.out.print(healthBarPanel.getSize());
        System.out.print(healthBarPanel.getLocation());
        JProgressBar healthBar = new JProgressBar(0,100);
        healthBar.setPreferredSize(new Dimension(width,height));
        healthBar.setBackground(Color.red);
        healthBar.setForeground(Color.green);
        healthBarPanel.add(healthBar);
        //set current hp
        healthBar.setValue(6);
        frame.add(healthBarPanel);
        

        frame.setVisible(true);  
        healthBar.setValue(50);
    }
    public HealthPointBar(int percentageToChange){
        
    }
    public void changeHealthBar(int CurrentHealth){
        healthBar.setValue(CurrentHealth);
    }
}
    