package Numerical_bow_2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.border.LineBorder;
import javax.swing.Timer;

public class BowGame extends JPanel {

    private Timer timer;
    private Graphics2D g2d;
    // Camera View Port
    private final Dimension world_size;
    private final Dimension viewport_size;
    private final int offsetMaxX;
    private final int offsetMaxY;
    private final int offsetMinX;
    private final int offsetMinY;
    private int camX = 0;
    private int camY = 0;
    private int camYAdjust = 0;
    // Game Component
    private final Point playerL;
    private final Point playerR;
    private int leftLife = 1000;
    private int rightLife = 1000;
    public int curX, curY;
    private final int landHeight = 5500;  //5500
//    private ArrayList<Rectangle> leftArrows_ = new ArrayList<>();
//    private ArrayList<Rectangle> rightArrows_ = new ArrayList<>();
    private ArrayList<Polygon> leftArrows = new ArrayList<>();
    private ArrayList<Polygon> rightArrows = new ArrayList<>();
    private ArrayList<Double> leftArrowsAngle = new ArrayList<>();
    private ArrayList<Double> rightArrowsAngle = new ArrayList<>();
    private Point[] leftJoints = new Point[6];
    private Point[] rightJoints = new Point[6];
    private Polygon[] bows = new Polygon[2];

    // Game Logic
    private boolean isReleased = false;
    private boolean initL = true;
    private boolean initR = true;
    private boolean isLeft = true;
    private boolean isLeftTurn = true;
    private boolean isTouch = false;
    private boolean isAttacked = false;
    //Arrow shape
    private final int length = 60;
    private final int width = 3;

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
    private final double MaxAngle = 89;

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
        renderHand(g2d, leftJoints);
        renderHand(g2d, rightJoints);
        renderLeftBow(g2d);
        renderRightBow(g2d);
        createLand(g2d);

        lifeBar(g2d);

//        renderLeftArrow_();
//        renderRightArrow_();
        renderLeftArrow();
        renderRightArrow();

