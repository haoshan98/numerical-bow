package Numerical_bow_2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.LineBorder;
import javax.swing.Timer;

public class BowGame extends JPanel {

    Timer timer;
    private Graphics2D g2d;
    // Camera View Port
    private Dimension world_size;
    private Dimension viewport_size;
    private int offsetMaxX;
    private int offsetMaxY;
    private int offsetMinX;
    private int offsetMinY;
    private int camX = 0;
    private int camY = 0;
    private int camYAdjust = 0;
    // Game Component
    private final Point playerL;
    private final Point playerR;
    private int leftLife = 1000;
    private int rightLife = 1000;
    public int curX, curY;
    private int landHeight = 5500;  //5500
    private ArrayList<Rectangle> leftArrows = new ArrayList<>();
    private ArrayList<Rectangle> rightArrows = new ArrayList<>();
    // Game Logic
    private boolean isReleased = false;
    private boolean initL = true;
    private boolean initR = true;
    private boolean isLeft = true;
    private boolean isLeftTurn = true;
    private boolean isTouch = false;
    private boolean isAttacked = false;
    //Arrow shape
    private int length = 60;
    private int width = 3;

    // Arrow Shooting
    private double angle = 0;
    private double angleDiff = 0;
    private double angleLast = 0;
    private double angleReleased = 0;
    private double angleL = 0;
    private double angleR = 0;
    private double power = 0;
    private double gravity = 9.81;
    private double vx = 10.0;
    private double vy = -10.0;
    //Arrow Adjust
    private int xStart = 0;
    private int yStart = 0;
    private double MaxAngle = 89;

    // Game Loop
    private int roundCtn = 0;
    private boolean END = false;

    public BowGame() {

        MyKeyListener myKeyListener = new MyKeyListener();
        addKeyListener(myKeyListener);
        this.setVisible(true);
        MouseHandler myHandler = new MouseHandler();
        addMouseListener(myHandler);
        addMouseMotionListener(myHandler);
        this.setVisible(true);

        setBackground(new Color(0xFFEBCD));

        this.viewport_size = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(10000, 10000));
        this.world_size = getPreferredSize();

        //camera view
        this.offsetMaxX = world_size.width - viewport_size.width;
        this.offsetMaxY = world_size.height - viewport_size.height;
        this.offsetMinX = 0;
        this.offsetMinY = 0;

