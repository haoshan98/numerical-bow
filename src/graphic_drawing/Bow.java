/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphic_drawing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author ultra
 */
public class Bow extends Container implements MouseListener, MouseMotionListener{
    private int[] bow_position;
    private int bow_size;
    private float rotation_angle=0;
    float x_axis_bow_string;
    private float string_power=0;
    private boolean left=true;
    int curX;int curY;
    static Label stat;
    
    public Bow(int[] bow_position,int bow_size) {
        this.bow_position=bow_position;
        this.bow_size=bow_size;
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    public void rotate_bow(int new_angle){
        this.rotation_angle=new_angle;
    }
    public void pull_string(float string_power){
        this.string_power=string_power;
        
    }
    @Override
    public void paint(Graphics g){
        //super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        AffineTransform old = g2d.getTransform();

        
        g.setColor(Color.red);
        int arrow_size=this.bow_size;
        int x_axis=this.bow_position[0];
        int y_axis=this.bow_position[1];
        g.drawRect(x_axis, y_axis, 5, 5);
        
        
        int x_arrow_top_down_position = x_axis;
        int x_arrow_middle_position_left = x_axis+40*arrow_size;
        int x_arrow_middle_position_right = 20*arrow_size+x_arrow_middle_position_left;
        int y_middle_arrow_postion = y_axis;
        int y_top_arrow_position = y_middle_arrow_postion+(-100)*arrow_size;
        int y_down_arrow_position = y_middle_arrow_postion+100*arrow_size;
        
        //for polygon
        int[] x_top_tri={x_arrow_middle_position_left,x_arrow_middle_position_right,x_arrow_top_down_position};
        int[] y_top_tri={y_middle_arrow_postion,y_middle_arrow_postion,y_top_arrow_position};
        int[] x_bottom_tri={x_arrow_middle_position_left,x_arrow_middle_position_right,x_arrow_top_down_position};
        int[] y_bottom_tri={y_middle_arrow_postion,y_middle_arrow_postion,y_down_arrow_position};
        
        
        
        //for count string power and bow string
        
        if (curX<x_axis){
            //this.string_power=curX/((x_arrow_middle_position_right-x_axis)*2);
            x_axis_bow_string=curX;
        }else{
            this.string_power=1;
        }
        System.out.print(this.string_power);
        //float x_axis_bow_string =x_axis-((x_arrow_middle_position_right-x_axis)*2*this.string_power);
        if (this.string_power==0){
            x_axis_bow_string=x_axis;
        }

        int[] x_bow_string={x_arrow_top_down_position,(int)curX,x_arrow_top_down_position};
        //int[] y_bow_string={y_top_arrow_position,y_middle_arrow_postion,y_down_arrow_position};
        int[] y_bow_string={y_top_arrow_position,(int)Math.round(curY),y_down_arrow_position};
        
        
        
        
        
        
        
        
        this.rotate_bow(getAngle(x_axis, y_axis));
        
        g2d.rotate(Math.toRadians(-this.rotation_angle),x_axis,y_axis);
        g.drawPolyline(x_bow_string, y_bow_string, 3);
        //draw shape/image (will be rotated)      
        g.drawPolygon( x_top_tri, y_top_tri, 3);
        g.drawPolygon( x_bottom_tri, y_bottom_tri, 3);
        
        //System.out.print("ok");
        g2d.setTransform(old);
        //things you draw after here will not be rotated

    }
    public static void main(String args[]) {


        
        
        JFrame jf = new JFrame("Mouse Dragger");
        Container cPane = jf.getContentPane();
        Bow t = new Bow(new int[]{300,300},1);

        //t.rotate_bow(45);
        //t.pull_string((float) 1);        
        
        cPane.setLayout(new BorderLayout());
        cPane.add(BorderLayout.NORTH, new Label(""));
        cPane.add(BorderLayout.CENTER, t);
        cPane.add(BorderLayout.SOUTH, stat = new Label());
        stat.setSize(jf.getSize().width, stat.getSize().height);
        stat.setSize(jf.getSize().width, stat.getSize().height);
        jf.setSize(900, 900);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.pack();
        jf.setVisible(true);     
    }

    
double getDistance(int x1, int y1, int x2, int y2){
        //System.out.println("Distance x: " + (x2-x1) );
        //System.out.println("Distance y: " + (y2-y1));
        double distance = Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1),2));
        //System.out.println("Lines differences " + distance);
        return distance;
    }    
int getAngle(int x1, int y1){
    
    double distance1=getDistance(curX, curY, x1,  y1);
    double distance2=getDistance(curX, y1, x1,  y1);
    int angle = (int)Math.round(Math.toDegrees(Math.acos(distance2/distance1)));
    //System.out.println("angle" + angle);
    //System.out.println(distance2/distance1);
    return angle;
}
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();
//        showStatus("Mouse move to " + e.getPoint());
//        System.out.println("Move to " + e.getPoint());
        curX = p.x;
        curY = p.y;
        System.out.println("position of curX " + curX);
        System.out.println("position of curY " + curY);

        repaint();

    }
    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }

}