        renderShootedArrow();
//        renderShootedArrow_();

    }

    public void createPlayer(Graphics2D g2d, Point p, boolean isLeft) {
        int height = 100;
        int width = 15;

        g2d.setColor(Color.BLACK);
        //body
        g2d.fillOval(p.x, p.y, width, height / 2);
        //head
        g2d.fillOval(p.x, p.y - width, width, width);
        //leg
        g2d.fillRect(p.x + 2, p.y + height / 2, 4, height / 2 - width);
        g2d.fillRect(p.x + width / 2, p.y + height / 2, 4, height / 2 - width);

//        //area (checking)
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

        if (initL & isLeft) {

            //arm & h 
            //backward shoulder
            leftJoints[0] = new Point(p.x, p.y + 8);
            //backward elbow
            leftJoints[1] = new Point(p.x - 10, p.y + 20);
            //backward h
            leftJoints[2] = new Point(p.x + 20, p.y + 25);

            //straight shoulder
            leftJoints[3] = new Point(p.x + width, p.y + 8);
            //straight elbow
            leftJoints[4] = new Point(p.x + width + 10, p.y + 20);
            //straight h
            leftJoints[5] = new Point(p.x + width + 30, p.y + 25);

            //bow
            Point h = leftJoints[5];  //hand
            int[] x_ = new int[]{h.x - 15, h.x - 5, h.x - 15, h.x};
            int[] y_ = new int[]{h.y - 50, h.y, h.y + 50, h.y};
            bows[0] = new Polygon(x_, y_, x_.length);

            //arrow
            h = leftJoints[2];  //hand
            if (!isReleased) {
                if (initL & isLeft) {
                    initL = false;
                    int[] i_ = new int[]{h.x, h.x + length, h.x + length - 10, h.x + length, h.x + length - 10, h.x + length, h.x};
                    int[] j_ = new int[]{h.y, h.y, h.y - 5, h.y, h.y + this.width + 5, h.y + this.width, h.y + this.width};
                    leftArrows.add(new Polygon(i_, j_, i_.length));
                    leftArrowsAngle.add(0.0);
                }

            }

        } else if (initR & !isLeft) {

            //arm & h 
            //backward shoulder
            rightJoints[0] = new Point(p.x + width, p.y + 8);
            //backward elbow
            rightJoints[1] = new Point(p.x + width + 10, p.y + 20);
            //backward h
            rightJoints[2] = new Point(p.x + width - 20, p.y + 25);

            //straight shoulder
            rightJoints[3] = new Point(p.x, p.y + 8);
            //straight elbow
            rightJoints[4] = new Point(p.x - 10, p.y + 20);
            //straight h
            rightJoints[5] = new Point(p.x - 30, p.y + 25);

            //bow
            Point h = rightJoints[5];  //hand
            int[] x_ = new int[]{h.x + 15, h.x + 5, h.x + 15, h.x};
            int[] y_ = new int[]{h.y - 50, h.y, h.y + 50, h.y};
            bows[1] = new Polygon(x_, y_, x_.length);

            //arrow
            h = rightJoints[2];  //hand
            if (!isReleased) {
                if (initR & !isLeft) {
                    initR = false;
                    int[] i_ = new int[]{h.x, h.x + length, h.x + length - 10, h.x + length, h.x + length - 10, h.x + length, h.x};
                    int[] j_ = new int[]{h.y, h.y, h.y - 5, h.y, h.y + this.width + 5, h.y + this.width, h.y + this.width};
                    rightArrows.add(new Polygon(i_, j_, i_.length));
                    rightArrowsAngle.add(180.0);
                }
            }

        }

//        //arrow
//        if (!isReleased) {
//            createArrow(isLeft);
//        }
    }

    public void renderHand(Graphics2D g2d, Point[] Joints) {
        g2d.setColor(Color.cyan);
        g2d.setStroke(new BasicStroke(2f));
        //pull backward
        //arm
        g2d.drawLine(Joints[1].x, Joints[1].y, Joints[0].x, Joints[0].y);
        //elbow
        g2d.drawLine(Joints[1].x, Joints[1].y, Joints[2].x, Joints[2].y);

        g2d.setColor(Color.GREEN);
        //straight
        //arm
        g2d.drawLine(Joints[3].x, Joints[3].y, Joints[4].x, Joints[4].y);
        //elbow
        g2d.drawLine(Joints[4].x, Joints[4].y, Joints[5].x, Joints[5].y);

    }

    public void renderLeftBow(Graphics2D g2d) {
        g2d.setColor(Color.DARK_GRAY);

        int current = leftArrows.size() - 1;

        double angle = Math.toRadians(-leftArrowsAngle.get(current));
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x0 = leftJoints[5].x;     // point to rotate about
        double y0 = leftJoints[5].y;
        Polygon temp = bows[0];
        int[] x_ = new int[bows[0].npoints];
        int[] y_ = new int[bows[0].npoints];
        for (int i = 0; i < bows[0].npoints; i++) {
            double a = bows[0].xpoints[i] - x0;
            double b = bows[0].ypoints[i] - y0;
            x_[i] = (int) (+a * cos - b * sin + x0);
            y_[i] = (int) (+a * sin + b * cos + y0);
        }
        bows[0] = new Polygon(x_, y_, x_.length);

        //bow
        g2d.fillPolygon(bows[0].xpoints, bows[0].ypoints, bows[0].npoints);
        //bow string
        g2d.drawLine(leftJoints[2].x, leftJoints[2].y, bows[0].xpoints[0], bows[0].ypoints[0]);
        g2d.drawLine(leftJoints[2].x, leftJoints[2].y, bows[0].xpoints[2], bows[0].ypoints[2]);

        bows[0] = temp;
    }

    public void renderRightBow(Graphics2D g2d) {
        g2d.setColor(Color.DARK_GRAY);

        int current = rightArrows.size() - 1;

        double angle = Math.toRadians(-rightArrowsAngle.get(current) - 180);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x0 = rightJoints[5].x;     // point to rotate about
        double y0 = rightJoints[5].y;
        Polygon temp = bows[1];
        int[] x_ = new int[bows[1].npoints];
        int[] y_ = new int[bows[1].npoints];
        for (int i = 0; i < bows[1].npoints; i++) {
            double a = bows[1].xpoints[i] - x0;
            double b = bows[1].ypoints[i] - y0;
            x_[i] = (int) (+a * cos - b * sin + x0);
            y_[i] = (int) (+a * sin + b * cos + y0);
        }
        bows[1] = new Polygon(x_, y_, x_.length);

        //bow
        g2d.fillPolygon(bows[1].xpoints, bows[1].ypoints, bows[1].npoints);
        //bow string
        g2d.drawLine(rightJoints[2].x, rightJoints[2].y, bows[1].xpoints[0], bows[1].ypoints[0]);
        g2d.drawLine(rightJoints[2].x, rightJoints[2].y, bows[1].xpoints[2], bows[1].ypoints[2]);
        bows[1] = temp;
    }

