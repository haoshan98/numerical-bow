package numerical_bow;

import java.awt.*;
import java.util.Arrays;

public class Arrow {

    private Point arrowInitLoc;
    private int arrowL = 35;
    private int arrowW = 2;
    private boolean isLeft;

    private double angle;
    private double power = 0;
    private double xVelocity = 0;
    private double yVelocity = 0;
    private double gravity = 9.81;
    private Polygon polygon;

    private boolean isStop = false;

    public Arrow(Graphics g, Point loc, boolean isLeft) {
        this.isLeft = isLeft;
        this.arrowInitLoc = loc;

        int[] xPoints;
        int[] yPoints;
        //set arrow facing side
        if (isLeft) {
            xPoints = new int[]{2, 0, 1, 3, 8, 8, 10, 8, 8, 3, 1, 0};
            yPoints = new int[]{6, 8, 9, 7, 7, 8, 6, 4, 5, 5, 3, 4};

        } else {
            xPoints = new int[]{0, 2, 2, 7, 9, 10, 8, 10, 9, 7, 2, 2};
            yPoints = new int[]{6, 8, 7, 7, 9, 8, 6, 4, 3, 5, 5, 4};

        }

        // set arrow size
        for (int i = 0; i < xPoints.length; i++) {
            if (xPoints[i] > 6) {
                xPoints[i] += arrowL;
            }
            if (yPoints[i] < 6) {
                yPoints[i] -= arrowW / 2;
            } else if (yPoints[i] > 6) {
                yPoints[i] += arrowW / 2;
            }
        }

        // set arrow to its location
        for (int i = 0; i < xPoints.length; i++) {
            xPoints[i] += loc.x;
        }
        for (int i = 0; i < yPoints.length; i++) {
            yPoints[i] += loc.y;
        }

        this.polygon = new Polygon(xPoints, yPoints, xPoints.length);

        this.arrowInitLoc = loc;

        drawArrow(g);
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public Point getArrowInitLoc() {
        return arrowInitLoc;
    }

    public Point getArrowCurrentLoc() {
        return new Point(getPolygon().xpoints[6], getPolygon().ypoints[6]);
    }

    public void drawArrow(Graphics g) {

        g.fillPolygon(polygon);
        System.out.println(polygon.xpoints[6]);
//        g.fillOval(polygon.xpoints[0], polygon.ypoints[0], 10, 10);
    }

    public void pull(Graphics g, int x, int y) {
        for (int i = 0; i < polygon.npoints; i++) {
            polygon.xpoints[i] += x / 100;
//          polygon.ypoints[i] -= y/100;
        }

        drawArrow(g);
    }


    public void accelerate(double xAcc, double yAcc, double difTime) {
        xVelocity += xAcc * difTime;
        yVelocity += yAcc * difTime;
        System.out.println("x v : " + xVelocity + "y v : " + yVelocity);
    }
    
    public void move(Graphics g, double diffTime) {
        for (int i = 0; i < polygon.npoints; i++) {
            polygon.xpoints[i] += xVelocity * diffTime;
            polygon.ypoints[i] += yVelocity * diffTime;
        }
        drawArrow(g);

    }


    public boolean getIsStop() {
        return isStop;
    }

    public void setIsStop(boolean isStop) {
        this.isStop = isStop;
    }

    public void rotate(Graphics g, int angle, Point p) {

        System.out.println("Angle : " + angle);
        float radian = (float) angle * 3.14f / 180;
        
        int[] xs = polygon.xpoints;
        int[] ys = polygon.ypoints;
        int xp = p.x;
        int yp = p.y;
        float t, v;
        for (int i = 0; i < polygon.npoints; i++) {
            t = xs[i] - xp;
            v = ys[i] - yp;
            xs[i] = (int) (xp + t * Math.cos(radian) - v * Math.sin(radian));
            ys[i] = (int) (yp + v * Math.cos(radian) + t * Math.sin(radian));
        }
        polygon.xpoints = xs;
        polygon.ypoints = ys;
//        System.out.println(Arrays.toString(polygon.xpoints));
        drawArrow(g);
    }

    public void projectile(Graphics g) {

    }
    
    
    
    //TODO: arrow movement (acceleration, decceleration), rotation
    public void move(Graphics g, int velocity) {
        for (int i = 0; i < polygon.npoints; i++) {
            polygon.xpoints[i] += velocity;
//            polygon.ypoints[i] += a;
        }
        drawArrow(g);

    }

//    public static double getDistance(int x1, int y1, int x2, int y2) {
//        
//        double distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
//        return distance;
//    }
//
//    public static int getAngle(int curX, int curY, int x1, int y1) {
//
//        double distance1 = getDistance(curX, curY, x1, y1);
//        double distance2 = getDistance(curX, y1, x1, y1);
//        int angle = (int) Math.round(Math.toDegrees(Math.acos(distance2 / distance1)));
//        System.out.println("angle : " + angle);
//        return angle;
//    }
//    public void rotate(Graphics g, int angle) {
//
//        AffineTransform rotate = AffineTransform.getRotateInstance(
//                Math.toRadians(angle), 100, 100);
//        
//        Polygon translatedPolygon = new Polygon();
//
//        for (int i = 0; i < polygon.npoints; i++) {
//            Point p = new Point(polygon.xpoints[i], polygon.ypoints[i]);
//            rotate.transform(p, p);
//            translatedPolygon.addPoint(p.x, p.y);
//        }
//        setPolygon(translatedPolygon);
//        drawArrow(g);
//
//    }
}
