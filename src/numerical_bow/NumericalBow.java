package numerical_bow;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.naming.CompositeName;
import javax.swing.*;
import javax.swing.JWindow;
import java.awt.geom.Line2D;
import javax.swing.WindowConstants;
import javax.swing.JFrame;
import java.lang.Math.*;

public class NumericalBow extends JPanel{

    private Image img;
    private Point lastPoint;
    private Dimension world_size;
    private Dimension viewport_size;
    private int offsetMaxX;
    private int offsetMaxY;
    private int offsetMinX;
    private int offsetMinY;
    private int camX = 0;
    private int camY = 0;
    private final Point p1;  //player 1
    private final Point p2;  //player 2
    private Arrow[] arrow = new Arrow[2];
    private Point[] arrowLoc = new Point[2];
    private boolean[] isReleased = {false, false};
    private boolean[] init = {true, true};
    private boolean[] toMove = {false, false};
    private boolean isDrag = false;
    private boolean once = true;
    private int sX, sY;
    public int curX, curY;

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
        this.viewport_size = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(10000, viewport_size.height));
        this.world_size = getPreferredSize();

        //camera view
        this.offsetMaxX = world_size.width - viewport_size.width;
        this.offsetMaxY = world_size.height - viewport_size.height;
        this.offsetMinX = 0;
        this.offsetMinY = 0;

        //Players init
        this.p1 = new Point(100, 100);
        this.p2 = new Point(700, 100);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getX() < 450) {
                    System.out.println("\nMouse press on left");
                    init[0] = false;
                    isReleased[0] = true;
                    toMove[0] = true;
                    toMove[1] = false;
                } else {
                    System.out.println("\nMouse press on right");
                    init[1] = false;
                    isReleased[1] = true;
                    toMove[1] = true;
                    toMove[0] = false;
                }
                updateCamera();
                repaint();
            }

        });


        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                init[0] = false;
                init[1] = false;
                isDrag = true;
                System.out.println(e.getX() + ", "+ e.getY());

                updateCamera();
                repaint();
            }


            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                curX = p.x;
                curY = p.y;
                System.out.println("position of curX " + curX);
                System.out.println("position of curY " + curY);

                repaint();
            }

        });
    };


    public int[] drawLinesLeft(Graphics2D g2d, Stroke stroke, Point p1, Point p2){
        g2d.setStroke(stroke);

        int screen_split = 450;

        int newX_p1 = 0, newY_p1 = 0;
        int newX_p2 = 0, newY_p2 = 0;

        int minX_p1 = p1.x-20, maxX_p1 = p1.x+20 ,maxY_p1 = p1.y+30, minY_p1 = p1.y-20;
        int minX_p2 = p2.x, maxX_p2 = p2.x+40 ,maxY_p2 = p2.y+30, minY_p2 = p2.y-20;


        if (curX<450) {

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
        }

        else if (curX>=450){
            newX_p1 = maxX_p1;
            newY_p1 = maxY_p1;
            if (curX >= maxX_p2) {
                if (curY >= maxY_p2) {
                    System.out.println("c1.1");
                    newX_p2 = maxX_p2;
                    newY_p2 = maxY_p2;
                }
                else if (curY <= minY_p2) {
                    System.out.println("c1.2");
                    newX_p2 = maxX_p2;
                    newY_p2 = minY_p2;
                }
                else {
                    System.out.println("c1");
                    newX_p2 = maxX_p2;
                    newY_p2 = curY;
                }
            }
            else if (curY >= maxY_p2) {
                System.out.println("c2");
                if (curX <= minX_p2) {
                    newX_p2 = minX_p2;
                    newY_p2 = maxY_p2;
                }
                else {
                    newY_p2 = maxY_p2;
                    newX_p2 = curX;
                }
            }
            else if (curY <= minY_p2) { //range = 20
                System.out.println("c3");
                if (curX <= minX_p2) {
                    newX_p2 = minX_p2;
                    newY_p2 = maxY_p2;
                }
                else {
                    newY_p2 = minY_p2;
                    newX_p2 = curX;
                }
            }
            else if ((curX < minX_p2 && curY > minY_p2 && curY < maxY_p2)) {
                System.out.println("c4");
                newY_p2 = curY;
                newX_p2 = minX_p2;
            }
            else if ((curX > minX_p2 && curY > minY_p2 && curY < maxY_p2)) {
                System.out.println("c4 2 ");
                newY_p2 = curY;
                newX_p2 = curX;
            }
            else{
                newX_p2 = curX;
                newY_p2 = curY;
            }


//            second player
        }

//       Player 1
        g2d.setColor(Color.black);
        g2d.drawLine(newX_p1,newY_p1,p1.x, p1.y);
        g2d.setColor(Color.black);
        g2d.drawLine(newX_p1,newY_p1,p1.x,p1.y+10);
//      Player 2
        g2d.setColor(Color.black);
        g2d.drawLine(newX_p2,newY_p2,p2.x + 20, p2.y);
        g2d.setColor(Color.black);
        g2d.drawLine(newX_p2,newY_p2,p2.x + 20,p2.y+10);

        int[] points = new int[4];

        points[0] = newX_p1;
        points[1] = newY_p1;

        points[2] = newX_p2;
        points[3] = newY_p2;

        System.out.println("points : " + p1.getX() + p1.getY());
        System.out.println("points : " + p2.getX() + p1.getY());

        return points;
    }

    public void drawLinesRight(int[] points, Graphics2D g2d, Stroke stroke){
        int oppositeY_p1 = points[1], oppositeX_p1 = points[0];
        int oppositeY_p2 = points[3], oppositeX_p2 = points[2];
        int distance_p1 = oppositeY_p1 - p1.y;
        int distance_p2 = oppositeY_p2 - p2.y;
        int newX_p1 = p1.x + 20;
        int newY_p1 = 0;
        int newX_p2 = p2.x;
        int newY_p2 = 0;

        g2d.getStroke();

//        player 1
        if (curX<450) {
            if (distance_p1 >= 0 && oppositeX_p1 <= 100) {
                newX_p1 += 20;
                newY_p1 = p1.y - distance_p1;
            } else if (distance_p1 < 0 && oppositeX_p1 <= 100) {
                newX_p1 += 30;
                newY_p1 = p1.y - distance_p1;
            } else if (oppositeX_p1 > 100) {
                newX_p1 = p1.x+10;
                newY_p1 = p1.y;
            }
            g2d.setColor(Color.black);
            g2d.drawLine(p1.x+20, p1.y,newX_p1,newY_p1);
        }

//        player 2
        else if(curX>=450) {

            if (distance_p2 >= 0 && oppositeX_p2 <= p2.x + 40) {
                newX_p2 -= 20;
                newY_p2 = p2.y - distance_p2;
            }
            else if (distance_p2 < 0 && oppositeX_p2 > p2.x+10) {
                newX_p2 -= 30;
                newY_p2 = p2.y - distance_p2 + 10;
            }
            if (curX <= p2.x ){
                newX_p2 = p2.x;
                newY_p2 = p2.y;
            }

            g2d.setColor(Color.black);
            g2d.drawLine(p2.x, p2.y,newX_p2,newY_p2);

        }
    }

    private void updateCamera() {

//        camX = playerX - VIEWPORT_SIZE_X / 2
//        camY = playerY - VIEWPORT_SIZE_Y / 2
        this.camX = arrow[0].getPolygon().xpoints[6] - this.viewport_size.width / 2;
        this.camY = arrow[0].getPolygon().ypoints[6] - this.viewport_size.height / 2;

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
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.translate(-camX, -camY);

        draw(g);
    }

    public void draw(Graphics g) {

        createPlayer(g, p1, true);
        createPlayer(g, p2, false);

        createLand(g);

        lifeBar(g);

    }

    public void createPlayer(Graphics g, Point p, boolean isFaceRight) {
        int height = 100;
        int width = 20;

        Graphics2D g2d = (Graphics2D) g;
        Stroke stroke = new BasicStroke(2f);

        //body
//        g.fillOval(p.x, p.y, width, height / 2);
        g.fillRect(p.x, p.y, width, height / 3);
        //head
        g.fillOval(p.x, p.y - width, width, width);


        //leg
//        g.fillOval(p.x, p.y + height / 2, height / width * 2, height / 2 - width);
//        g.fillOval(p.x + width / 2, p.y + height / 2, height / width * 2, height / 2 - width);

        //hand
//        g.fillOval(p.x - width - 10, p.y + width, height / 2 - width, height / width + 2);
//        g.fillOval(p.x + width, p.y + width, height / 2 - width, height / width + 2);

        int[] pointsLeft = drawLinesLeft(g2d, stroke, p1, p2);
        drawLinesRight(pointsLeft, g2d, stroke);


        //bow
        //TODO: Movable hand & bow, link with arrow
        //TODO: Arrow dragging (control both power & angle)
        //arrow
        if (init[0] & !isReleased[0] & isFaceRight) {
            arrow[0] = new Arrow(g, new Point(p.x + 20, p.y), isFaceRight);

            System.out.println("Arrow 0 created");
//            System.out.println("Arrow 0 initial : " + Arrays.toString(arrow[0].xPoints));

        } else if (init[1] & !isReleased[1] & !isFaceRight) {
            arrow[1] = new Arrow(g, new Point(p.x - 50, p.y), isFaceRight);

            System.out.println("Arrow 1 created");
//            System.out.println("Arrow 1 initial : " + Arrays.toString(arrow[1].xPoints));

        }

        if (isReleased[0] & isFaceRight) {
            if (toMove[0]) {
                arrow[0].move(g, 50);
                System.out.println("Arrow 0 : " + Arrays.toString(arrow[0].getPolygon().xpoints));
            } else {
                arrow[0].move(g, 0);
            }
        } else if (isReleased[1] & !isFaceRight) {
            if (toMove[1]) {
                arrow[1].move(g, 50);
                System.out.println("Arrow 1 : " + Arrays.toString(arrow[1].getPolygon().xpoints));

            } else {
                arrow[1].move(g, 0);
            }

        }
        
//        if (isDrag & once){
//            once = false;
//            arrow[0].rotate(g, 100);
//            System.out.println("=======================");
//        }

    }

    public void createLand(Graphics g) {

        g.drawLine(0, 300, 900, 300);
    }

    //TODO: lifeBar update
    public void lifeBar(Graphics g) {
        //lifebar

        //power
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("Power : 10", 100, 50);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("Power : 10", 700, 50);

        //angle
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("Angle : 0", 100, 60);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("Angle : 0", 700, 60);
    }

    //TODO: scene follow arrow position
    //TODO: scene scrolling (keyboard)
    public void sceneScroll() {

    }

    //TODO: maintain dropped arrow, change color
    //
}