//    public void createArrow(boolean isLeft) {
//        Point arrowPoint;
//        if (initL & isLeft) {
//            initL = false;
//            arrowPoint = new Point(leftJoints[2].x, leftJoints[2].y);
//            System.out.println("Arrow 0 created");
//            leftArrows_.add(new Rectangle(arrowPoint.x, arrowPoint.y, length, width));
//            leftArrowsAngle.add(0.0);
//            renderLeftArrow_();
//
//        } else if (initR & !isLeft) {
//            initR = false;
//            arrowPoint = new Point(rightJoints[2].x, rightJoints[2].y);
//            System.out.println("Arrow 1 created");
//            rightArrows_.add(new Rectangle(arrowPoint.x, arrowPoint.y, length, width));
//            rightArrowsAngle.add(180.0);
//            renderRightArrow_();
//        }
//
//    }
    public void renderLeftArrow() {
        g2d.setColor(Color.DARK_GRAY);
        if (isLeftTurn) {
            if (isTouch) {
                g2d.setColor(Color.RED);
            }
        }
        int current = leftArrows.size() - 1;

        double angle = Math.toRadians(-leftArrowsAngle.get(current));
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x0 = leftJoints[2].x;     // point to rotate about
        double y0 = leftJoints[2].y;
        Polygon temp = leftArrows.get(current);
        int[] x_ = new int[temp.npoints];
        int[] y_ = new int[temp.npoints];
        for (int i = 0; i < temp.npoints; i++) {
            double a = temp.xpoints[i] - x0;
            double b = temp.ypoints[i] - y0;
            x_[i] = (int) (+a * cos - b * sin + x0);
            y_[i] = (int) (+a * sin + b * cos + y0);
        }
        leftArrows.set(current, new Polygon(x_, y_, x_.length));

        g2d.draw(leftArrows.get(current));
        g2d.fill(leftArrows.get(current));

        leftArrows.set(current, temp);
    }

    public void renderRightArrow() {
        g2d.setColor(Color.DARK_GRAY);
        if (!isLeftTurn) {
            if (isTouch) {
                g2d.setColor(Color.RED);
            }
        }
        int current = rightArrows.size() - 1;

        double angle = Math.toRadians(-rightArrowsAngle.get(current)-180);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x0 = rightJoints[2].x;     // point to rotate about
        double y0 = rightJoints[2].y;
        Polygon temp = rightArrows.get(current);
        int[] x_ = new int[temp.npoints];
        int[] y_ = new int[temp.npoints];
        for (int i = 0; i < temp.npoints; i++) {
            double a = temp.xpoints[i] - x0;
            double b = temp.ypoints[i] - y0;
            x_[i] = (int) (+a * cos - b * sin + x0);
            y_[i] = (int) (+a * sin + b * cos + y0);
        }
        rightArrows.set(current, new Polygon(x_, y_, x_.length));

        g2d.draw(rightArrows.get(current));
        g2d.fill(rightArrows.get(current));

        rightArrows.set(current, temp);
    }

