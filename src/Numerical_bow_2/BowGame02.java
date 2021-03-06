package Numerical_bow_2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.LineBorder;
import javax.swing.Timer;

public class BowGame02 extends JPanel {

    private Timer timer;
    private Timer animate;
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
    private ArrayList<Polygon> leftArrows = new ArrayList<>();
    private ArrayList<Polygon> rightArrows = new ArrayList<>();
    private ArrayList<Double> leftArrowsAngle = new ArrayList<>();
    private ArrayList<Double> rightArrowsAngle = new ArrayList<>();
    private ArrayList<Node> leftArrowsValue = new ArrayList<>();
    private ArrayList<Node> rightArrowsValue = new ArrayList<>();
    private Point[] leftJoints = new Point[6];
    private Point[] rightJoints = new Point[6];
    private Point[] leftJointsOri = new Point[6];
    private Point[] rightJointsOri = new Point[6];
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
    private final int length = 80;
    private final int width = 3;
    // Arrow Shooting
    private double angle = 0;
    private double angleReleased = 0;
    private double power = 0;
    private double gravity = 9.81;
    private double vx = 0;
    private double vy = 0;
    private double x = 0;
    private double y = 0;
    private double initV = 0;
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
    private int boxSize = 16;
    private int boxX;
    private int boxY;
    private boolean validClick = false;
    private boolean validBoxPress = false;
    private int position;
    // Game Loop
    private boolean END = false;

    public BowGame02() {
        MyKeyListener myKeyListener = new MyKeyListener();
        addKeyListener(myKeyListener);
        this.setVisible(true);
        MouseHandler myHandler = new MouseHandler();
        addMouseListener(myHandler);
        addMouseMotionListener(myHandler);
        this.setVisible(true);
        setBackground(new Color(0xFFEBCD));

        this.viewport_size = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(100010000, 10008000));  //10000, 8000
        this.world_size = getPreferredSize();

        //camera view
        this.offsetMaxX = world_size.width - viewport_size.width;
        this.offsetMaxY = world_size.height - viewport_size.height;
        this.offsetMinX = 0;
        this.offsetMinY = 0;

        //Players init
        this.playerL = new Point(10004000, landHeight - 80);
        this.playerR = new Point(10004500, landHeight - 80);  //6000
        this.camYAdjust = 450;
        this.camY = landHeight - camYAdjust;
        this.camX = playerL.x - 120;
        System.out.println("Init : camX : " + camX % 10000 + ", camY : " + camY % 10000);

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
            System.out.println("click once");
            this.camX = this.world_size.width * position / 100;
            this.camY = landHeight - camYAdjust;
        } else {
            if (isLeftTurn) {
                if (initL & !isReleased) {
//                    System.out.println("initL (camera) ");
                    this.camX = playerL.x - 120;
                    this.camY = landHeight - camYAdjust;
                } else if (!isReleased) {
//                    System.out.println("prepare to shoot");
                    int pivotX = leftArrows.get(leftArrows.size() - 1).xpoints[0];
                    this.camX = pivotX - this.viewport_size.width / 3;
                    this.camY = landHeight - camYAdjust;
                } else {
                    int pivotX = leftArrows.get(leftArrows.size() - 1).xpoints[0];
                    int pivotY = leftArrows.get(leftArrows.size() - 1).ypoints[0];
//                    System.out.println("--------------------pivotY " + pivotY);
                    this.camX = pivotX - this.viewport_size.width / 3;
                    this.camY = pivotY - this.viewport_size.height / 3;

//                    this.camY = pivotY - 1000;
                }
            } else {
                if (initR & !isReleased) {
//                    System.out.println("initR (camera) ");
                    this.camX = playerR.x + 120;
                    this.camY = landHeight - camYAdjust;
                } else if (!isReleased) {
                    int pivotX = rightArrows.get(rightArrows.size() - 1).xpoints[0];
                    this.camX = pivotX - this.viewport_size.width / 3;
                    this.camY = landHeight - camYAdjust;
                } else {
                    int pivotX = rightArrows.get(rightArrows.size() - 1).xpoints[0];
                    int pivotY = rightArrows.get(rightArrows.size() - 1).ypoints[0];
                    this.camX = pivotX - this.viewport_size.width / 3;
                    this.camY = pivotY - this.viewport_size.height / 3;
//                    this.camY = pivotY - 1000;
                }
            }
            if (camY > (landHeight - camYAdjust)) {
//                System.out.println("----------------");
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

        createSlider(g2d);

        createPlayer(g2d, playerL, true);
        createPlayer(g2d, playerR, false);
        renderLeftPlayerHand(g2d);
        renderRightPlayerHand(g2d);

        createLand(g2d);

        lifeBar(g2d);

        updateCamera();

        renderLeftArrow();
        renderRightArrow();
        renderShootedArrow();

    }

    public void createPlayer(Graphics2D g2d, Point p, boolean isLeft) {
        int height = 100;
        int width = 15;

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1f));

        //body
        g2d.fillOval(p.x, p.y, width, height / 2);
        //head
        g2d.fillOval(p.x, p.y - width, width, width);
        //leg
        g2d.fillRect(p.x + 2, p.y + height / 2, 4, height / 2 - width);
        g2d.fillRect(p.x + width / 2, p.y + height / 2, 4, height / 2 - width);

