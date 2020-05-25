package archieved;

import numerical_bow.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

// http://greenteapress.com/thinkjava6/html/thinkjava6017.html
// https://www.youtube.com/watch?v=pDafZdIIeNE
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

    private Image img;
    private Point lastPoint;
    private final Point p1;  //player 1
    private final Point p2;  //player 2
    private Arrow[] arrow = new Arrow[2];
    private Point[] arrowLoc = new Point[2];
    private boolean[] isReleased = {false, false};
    private boolean[] init = {true, true};
    private boolean[] toMove = {false, false};

    public NumericalBow() {

        //Layout
        setBackground(new Color(0xFFEBCD));

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
                repaint();
            }
        });

//        addMouseMotionListener(new MouseMotionAdapter() {
//            public void mouseDragged(MouseEvent e) {
//                Graphics g = getGraphics();
//                g.drawLine(lastPoint.x, lastPoint.y, e.getX(), e.getY());
//                g.dispose();
//            }
//        });
//
//        addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                switch (e.getKeyCode()) {
//                    case KeyEvent.VK_LEFT:
//                        if (!END) {
//                            moveLeft();
//                        }
//                        break;
//                    case KeyEvent.VK_RIGHT:
//                        if (!END) {
//                            moveRight();
//                        }
//                        break;
//                    case KeyEvent.VK_U:
//                        if (!END) {
//                            undo();
//                        }
//                        break;
//                }
//                repaint();
//            }
//        });
    }

    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

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

        //body
        g.fillOval(p.x, p.y, width, height / 2);
        //head
        g.fillOval(p.x, p.y - width, width, width);
        //leg
        g.fillOval(p.x, p.y + height / 2, height / width * 2, height / 2 - width);
        g.fillOval(p.x + width / 2, p.y + height / 2, height / width * 2, height / 2 - width);

        //hand
        g.fillOval(p.x - width - 10, p.y + width, height / 2 - width, height / width + 2);
        g.fillOval(p.x + width, p.y + width, height / 2 - width, height / width + 2);

        //bow
        //TODo: Movable hand & bow, link with arrow
        
        //TODo: Arrow dragging (control both power & angle)
        
        
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
                arrow[0].move(g, 10);
                System.out.println("Arrow 0 : " + Arrays.toString(arrow[0].xPoints));
            } else {
                arrow[0].move(g, 0);
            }
        } else if (isReleased[1] & !isFaceRight) {
            if (toMove[1]) {
                arrow[1].move(g, 10);
                System.out.println("Arrow 1 : " + Arrays.toString(arrow[1].xPoints));

            } else {
                arrow[1].move(g, 0);
            }

        }

    }

    public void createLand(Graphics g) {

        g.drawLine(0, 300, 900, 300);
    }

    //TODo: lifeBar update
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

    //TODo: scene follow arrow position
    //TODo: scene scrolling (keyboard)
    public void sceneScroll() {

    }

    //TODo: maintain dropped arrow, change color
    //
}