//    public void renderLeftArrow_() {
//        g2d.setColor(Color.BLUE);
//        if (isLeftTurn) {
//            if (isTouch) {
//                g2d.setColor(Color.RED);
//            }
//        }
//        int current = leftArrows_.size() - 1;
//
//        g2d.rotate(Math.toRadians(-leftArrowsAngle.get(current)), leftArrows_.get(current).x, leftArrows_.get(current).y);
//        g2d.draw(leftArrows_.get(current));
//        g2d.fill(leftArrows_.get(current));
//        g2d.rotate(Math.toRadians(+leftArrowsAngle.get(current)), leftArrows_.get(current).x, leftArrows_.get(current).y);
//
//    }
//
//    public void renderRightArrow_() {
//        g2d.setColor(Color.BLUE);
//        if (!isLeftTurn) {
//            if (isTouch) {
//                g2d.setColor(Color.RED);
//            }
//        }
//        int current = rightArrows_.size() - 1;
//        g2d.rotate(Math.toRadians(-rightArrowsAngle.get(current)), rightArrows_.get(current).x, rightArrows_.get(current).y);
//        g2d.draw(rightArrows_.get(current));
//        g2d.fill(rightArrows_.get(current));
//        g2d.rotate(Math.toRadians(+rightArrowsAngle.get(current)), rightArrows_.get(current).x, rightArrows_.get(current).y);
//
//    }
    public void renderShootedArrow() {
        g2d.setColor(Color.RED);

        for (int n = 0; n < leftArrows.size() - 1; n++) {

            int current = n;

            double angle = Math.toRadians(-leftArrowsAngle.get(current));
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            double x0 = leftJoints[2].x;     // point to rotate about
            double y0 = leftJoints[2].y;
            Polygon temp = leftArrows.get(current);
            int[] x_ = new int[temp.npoints];
            int[] y_ = new int[temp.npoints];

            for (int i = 0; i < temp.npoints; i++) {
                double a = temp.xpoints[i] - x0;
                double b = temp.ypoints[i] - y0;
                x_[i] = (int) (+a * cos - b * sin + x0);
                y_[i] = (int) (+a * sin + b * cos + y0);
            }
            leftArrows.set(current, new Polygon(x_, y_, x_.length));

            g2d.draw(leftArrows.get(n));
            g2d.fill(leftArrows.get(n));
            leftArrows.set(current, temp);

        }
        for (int n = 0; n < rightArrows.size() - 1; n++) {

            int current = n;

            double angle = Math.toRadians(-rightArrowsAngle.get(current));
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            double x0 = rightJoints[2].x;     // point to rotate about
            double y0 = rightJoints[2].y;
            Polygon temp = rightArrows.get(current);
            int[] x_ = new int[temp.npoints];
            int[] y_ = new int[temp.npoints];
            for (int i = 0; i < temp.npoints; i++) {
                double a = temp.xpoints[i] - x0;
                double b = temp.ypoints[i] - y0;
                x_[i] = (int) (+a * cos - b * sin + x0);
                y_[i] = (int) (+a * sin + b * cos + y0);
            }
            rightArrows.set(current, new Polygon(x_, y_, x_.length));

            g2d.draw(rightArrows.get(n));
            g2d.fill(rightArrows.get(n));
            rightArrows.set(current, temp);

        }
    }

