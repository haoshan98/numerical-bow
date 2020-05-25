package numerical_bow;

import java.awt.*;
import java.awt.geom.*;

public class Arrow {

    private Point arrowLoc;
    private int arrowL = 35;
    private int arrowW = 2;
    private boolean isToRight;

    private double angle;
    private double power;
    private double accelaration;
    private double gravity = 9.81;
    private Polygon polygon;

    public Arrow(Graphics g, Point loc, boolean isToRight) {
        this.isToRight = isToRight;
        this.arrowLoc = loc;

        int[] xPoints;
        int[] yPoints;
        //set arrow facing side
        if (isToRight) {
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

        this.arrowLoc = loc;

        drawArrow(g);
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public Point getArrowLoc() {
        return arrowLoc;
    }

    public void drawArrow(Graphics g) {
        g.drawPolygon(polygon);
    }

    //TODO: arrow movement (acceleration, decceleration), rotation
    public void move(Graphics g, int velocity) {
        if (isToRight) {
            for (int i = 0; i < polygon.npoints; i++) {
                polygon.xpoints[i] += velocity;
            }
        } else {
            for (int i = 0; i < polygon.npoints; i++) {
                polygon.xpoints[i] -= velocity;
            }
        }
        drawArrow(g);

    }

    public void rotate(Graphics g, int angle) {

        AffineTransform rotate = AffineTransform.getRotateInstance(
                Math.toRadians(angle), 100, 100);
        
        Polygon translatedPolygon = new Polygon();

        for (int i = 0; i < polygon.npoints; i++) {
            Point p = new Point(polygon.xpoints[i], polygon.ypoints[i]);
            rotate.transform(p, p);
            translatedPolygon.addPoint(p.x, p.y);
        }
        setPolygon(translatedPolygon);
        drawArrow(g);

    }

}
