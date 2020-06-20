package Numerical_bow_2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
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
    private final int landHeight = 10005500;  //5500
    private final ArrayList<Polygon> leftArrows = new ArrayList<>();
    private final ArrayList<Polygon> rightArrows = new ArrayList<>();
    private final ArrayList<Double> leftArrowsAngle = new ArrayList<>();
    private final ArrayList<Double> rightArrowsAngle = new ArrayList<>();
    private final Point[] leftJoints = new Point[6];
    private final Point[] rightJoints = new Point[6];
    private final Point[] leftJointsOri = new Point[6];
    private final Point[] rightJointsOri = new Point[6];
    private Polygon[] bows = new Polygon[2];
    // Game Logic
    private boolean isReleased = false;
    private boolean initL = true;
    private boolean initR = true;
    private boolean isLeftTurn = true;
    private boolean isTouch = false;
    private boolean isAttacked = false;
    // Arrow size
    private final int length = 80;
    private final int width = 3;
    // Arrow Shooting
    private double angle = 0;
    private double power = 0;
    private final double gravity = 9.81;
    private double vx = 0;
    private double vy = 0;
    private double initvx = 0;
    private double initvy = 0;
    private int flightTime = 0;
    private boolean isDrag = false;
    private boolean isPullString = true;
    private boolean isRaiseArrow = true;
    private double raiseAngle = 40.0;
    //Arrow Adjust
    private int xStart = 0;
    private int yStart = 0;
    private final double MaxAngle = 60;
    //Slider
    private int sliderX1;
    private int sliderX2;
    private int sliderY;
    private final int boxSize = 16;
    private int boxX;
    private int boxY;
    private int currX;
    private int currY;
    private boolean validClick = false;
    private boolean validBoxPress = false;
    private int position;
    private final int wallWidth = 50;
    // Game Loop
    private boolean END = false;

    public BowGame() {
        MyKeyListener myKeyListener = new MyKeyListener();
        addKeyListener(myKeyListener);
        this.setVisible(true);
        MouseHandler myHandler = new MouseHandler();
        addMouseListener(myHandler);
        addMouseMotionListener(myHandler);
        this.setVisible(true);
        setBackground(new Color(0xffFFE5CC));

        this.viewport_size = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(10000, 10008000));  
        this.world_size = getPreferredSize();

        //camera view
        this.offsetMaxX = world_size.width - viewport_size.width;
        this.offsetMaxY = world_size.height - viewport_size.height;
        this.offsetMinX = 0;
        this.offsetMinY = 0;

        //Players init
        this.playerL = new Point(4000, landHeight - 80);  
        this.playerR = new Point(6000, landHeight - 80);  
        this.camYAdjust = 450;
        this.camY = landHeight - camYAdjust;
        this.camX = playerL.x - 120;

        //Slider init
        this.sliderX1 = camX + 200;
        this.sliderX2 = camX + 200 + 300;
        this.sliderY = landHeight + 150;
        this.boxX = ((sliderX1 + sliderX2) / 2) - (boxSize / 2);
        this.boxY = sliderY - (boxSize / 2);
        this.position = (int) ((playerL.x / (world_size.width * 1.0)) * 100);

    }

    private void updateCamera() {
        if (validBoxPress) {
            this.camX = this.world_size.width * position / 100;
            this.camY = landHeight - camYAdjust;
        } else {
            if (isLeftTurn) {
                int current = leftArrows.size() - 1;
                if (initL & !isReleased) {
                    this.camX = playerL.x - 120;
                    this.camY = landHeight - camYAdjust;
                } else if (!isReleased) {
                    int pivotX = leftArrows.get(current).xpoints[4];
                    this.camX = pivotX - this.viewport_size.width / 3;
                    this.camY = landHeight - camYAdjust;
                } else {
                    int[] pivot = arrowRotatedPoint(current, isLeftTurn); //arrow head  
                    int pivotX = pivot[0];
                    int pivotY = pivot[1];
                    this.camX = pivotX - this.viewport_size.width / 3;
                    this.camY = pivotY - this.viewport_size.height / 6;
                }
            } else {
                int current = rightArrows.size() - 1;
                if (initR & !isReleased) {
                    this.camX = playerR.x + 120;
                    this.camY = landHeight - camYAdjust;
                } else if (!isReleased) {
                    int pivotX = rightArrows.get(current).xpoints[4];
                    this.camX = pivotX - this.viewport_size.width / 3;
                    this.camY = landHeight - camYAdjust;
                } else {
                    int[] pivot = arrowRotatedPoint(current, isLeftTurn); //arrow head  
                    int pivotX = pivot[0];
                    int pivotY = pivot[1];
                    this.camX = pivotX - this.viewport_size.width / 3;
                    this.camY = pivotY - this.viewport_size.height / 6;
                }
            }
            if (camY > (landHeight - camYAdjust)) {
                this.camY = landHeight - camYAdjust;
            }
//            System.out.println("camX : " + camX + ", camY : " + camY);
        }

        if (camX > offsetMaxX) {
            camX = offsetMaxX;
            System.out.println("camX > offsetMaxX");
        } else if (camX < offsetMinX) {
            camX = offsetMinX;
            System.out.println("camX < offsetMinX");
        }
        if (camY > offsetMaxY) {
            camY = offsetMaxY;
            System.out.println("camY > offsetMaxY");
        } else if (camY < offsetMinY) {
            camY = offsetMinY;
            System.out.println("camY < offsetMinY");
        }
    }

    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        g2d = (Graphics2D) gg;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.translate(-camX, -camY);

        createLand(g2d);
        createWall(g2d);

        createSlider(g2d);

        createPlayer(g2d, playerL, true);
        createPlayer(g2d, playerR, false);
        renderLeftPlayerHand(g2d);
        renderRightPlayerHand(g2d);

        lifeBar(g2d);

        updateCamera();

        renderLeftArrow();
        renderRightArrow();
        renderShootedArrow();

    }

    public void createPlayer(Graphics2D g2d, Point p, boolean isLeft) {
        int height = 100;
        int width = 15;

        g2d.setColor(new Color(0xff003366));
        g2d.setStroke(new BasicStroke(1f));

        //body
        g2d.fillOval(p.x, p.y, width, height / 2);
        //head
        g2d.fillOval(p.x, p.y - width, width, width);
        //leg
        g2d.fillRect(p.x + 2, p.y + height / 2, 4, height / 2 - width);
        g2d.fillRect(p.x + width / 2, p.y + height / 2, 4, height / 2 - width);

        // player area (checking purpose)
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
            leftJointsOri[0] = leftJoints[0] = new Point(p.x, p.y + 8);
            //backward elbow
            leftJointsOri[1] = leftJoints[1] = new Point(p.x - 10, p.y + 20);
            //backward hand
            leftJointsOri[2] = leftJoints[2] = new Point(p.x + 20, p.y + 25);

            //straight shoulder
            leftJointsOri[3] = leftJoints[3] = new Point(p.x + width, p.y + 8);
            //straight elbow
            leftJointsOri[4] = leftJoints[4] = new Point(p.x + width + 10, p.y + 20);
            //straight hand
            leftJointsOri[5] = leftJoints[5] = new Point(p.x + width + 30, p.y + 25);

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
                    int[] i_ = new int[]{h.x, h.x + length, h.x + length - 10, h.x + length, h.x + length + 2, h.x + length - 10, h.x + length, h.x};
                    int[] j_ = new int[]{h.y, h.y, h.y - 5, h.y, h.y + (this.width / 2), h.y + this.width + 5, h.y + this.width, h.y + this.width};
                    leftArrows.add(new Polygon(i_, j_, i_.length));
                    leftArrowsAngle.add(0.0);
                }
            }
        } else if (initR & !isLeft) {
            //arm & h 
            //backward shoulder
            rightJointsOri[0] = rightJoints[0] = new Point(p.x + width, p.y + 8);
            //backward elbow
            rightJointsOri[1] = rightJoints[1] = new Point(p.x + width + 10, p.y + 20);
            //backward h
            rightJointsOri[2] = rightJoints[2] = new Point(p.x + width - 20, p.y + 25);

            //straight shoulder
            rightJointsOri[3] = rightJoints[3] = new Point(p.x, p.y + 8);
            //straight elbow
            rightJointsOri[4] = rightJoints[4] = new Point(p.x - 10, p.y + 20);
            //straight h
            rightJointsOri[5] = rightJoints[5] = new Point(p.x - 30, p.y + 25);

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

                    int[] i_ = new int[]{h.x, h.x - length, h.x - length + 10, h.x - length, h.x - length - 2, h.x - length + 10, h.x - length, h.x};
                    int[] j_ = new int[]{h.y, h.y, h.y - 5, h.y, h.y + (this.width / 2), h.y + this.width + 5, h.y + this.width, h.y + this.width};

                    rightArrows.add(new Polygon(i_, j_, i_.length));
                    rightArrowsAngle.add(180.0);
                }
            }
        }
    }

    public void renderLeftPlayerHand(Graphics2D g2d) {
        g2d.setColor(new Color(0xff003366));
        g2d.setStroke(new BasicStroke(2f));

        if (angle == 0) {
            //pull backward
            //arm
            g2d.drawLine(leftJoints[1].x, leftJoints[1].y, leftJoints[0].x, leftJoints[0].y);
            //elbow
            g2d.drawLine(leftJoints[1].x, leftJoints[1].y, leftJoints[2].x, leftJoints[2].y);

            //straight arm
            g2d.drawLine(leftJoints[3].x, leftJoints[3].y, leftJoints[4].x, leftJoints[4].y);
            //straight elbow
            g2d.drawLine(leftJoints[4].x, leftJoints[4].y, leftJoints[5].x, leftJoints[5].y);

            //update bow position
            Point h = leftJoints[5];  //hand
            int[] x_ = new int[]{h.x - 15, h.x - 5, h.x - 15, h.x};
            int[] y_ = new int[]{h.y - 50, h.y, h.y + 50, h.y};
            bows[0] = new Polygon(x_, y_, x_.length);

            renderLeftBow(g2d);
        } else {
            int current = leftArrows.size() - 1;
            double angle = Math.toRadians(-leftArrowsAngle.get(current));
            double sin = Math.sin(angle * 1.5);
            double cos = Math.cos(angle * 1.5);
            double x0 = leftJoints[3].x;     // point to rotate about
            double y0 = leftJoints[3].y;
            Point temp = leftJoints[5];

            double a = leftJoints[5].x - x0;
            double b = leftJoints[5].y - y0;
            int x = (int) (+a * cos - b * sin + x0);
            int y = (int) (+a * sin + b * cos + y0);

            leftJoints[5] = new Point(x, y);

            //rotate with angle
            //straight
            g2d.drawLine(leftJoints[3].x, leftJoints[3].y, leftJoints[5].x, leftJoints[5].y);

            //update bow position
            Point h = leftJoints[5];  //hand
            int[] x_ = new int[]{h.x - 15, h.x - 5, h.x - 15, h.x};
            int[] y_ = new int[]{h.y - 50, h.y, h.y + 50, h.y};
            bows[0] = new Polygon(x_, y_, x_.length);

            renderLeftBow(g2d);

            leftJoints[5] = temp;

            //bend
            sin = Math.sin(angle * 0.35);
            cos = Math.cos(angle * 0.35);
            x0 = leftJoints[0].x;     // point to rotate about
            y0 = leftJoints[0].y;
            temp = leftJoints[1];

            a = leftJoints[1].x - x0;
            b = leftJoints[1].y - y0;
            x = (int) (+a * cos - b * sin + x0);
            y = (int) (+a * sin + b * cos + y0);
            leftJoints[1] = new Point(x, y);
            //pull backward
            //arm
            g2d.drawLine(leftJoints[1].x, leftJoints[1].y, leftJoints[0].x, leftJoints[0].y);
            //elbow
            g2d.drawLine(leftJoints[1].x, leftJoints[1].y, leftJoints[2].x, leftJoints[2].y);
            leftJoints[1] = temp;

        }
    }

    public void renderRightPlayerHand(Graphics2D g2d) {
        g2d.setColor(new Color(0xff003366));
        g2d.setStroke(new BasicStroke(2f));

        //straight
        if (angle == 0) {
            //pull backward
            //arm
            g2d.drawLine(rightJoints[1].x, rightJoints[1].y, rightJoints[0].x, rightJoints[0].y);
            //elbow
            g2d.drawLine(rightJoints[1].x, rightJoints[1].y, rightJoints[2].x, rightJoints[2].y);

            //arm
            g2d.drawLine(rightJoints[3].x, rightJoints[3].y, rightJoints[4].x, rightJoints[4].y);
            //elbow
            g2d.drawLine(rightJoints[4].x, rightJoints[4].y, rightJoints[5].x, rightJoints[5].y);

            //update bow position
            Point h = rightJoints[5];  //hand
            int[] x_ = new int[]{h.x + 15, h.x + 5, h.x + 15, h.x};
            int[] y_ = new int[]{h.y - 50, h.y, h.y + 50, h.y};
            bows[1] = new Polygon(x_, y_, x_.length);
            renderRightBow(g2d);
        } else if (isLeftTurn) {
            //arm
            g2d.drawLine(rightJoints[3].x, rightJoints[3].y, rightJoints[4].x, rightJoints[4].y);
            //elbow
            g2d.drawLine(rightJoints[4].x, rightJoints[4].y, rightJoints[5].x, rightJoints[5].y);
            renderRightBow(g2d);
        } else {
            int current = rightArrows.size() - 1;
            double angle = Math.toRadians(-rightArrowsAngle.get(current) - 180);
            double sin = Math.sin(angle * 1.5);
            double cos = Math.cos(angle * 1.5);
            double x0 = rightJoints[3].x;     // point to rotate about
            double y0 = rightJoints[3].y;
            Point temp = rightJoints[5];

            double a = rightJoints[5].x - x0;
            double b = rightJoints[5].y - y0;
            int x = (int) (+a * cos - b * sin + x0);
            int y = (int) (+a * sin + b * cos + y0);
            rightJoints[5] = new Point(x, y);

            //rotate with angle
            //straight
            g2d.drawLine(rightJoints[3].x, rightJoints[3].y, rightJoints[5].x, rightJoints[5].y);

            //update bow position
            Point h = rightJoints[5];  //hand
            int[] x_ = new int[]{h.x + 15, h.x + 5, h.x + 15, h.x};
            int[] y_ = new int[]{h.y - 50, h.y, h.y + 50, h.y};
            bows[1] = new Polygon(x_, y_, x_.length);

            renderRightBow(g2d);

            rightJoints[5] = temp;

            //bend
            sin = Math.sin(angle * 0.35);
            cos = Math.cos(angle * 0.35);
            x0 = rightJoints[0].x;     // point to rotate about
            y0 = rightJoints[0].y;
            temp = rightJoints[1];

            a = rightJoints[1].x - x0;
            b = rightJoints[1].y - y0;
            x = (int) (+a * cos - b * sin + x0);
            y = (int) (+a * sin + b * cos + y0);
            rightJoints[1] = new Point(x, y);
            //pull backward
            //arm
            g2d.drawLine(rightJoints[1].x, rightJoints[1].y, rightJoints[0].x, rightJoints[0].y);
            //elbow
            g2d.drawLine(rightJoints[1].x, rightJoints[1].y, rightJoints[2].x, rightJoints[2].y);
            rightJoints[1] = temp;
        }
    }

    public void animatePull() {
        if (isPullString) {

            double strength = power;
            double maxStraight = 30;
            double maxBend = 80;
            Point elbow, hand, elbowB, handB;
            if (isLeftTurn) {

                if (strength <= maxStraight) {

                    elbow = new Point((int) (leftJointsOri[4].x + (8 * (strength / maxStraight))),
                            (int) (leftJointsOri[4].y - (7 * (strength / maxStraight))));
                    hand = new Point((int) (leftJointsOri[5].x + (10 * (strength / maxStraight))),
                            (int) (leftJointsOri[5].y - (5 * (strength / maxStraight))));
                } else {
                    elbow = new Point((int) (leftJointsOri[4].x + (8 * (maxStraight / maxStraight))),
                            (int) (leftJointsOri[4].y - (7 * (maxStraight / maxStraight))));
                    hand = new Point((int) (leftJointsOri[5].x + (10 * (maxStraight / maxStraight))),
                            (int) (leftJointsOri[5].y - (5 * (maxStraight / maxStraight))));
                }
                if (strength <= maxBend) {
                    elbowB = new Point((int) (leftJointsOri[1].x - (20 * (strength / maxBend))),
                            (int) (leftJointsOri[1].y - (10 * (strength / maxBend))));
                    handB = new Point((int) (leftJointsOri[2].x - (25 * (strength / maxBend))),
                            (int) (leftJointsOri[2].y - (10 * (strength / maxBend))));

                } else {
                    elbowB = new Point((int) (leftJointsOri[1].x - (20 * (maxBend / maxBend))),
                            (int) (leftJointsOri[1].y - (10 * (maxBend / maxBend))));
                    handB = new Point((int) (leftJointsOri[2].x - (25 * (maxBend / maxBend))),
                            (int) (leftJointsOri[2].y - (10 * (maxBend / maxBend))));
                }

                //straight elbow
                leftJoints[4] = elbow;
                //straight hand
                leftJoints[5] = hand;

                leftJoints[1] = elbowB;
                leftJoints[2] = handB;

                //update arrow position
                if (!isReleased) {
                    Point h = leftJoints[2];  //hand

                    int[] i_ = new int[]{h.x, h.x + length, h.x + length - 10, h.x + length, h.x + length + 2, h.x + length - 10, h.x + length, h.x};
                    int[] j_ = new int[]{h.y, h.y, h.y - 5, h.y, h.y + (this.width / 2), h.y + this.width + 5, h.y + this.width, h.y + this.width};
                    int current = leftArrows.size() - 1;
                    leftArrows.set(current, new Polygon(i_, j_, i_.length));
                }
            } else {
                if (strength <= maxStraight) {

                    elbow = new Point((int) (rightJointsOri[4].x - (8 * (strength / maxStraight))),
                            (int) (rightJointsOri[4].y - (7 * (strength / maxStraight))));
                    hand = new Point((int) (rightJointsOri[5].x - (10 * (strength / maxStraight))),
                            (int) (rightJointsOri[5].y - (5 * (strength / maxStraight))));
                } else {
                    elbow = new Point((int) (rightJointsOri[4].x - (8 * (maxStraight / maxStraight))),
                            (int) (rightJointsOri[4].y - (7 * (maxStraight / maxStraight))));
                    hand = new Point((int) (rightJointsOri[5].x - (10 * (maxStraight / maxStraight))),
                            (int) (rightJointsOri[5].y - (5 * (maxStraight / maxStraight))));
                }
                if (strength <= maxBend) {
                    elbowB = new Point((int) (rightJointsOri[1].x + (20 * (strength / maxBend))),
                            (int) (rightJointsOri[1].y - (10 * (strength / maxBend))));
                    handB = new Point((int) (rightJointsOri[2].x + (25 * (strength / maxBend))),
                            (int) (rightJointsOri[2].y - (10 * (strength / maxBend))));

                } else {
                    elbowB = new Point((int) (rightJointsOri[1].x + (20 * (maxBend / maxBend))),
                            (int) (rightJointsOri[1].y - (10 * (maxBend / maxBend))));
                    handB = new Point((int) (rightJointsOri[2].x + (25 * (maxBend / maxBend))),
                            (int) (rightJointsOri[2].y - (10 * (maxBend / maxBend))));
                }

                //straight elbow
                rightJoints[4] = elbow;
                //straight hand
                rightJoints[5] = hand;

                rightJoints[1] = elbowB;
                rightJoints[2] = handB;

                //update arrow position
                if (!isReleased) {
                    Point h = rightJoints[2];  //hand

                    int[] i_ = new int[]{h.x, h.x - length, h.x - length + 10, h.x - length, h.x - length - 2, h.x - length + 10, h.x - length, h.x};
                    int[] j_ = new int[]{h.y, h.y, h.y - 5, h.y, h.y + (this.width / 2), h.y + this.width + 5, h.y + this.width, h.y + this.width};
                    int current = rightArrows.size() - 1;
                    rightArrows.set(current, new Polygon(i_, j_, i_.length));
                }
            }
        }
    }

    public Polygon leftBowRotate(int current) {
        double angle = Math.toRadians(-leftArrowsAngle.get(current));
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x0 = leftJoints[5].x;    // point to rotate about
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
        return temp;
    }

    public void renderLeftBow(Graphics2D g2d) {
        int current = leftArrows.size() - 1;
        Polygon temp = leftBowRotate(current);
        //bow
        g2d.setColor(new Color(0xff003319));
        g2d.fillPolygon(bows[0].xpoints, bows[0].ypoints, bows[0].npoints);
        //bow string
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(leftJoints[2].x, leftJoints[2].y, bows[0].xpoints[0], bows[0].ypoints[0]);
        g2d.drawLine(leftJoints[2].x, leftJoints[2].y, bows[0].xpoints[2], bows[0].ypoints[2]);
        bows[0] = temp;
    }

    public Polygon rightBowRotate(int current) {
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
        return temp;
    }

    public void renderRightBow(Graphics2D g2d) {
        int current = rightArrows.size() - 1;
        Polygon temp = rightBowRotate(current);
        //bow
        g2d.setColor(new Color(0xff003319));
        g2d.fillPolygon(bows[1].xpoints, bows[1].ypoints, bows[1].npoints);
        //bow string
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(rightJoints[2].x, rightJoints[2].y, bows[1].xpoints[0], bows[1].ypoints[0]);
        g2d.drawLine(rightJoints[2].x, rightJoints[2].y, bows[1].xpoints[2], bows[1].ypoints[2]);
        bows[1] = temp;
    }

    public Polygon leftArrowRotate(int current) {
        double angle = Math.toRadians(-leftArrowsAngle.get(current));
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x0 = leftJoints[2].x;    // point to rotate about
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
        return temp;
    }

    public void renderLeftArrow() {
        g2d.setColor(Color.BLACK);
        if (isLeftTurn & isTouch) {
            g2d.setColor(Color.RED);
        }
        int current = leftArrows.size() - 1;
        Polygon temp = leftArrowRotate(current);
        g2d.draw(leftArrows.get(current));
        g2d.fill(leftArrows.get(current));
        leftArrows.set(current, temp);
    }

    public Polygon rightArrowRotate(int current) {
        double angle = Math.toRadians(-rightArrowsAngle.get(current) - 180);
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
        return temp;
    }

    public void renderRightArrow() {
        g2d.setColor(Color.BLACK);
        if (!isLeftTurn & isTouch) {
            g2d.setColor(Color.RED);
        }
        int current = rightArrows.size() - 1;
        Polygon temp = rightArrowRotate(current);
        g2d.draw(rightArrows.get(current));
        g2d.fill(rightArrows.get(current));
        rightArrows.set(current, temp);

    }

    public void renderShootedArrow() {
        g2d.setColor(Color.RED);
        for (int n = 0; n < leftArrows.size() - 1; n++) {
            Polygon temp = leftArrowRotate(n);
            g2d.draw(leftArrows.get(n));
            g2d.fill(leftArrows.get(n));
            leftArrows.set(n, temp);
        }
        for (int n = 0; n < rightArrows.size() - 1; n++) {
            Polygon temp = rightArrowRotate(n);
            g2d.draw(rightArrows.get(n));
            g2d.fill(rightArrows.get(n));
            rightArrows.set(n, temp);
        }
    }

    public void createLand(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.drawLine(0, landHeight, 200010000, landHeight);
        g2d.setColor(new Color(0xffFC9803));
        g2d.fillRect(0, landHeight, 200010000, world_size.height - landHeight);

    }

    public void createWall(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, wallWidth, world_size.height - (world_size.height - landHeight));
        g2d.fillRect(world_size.width + 265, 0, wallWidth, world_size.height - (world_size.height - landHeight));

    }

    public void createSlider(Graphics2D g2d) {
        this.sliderX1 = camX + 200;
        this.sliderX2 = camX + 200 + 300;
        this.sliderY = landHeight + 250;

        this.boxX = sliderX1 + (int) ((sliderX2 - sliderX1) * ((position * 1.0) / 100));
        this.boxY = sliderY - (boxSize / 2);

        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2d.setColor(Color.BLACK);
        g2d.drawLine(sliderX1, sliderY, sliderX2, sliderY);
        if (validBoxPress) {
            g2d.setColor(Color.GREEN);
        } else {
            currX = boxX - camX;
            currY = boxY - camY;
            g2d.setColor(Color.DARK_GRAY);
        }
        g2d.fillRect(boxX, boxY, boxSize, boxSize);

        g2d.setStroke(new BasicStroke(0.0f));

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
            g2d.drawString(String.format("\tGame Over"), camX + 300, camY + 160);
            g2d.setColor(Color.GREEN);
            if (isLeftTurn) {
                g2d.drawString(String.format("Right Player Won"), camX + 300, camY + 230);
            } else {
                g2d.drawString(String.format("Left Player Won"), camX + 300, camY + 230);
            }
        }

        //debug purpose
