package numerical_bow;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

// http://greenteapress.com/thinkjava6/html/thinkjava6017.html
// https://www.youtube.com/watch?v=pDafZdIIeNE
// https://gamedev.stackexchange.com/questions/44256/how-to-add-a-scrolling-camera-to-a-2d-java-game
/**
 * 1) Once the game is start, both player stand at their position, with bow and
 * arrow
 *
 * 2) When mouse click is pointed near the particular player, the dragging(X)
 * control power, dragging(Y) control direction(angle).
 *
 * 3) When mouse click released, arrow is being shoot with certain acceleration.
 *
 *
 * --later when we make 2 players far apart, our scene should follow the flying
 * of the arrow.
 *
 * 4) Two condition for the released arrow:
 *
 * --4a) successfully attacked opponent
 *
 * ----decrease opponent life, if less than 0, died, game over
 *
 * --4b) drop to floor (then change arrow to another color)
 *
 * 5) Once the shooting is done, created new arrow for player
 *
 * 6) The scene will stop at the arrow stop point (either land or another
 * player)
 *
 * --We use scroll bar(controlled by keyboard?) to find another player
 *
 * 7) Another player start to shoot.
 */
public class NumericalBow extends JPanel {

    private Graphics2D g;
    private BufferedImage image;
    private Background background;
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
    public int curX, curY;
    private int landHeight = 5500;  //5500
    private ArrayList<Arrow> arrows = new ArrayList<>();
    // Game Logic
    private boolean isReleased = false;
    private boolean initL = true;
    private boolean initR = true;
    private boolean isDragL = false;
    private boolean isLeft = true;
    private boolean isLeftTurn = true;
    private boolean isTouch = false;
    // Arrow Shooting
    private double angle = 0;
    private double angleDiff = 0;
    private double angleLast = 0;
    private double angleReleased = 0;
    private double rotation = 0;
    private double power = 0;
    private double deltaTime = 0.0;
    private double ax = 0.0;
    private double ay = -9.81;
    private double vx = 0.0;
    private double vy = 0.0;
    //Arrow Adjust
    private int xStart = 0;
    private int yStart = 0;
    private double MaxAngle = 90;
    // Game Loop
    private int roundCtn = 1;
    private boolean END = false;

    public NumericalBow() {

//        SplashScreen splash = new SplashScreen();
//        try {
//            // Make JWindow appear for 1.5 seconds before disappear
//            Thread.sleep(1500);
//            splash.dispose();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //Layout
        setBackground(new Color(0xFFEBCD));
        background = new Background();
//        try {
//            image = ImageIO.read(new File("src\\numerical_bow\\background.jpg"));
//        } catch (Exception e) {
//            System.out.println(e);
//        }

        this.viewport_size = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(10000, 10000));
        this.world_size = getPreferredSize();

        //camera view
        this.offsetMaxX = world_size.width - viewport_size.width;
        this.offsetMaxY = world_size.height - viewport_size.height;
        this.offsetMinX = 0;
        this.offsetMinY = 0;

        //Players init
        this.playerL = new Point(100, landHeight - 100);
        this.playerR = new Point(1200, landHeight - 100);
        this.camYAdjust = 300;
        this.camY = landHeight - camYAdjust;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                isReleased = true;
//                angleReleased = angle;
                shootArrow().start();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isTouch = false;
                xStart = e.getX();
                yStart = e.getY();
//                if (isLeftTurn) {
//                    switch (e.getButton()) {
//                        case MouseEvent.BUTTON1:
//                            System.out.println("Left mouse click");
//                            rotation -= 5;
//
//                            repaint();
//                            rotation += 5;
//                            break;
//                        case MouseEvent.BUTTON3:
//                            System.out.println("Right mouse click");
//                            rotation++;
//
//                            repaint();
//                            rotation--;
//                            break;
//                    }
//                } else {
//                    switch (e.getButton()) {
//                        case MouseEvent.BUTTON1:
//                            System.out.println("Left mouse click");
//                            rotation += 5;
//
//                            repaint();
//                            rotation -= 5;
//                            break;
//                        case MouseEvent.BUTTON3:
//                            System.out.println("Right mouse click");
//                            rotation--;
//
//                            repaint();
//                            rotation++;
//                            break;
//                    }
//                }
                updateCamera();
            }

        }
        );

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {

                Point p = e.getPoint();
                curX = p.x;
                curY = p.y;

                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {

                // x for power
                int horizontalDrag = (e.getX() - xStart) / 10;
                if (isLeftTurn) {
                    horizontalDrag *= -1;
                } else {

                }
//                System.out.println("horizontalDrag : " + horizontalDrag);
                if (horizontalDrag > 0) {
                    power = horizontalDrag * (10 + horizontalDrag);
                }

                // y for angle
                int verticalDrag = Math.abs((e.getX() - xStart) / 10);
//                System.out.println("verticalDrag : " + verticalDrag);
                angle = verticalDrag * (10 + verticalDrag);
                angleDiff = angle - angleLast;
                if (angle > 85) {
                    angle = 85;
                    angleDiff = 0;
                }
                angleLast = angle;
                angleReleased = angleLast;
                repaint();

            }
        }
        );

