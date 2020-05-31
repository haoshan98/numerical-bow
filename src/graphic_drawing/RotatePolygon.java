/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphic_drawing;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;

/**
 *
 * @author ASUS
 */
public class RotatePolygon extends Applet implements KeyListener, MouseListener {

    private int[] xpoints = {0, -10, -7, 7, 10};
    private int[] ypoints = {-10, -2, 10, 10, -2};

    private Polygon poly;

    int rotation = 0;

    //applet init event
    public void init() {
        poly = new Polygon(xpoints, ypoints, xpoints.length);

        //init listeners
        addKeyListener(this);
        addMouseListener(this);
    }

    //applet paint event
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        AffineTransform identify = new AffineTransform();

        int width = getSize().width;
        int height = getSize().height;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        g2d.translate(width / 2, height / 2);
        g2d.scale(20, 20);
        g2d.rotate(Math.toRadians(rotation));

        g2d.setColor(Color.red);
        g2d.fillPolygon(poly);
        g2d.setColor(Color.BLUE);
        g2d.draw(poly);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                System.out.println("button 1");
                rotation--;
                if (rotation < 0) {
                    rotation = 359;
                }
                repaint();
                break;
            case MouseEvent.BUTTON3:
                System.out.println("button 3");
                rotation++;
                if (rotation > 360) {
                    rotation = 0;
                }
                repaint();
                break;

        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
