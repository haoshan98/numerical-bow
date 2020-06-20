/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package create_player;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.WindowConstants;
import javax.swing.JFrame;
import java.lang.Math.*;

public class playerGraphics extends Component implements MouseListener, MouseMotionListener {

    int sX = -1, sY = -1;
    static Label stat;
    Image bImage;
    boolean dragging = false;
    int curX = 300, curY = 190;
    int maxX, maxY_upper, maxY_lower, minX;

    public static void main(String[] av) {

        JFrame jFrame = new JFrame("Mouse Dragger");

        Container cPane = jFrame.getContentPane();

        playerGraphics sk = new playerGraphics();

        cPane.setLayout(new BorderLayout());

        cPane.add(BorderLayout.NORTH, new Label(""));

        cPane.add(BorderLayout.CENTER, sk);

        cPane.add(BorderLayout.SOUTH, stat = new Label());

        stat.setSize(jFrame.getSize().width, stat.getSize().height);

        jFrame.setSize(900, 900);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        jFrame.pack();
        jFrame.setVisible(true);

    }

    public playerGraphics() {
        super();
        setSize(300, 200);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void showStatus(String s) {

        stat.setText(s);
    }

    @Override
    public void mouseClicked(MouseEvent event) {
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        dragging = false;
    }

    @Override
    public void mouseEntered(MouseEvent event) {
    }

    @Override
    public void mouseExited(MouseEvent event) {
    }

    @Override
    public void mousePressed(MouseEvent event) {
//  use for shooting mechanism
        Point point = event.getPoint();
        System.out.println("mousePressed at " + point);
        sX = point.x;
        sY = point.y;
        dragging = true;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

//        Point p = event.getPoint();
//        // System.err.println("mouse drag to " + p);
//        showStatus("mouse Dragged to " + p);
//        curX = p.x;
//        curY = p.y;
        Point p = e.getPoint();
//        showStatus("Mouse move to " + e.getPoint());
//        System.out.println("Move to " + e.getPoint());
        curX = p.x;
        curY = p.y;
        System.out.println("position of curX " + curX);
        System.out.println("position of curY " + curY);

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        Point p = e.getPoint();
////        showStatus("Mouse move to " + e.getPoint());
////        System.out.println("Move to " + e.getPoint());
//        curX = p.x;
//        curY = p.y;
//        System.out.println("position of curX " + curX);
//        System.out.println("position of curY " + curY);
//
//        repaint();

    }

    double getDistance(int x1, int y1, int x2, int y2) {
        System.out.println("Distance x: " + (x2 - x1));
        System.out.println("Distance y: " + (y2 - y1));
        double distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        System.out.println("Lines differences " + distance);
        return distance;
    }

    int[] drawLinesLeft(Graphics2D g2d, Stroke stroke) {
//        Graphics2D g2d = (Graphics2D) graphics;
//        Stroke stroke = new BasicStroke(3f);
        g2d.setStroke(stroke);
        int newX = 0, newY = 0;
        int minX = 270, maxX = 335, maxY = 220, minY = 160;
        if (curX <= minX) { //range
            if (curY >= maxY) {
                System.out.println("c1.1");
                newX = minX;
                newY = maxY;
            } else if (curY <= minY) {
                System.out.println("c1.2");
                newX = minX;
                newY = minY;
            } else {
                System.out.println("c1");
                newX = minX - 10;
                newY = curY;
            }
        } else if (curY >= maxY) { //range = 20
            System.out.printf("c2");
            if (curX >= maxX) {
                newX = maxX;
                newY = maxY;
            } else {
                newY = maxY;
                newX = curX;
            }
        } else if (curY <= minY) { //range = 20
            System.out.println("c3");
            if (curX >= maxX) {
                newX = maxX;
                newY = maxY;
            } else {
                newY = minY;
                newX = curX;
            }
        } else if ((curX > maxX && curY > minY && curY < maxY)) {
            System.out.println("c45");
            newY = curY;
            newX = maxX;
        } else if ((curX > minX && curY > minY && curY < maxY)) {
            System.out.println("c4");
            newY = curY;
            newX = curX;
        }

        g2d.setColor(Color.black);
        g2d.drawLine(newX, newY, 300, 180);
        g2d.setColor(Color.black);

        g2d.drawLine(newX, newY, 300, 200);

        int[] points = new int[2];

        points[0] = newX;
        points[1] = newY;
        return points;
    }

    void drawLinesRight(int[] points, Graphics2D g2d, Stroke stroke) {
        int minY = 185;
        int oppositeY = points[1], oppositeX = points[0];
        int distance = oppositeY - minY;
        int newX = 335;
        int newY = 0;
//        int newY = minY - distance + 5;

        if (distance >= 0 && oppositeX <= 300) {
            newX += 35;
            newY = minY - distance + 5;
        } else if (distance <= 0 && oppositeX <= 300) {
            newX += 30;
            newY = minY - distance;
        } else if (oppositeX > 300) {
            newX = 335;
            newY = 185;
        }

        g2d.getStroke();
        g2d.setColor(Color.black);
        g2d.drawLine(335, minY, newX, newY);

    }

//    double[] getNewPoint(int curX, int curY, int initialX, int initialY, double length){ // return two integers
//        double newPoints[] = new double[2];
//        double vectorX = curX - initialX;
//        double vectorY = curY - initialY;
//        double gradient = vectorY / vectorX;  // straight line eqm y = mx + c
//        double norm = Math.sqrt((Math.pow(vectorX,2) + Math.pow(vectorY,2)));
//        double unitVectorX = curX / norm;
//        double unitVectorY = curY / norm;
//
//        newPoints[0] = length * unitVectorX ;
//        newPoints[1] = length * unitVectorY ;
//        return newPoints;
//    }
    // TODO initial point x1 x2
    @Override
    public void paint(Graphics graphic) {
        System.out.println("position of curX " + curX);
        System.out.println("position of curY " + curY);
        // player
        graphic.setColor(Color.BLACK);
        graphic.fillOval(300, 140, 35, 35);

        graphic.setColor(Color.BLACK);
        graphic.fillRect(300, 180, 35, 50);

        Graphics2D g2d = (Graphics2D) graphic;
        Stroke stroke = new BasicStroke(3f);

        int[] pointsLeft = drawLinesLeft(g2d, stroke);
        drawLinesRight(pointsLeft, g2d, stroke);

    }
}
