/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package numerical;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

/**
 *
 * @author ASUS
 */
//https://www.cs.tut.fi/lintula/manual/java/tutorial/uiswing/misc/timer.html
//https://www.cs.tut.fi/lintula/manual/java/tutorial/uiswing/painting/animLoop.html
//control the speed of rerendering of component
public class TimerHandler extends JFrame {

    private boolean testColorBlack = true;
    private boolean thickLine = false;
    private int X = 50;
    private int Y = 50;
    private int maxX = 1000;
    private int maxY = 900;
    private int minX = 0;
    private int minY = 0;

    private int temporaryStore = 0;

    private int i = 0;
    private int j = 0;

    private int theTempXMouse = 0;
    private int theTempYMouse = 0;

    private int thePrevTempXMouse = 0;
    private int thePrevTempYMouse = 0;

    private boolean drawControl = false;

    private boolean[][] screen = null;
    private ArrayList<Point> point = new ArrayList<>();
    private ArrayList<Boolean> shouldConnect = new ArrayList<>();

    final drawPanel myDrawPanel;

    public TimerHandler() {
        myDrawPanel = new drawPanel();
        MyKeyListener myKeyListener = new MyKeyListener();

        this.add(myDrawPanel);
        addKeyListener(myKeyListener);
        this.setVisible(true);

        int windowXSize = maxX * 125 / 100;
        int windowYSize = maxY * 125 / 100;

        screen = new boolean[maxX][maxY];
        initializeScreen();

        this.setSize(windowXSize, windowYSize);

        MouseHandler myHandler = new MouseHandler();
        myDrawPanel.addMouseListener(myHandler);
        myDrawPanel.addMouseMotionListener(myHandler);

        myDrawPanel.setVisible(true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(
                new WindowAdapter() {
            // exit when window has closed
            public void windowClosed(WindowEvent event) {
                System.exit(0);
            } // end method windowClosed
        } // end WindowAdapter inner class
        ); // end call to addWindowListener
    }

    private class MouseHandler implements MouseListener, MouseMotionListener, ActionListener {

        int frameNumber = -1;
//    Timer timer;
        boolean frozen = true;
        JLabel label;

        //In initialization code: 
        //From user-specified frames-per-second value, determine
        //how long to delay between frames.
        int fps = 50;
        int delay = (fps > 0) ? (1000 / fps) : 100;
        //Set up a timer that calls this object's action handler.
        Timer timer = new Timer(delay, this);

        //Set up the components in the GUI.
        public synchronized void startAnimation() {

            if (frozen) {
                //Do nothing.  The user has requested that we
                //stop changing the image.
            } else {
                //Start animating!
                timer.start();
                System.out.println("Start animation");
            }

        }

        public synchronized void stopAnimation() {

            timer.stop();
            System.out.println("Stop animation");

        }

        @Override
        public void actionPerformed(ActionEvent e) {
//            //Advance the animation frame.
//            frameNumber++;
//
//            //Request that the frame be painted.
//            label.setText("Frame " + frameNumber);

            Random r = new Random();
            theTempXMouse = r.nextInt(800);
            theTempYMouse = r.nextInt(800);
            screen[theTempXMouse][theTempYMouse] = true;
            myDrawPanel.repaint();
            System.out.println("Timer Delay : " + timer.getDelay());

        }

        //When the application's GUI appears:
        //startAnimation();
        public void mouseClicked(MouseEvent event) {
        }//end public void mouseClicked(mouseEvent event)

        //In a mouse listener registered on the animating component:
        public void mousePressed(MouseEvent event) {
            thePrevTempXMouse = theTempXMouse;
            thePrevTempYMouse = theTempYMouse;

            theTempXMouse = event.getX();
            theTempYMouse = event.getY();

            if (frozen) {
                frozen = false;
                startAnimation();
            } else {
                frozen = true;
                stopAnimation();
            }

        }//end public void mousePressed(mouseEvent event)

        public void mouseReleased(MouseEvent event) {
            drawControl = false;
            theTempXMouse = event.getX();
            theTempYMouse = event.getY();
            if (theTempXMouse > 0 && theTempXMouse < maxX && theTempYMouse > 0 && theTempYMouse < maxY) {
//                screen[theTempXMouse][theTempYMouse] = true;
//                point.add(new Point(theTempXMouse, theTempYMouse));
//                shouldConnect.add(false);

                myDrawPanel.repaint();
            }

        }//end public void mouseReleased(mouseEvent event)

        public void mouseEntered(MouseEvent event) {
            theTempXMouse = 0;
            theTempYMouse = 0;
        }//end public void mouseEntered(mouseEvent event)

        public void mouseExited(MouseEvent event) {
            theTempXMouse = 0;
            theTempYMouse = 0;
        }//end public void mouseExited(mouseEvent event)

        public void mouseDragged(MouseEvent event) {
            theTempXMouse = event.getX();
            theTempYMouse = event.getY();
            if (theTempXMouse > 0 && theTempXMouse < maxX && theTempYMouse > 0 && theTempYMouse < maxY) {
//                screen[theTempXMouse][theTempYMouse] = true;
//                point.add(new Point(theTempXMouse, theTempYMouse));
//                shouldConnect.add(true);

                myDrawPanel.repaint();
            }
        }//end public void mouseDragged(mouseEvent event)

        public void mouseMoved(MouseEvent event) {
            //theTempXMouse = event.getX();
            //theTempYMouse = event.getY();
        }//end public void mouseMoved(mouseEvent event)
    }//end private class MouseHandler implements MouseListener, MouseMotionListener

    private class MyKeyListener implements KeyListener {

        public void keyPressed(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                    initializeScreen();
                    myDrawPanel.repaint();
                    break;
                case KeyEvent.VK_INSERT:
                    if (testColorBlack == true) {
                        testColorBlack = false;
                    } else {
                        testColorBlack = true;
                    }
                    myDrawPanel.repaint();
                    break;
                case KeyEvent.VK_K:
                    if (thickLine == true) {
                        thickLine = false;
                    } else {
                        thickLine = true;
                    }
                    myDrawPanel.repaint();
                    break;
            }
        }

        public void keyReleased(KeyEvent event) {
        }

        public void keyTyped(KeyEvent event) {
        }
    }

