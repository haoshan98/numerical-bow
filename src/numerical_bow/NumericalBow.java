package numerical_bow;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JWindow;

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
    private Dimension world_size;
    private Dimension viewport_size;
    private int offsetMaxX;
    private int offsetMaxY;
    private int offsetMinX;
    private int offsetMinY;
    private int camX = 0;
    private int camY = 0;
    private final Point playerL;
    private final Point playerR;
    private ArrayList<Arrow> landedArrow = new ArrayList<>();
    private Arrow arrowL;
    private Arrow arrowR;
    private boolean isReleasedL = false;
    private boolean isReleasedR = false;
    private boolean initL = true;
    private boolean initR = true;
    private boolean toMoveL = false;
    private boolean toMoveR = false;
    private boolean isDragL = false;
    private boolean isDragR = false;
    private boolean isLeft = true;
    private boolean isDropL = false;
    private boolean END = false;
    private boolean isLeftTurn = false;
    private boolean isRightTurn = false;
    private int angleL = 0;
    private int angleR = 0;
    private int rotation = 0;
    private int width = 0;
    private int height = 0;

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
        setPreferredSize(new Dimension(10000, viewport_size.height));
        this.world_size = getPreferredSize();

        //camera view
        this.offsetMaxX = world_size.width - viewport_size.width;
        this.offsetMaxY = world_size.height - viewport_size.height;
        this.offsetMinX = 0;
        this.offsetMinY = 0;

        //Players init
        this.playerL = new Point(100, 100);
        this.playerR = new Point(700, 100);

        this.width = getSize().width;
        this.height = getSize().height;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                isReleasedL = true;
                Thread shoot = new Thread() {
                    public void run() {
                        while (!isDropL) {
                            updateCamera();
                            repaint();
                            isDropL = arrowL.getIsStop();
                            try {
                                Thread.sleep(1000 / 10);  // milliseconds
                            } catch (InterruptedException ex) {
                            }

                        }
                    }
                };
                shoot.start();
            }

            @Override
            public void mousePressed(MouseEvent e) {

                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        System.out.println("button 1");
                        rotation-=5;
//                        if (rotation < 0) {
//                            rotation = 359;
//                        }
                        
                        initL = false;
                        isReleasedL = true;
                        toMoveL = true;
                        toMoveR = false;
                        isLeft = true;
                        
                        repaint();
                        break;
                    case MouseEvent.BUTTON3:
                        System.out.println("button 3");
                        rotation++;
//                        if (rotation > 360) {
//                            rotation = 0;
//                        }
                        repaint();
                        break;

                }
//                if (e.getX() < 450) {
//                    System.out.println("\nMouse press on left");
//                    initL = false;
//                    isReleasedL = true;
//                    toMoveL = true;
//                    toMoveR = false;
//                    isLeft = true;
//
//                } else {
//                    System.out.println("\nMouse press on right");
//                    initR = false;
//                    isReleasedR = true;
//                    toMoveR = true;
//                    toMoveL = false;
//                    isLeft = false;
//                }

//                updateCamera();
//
//                repaint();
            }

        }
        );

        addMouseMotionListener(
                new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
//                 Thread pullBow = new Thread() {
//                        public void run() {
//                            while (!isReleasedL) {
//                                arrowL.pull(g, e.getX(), e.getY());
//                                System.out.println(arrowL.getArrowCurrentLoc().x);
//                                updateCamera();
//                                repaint();
//                                try {
//                                    Thread.sleep(1000 / 10);  // milliseconds
//                                } catch (InterruptedException ex) {
//                                }
//
//                            }
//                        }
//                    };
//                    pullBow.start();

//                if (e.getX() < 450) {
//                    System.out.println("\nMouse drag on left");
//                    initL = false;
//                    isReleasedL = true;
//                    toMoveL = true;
//                    toMoveR = false;
//                    isDragL = true;
//                    isLeft = true;
////                    adjustArrow(e.getX(), e.getY(), true);
//                    arrowL.rotate(g, e.getX(), e.getY(), new Point(100, 100));
//
//                } else {
//                    System.out.println("\nMouse drag on right");
//                    initR = false;
//                    isReleasedR = true;
//                    toMoveR = true;
//                    toMoveL = false;
//                    isDragR = true;
//                    isLeft = false;
//
//                }
//                System.out.println(e.getX() + ", " + e.getY());
//                updateCamera();
//                repaint();
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

        this.camX = arrowL.getPolygon().xpoints[6] - this.viewport_size.width / 2;
        this.camY = arrowL.getPolygon().ypoints[6] - this.viewport_size.height / 2;

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

        shootArrow(isLeft);
//        adjustArrow(isLeft);
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
//        g.fillOval(p.x - width - 10, p.y + width, height / 2 - width, height / width + 2);
//        g.fillOval(p.x + width, p.y + width, height / 2 - width, height / width + 2);
        //bow
        //TODO: Movable hand & bow, link with arrow
        //TODO: Arrow dragging (control both power & angle)
        //arrow
        createArrow(isLeft);

    }

    public void createArrow(boolean isLeft) {
        if (initL & !isReleasedL & isLeft) {
            initL = false;
            arrowL = new Arrow(g, new Point(playerL.x + 20, playerL.y), isLeft);

            System.out.println("Arrow 0 created");

        } else if (initR & !isReleasedR & !isLeft) {
            initR = false;
            arrowR = new Arrow(g, new Point(playerR.x - 50, playerR.y), isLeft);

            System.out.println("Arrow 1 created");

        }
    }

    public void shootArrow(boolean isLeft) {
        if (isReleasedL & toMoveL & isLeft) {
            arrowL.move(g, 50);
//            System.out.println("x : " + arrowL.getArrowCurrentLoc().x);
            if (arrowL.getArrowCurrentLoc().x > 650) {
                System.out.println("should stop");
                arrowL.move(g, 0);
                arrowL.setIsStop(true);
            }

            System.out.println("Arrow 0 : " + Arrays.toString(arrowL.getPolygon().xpoints));
            arrowR.move(g, 0);

        } else if (isReleasedR & toMoveR & !isLeft) {
            arrowR.move(g, 50);
            System.out.println("Arrow 1 : " + Arrays.toString(arrowR.getPolygon().xpoints));
            arrowL.move(g, 0);

        }
    }
    
    public void rotate(){
        arrowL.rotate(g, rotation, new Point(100, 100));
        arrowR.rotate(g, 0, new Point(100, 100));

    }

//    public void adjustArrow(int curX, int curY, boolean isLeft) {
//
//        if (isDragL & toMoveL & isLeft) {
//            
//            arrowL.rotate(g, curX, curY, new Point(100, 100));
////            arrowR.rotate(g, 0, new Point(700, 100));
//        } else if (isDragR & toMoveR & !isLeft) {
////            arrowR.rotate(g, 10, new Point(700, 100));
////            arrowL.rotate(g, 0, new Point(100, 100));
//        }
//    }

    public void createLand(Graphics g) {

        g.drawLine(0, 300, 10000, 300);
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