//        addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                switch (e.getKeyCode()) {
//                    case KeyEvent.VK_LEFT:
//                        if (!END) {
//                            System.out.println("Left key pressed.");
//                        }
//                        break;
//                    case KeyEvent.VK_RIGHT:
//                        if (!END) {
//                            System.out.println("Right key pressed.");
//                        }
//                        break;
//    
//                }
//                repaint();
//            }
//        });
    }

    private void updateCamera() {
        if (isLeftTurn) {
            this.camX = arrows.get(roundCtn - 1).getPolygon().xpoints[6] - this.viewport_size.width / 2;
            this.camY = arrows.get(roundCtn - 1).getPolygon().ypoints[6] - this.viewport_size.height / 2 + 800;
        } else {
            this.camX = arrows.get(roundCtn).getPolygon().xpoints[6] - this.viewport_size.width / 2;
            this.camY = arrows.get(roundCtn).getPolygon().ypoints[6] - this.viewport_size.height / 2 + 800;

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

    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
//        background.drawBackground(g, viewport_size.width, 300);
        g.translate(-camX, -camY);

        draw(g);
    }

    public void draw(Graphics2D g) {
        createPlayer(g, playerL, true);
        createPlayer(g, playerR, false);

        createLand(g);

        rotate();

        lifeBar(g);
    }

    public void createPlayer(Graphics g, Point p, boolean isLeft) {
        int height = 100;
        int width = 15;

        //body
        g.fillOval(p.x, p.y, width, height / 2);
        //head
        g.fillOval(p.x, p.y - width, width, width);
        //leg
        g.fillOval(p.x, p.y + height / 2, height / (width + 5) * 2, height / 2 - width);
        g.fillOval(p.x + width / 2, p.y + height / 2, height / (width + 5) * 2, height / 2 - width);

        //hand
//        Graphics2D g2d = (Graphics2D) g;
//        Stroke stroke = new BasicStroke(2f);
//        int[] pointsLeft = drawLinesLeft(g2d, stroke, playerL, playerR);
//        drawLinesRight(pointsLeft, g2d, stroke);
//        g.fillOval(p.x - width - 10, p.y + width, height / 2 - width, height / width + 2);
//        g.fillOval(p.x + width, p.y + width, height / 2 - width, height / width + 2);
        //bow
        //TODO: Movable hand & bow, link with arrow
        //TODO: Arrow dragging (control both power & angle)
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
            arrows.add(new Arrow(g, arrowPoint, isLeft));  //even-left side; odd-right side

        } else if (initR & !isLeft) {
            initR = false;
            arrowPoint = new Point(playerR.x - 50, playerR.y);
            System.out.println("Arrow 1 created");
            arrows.add(new Arrow(g, arrowPoint, isLeft));  //even-left side; odd-right side
        }

    }

    public void timeStep() {  //https://stackoverflow.com/questions/17032820/projectile-motion-of-an-object
        vx += ax * deltaTime;
        vy += ay * deltaTime;
        System.out.println("velocity : " + vx + ", " + vy);
//        if (vy < 20) {
//            angle -- ;
//            if (angle < -angleReleased){
//                angle = -angleReleased;
//                System.out.println("now stop angle " + angle);
//            }
//            angleDiff = angle - angleLast;
//            angleLast = angle;
//        }         

    }

    public Thread shootArrow() {
        return new Thread() {
            public void run() {
                long time = System.currentTimeMillis();
                if(angle > 45){
                    angle = 45;
                }
                double initVel = power;
                vx = initVel * Math.cos(Math.toRadians(angle));
                vy = initVel * 2 * Math.sin(Math.toRadians(angle));

                while (isReleased & !isTouch) {
                    long newTime = System.currentTimeMillis();
                    deltaTime = (newTime - time) / (1000.0 / 3);
                    time = newTime;

                    if (isReleased & isLeftTurn) {
                        System.out.println("shoot left");
                        if (arrows.get(roundCtn - 1).getArrowCurrentLoc().x > playerR.x
                                || arrows.get(roundCtn - 1).getArrowCurrentLoc().y > landHeight) {
                            System.out.println("L should stop=============");
                            arrows.get(roundCtn - 1).projectile(g, 0, 0, deltaTime);
                            arrows.get(roundCtn - 1).setIsStop(true);
                        } else {
                            System.out.println("left arrow shooting");
                            timeStep();
                            arrows.get(roundCtn - 1).projectile(g, vx, vy, deltaTime);
                        }
                        System.out.println("Arrow 0 : " + Arrays.toString(arrows.get(roundCtn - 1).getPolygon().xpoints));
                        arrows.get(roundCtn).projectile(g, 0, 0, deltaTime);

                        isTouch = arrows.get(roundCtn - 1).getIsStop();
                        if (isTouch) {
                            initL = true;
                            isLeftTurn = false;
                            isReleased = false;
                            rotation = 0;
                            deltaTime = 0;
                            try {
                                Thread.sleep(1000);  // milliseconds
                                Thread.currentThread().interrupt();
                                return;
                            } catch (InterruptedException ex) {
                            }
                        }
                    } else if (isReleased & !isLeftTurn) {
                        System.out.println("shoot right");
                        if (arrows.get(roundCtn).getArrowCurrentLoc().x < playerL.x + 50
                                || arrows.get(roundCtn).getArrowCurrentLoc().y > landHeight) {
                            System.out.println("R should stop=============");
                            arrows.get(roundCtn).projectile(g, 0, 0, deltaTime);
                            arrows.get(roundCtn).setIsStop(true);
                        } else {
                            System.out.println("right arrow shooting");
                            timeStep();
                            arrows.get(roundCtn).projectile(g, -vx, vy, deltaTime);
                        }
                        System.out.println("Arrow 1 : " + Arrays.toString(arrows.get(roundCtn).getPolygon().xpoints));
                        arrows.get(roundCtn - 1).projectile(g, 0, 0, deltaTime);

                        isTouch = arrows.get(roundCtn).getIsStop();
                        if (isTouch) {
                            initR = true;
                            isLeftTurn = true;
                            isReleased = false;
                            rotation = 0;
                            deltaTime = 0;
                            try {
                                Thread.sleep(1000);  // milliseconds
                                Thread.currentThread().interrupt();
                                return;
                            } catch (InterruptedException ex) {
                            }
                        }
                    }
                    try {
                        Thread.sleep(1000 / 30);  // milliseconds
                    } catch (InterruptedException ex) {
                    }
                    updateCamera();
                    repaint();
                }
            }
        };
    }

    public void rotate() {
        if (isLeftTurn) {
            arrows.get(roundCtn - 1).rotate(g, -angleDiff, new Point(playerL.x, landHeight - 100));
            arrows.get(roundCtn).rotate(g, 0, new Point(playerL.x, landHeight - 100));
        } else {
            arrows.get(roundCtn).rotate(g, angleDiff, new Point(playerR.x, landHeight - 100));
            arrows.get(roundCtn - 1).rotate(g, 0, new Point(playerR.x, landHeight - 100));

        }

    }

    public void createLand(Graphics g) {

        g.drawLine(0, landHeight, 10000, landHeight);
    }

    //TODO: lifeBar update
    public void lifeBar(Graphics g) {

        if (isLeftTurn) {
            //lifebar

            //power
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString(String.format("Power : %.2f", power), playerL.x, landHeight - 230);
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString("Power : 0", playerR.x, landHeight - 230);

            //angle
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString(String.format("Angle : %.2f", angle), playerL.x, landHeight - 240);
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString("Angle : 0", playerR.x, landHeight - 240);
        } else {
            //lifebar

            //power
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString("Power : 0", playerL.x, landHeight - 230);
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString(String.format("Power : %.2f", power), playerR.x, landHeight - 230);

            //angle
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString("Angle : 0", playerL.x, landHeight - 240);
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString(String.format("Angle : %.2f", angle), playerR.x, landHeight - 240);
        }

    }

    //TODO: scene follow arrow position
    //TODO: scene scrolling (keyboard)
    public void sceneScroll() {

    }

    public int[] drawLinesLeft(Graphics2D g2d, Stroke stroke, Point p1, Point p2) {
        g2d.setStroke(stroke);

        int screen_split = 450;

        int newX_p1 = 0, newY_p1 = 0;
        int newX_p2 = 0, newY_p2 = 0;

        int minX_p1 = p1.x - 20, maxX_p1 = p1.x + 20, maxY_p1 = p1.y + 30, minY_p1 = p1.y - 20;
        int minX_p2 = p2.x, maxX_p2 = p2.x + 40, maxY_p2 = p2.y + 30, minY_p2 = p2.y - 20;

        if (curX < 450) {

            newX_p2 = minX_p2;
            newY_p2 = minY_p2;
            if (curX <= minX_p1) { //range
                if (curY >= maxY_p1) {
                    System.out.println("p1 c1.1");
                    newX_p1 = minX_p1;
                    newY_p1 = maxY_p1;
                } else if (curY <= minY_p1) {
                    System.out.println("p1 c1.2");
                    newX_p1 = minX_p1;
                    newY_p1 = minY_p1;
                } else {
                    System.out.println("p1 c1");
                    newX_p1 = minX_p1 - 10;
                    newY_p1 = curY;
                }
            } else if (curY >= maxY_p1) { //range = 20
                System.out.printf("p1 c2");
                if (curX >= maxX_p1) {
                    newX_p1 = maxX_p1;
                    newY_p1 = maxY_p1;
                } else {
                    newY_p1 = maxY_p1;
                    newX_p1 = curX;
                }
            } else if (curY <= minY_p1) { //range = 20
                System.out.println("p1 c3");
                if (curX >= maxX_p1) {
                    newX_p1 = maxX_p1;
                    newY_p1 = maxY_p1;
                } else {
                    newY_p1 = minY_p1;
                    newX_p1 = curX;
                }
            } else if ((curX > maxX_p1 && curY > minY_p1 && curY < maxY_p1)) {
                System.out.println("p1 c4");
                newY_p1 = curY;
                newX_p1 = maxX_p1;
            } else if ((curX > minX_p1 && curY > minY_p1 && curY < maxY_p1)) {
                System.out.println("p1 c4");
                newY_p1 = curY;
                newX_p1 = curX;
            }
        } else if (curX >= 450) {
            newX_p1 = maxX_p1;
            newY_p1 = maxY_p1;
            if (curX >= maxX_p2) {
                if (curY >= maxY_p2) {
                    System.out.println("c1.1");
                    newX_p2 = maxX_p2;
                    newY_p2 = maxY_p2;
                } else if (curY <= minY_p2) {
                    System.out.println("c1.2");
                    newX_p2 = maxX_p2;
                    newY_p2 = minY_p2;
                } else {
                    System.out.println("c1");
                    newX_p2 = maxX_p2;
                    newY_p2 = curY;
                }
            } else if (curY >= maxY_p2) {
                System.out.println("c2");
                if (curX <= minX_p2) {
                    newX_p2 = minX_p2;
                    newY_p2 = maxY_p2;
                } else {
                    newY_p2 = maxY_p2;
                    newX_p2 = curX;
                }
            } else if (curY <= minY_p2) { //range = 20
                System.out.println("c3");
                if (curX <= minX_p2) {
                    newX_p2 = minX_p2;
                    newY_p2 = maxY_p2;
                } else {
                    newY_p2 = minY_p2;
                    newX_p2 = curX;
                }
            } else if ((curX < minX_p2 && curY > minY_p2 && curY < maxY_p2)) {
                System.out.println("c4");
                newY_p2 = curY;
                newX_p2 = minX_p2;
            } else if ((curX > minX_p2 && curY > minY_p2 && curY < maxY_p2)) {
                System.out.println("c4 2 ");
                newY_p2 = curY;
                newX_p2 = curX;
            } else {
                newX_p2 = curX;
                newY_p2 = curY;
            }

//            second player
        }

//       Player 1
        g2d.setColor(Color.black);
        g2d.drawLine(newX_p1, newY_p1, p1.x, p1.y);
        g2d.setColor(Color.black);
        g2d.drawLine(newX_p1, newY_p1, p1.x, p1.y + 10);
//      Player 2
        g2d.setColor(Color.black);
        g2d.drawLine(newX_p2, newY_p2, p2.x + 20, p2.y);
        g2d.setColor(Color.black);
        g2d.drawLine(newX_p2, newY_p2, p2.x + 20, p2.y + 10);

        int[] points = new int[4];

        points[0] = newX_p1;
        points[1] = newY_p1;

        points[2] = newX_p2;
        points[3] = newY_p2;

        System.out.println("points : " + p1.getX() + p1.getY());
        System.out.println("points : " + p2.getX() + p1.getY());

        return points;
    }

    public void drawLinesRight(int[] points, Graphics2D g2d, Stroke stroke) {
        int oppositeY_p1 = points[1], oppositeX_p1 = points[0];
        int oppositeY_p2 = points[3], oppositeX_p2 = points[2];
        int distance_p1 = oppositeY_p1 - playerL.y;
        int distance_p2 = oppositeY_p2 - playerR.y;
        int newX_p1 = playerL.x + 20;
        int newY_p1 = 0;
        int newX_p2 = playerR.x;
        int newY_p2 = 0;

        g2d.getStroke();

//        player 1
        if (curX < 450) {
            if (distance_p1 >= 0 && oppositeX_p1 <= 100) {
                newX_p1 += 20;
                newY_p1 = playerL.y - distance_p1;
            } else if (distance_p1 < 0 && oppositeX_p1 <= 100) {
                newX_p1 += 30;
                newY_p1 = playerL.y - distance_p1;
            } else if (oppositeX_p1 > 100) {
                newX_p1 = playerL.x + 10;
                newY_p1 = playerL.y;
            }
            g2d.setColor(Color.black);
            g2d.drawLine(playerL.x + 20, playerL.y, newX_p1, newY_p1);
        } //        player 2
        else if (curX >= 450) {

            if (distance_p2 >= 0 && oppositeX_p2 <= playerR.x + 40) {
                newX_p2 -= 20;
                newY_p2 = playerR.y - distance_p2;
            } else if (distance_p2 < 0 && oppositeX_p2 > playerR.x + 10) {
                newX_p2 -= 30;
                newY_p2 = playerR.y - distance_p2 + 10;
            }
            if (curX <= playerR.x) {
                newX_p2 = playerR.x;
                newY_p2 = playerR.y;
            }

            g2d.setColor(Color.black);
            g2d.drawLine(playerR.x, playerR.y, newX_p2, newY_p2);

        }
    }

    //TODO: maintain dropped arrow, change color
    //