//        //area (checking)
        Point topLeft = new Point(p.x - 3, p.y - 16);
        Point topRight = new Point(p.x + 16, p.y - 16);
        Point bottomLeft = new Point(p.x - 3, p.y + 85);
        Point bottomRight = new Point(p.x + 16, p.y + 85);
        g2d.drawLine(topLeft.x, topLeft.y, topRight.x, topRight.y);  //top
        g2d.drawLine(topLeft.x, topLeft.y, bottomLeft.x, bottomLeft.y);  //left
        g2d.drawLine(topRight.x, topRight.y, bottomRight.x, bottomRight.y);  //right
        g2d.drawLine(bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y);  //bottom
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
                    leftArrowsValue.add(new Node());
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
                    rightArrowsValue.add(new Node());
                }
            }
        }
    }

    public void renderLeftPlayerHand(Graphics2D g2d) {
        g2d.setColor(Color.cyan);
        g2d.setStroke(new BasicStroke(2f));
        //pull backward
        //arm
        g2d.drawLine(leftJoints[1].x, leftJoints[1].y, leftJoints[0].x, leftJoints[0].y);
        //elbow
        g2d.drawLine(leftJoints[1].x, leftJoints[1].y, leftJoints[2].x, leftJoints[2].y);

        g2d.setColor(Color.GREEN);
        //straight
        if (isRaiseArrow) {
            //arm
            g2d.drawLine(leftJoints[3].x, leftJoints[3].y, leftJoints[4].x, leftJoints[4].y);
            //elbow
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
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            double x0 = leftJoints[3].x;     // point to rotate about
            double y0 = leftJoints[3].y;
            Point temp = leftJoints[5];

            double a = leftJoints[5].x - x0;
            double b = leftJoints[5].y - y0;
            int x = (int) (+a * cos - b * sin + x0);
            int y = (int) (+a * sin + b * cos + y0);

            leftJoints[5] = new Point(x, y);

            //rotate with angle
            g2d.drawLine(leftJoints[3].x, leftJoints[3].y, leftJoints[5].x, leftJoints[5].y);

            //update bow position
            Point h = leftJoints[5];  //hand
            int[] x_ = new int[]{h.x - 15, h.x - 5, h.x - 15, h.x};
            int[] y_ = new int[]{h.y - 50, h.y, h.y + 50, h.y};
            bows[0] = new Polygon(x_, y_, x_.length);

            renderLeftBow(g2d);

            leftJoints[5] = temp;
        }
    }

    public void renderRightPlayerHand(Graphics2D g2d) {
        g2d.setColor(Color.cyan);
        g2d.setStroke(new BasicStroke(2f));
        //pull backward
        //arm
        g2d.drawLine(rightJoints[1].x, rightJoints[1].y, rightJoints[0].x, rightJoints[0].y);
        //elbow
        g2d.drawLine(rightJoints[1].x, rightJoints[1].y, rightJoints[2].x, rightJoints[2].y);

        g2d.setColor(Color.GREEN);
        //straight
        if (isRaiseArrow) {
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
        } else {
            int current = rightArrows.size() - 1;
            double angle = Math.toRadians(-rightArrowsAngle.get(current) - 180);
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            double x0 = rightJoints[3].x;     // point to rotate about
            double y0 = rightJoints[3].y;
            Point temp = rightJoints[5];

            double a = rightJoints[5].x - x0;
            double b = rightJoints[5].y - y0;
            int x = (int) (+a * cos - b * sin + x0);
            int y = (int) (+a * sin + b * cos + y0);
            rightJoints[5] = new Point(x, y);

            //rotate with angle
            g2d.drawLine(rightJoints[3].x, rightJoints[3].y, rightJoints[5].x, rightJoints[5].y);

            //update bow position
            Point h = rightJoints[5];  //hand
            int[] x_ = new int[]{h.x + 15, h.x + 5, h.x + 15, h.x};
            int[] y_ = new int[]{h.y - 50, h.y, h.y + 50, h.y};
            bows[1] = new Polygon(x_, y_, x_.length);

            renderRightBow(g2d);

            rightJoints[5] = temp;
        }
    }

    public void animatePullString() throws InterruptedException {
        if (isPullString) {

            if (isLeftTurn) {
                Point shoulder = leftJoints[3];
                Point elbow = new Point(leftJoints[4].x + 1, (int) (leftJoints[4].y - 0.5));
                if (elbow.y <= shoulder.y) {
                    elbow = new Point(leftJoints[4].x + 1, shoulder.y);
                }
                Point hand = new Point(leftJoints[5].x + 1, leftJoints[5].y - 1);
                Point elbowB = new Point((int) (leftJoints[1].x - 0.5), (int) (leftJoints[1].y - 0.5));
                Point handB = new Point((int) (leftJoints[2].x - 0.5), (int) (leftJoints[2].y - 0.5));
                if (elbowB.y < shoulder.y + 3) {
                    elbowB = new Point((int) (leftJoints[1].x - 0.5), (int) (leftJoints[1].y));
                    handB = new Point((int) (leftJoints[2].x - 0.5), (int) (leftJoints[2].y));
                }

                if (hand.y > leftJoints[3].y) {

                    //straight elbow
                    leftJoints[4] = elbow;
                    //straight hand
                    leftJoints[5] = hand;

                    leftJoints[1] = elbowB;
                    leftJoints[2] = handB;

                } else {
                    animate.stop();

                }
                //update bow position

                //update arrow position
                if (!isReleased) {
                    Point h = leftJoints[2];  //hand
                    x = h.x;
                    y = h.y;  ///!!!!!

                    int[] i_ = new int[]{h.x, h.x + length, h.x + length - 10, h.x + length, h.x + length + 2, h.x + length - 10, h.x + length, h.x};
                    int[] j_ = new int[]{h.y, h.y, h.y - 5, h.y, h.y + (this.width / 2), h.y + this.width + 5, h.y + this.width, h.y + this.width};
                    int current = leftArrows.size() - 1;
//                    leftArrows.set(n, new Polygon(i_, j_, i_.length));
                    if (isRaiseArrow) {
                        leftArrowsAngle.set(current, raiseAngle);
                        raiseAngle -= 1;
                        if (raiseAngle <= angle) {
                            isRaiseArrow = false;
                        }
                    } else {
                        leftArrowsAngle.set(current, angle);
                    }
                }
            } else {
                Point shoulder = rightJoints[3];
                Point elbow = new Point(rightJoints[4].x - 1, (int) (rightJoints[4].y - 0.5));
                if (elbow.y <= shoulder.y) {
                    elbow = new Point(rightJoints[4].x - 1, shoulder.y);
                }
                Point hand = new Point(rightJoints[5].x - 1, rightJoints[5].y - 1);
                Point elbowB = new Point((int) (rightJoints[1].x + 0.5), (int) (rightJoints[1].y - 0.5));
                Point handB = new Point((int) (rightJoints[2].x + 0.5), (int) (rightJoints[2].y - 0.5));
                if (elbowB.y < shoulder.y + 3) {
                    elbowB = new Point((int) (rightJoints[1].x + 0.5), (int) (rightJoints[1].y));
                    handB = new Point((int) (rightJoints[2].x + 0.5), (int) (rightJoints[2].y));
                }

                if (hand.y > rightJoints[3].y) {

                    //straight elbow
                    rightJoints[4] = elbow;
                    //straight hand
                    rightJoints[5] = hand;

                    rightJoints[1] = elbowB;
                    rightJoints[2] = handB;

                } else {
                    animate.stop();

                }
//                update bow position

                //update arrow position
                if (!isReleased) {
                    Point h = rightJoints[2];  //hand

                    int[] i_ = new int[]{h.x, h.x - length, h.x - length + 10, h.x - length, h.x - length - 2, h.x - length + 10, h.x - length, h.x};
                    int[] j_ = new int[]{h.y, h.y, h.y - 5, h.y, h.y + (this.width / 2), h.y + this.width + 5, h.y + this.width, h.y + this.width};
                    int current = rightArrows.size() - 1;
                    rightArrows.set(current, new Polygon(i_, j_, i_.length));
                    if (isRaiseArrow) {
                        rightArrowsAngle.set(current, -raiseAngle - 180);
                        raiseAngle -= 1;
//                        Thread.sleep(100);
                        if (raiseAngle <= angle) {
                            isRaiseArrow = false;
                        }
                    } else {
                        rightArrowsAngle.set(current, -angle - 180);
                    }
                }
            }
        }
    }

    public Polygon leftBowRotate(int current) {
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
        return temp;
    }

    public void renderLeftBow(Graphics2D g2d) {
        g2d.setColor(Color.DARK_GRAY);
        int current = leftArrows.size() - 1;
        Polygon temp = leftBowRotate(current);
        //bow
        g2d.fillPolygon(bows[0].xpoints, bows[0].ypoints, bows[0].npoints);
        //bow string
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
        g2d.setColor(Color.DARK_GRAY);
        int current = rightArrows.size() - 1;
        Polygon temp = rightBowRotate(current);
        //bow
        g2d.fillPolygon(bows[1].xpoints, bows[1].ypoints, bows[1].npoints);
        //bow string
        g2d.drawLine(rightJoints[2].x, rightJoints[2].y, bows[1].xpoints[0], bows[1].ypoints[0]);
        g2d.drawLine(rightJoints[2].x, rightJoints[2].y, bows[1].xpoints[2], bows[1].ypoints[2]);
        bows[1] = temp;
    }

    public Polygon leftArrowRotate(int current) {
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
        g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2d.drawString(String.format("x_[4] : %d, y_[4] : %d", x_[4] % 10000, y_[4] % 10000), x_[4], y_[4]);
        return temp;
    }

    public void renderLeftArrow() {
        g2d.setColor(Color.DARK_GRAY);
        if (isLeftTurn & isTouch) {
            g2d.setColor(Color.RED);
        }
        int current = leftArrows.size() - 1;
        if (!isReleased) {
            Polygon temp = leftArrowRotate(current);
            g2d.draw(leftArrows.get(current));
            g2d.fill(leftArrows.get(current));
            leftArrows.set(current, temp);
        } else {
            AffineTransform originalTransform = g2d.getTransform();
            int[] x_ = new int[]{(int) x, (int) x + length, (int) x + length - 10, (int) x + length,
                (int) x + length + 2, (int) x + length - 10, (int) x + length, (int) x};
            int[] y_ = new int[]{(int) y, (int) y, (int) y - 5, (int) y, (int) y + (this.width / 2),
                (int) y + this.width + 5, (int) y + this.width, (int) y + this.width};
            leftArrows.set(current, new Polygon(x_, y_, x_.length));

            g2d.rotate(Math.atan2(vy, vx), x - width, y);
            leftArrowsValue.set(current, new Node(vx, vy, x- width, y));

            g2d.draw(leftArrows.get(current));
            g2d.fill(leftArrows.get(current));
            g2d.setTransform(originalTransform);
        }
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
        g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2d.drawString(String.format("x_[4] : %d, y_[4] : %d", x_[4] % 10000, y_[4] % 10000), x_[4], y_[4]);
        return temp;
    }

    public void renderRightArrow() {
        g2d.setColor(Color.DARK_GRAY);
        if (!isLeftTurn & isTouch) {
            g2d.setColor(Color.RED);
        }
        int current = rightArrows.size() - 1;
        if (!isReleased) {
            Polygon temp = rightArrowRotate(current);
            g2d.draw(rightArrows.get(current));
            g2d.fill(rightArrows.get(current));
            rightArrows.set(current, temp);
        } else {
            AffineTransform originalTransform = g2d.getTransform();
            int[] x_ = new int[]{(int) x, (int) x - length, (int) x - length + 10, (int) x - length,
                (int) x - length - 2, (int) x - length + 10, (int) x - length, (int) x};
            int[] y_ = new int[]{(int) y, (int) y, (int) y - 5, (int) y, (int) y + (this.width / 2),
                (int) y + this.width + 5, (int) y + this.width, (int) y + this.width};
            rightArrows.set(current, new Polygon(x_, y_, x_.length));

            g2d.rotate(Math.atan2(-vy, vx), x + width, y);
            rightArrowsValue.set(current, new Node(vx, -vy, x+ width, y));

            
            g2d.draw(rightArrows.get(current));
            g2d.fill(rightArrows.get(current));
            g2d.setTransform(originalTransform);
        }

    }

    public void renderShootedArrow() {
        g2d.setColor(Color.RED);
        for (int n = 0; n < leftArrows.size() - 1; n++) {
//            Polygon temp = leftArrowRotate(n);
//            g2d.draw(leftArrows.get(n));
//            g2d.fill(leftArrows.get(n));
//            leftArrows.set(n, temp);
//            
            AffineTransform originalTransform = g2d.getTransform();
            double vx = leftArrowsValue.get(n).vx;
            double vy = leftArrowsValue.get(n).vy;
            double x = leftArrowsValue.get(n).x;
            double y = leftArrowsValue.get(n).y;
            int[] x_ = new int[]{(int) x, (int) x + length, (int) x + length - 10, (int) x + length,
                (int) x + length + 2, (int) x + length - 10, (int) x + length, (int) x};
            int[] y_ = new int[]{(int) y, (int) y, (int) y - 5, (int) y, (int) y + (this.width / 2),
                (int) y + this.width + 5, (int) y + this.width, (int) y + this.width};
            leftArrows.set(n, new Polygon(x_, y_, x_.length));

            g2d.rotate(Math.atan2(vy, vx), x - width, y);

            g2d.draw(leftArrows.get(n));
            g2d.fill(leftArrows.get(n));
            g2d.setTransform(originalTransform);
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

        //debug
        g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2d.drawString(String.format("Height : %d", landHeight % 10000), playerL.x + 50, landHeight - 10);
        g2d.drawString(String.format("CamX : %d, CamY : %d", camX % 10000, camY % 10000), camX, camY + 20);
        int pivotX = leftArrows.get(leftArrows.size() - 1).xpoints[0];
        int pivotY = leftArrows.get(leftArrows.size() - 1).ypoints[0];
        int pivotXR = rightArrows.get(rightArrows.size() - 1).xpoints[0];
        int pivotYR = rightArrows.get(rightArrows.size() - 1).ypoints[0];
        g2d.drawString(String.format("Left : PivotX : %d, PivotY : %d", pivotX % 10000, pivotY % 10000), camX, camY + 150);
        g2d.drawString(String.format("Right : PivotX : %d, PivotY : %d", pivotXR % 10000, pivotYR % 10000), camX, camY + 170);
    }

    public void createSlider(Graphics2D g2d) {
        this.sliderX1 = camX + 200;
        this.sliderX2 = camX + 200 + 300;
        this.sliderY = landHeight + 150;
        this.boxX = sliderX1 + (int) ((sliderX2 - sliderX1) * ((position * 1.0) / 100));
        this.boxY = sliderY - (boxSize / 2);

//        System.out.println("Position : " + position);
//        System.out.println("boxX : " + boxX);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2d.drawLine(sliderX1, sliderY, sliderX2, sliderY);

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
            // time
            flightTime++;
            // speeds: vx constant (ignoring .. stuff), and vy =
            vy = (float) (initV * Math.sin(angle) + 9.8 * flightTime / 180); // in m/s
            vy = vy * 100 / 180; // in px/ticks

            if (!isLeftTurn) {
                x -= vx;
            } else {
                x += vx;
            }
            y += vy;

//            int n = arrow.size() - 1;
//            Polygon temp = arrow.get(n);
//            int[] x_ = new int[temp.npoints];
//            int[] y_ = new int[temp.npoints];
//            for (int i = 0; i < temp.npoints; i++) {
//                x_[i] = temp.xpoints[i] + (int) (vx * 0.5);
//                y_[i] = temp.ypoints[i] + (int) (vy * 0.5);
//            }
//            arrow.set(n, new Polygon(x_, y_, x_.length));
//            System.out.println("vx : " + vx + " | vy :" + vy);
            // rotation effect
//            arrowRotationUpdate(n, isLeftTurn, vx, vy);
//            vy += gravity / 100;
//            flightTime += 2;
//            vy = (float) (initV * Math.sin(angle) + 9.8 * flightTime / 100); // in m/s
//            vy = vy * 100 / 180; // in px/ticks
//            
//            if (vx - 0.005 >= power / 2) {
//                vx -= 0.005;
//            }
            // rotate to confirm position
//            int[] pivot = arrowRotatedPoint(n, isLeftTurn); //arrow head  
//            int pivotX = pivot[0];
//            int pivotY = pivot[1];
            int pivotX = (int) x;
            int pivotY = (int) y;
            // check touch
            if (isLeftTurn) {
                if ((pivotX) > (playerR.x - 3) && (pivotX) < (playerR.x + 16)
                        && pivotY < (playerR.y + 85) && pivotY > (playerR.y - 16)) {  //touch player
                    isTouch = true;
                    isAttacked = true;
                    rightLife -= 350;
                    System.out.println("Left arrow attacked");
                    arrowisTouch();
                } else if (pivotY > (landHeight + 50)) {  //touch floor   
                    isTouch = true;
                    System.out.println("Left arrow dropped to floor " + (landHeight + 120));
                    arrowisTouch();
                }
            } else {
                if (pivotX < (playerL.x + 16) && pivotX > (playerL.x - 3)
                        && pivotY < (playerL.y + 85) && pivotY > (playerL.y - 16)) {
                    isTouch = true;
                    isAttacked = true;
                    leftLife -= 350;
                    System.out.println("Right arrow attacked");
                    arrowisTouch();
                } else if (pivotY > (landHeight + 50)) {
                    isTouch = true;
                    System.out.println("Right arrow dropped to floor " + (landHeight + 120));
                    arrowisTouch();
                }
            }
        }
    }

    public void arrowisTouch() { // init component
        validClick = false;
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        timer.stop();
        if (rightLife <= 0 || leftLife <= 0) {
            END = true;
        }
        if (isLeftTurn) {
            initL = true;
            position = (int) ((playerL.x / (world_size.width * 1.0)) * 100);
        } else {
            initR = true;
            position = (int) ((playerR.x / (world_size.width * 1.0)) * 100);
        }
        isLeftTurn = !isLeftTurn;
        System.out.println("isLeftTurn : " + isLeftTurn);
        angle = 0;
        angleReleased = 0;
        flightTime = 0;

        power = 0;
        isTouch = false;
        isAttacked = false;
        isReleased = false;
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
                System.out.println("xStart : " + xStart + ", yStart : " + yStart);
                int x = 200 + (int) ((500 - 200) * position * 1.0 / 100);
                System.out.println("-- x : " + x);
                if ((xStart >= x - 8 && xStart >= x + 8) && (yStart >= 590 && yStart <= 606)) {
                    System.out.println("validBoxPress = true;");
                    validBoxPress = true;
                    isDrag = false;
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
                if (validClick & isDrag) {
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
                        timer = new Timer(100, l -> {  //10
                            shootArrow(leftArrows);
                            validClick = false;
                            repaint();
                        });
                    } else {
                        timer = new Timer(100, l -> {
                            shootArrow(rightArrows);
                            validClick = false;
                            repaint();
                        });
                    }
                    timer.start();
                }

                if (validBoxPress) {
                    validBoxPress = false;
                    System.out.println("mouse Released");
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

                    // pull bowString animation
                    if (isPullString) {
                        animate = new Timer(200, l -> {
                            try {
                                animatePullString();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(BowGame02.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            repaint();
                        });
                        animate.start();
                    }

                    // x-axis for power
                    int horizontalDrag = (e.getX() - xStart) / 1;
//                    System.out.println("===================horizontalDrag : " + horizontalDrag);
                    if (isLeftTurn) {
                        horizontalDrag *= -1;
                    }
                    power = horizontalDrag;
                    if (power < 0) {
                        power = 0;
                    } else if (power >= 150) {
                        power = 150;
                    }
//                    //update vx vy
//                    if (isLeftTurn) {
//                        vx = 10 + power % 10 + power / 10;  // 10, time delay 10
//                    } else {
//                        vx = -10 - power % 10 - power / 10;
//                    }
                   
//                    vx = 10 + power % 10 + power / 10;
//                    vy = -power % 10;


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
                    } else {
//                        rightArrowsAngle.set(rightArrows.size() - 1, -angle - 180);
                        rightArrowsAngle.set(rightArrows.size() - 1, angle);
                    }
                    
                    initV = power * 150/100;
                    double radAngle = Math.toRadians(angle);
                    vx = initV * Math.cos(radAngle);
                    vx = vx * 100 / 180;
                    vy = initV * Math.sin(radAngle);
                    
                    repaint();
                }

                // slider update
                if (validBoxPress) {
                    //200 - 500
                    //200-320  //321 - 500
                    double diff = (e.getX() - xStart);

                    System.out.println("---diff " + diff);
                    int a = sliderX1;
                    int b = sliderX2;
                    int total = sliderX2 - sliderX1;
                    double realDiff = ((diff * 1.0 / total) * 1.01);
                    System.out.println("real diff : " + realDiff);
                    if (diff < 0) {
                        position += realDiff;
                    } else {
                        position += realDiff * 3;
                    }
                    if (position <= 0) {
                        position = 0;
                        xStart = e.getX();

                    } else if (position >= 100) {
                        position = 100;
                        xStart = e.getX();

                    }
                    System.out.println("--- postiion : " + position);

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
    
    private class Node{
        private double vx;
        private double vy;
        private double x;
        private double y;
        
        public Node(){
            
        }
        
        public Node(double vx, double vy, double x, double y){
            this.vx = vx;
            this.vy = vy;
            this.x = x;
            this.y = y;
                      
        }

        public double getVx() {
            return vx;
        }

        public void setVx(double vx) {
            this.vx = vx;
        }

        public double getVy() {
            return vy;
        }

        public void setVy(double vy) {
            this.vy = vy;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
        
    }

    public static void main(String args[]) {

        JFrame frame = new JFrame("Bow Shooting Game");
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);
        BowGame02 bowPanel = new BowGame02();

        JScrollPane gameboard = new JScrollPane(bowPanel);
        gameboard.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        gameboard.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        gameboard.setViewportBorder(new LineBorder(Color.BLACK));
        frame.getContentPane().add(gameboard, BorderLayout.CENTER);

//        frame.getContentPane().add(bowPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

}
