/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package numerical_bow;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
 
public class Background {
    private BufferedImage image;
 
    private int x;
    private int y;
 
    public Background() {
        this(0,0);
    }
 
    public Background(int x, int y) {
        this.x = x;
        this.y = y;
 
        // Try to open the image file background.png
        try {
            image = ImageIO.read(new File("src\\numerical_bow\\background.jpg"));
        }
        catch (Exception e) { System.out.println(e); }
 
    }
 
    /**
     * Method that draws the image onto the Graphics object passed
     * @param window
     */
    public void drawBackground(Graphics g, int W, int H) {
 
        // Draw the image onto the Graphics reference
        g.drawImage(image, getX(), getY(), W, H, null);
 
        // Move the x position left for next time
        this.x -= 5;
 
        // Check to see if the image has gone off stage left
        if (this.x <= -1 * image.getWidth()) {
 
            // If it has, line it back up so that its left edge is
            // lined up to the right side of the other background image
            this.x = this.x + image.getWidth() * 2;
        }
 
    }
 
    public void setX(int x) {
        this.x = x;
    }
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public int getImageWidth() {
        return image.getWidth();
    }
 
    public String toString() {
        return "Background: x=&amp;amp;amp;quot;" + getX() + ", y=" + getY() + ", height=" + image.getHeight() + ", width=" + image.getWidth();
    }
 
}