//        g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
//        g2d.drawString(String.format("Height : %d", landHeight % 10000), playerL.x + 50, landHeight - 10);
//        g2d.drawString(String.format("CamX : %d, CamY : %d", camX % 10000, camY % 10000), camX, camY + 20);
//        int pivotX = leftArrows.get(leftArrows.size() - 1).xpoints[0];
//        int pivotY = leftArrows.get(leftArrows.size() - 1).ypoints[0];
//        int pivotXR = rightArrows.get(rightArrows.size() - 1).xpoints[0];
//        int pivotYR = rightArrows.get(rightArrows.size() - 1).ypoints[0];
//        g2d.drawString(String.format("Left : PivotX : %d, PivotY : %d", pivotX % 10000, pivotY % 10000), camX, camY + 150);
//        g2d.drawString(String.format("Right : PivotX : %d, PivotY : %d", pivotXR % 10000, pivotYR % 10000), camX, camY + 170);
    }

    public int[] arrowRotatedPoint(int current, boolean isLeftTurn) {
        if (isLeftTurn) {
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
            return new int[]{x_[4], y_[4]};
        } else {
            double angle = Math.toRadians(-rightArrowsAngle.get(current) - 180);
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
            return new int[]{x_[4], y_[4]};
        }
    }

    public void arrowRotationUpdate(int current, boolean isLeftTurn, double vx, double vy) {
        if (isLeftTurn) {
            leftArrowsAngle.set(current,
                    angle - Math.toDegrees(Math.atan2(vy, vx)));
        } else {
            rightArrowsAngle.set(current,
                    -(angle + (Math.toDegrees(Math.atan2(vy, vx)) + 180)) - 180);
        }
    }

    public void shootArrow(ArrayList<Polygon> arrow) {
        if (!isTouch) {
            int current = arrow.size() - 1;
            Polygon temp = arrow.get(current);
            int[] x_ = new int[temp.npoints];
            int[] y_ = new int[temp.npoints];
            for (int i = 0; i < temp.npoints; i++) {
                x_[i] = temp.xpoints[i] + (int) (vx * 0.5);
                y_[i] = temp.ypoints[i] + (int) (vy * 0.5);
            }
            arrow.set(current, new Polygon(x_, y_, x_.length));
            System.out.printf("vx : %.2f | vy : %.2f \n", vx, vy);

            // rotation effect, update angle
            if (isLeftTurn) {
                leftArrowsAngle.set(current,
                        angle * vy / initvy);
            } else {
                rightArrowsAngle.set(current,
                        -(angle * vy / initvy) - 180);
            }
            updateCamera();
            vy += gravity / 40;

            if (vx - 0.005 >= power / 2) {
                vx -= 0.005;
            }

            // rotate to confirm position
            int[] pivot = arrowRotatedPoint(current, isLeftTurn); //arrow head  
            int pivotX = pivot[0];
            int pivotY = pivot[1];

            // check touch
            if (isLeftTurn) {
                if ((pivotX) > (playerR.x - 5) && (pivotX) < (playerR.x + 22)
                        && pivotY < (playerR.y + 85) && pivotY > (playerR.y - 16)) {  //touch player
                    isTouch = true;
                    isAttacked = true;
                    if (pivotY < (playerR.y + 15)) {  //head position
                        rightLife -= 400;
                    } else {
                        rightLife -= 320;
                    }
//                    System.out.println("Left arrow attacked");

                    arrowisTouch();
                } else if (pivotY > (landHeight + 50)) {  //touch floor   
                    isTouch = true;
//                    System.out.println("Left arrow dropped to floor " + (landHeight + 120));
                    arrowisTouch();
                } else if (pivotX >= (world_size.width + 265) + 25) {
                    isTouch = true;
//                    System.out.println("Left arrow touch the wall ");
                    arrowisTouch();
                }
            } else {
                if (pivotX < (playerL.x + 18) && pivotX > (playerL.x - 9)
                        && pivotY < (playerL.y + 85) && pivotY > (playerL.y - 16)) {
                    isTouch = true;
                    isAttacked = true;
                    if (pivotY < (playerR.y + 15)) {  //head position
                        leftLife -= 400;
                    } else {
                        leftLife -= 320;
                    }
//                    System.out.println("Right arrow attacked");
                    arrowisTouch();
                } else if (pivotY > (landHeight + 50)) {
                    isTouch = true;
//                    System.out.println("Right arrow dropped to floor " + (landHeight + 120));
                    arrowisTouch();
                } else if (pivotX <= (0 + wallWidth) - 25) {
                    isTouch = true;
//                    System.out.println("Right arrow touch the wall ");
                    arrowisTouch();
                }
            }
        }
    }

    public void arrowisTouch() { // init component
        validClick = false;

        if (!isAttacked) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        timer.stop();
        if (rightLife <= 0 || leftLife <= 0) {
            END = true;
        }
        if (isLeftTurn) {
            initL = true;
        } else {
            initR = true;
        }
        isLeftTurn = !isLeftTurn;
        System.out.println("Is Left Turn : " + isLeftTurn);
        if (isLeftTurn) {
            position = (int) ((playerL.x / (world_size.width * 1.0)) * 100);
        } else {
            position = (int) ((playerR.x / (world_size.width * 1.0)) * 100);
        }
        angle = 0;
        flightTime = 0;
        initvx = 0;
        initvy = 0;

        power = 0;
        isTouch = false;
        isAttacked = false;
        isReleased = false;
        raiseAngle = 40.0;
        isRaiseArrow = true;
        sliderX1 = camX + 200;
        sliderX2 = camX + 200 + 300;
        sliderY = landHeight + 150;

        repaint();
        updateCamera();
    }

    private class MouseHandler implements MouseListener, MouseMotionListener {

        public void mousePressed(MouseEvent e) {
            if (!END) {
                xStart = e.getX();
                yStart = e.getY();

                if ((xStart >= currX && xStart <= (currX + boxSize)) && (yStart >= currY - boxSize && yStart <= (currY + boxSize))) {
//                    System.out.println("validBoxPress = true;");
                    validBoxPress = true;
                    isDrag = false;
                    repaint();
                }
                if (!validBoxPress && yStart < 500) {
                    validClick = true;
                    isTouch = false;
                    isPullString = true;
                    repaint();
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (!END) {
                if (validClick & isDrag & power > 0) {
                    isReleased = true;
                    isRaiseArrow = true;

                    //bow & hand back to ori position
                    if (isLeftTurn) {
                        for (int i = 0; i < leftJoints.length; i++) {
                            leftJoints[i] = leftJointsOri[i];
                        }
                        Point h = leftJoints[5];  //hand
                        int[] x_ = new int[]{h.x - 15, h.x - 5, h.x - 15, h.x};
                        int[] y_ = new int[]{h.y - 50, h.y, h.y + 50, h.y};
                        bows[0] = new Polygon(x_, y_, x_.length);
                    } else {
                        for (int i = 0; i < rightJoints.length; i++) {
                            rightJoints[i] = rightJointsOri[i];
                        }
                        Point h = rightJoints[5];  //hand
                        int[] x_ = new int[]{h.x + 15, h.x + 5, h.x + 15, h.x};
                        int[] y_ = new int[]{h.y - 50, h.y, h.y + 50, h.y};
                        bows[1] = new Polygon(x_, y_, x_.length);
                    }
                    isPullString = false;
                    if (isLeftTurn) {
                        timer = new Timer(20, l -> { 
                            shootArrow(leftArrows);
                            validClick = false;
                            repaint();
                        });
                    } else {
                        timer = new Timer(20, l -> {
                            shootArrow(rightArrows);
                            validClick = false;
                            repaint();
                        });
                    }
                    timer.start();
                } else {
                    angle = 0;
                    if (isLeftTurn) {
                        leftArrowsAngle.set(leftArrows.size() - 1, angle);
                    } else {
                        rightArrowsAngle.set(rightArrows.size() - 1, -angle - 180);
                    }
                    raiseAngle = 40.0;
                    isRaiseArrow = true;
                    repaint();
                }

                if (validBoxPress) {
                    validBoxPress = false;
                    if (isLeftTurn) {
                        position = (int) ((playerL.x / (world_size.width * 1.0)) * 100);
                    } else {
                        position = (int) ((playerR.x / (world_size.width * 1.0)) * 100);
                    }
                    repaint();
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
            if (!END) {
                if (validClick) {
                    isDrag = true;

                    animatePull();

                    // x-axis for power
                    int horizontalDrag = (e.getX() - xStart) / 1;
                    if (isLeftTurn) {
                        horizontalDrag *= -1;
                    }
                    power = horizontalDrag;
                    if (power < 0) {
                        power = 0;
                    } else if (power >= 150) {
                        power = 150;
                    }
                    // y-axis for angle
                    int verticalDrag = (e.getY() - yStart) / 2;
                    verticalDrag *= -1;
                    angle = verticalDrag;
                    if (angle > MaxAngle) {
                        angle = MaxAngle;
                    } else if (angle < 0) {
                        angle = 0;
                    }

                    // Angle
                    if (isLeftTurn) {
                        leftArrowsAngle.set(leftArrows.size() - 1, angle);
                        if (isRaiseArrow) {
                            leftArrowsAngle.set(leftArrows.size() - 1, raiseAngle);
                            raiseAngle -= 1;

                            if (raiseAngle <= angle) {
                                isRaiseArrow = false;
                            }
                        }
                    } else {
                        rightArrowsAngle.set(rightArrows.size() - 1, -angle - 180);
                        if (isRaiseArrow) {
                            rightArrowsAngle.set(rightArrows.size() - 1, -raiseAngle - 180);
                            raiseAngle -= 1;

                            if (raiseAngle <= angle) {
                                isRaiseArrow = false;
                            }
                        }
                    }

                    if (power > 0) {
                        //update vx vy
                        if (isLeftTurn) {
                            vx = (10 + power % 10 + power / 10) * 3.5;  
                        } else {
                            vx = (-10 - power % 10 - power / 10) * 3.5;
                        }

                        vy = (-10 - power % 10 - power / 10) * 1.5;

                        initvy = vy;

                        vy = (float) (vy * Math.sin(Math.toRadians(angle)) + 9.8 * flightTime / 180);
                    } else {
                        vx = vy = initvy = 1;
                    }
                    repaint();
                }

                // slider update
                if (validBoxPress) {
                    if (e.getX() >= 200 - 5 && e.getX() <= 500 - boxSize + 5) {
                        position = (int) ((e.getX() - 200) * 1.0 / 300 * 100);
                    }

                    repaint();
                }
            }
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
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

        frame.getContentPane().add(bowPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}