    private class ActionHandler implements ActionListener {

        int frameNumber = -1;
//    Timer timer;
        boolean frozen = false;
        JLabel label;

        //In initialization code: 
        //From user-specified frames-per-second value, determine
        //how long to delay between frames.
        int fps = 5;
        int delay = (fps > 0) ? (1000 / fps) : 100;
        //Set up a timer that calls this object's action handler.
        Timer timer = new Timer(delay, this);

        //Set up the components in the GUI.
        public synchronized void startAnimation() {

            if (frozen) {
                //Do nothing.  The user has requested that we
                //stop changing the image.
            } else {
                //Start animating!
                timer.start();

            }

        }

        public synchronized void stopAnimation() {

            timer.stop();

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //Advance the animation frame.
            frameNumber++;

            //Request that the frame be painted.
            label.setText("Frame " + frameNumber);

            Random r = new Random();
            theTempXMouse = r.nextInt(500);
            theTempYMouse = r.nextInt(500);
            screen[theTempXMouse][theTempYMouse] = true;
        }

        //In a mouse listener registered on the animating component:
        public void mousePressed(MouseEvent e) {
            if (frozen) {
                frozen = false;
                startAnimation();
            } else {
                frozen = true;
                stopAnimation();
            }
        }

        //When the application's GUI appears:
        //startAnimation();
    }

    private class drawPanel extends JPanel {

        private int width = 10;
        private int height = 10;
        private BasicStroke strokeWidth = new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        public void paint(Graphics grap) {
            super.paint(grap);

            if (thickLine == true) {
                width = 30;
                height = 30;
                strokeWidth = new BasicStroke(10.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            } else {
                width = 10;
                height = 10;
                strokeWidth = new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            }

            //System.out.println("Repaint");
            for (i = 0; i < maxX; i++) {
                for (j = 0; j < maxY; j++) {
                    if (screen[i][j] == true) {
                        if (testColorBlack == true) {
                            grap.setColor(Color.RED);
                        } else {
                            grap.setColor(Color.BLUE);
                        }
                        grap.fillOval(i, j, width, height);
                        //System.out.println("BLACK " + i + "  " + j);
                    }
                }
            }
//
//            Graphics2D g2d = (Graphics2D) grap;
//            for (i = 0; i < point.size() - 1; i++) {  //draw line to connect all dot
//                if (shouldConnect.get(i)) {
//                    g2d.setStroke(strokeWidth);
//                    grap.drawLine(point.get(i).x, point.get(i).y, point.get(i + 1).x, point.get(i + 1).y);
//                }
//            }
        }

    }

    private void initializeScreen() {
        for (i = 0; i < maxX; i++) {
            for (j = 0; j < maxY; j++) {
                screen[i][j] = false;
            }
        }
        point.clear();
        shouldConnect.clear();
    }

    public static void main(String args[]) {
        new TimerHandler();
    }

}