//    public Thread shootArrow() {
//        return new Thread() {
//            public void run() {
//                long time = System.currentTimeMillis();
//                double rad = Math.toRadians(5);
//                int power = 50;
//                ax = Math.cos(rad) * power;
//                ay = -Math.sin(rad) * power;
//
//                while (isReleased & !isTouch) {
//                    long newTime = System.currentTimeMillis();
//                    deltaTime = (newTime - time) / 1000.0;
//                    time = newTime;
//
//                    if (isReleased & isLeftTurn) {
//                        System.out.println("shoot left");
//                        arrows.get(roundCtn - 1).move(g, deltaTime);
//                        arrows.get(roundCtn - 1).accelerate(ax, ay, deltaTime);
//                        if (arrows.get(roundCtn - 1).getArrowCurrentLoc().x > playerR.x) {
//                            System.out.println("should stop");
//                            arrows.get(roundCtn - 1).move(g, 0);
//                            arrows.get(roundCtn - 1).setIsStop(true);
//                        }
//                        System.out.println("Arrow 0 : " + Arrays.toString(arrows.get(roundCtn - 1).getPolygon().xpoints));
//                        arrows.get(roundCtn).move(g, 0);
//
//                        isTouch = arrows.get(roundCtn - 1).getIsStop();
//                        if (isTouch) {
//                            initL = true;
//                            isLeftTurn = false;
//                            isReleased = false;
//                            rotation = 0;
//                            deltaTime = 0;
//                            try {
//                                Thread.sleep(1000);  // milliseconds
//                                Thread.currentThread().interrupt();
//                                return;
//                            } catch (InterruptedException ex) {
//                            }
//                        }
//                    } else if (isReleased & !isLeftTurn) {
//                        System.out.println("shoot right");
//                        arrows.get(roundCtn).move(g, deltaTime);
//                        arrows.get(roundCtn).accelerate(-ax, ay, deltaTime);
//                        if (arrows.get(roundCtn).getArrowCurrentLoc().x < playerL.x + 50) {
//                            arrows.get(roundCtn).move(g, 0);
//                            arrows.get(roundCtn).setIsStop(true);
//                        }
//                        System.out.println("Arrow 1 : " + Arrays.toString(arrows.get(roundCtn).getPolygon().xpoints));
//                        arrows.get(roundCtn - 1).move(g, 0);
//
//                        isTouch = arrows.get(roundCtn).getIsStop();
//                        if (isTouch) {
//                            initR = true;
//                            isLeftTurn = true;
//                            isReleased = false;
//                            rotation = 0;
//                            deltaTime = 0;
//                            try {
//                                Thread.sleep(1000);  // milliseconds
//                                Thread.currentThread().interrupt();
//                                return;
//                            } catch (InterruptedException ex) {
//                            }
//                        }
//                    }
//                    try {
//                        Thread.sleep(1000/10);  // milliseconds
//                    } catch (InterruptedException ex) {
//                    }
//                    updateCamera();
//                    repaint();
//                }
//            }
//        };
//    }
    //    public void shootArrow() {
    //        if (isReleased & isLeftTurn) {
    //            System.out.println("shoot left");
    //            arrows.get(roundCtn - 1).move(g, deltaTime);
    //            arrows.get(roundCtn - 1).accelerate(ax, ay, deltaTime);
    //            if (arrows.get(roundCtn - 1).getArrowCurrentLoc().x > playerR.x) {
    //                arrows.get(roundCtn - 1).move(g, 0);
    //                arrows.get(roundCtn - 1).setIsStop(true);
    //            }
    //            System.out.println("Arrow 0 : " + Arrays.toString(arrows.get(roundCtn - 1).getPolygon().xpoints));
    //            arrows.get(roundCtn).move(g, 0);
    //
    //        } else if (isReleased & !isLeftTurn) {
    //            System.out.println("shoot right");
    //            arrows.get(roundCtn).move(g, deltaTime);
    //            arrows.get(roundCtn).accelerate(-ax, ay, deltaTime);
    //            if (arrows.get(roundCtn).getArrowCurrentLoc().x < playerL.x) {
    //                arrows.get(roundCtn).move(g, 0);
    //                arrows.get(roundCtn).setIsStop(true);
    //            }
    //            System.out.println("Arrow 1 : " + Arrays.toString(arrows.get(roundCtn).getPolygon().xpoints));
    //            arrows.get(roundCtn - 1).move(g, 0);
    //
    //        }
    //    }
    //    public void shootArrow() {
    //        if (isReleased & isLeftTurn) {
    //            System.out.println("shoot left");
    //            arrows.get(roundCtn - 1).move(g, 50);
    //            if (arrows.get(roundCtn - 1).getArrowCurrentLoc().x > 650) {
    //                arrows.get(roundCtn - 1).move(g, 0);
    //                arrows.get(roundCtn - 1).setIsStop(true);
    //            }
    //            System.out.println("Arrow 0 : " + Arrays.toString(arrows.get(roundCtn - 1).getPolygon().xpoints));
    //            arrows.get(roundCtn).move(g, 0);
    //
    //        } else if (isReleased & !isLeftTurn) {
    //            System.out.println("shoot right");
    //            arrows.get(roundCtn).move(g, -50);
    //            if (arrows.get(roundCtn).getArrowCurrentLoc().x < 100) {
    //                arrows.get(roundCtn).move(g, 0);
    //                arrows.get(roundCtn).setIsStop(true);
    //            }
    //            System.out.println("Arrow 1 : " + Arrays.toString(arrows.get(roundCtn).getPolygon().xpoints));
    //            arrows.get(roundCtn - 1).move(g, 0);
    //
    //        }
    //    }
}