        //Players init
        this.playerL = new Point(1200, landHeight - 100);
        this.playerR = new Point(2200, landHeight - 100);
        this.camYAdjust = 300;
        this.camY = landHeight - camYAdjust;
        this.camX = playerL.x - 120;
    }

    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        g2d = (Graphics2D) gg;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.translate(-camX, -camY);

        createPlayer(g2d, playerL, true);
        createPlayer(g2d, playerR, false);

        createLand(g2d);

        lifeBar(g2d);

        renderLeftArrow();
        renderRightArrow();
        System.out.println("rightLife : " + rightLife);
        System.out.println("leftLife : " + leftLife);
        renderShootedArrow();

    }

    public void createPlayer(Graphics2D g2d, Point p, boolean isLeft) {
        int height = 100;
        int width = 15;

        //body
        g2d.fillOval(p.x, p.y, width, height / 2);
        //head
        g2d.fillOval(p.x, p.y - width, width, width);
        //leg
        g2d.fillRect(p.x + 2, p.y + height / 2, 4, height / 2 - width);
        g2d.fillRect(p.x + width / 2, p.y + height / 2, 4, height / 2 - width);

//        //area
//        Point topLeft = new Point(p.x - 3, p.y - 16);
//        Point topRight = new Point(p.x + 16, p.y - 16);
//        Point bottomLeft = new Point(p.x - 3, p.y + 85);
//        Point bottomRight = new Point(p.x + 16, p.y + 85);
//        g2d.drawLine(topLeft.x, topLeft.y, topRight.x, topRight.y);  //top
//        g2d.drawLine(topLeft.x, topLeft.y, bottomLeft.x, bottomLeft.y);  //left
//        g2d.drawLine(topRight.x, topRight.y, bottomRight.x, bottomRight.y);  //right
//        g2d.drawLine(bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y);  //bottom
//        System.out.println("Area : ");
//        System.out.println("topLeft : " + topLeft.x + " | " + topLeft.y);
//        System.out.println("topRight : " + topRight.x + " | " + topRight.y);
//        System.out.println("bottomLeft : " + bottomLeft.x + " | " + bottomLeft.y);
//        System.out.println("bottomRight : " + bottomRight.x + " | " + bottomRight.y);
        //hand
        Stroke stroke = new BasicStroke(2f);

//        int[] pointsLeft = drawLinesLeft(g2d, stroke, playerL, playerR);
//        drawLinesRight(pointsLeft, g2d, stroke);
//        g2d.fillOval(p.x - width - 10, p.y + width, height / 2 - width, height / width + 2);
//        g2d.fillOval(p.x + width, p.y + width, height / 2 - width, height / width + 2);
        //bow
        //arrow
        if (!isReleased) {
            createArrow(isLeft);
        }

    }

    public void createArrow(boolean isLeft) {
        Point arrowPoint;
        if (initL & isLeft) {
            initL = false;
            arrowPoint = new Point(playerL.x + 20, playerL.y);
            System.out.println("Arrow 0 created");
            leftArrows.add(new Rectangle(arrowPoint.x, arrowPoint.y, length, width));
            renderLeftArrow();

        } else if (initR & !isLeft) {
            initR = false;
            arrowPoint = new Point(playerR.x - 50, playerR.y);
            System.out.println("Arrow 1 created");
            rightArrows.add(new Rectangle(arrowPoint.x, arrowPoint.y, length, width));
            renderRightArrow();
        }

    }

    public void renderLeftArrow() {
        g2d.setColor(Color.BLUE);
        if (isLeftTurn) {
            if (isTouch) {
                g2d.setColor(Color.RED);
            }
        }
        g2d.rotate(Math.toRadians(-angleL), leftArrows.get(leftArrows.size() - 1).x, leftArrows.get(leftArrows.size() - 1).y);
        g2d.draw(leftArrows.get(leftArrows.size() - 1));
        g2d.fill(leftArrows.get(leftArrows.size() - 1));
        g2d.rotate(Math.toRadians(+angleL), leftArrows.get(leftArrows.size() - 1).x, leftArrows.get(leftArrows.size() - 1).y);

    }

    public void renderRightArrow() {
        g2d.setColor(Color.BLUE);
        if (!isLeftTurn) {
            if (isTouch) {
                g2d.setColor(Color.RED);
            }
        }
        g2d.rotate(Math.toRadians(-angleR), rightArrows.get(rightArrows.size() - 1).x, rightArrows.get(rightArrows.size() - 1).y);
        g2d.draw(rightArrows.get(rightArrows.size() - 1));
        g2d.fill(rightArrows.get(rightArrows.size() - 1));
        g2d.rotate(Math.toRadians(+angleR), rightArrows.get(rightArrows.size() - 1).x, rightArrows.get(rightArrows.size() - 1).y);

    }

    public void renderShootedArrow() {
        g2d.setColor(Color.RED);
        for (int i = 0; i < leftArrows.size() - 1; i++) {
            g2d.draw(leftArrows.get(i));
            g2d.fill(leftArrows.get(i));
        }
        for (int i = 0; i < rightArrows.size() - 1; i++) {
            g2d.draw(rightArrows.get(i));
            g2d.fill(rightArrows.get(i));
        }
    }

    public void createLand(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.drawLine(0, landHeight, 10000, landHeight);
    }

    private void updateCamera() {
        if (isLeftTurn) {
//            System.out.println(leftArrows.get(roundCtn).x - camX );
            if (leftArrows.get(leftArrows.size() - 1).x - camX > 500) {
                this.camX = leftArrows.get(leftArrows.size() - 1).x - this.viewport_size.width / 2;
            }
            this.camY = leftArrows.get(leftArrows.size() - 1).y - this.viewport_size.height / 2 + 800;
        } else {
            this.camX = rightArrows.get(rightArrows.size() - 1).x - this.viewport_size.width / 2;
            this.camY = rightArrows.get(rightArrows.size() - 1).y - this.viewport_size.height / 2 + 800;

        }

        if (camX > offsetMaxX) {
            camX = offsetMaxX;
        } else if (camX < offsetMinX) {
            camX = offsetMinX;
        }
        if (camY > offsetMaxY) {
            camY = offsetMaxY;
        } else if (camY < offsetMinY) {
            camY = offsetMinY;
        }
    }

    public void lifeBar(Graphics2D g2d) {
        int lifebarLength = 150;
        Point leftBarLoc = new Point(camX + 120, camY + 20);
        Point rightBarLoc = new Point(camX + 620, camY + 20);
        Point leftPowerLoc = new Point(camX + 120, camY + 50);
        Point rightPowerLoc = new Point(camX + 620, camY + 50);
        Point leftAngleLoc = new Point(camX + 120, camY + 60);
        Point rightAngleLoc = new Point(camX + 620, camY + 60);

        g2d.setColor(Color.BLACK);

        //lifebar
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawRect(leftBarLoc.x, leftBarLoc.y, lifebarLength, 10);
        g2d.drawRect(rightBarLoc.x, rightBarLoc.y, lifebarLength, 10);
        g2d.setColor(Color.BLUE);
        g2d.fillRect(leftBarLoc.x, leftBarLoc.y, (int) (lifebarLength * (leftLife / 1000.0)), 10);
        g2d.fillRect(rightBarLoc.x, rightBarLoc.y, (int) (lifebarLength * (rightLife / 1000.0)), 10);

        g2d.setStroke(new BasicStroke(0.0f));
        g2d.setColor(Color.BLACK);
        if (isLeftTurn) {

            //power
            g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2d.drawString(String.format("Power : %.2f", power), leftPowerLoc.x, leftPowerLoc.y);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2d.drawString("Power : 0.00", rightPowerLoc.x, rightPowerLoc.y);

            //angle
            g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2d.drawString(String.format("Angle : %.2f", angle), leftAngleLoc.x, leftAngleLoc.y);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2d.drawString("Angle : 0.00", rightAngleLoc.x, rightAngleLoc.y);
        } else {

            //power
            g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2d.drawString("Power : 0.00", leftPowerLoc.x, leftPowerLoc.y);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2d.drawString(String.format("Power : %.2f", power), rightPowerLoc.x, rightPowerLoc.y);

            //angle
            g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2d.drawString("Angle : 0.00", leftAngleLoc.x, leftAngleLoc.y);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2d.drawString(String.format("Angle : %.2f", angle), rightAngleLoc.x, rightAngleLoc.y);
        }

        if (END) {
            g2d.setFont(new Font("SansSerif", Font.BOLD, 50));
            g2d.setColor(Color.red);
            g2d.drawString(String.format("Game Over"), camX + 300, camY + 160);
            g2d.setColor(Color.GREEN);
            g2d.drawString(String.format("Player won !"), camX + 300, camY + 230);

        }

    }

    public void shootArrow(ArrayList<Rectangle> arrow) {

        if (!isTouch) {
            arrow.get(arrow.size() - 1).x += vx * 0.5;
            arrow.get(arrow.size() - 1).y += vy * 0.5;

            System.out.println("vx : " + vx + " | vy :" + vy);
            System.out.println("PLx : " + playerL.x + " | PLy :" + playerL.y);
            System.out.println("PRx : " + playerR.x + " | PRy :" + playerR.y);

            vy += gravity / 100;
            if (vx - 0.005 >= power / 2) {
                vx -= 0.005;
            }

            //checkTouch
            if (isLeftTurn) {
                //touch player

                if (arrow.get(arrow.size() - 1).x + length > playerR.x - 3
                        && arrow.get(arrow.size() - 1).x + length < playerR.x + 16
                        && arrow.get(arrow.size() - 1).y < playerR.y + 85
                        && arrow.get(arrow.size() - 1).y > playerR.y - 16) {
                    isTouch = true;
                    isAttacked = true;
                    initL = true;
                    //minus player life
                    rightLife -= 350;
                    System.out.println("Riht player life : " + rightLife);
                    //touch floor    
                } else if (arrow.get(arrow.size() - 1).y > landHeight + 30) {

                    isTouch = true;
                    initL = true;
                }

            } else {
                g2d.drawRect(playerL.x, playerL.y, 5, 50);
                if (arrow.get(arrow.size() - 1).x < playerL.x - 3
                        && arrow.get(arrow.size() - 1).x > playerL.x + 16
                        && arrow.get(arrow.size() - 1).y < playerL.y + 85
                        && arrow.get(arrow.size() - 1).y > playerL.y - 16) {

//                    System.out.println("arrow.get(arrow.size() - 1).x : " + arrow.get(arrow.size() - 1).x);
//                    System.out.println("playerL.x : " + playerL.x);
//                    System.out.println("arrow.get(arrow.size() - 1).y : " + arrow.get(arrow.size() - 1).y);
//                    System.out.println("playerL.y : " + playerL.y);
                    isTouch = true;
                    isAttacked = true;
                    initR = true;
                    leftLife -= 350;
                    System.out.println("here=======");
                    //touch floor    
                } else if (arrow.get(arrow.size() - 1).y > landHeight + 30) {
                    isTouch = true;
                    initR = true;
                }

            }

        } else {
            timer.stop();
            if(rightLife <= 0 || leftLife <= 0){
                END = true;
            }
            isLeftTurn = !isLeftTurn;
            System.out.println("isLeftTurn : " + isLeftTurn);
            angle = 0;
            angleDiff = 0;
            angleLast = 0;
            angleReleased = 0;
            angleL = 0;
            angleR = 0;
            power = 0;
            if (isLeftTurn) {
                vx = 10.0;
            } else {
                vx = -vx;
            }
            vy = -10.0;
            isTouch = false;
            isAttacked = false;

            isReleased = false;

        }
    }

    private class MouseHandler implements MouseListener, MouseMotionListener {

        public void mouseClicked(MouseEvent e) {

        }

        public void mousePressed(MouseEvent e) {
            isTouch = false;
            xStart = e.getX();
            yStart = e.getY();
            repaint();

        }

        public void mouseReleased(MouseEvent e) {

            if (isLeftTurn) {
                timer = new Timer(10, l -> {
                    shootArrow(leftArrows);
                    updateCamera();

                    repaint();
                });
            } else {
                timer = new Timer(10, l -> {
                    shootArrow(rightArrows);
                    updateCamera();

                    repaint();
                });
            }
            timer.start();

        }

        public void mouseEntered(MouseEvent e) {

        }

        public void mouseExited(MouseEvent e) {

        }

        public void mouseDragged(MouseEvent e) {
            // x for power
            int horizontalDrag = (e.getX() - xStart) / 10;
            if (isLeftTurn) {
                horizontalDrag *= -1;
            } else {

            }
            if (horizontalDrag > 0) {
                power = horizontalDrag * (10 + horizontalDrag);
            }
            vx = power;
            vy = -power;

            // y for angle
            int verticalDrag = Math.abs((e.getY() - yStart) / 10);
            angle = verticalDrag * (10 + verticalDrag);
            angleDiff = angle - angleLast;
            if (angle > MaxAngle) {
                angle = MaxAngle;
                angleDiff = 0;
            }
            angleLast = angle;
            angleReleased = angleLast;

            if (isLeftTurn) {
                angleL = angle;
            } else {
                angleR = -angle;
            }

            repaint();

        }

        public void mouseMoved(MouseEvent e) {
        }
    }

    private class MyKeyListener implements KeyListener {

        public void keyPressed(KeyEvent e) {
            repaint();

        }

        public void keyReleased(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }
    }

    public static void main(String args[]) {

        JFrame frame = new JFrame("Bow Shooting Game");
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);
        BowGame bowPanel = new BowGame();

        JScrollPane gameboard = new JScrollPane(bowPanel);
        gameboard.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        gameboard.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        gameboard.setViewportBorder(new LineBorder(Color.BLACK));
        frame.getContentPane().add(gameboard, BorderLayout.CENTER);

//        frame.getContentPane().add(bowPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

}

//     private class Arrow {
//
//        private Point loc;
//        private int length = 50;
//        private int width = 3;
//        private Rectangle myArrow;
//        private double angle;
//        private boolean isStop = false;
//
//        public Arrow(Graphics2D g2d, Point loc, boolean isLeft) {
//
//            this.loc = loc;
//
//            renderArrow(g2d);
//        }
//
//        public Point getLoc() {
//            return loc;
//        }
//
//        public void renderArrow(Graphics2D g2d) {
//            this.myArrow = new Rectangle(loc.x, loc.y, length, width);
//            g2d.setColor(Color.BLUE);
//            if (isStop) {
//                g2d.setColor(Color.RED);
//            }
//            g2d.draw(myArrow);
//            g2d.fill(myArrow);
//            System.out.println("here");
//        }
//
//        public void projectile(Graphics2D g2d, int vx, int vy) {
//
//            this.loc = new Point(loc.x + vx, loc.y + vy);
//            System.out.println(loc.x + " | " + loc.y);
//            renderArrow(g2d);
//        }
//
//        public void rotate(Graphics2D g2d, int angle) {
//            g2d.rotate(Math.toRadians(angle));
//            renderArrow(g2d);
//
//        }
//    }