//    public void renderShootedArrow_() {
//        g2d.setColor(Color.RED);
//        for (int n = 0; n < leftArrows_.size() - 1; n++) {
//            g2d.rotate(Math.toRadians(-leftArrowsAngle.get(n)), leftArrows_.get(n).x, leftArrows_.get(n).y);
//            g2d.draw(leftArrows_.get(n));
//            g2d.fill(leftArrows_.get(n));
//            g2d.rotate(Math.toRadians(+leftArrowsAngle.get(n)), leftArrows_.get(n).x, leftArrows_.get(n).y);
//
//        }
//        for (int n = 0; n < rightArrows_.size() - 1; n++) {
//            g2d.rotate(Math.toRadians(-rightArrowsAngle.get(n)), rightArrows_.get(n).x, rightArrows_.get(n).y);
//            g2d.draw(rightArrows_.get(n));
//            g2d.fill(rightArrows_.get(n));
//            g2d.rotate(Math.toRadians(+rightArrowsAngle.get(n)), rightArrows_.get(n).x, rightArrows_.get(n).y);
//
//        }
//    }
    public void createLand(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.drawLine(0, landHeight, 10000, landHeight);
    }

    private void updateCamera() {
        if (isLeftTurn) {
            int pivotX = leftArrows.get(leftArrows.size() - 1).xpoints[0];
            int pivotY = leftArrows.get(leftArrows.size() - 1).ypoints[0];
//            System.out.println(leftArrows_.get(roundCtn).x - camX );
            if (pivotX - camX > 300) {
                this.camX = pivotX - this.viewport_size.width / 2;
            }
            this.camY = pivotY - this.viewport_size.height / 2 + 800;
        } else {
            int pivotX = rightArrows.get(rightArrows.size() - 1).xpoints[0];
            int pivotY = rightArrows.get(rightArrows.size() - 1).ypoints[0];
            this.camX = pivotX - this.viewport_size.width / 2;
            this.camY = pivotY - this.viewport_size.height / 2 + 800;

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

    public void shootArrow(ArrayList<Polygon> arrow) {

        if (!isTouch) {
            int current = arrow.size() - 1;

            Polygon temp = arrow.get(current);
            System.out.println(temp.xpoints[0] + ", " + temp.ypoints[0]);
            int[] x_ = new int[temp.npoints];
            int[] y_ = new int[temp.npoints];
            for (int i = 0; i < temp.npoints; i++) {
                x_[i] = temp.xpoints[i] + (int) (vx * 0.5);
                y_[i] = temp.ypoints[i] + (int) (vy * 0.5);
            }
            arrow.set(current, new Polygon(x_, y_, x_.length));
            System.out.println("vx : " + vx + " | vy :" + vy);

            vy += gravity / 100;
            if (vx - 0.005 >= power / 2) {
                vx -= 0.005;
            }

//            //rotation effect
//            arrow.set(arrow.size()-1, element)
//            if (isLeftTurn) {
//                leftArrowsAngle.set(leftArrows_.size() - 1, angle);
//            } else {
//                rightArrowsAngle.set(rightArrows_.size() - 1, -angle-180);
//
//            }
            //checkTouch
            int pivotX = arrow.get(current).xpoints[0];
            int pivotY = arrow.get(current).ypoints[0];
            System.out.println("Pivot X : " + pivotX + ", PivotY : " + pivotY);
            System.out.println("landHeight : " +  (landHeight + 250));
            if (isLeftTurn) {
                //touch player
                if (pivotX + length > playerR.x - 3
                        && pivotX + length < playerR.x + 16
                        && pivotY < playerR.y + 85
                        && pivotY > playerR.y - 16) {
                    isTouch = true;
                    isAttacked = true;
                    initL = true;
                    //minus player life
                    rightLife -= 350;

                    System.out.println("Left arrow attacked");
                    //touch floor    
                } else if (pivotY > landHeight + 250) {

                    isTouch = true;
                    initL = true;
                    System.out.println("Left arrow dropped to floor");
                }

            } else {
                if (pivotX < playerL.x - 3
                        && pivotX > playerL.x + 16
                        && pivotY < playerL.y + 85
                        && pivotY > playerL.y - 16) {

//                    System.out.println("arrow.get(arrow.size() - 1).x : " + arrow.get(arrow.size() - 1).x);
//                    System.out.println("playerL.x : " + playerL.x);
//                    System.out.println("arrow.get(arrow.size() - 1).y : " + arrow.get(arrow.size() - 1).y);
//                    System.out.println("playerL.y : " + playerL.y);
                    isTouch = true;
                    isAttacked = true;
                    initR = true;
                    leftLife -= 350;
                    System.out.println("Right arrow attacked");

                    //touch floor    
                } else if (pivotY > landHeight + 250) {
                    isTouch = true;
                    initR = true;
                    System.out.println("Right arrow dropped to floor");

                }

            }

        } else {

            // let scene movement smooth 
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            timer.stop();
            if (rightLife <= 0 || leftLife <= 0) {
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

            repaint();
            updateCamera();

        }
    }

    public void shootArrow_(ArrayList<Rectangle> arrow) {

        if (!isTouch) {
            arrow.get(arrow.size() - 1).x += vx * 0.5;
            arrow.get(arrow.size() - 1).y += vy * 0.5;

            System.out.println("vx : " + vx + " | vy :" + vy);
//            System.out.println("PLx : " + playerL.x + " | PLy :" + playerL.y);
//            System.out.println("PRx : " + playerR.x + " | PRy :" + playerR.y);

            vy += gravity / 100;
            if (vx - 0.005 >= power / 2) {
                vx -= 0.005;
            }

//            //rotation effect
//            arrow.set(arrow.size()-1, element)
//            if (isLeftTurn) {
//                leftArrowsAngle.set(leftArrows_.size() - 1, angle);
//            } else {
//                rightArrowsAngle.set(rightArrows_.size() - 1, -angle-180);
//
//            }
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

                    System.out.println("Left arrow attacked");
                    //touch floor    
                } else if (arrow.get(arrow.size() - 1).y > landHeight + 30) {

                    isTouch = true;
                    initL = true;
                    System.out.println("Left arrow dropped to floor");
                }

            } else {
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
                    System.out.println("Right arrow attacked");

                    //touch floor    
                } else if (arrow.get(arrow.size() - 1).y > landHeight + 30) {
                    isTouch = true;
                    initR = true;
                    System.out.println("Right arrow dropped to floor");

                }

            }

        } else {

            // let scene movement smooth 
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            timer.stop();
            if (rightLife <= 0 || leftLife <= 0) {
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

            repaint();
            updateCamera();

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
//                    shootArrow_(leftArrows_);
                    shootArrow(leftArrows);
                    updateCamera();

                    repaint();
                });
            } else {
                timer = new Timer(10, l -> {
//                    shootArrow_(rightArrows_);
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
            
//            vx = 10 + power % 10 + power / 10;
            if (isLeftTurn) {
                vx = 10 + power % 10 + power / 10;
            } else {
                vx = -10 - power % 10 - power / 10;
            }
            vy = -power % 10;

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

            // Angle
            if (isLeftTurn) {
                leftArrowsAngle.set(leftArrows.size() - 1, angle);
            } else {
                rightArrowsAngle.set(rightArrows.size() - 1, -angle - 180);

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
