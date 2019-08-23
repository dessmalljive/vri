/*
 * vriGreyDisp.java
 *
 * Used in the Virtual Radio Interferometer to transform between images
 * and the U-V plane. Based on FFTTool by Mark Wieringa, 26/Dec/1996.
 *
 * 16/Jan/1997  Derek McKay
 *
 */

package nl.jive.vri;

import java.lang.Math;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.net.*;
import javax.swing.*;
import edu.emory.mathcs.jtransforms.fft.*;
import java.util.Arrays;

//####################################################################//


class vriGreyDisp extends vriDisplay {
    public enum PlotTypes {PHASE, REAL, IMAG, AMPL, COLOUR};
    
    public static class EmptyImageException extends Exception {};


    JApplet applet;
    //  boolean replot = false;
    URL imgURL;
    Image img;
    static int imsize;     // Size of the "squared" image
    String message = null; // Message to print on the Display
    PlotTypes type = PlotTypes.AMPL;    // Used to select real/imag/amp/phase display
    double fullScale = 73000.0;
    String unit = "lobster";
    boolean hasAxes = false;
    String[] axesLabels = new String[] {"x", "y"};

    public vriGreyDisp(JApplet app) {
        super();
        applet = app;
    }

    void setHasAxes(boolean b) {
        hasAxes = b;
    }
	 
    boolean getHasAxes() {
        return hasAxes;
    }

    void setUnit(String u){
        unit = u;
    }
    String getUnit() {
        return unit;
    }
    void setFullScale(double s){
        fullScale = s;
    }
    double getFullScale(){
        return fullScale;
    }

    void paintScale(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Rectangle r = getBounds();
        int width = img.getWidth(this); 
        double displayScale = getDisplayScale();
        double l = roundPower(fullScale / displayScale * (width - 20.0) / width);
        int m = (int) Math.round(l * width * displayScale /  fullScale);
        String str = roundUnit(l, unit);
        paintRealScale(g2, r, str, m);
    }

    void paintAxes(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Color oldColor = g2.getColor();
        g2.setColor(Color.orange);
        int imh = img.getHeight(this);
        int imw = img.getWidth(this);
        int offset = 10;
        g2.draw(new Line2D.Float((float)offset, imh/2.0f, 
                                 (float)imw-offset, imh/2.0f));
        g2.draw(new Line2D.Float(imw/2.0f, (float)offset, 
                                 imw/2.0f, (float)imh-offset));
        // Arrow heads
        int arrowWidth = 6;
        int arrowHeight = 10;
        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        // top of y-axis
        g2.draw(new Line2D.Float(imw/2.0f, (float)offset,
                                 imw/2.0f+arrowWidth, (float)offset+arrowHeight));
        g2.draw(new Line2D.Float(imw/2.0f, (float)offset,
                                 imw/2.0f-arrowWidth, (float)offset+arrowHeight));
        // and text for y-axis
        Rectangle2D bounds = font.getStringBounds(axesLabels[1], frc);
        g2.drawString(axesLabels[1], 
                      imw/2.0f+(float)arrowWidth+2.0f, 
                      (float)offset+(float)bounds.getHeight()/2.0f);
        // right of x-axis
        g2.draw(new Line2D.Float((float)imh-offset, imh/2.0f,
                                 (float)imh-offset-arrowHeight, imh/2.0f+arrowWidth));
        g2.draw(new Line2D.Float((float)imh-offset, imh/2.0f,
                                 (float)imh-offset-arrowHeight, imh/2.0f-arrowWidth));
        // text for x-axis
        g2.drawString(axesLabels[0], 
                      (float)imh-(float)offset-(float)bounds.getWidth(), 
                      imw/2.0f+(float)bounds.getHeight()/2.0f+(float)arrowWidth+2.0f);
        g2.setColor(oldColor);
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Rectangle r = getBounds();
        // System.err.println("vriGreyDisp.paint invoked.");
        if (message != null) {
            g.setColor(Color.red);
            g.drawString(message, 20,20);
            System.err.println("vriGreyDisp.paint: displaying message '"+message+"'");
        } else if (img != null) {
            // System.err.println("vriGreyDisp.paint: message unset.");            
            // Get current image scale
            int imh = img.getHeight(this);
            int imw = img.getWidth(this);
            // System.err.println("vriGreyDisp.paint: imh: "+imh+" imw: "+imw);
            // System.err.println("vriGreyDisp.paint: width: "+getWidth()+" height: "+getHeight());
            AffineTransform a = new AffineTransform();
            a.translate(-imw/2.0, -imh/2.0);
            a.preConcatenate(aff);
            a.preConcatenate(AffineTransform.getTranslateInstance(getWidth()/2.0,
                                                                  getHeight()/2.0));
            g2.drawImage(img, a, applet);
            if (hasAxes) {
                System.err.println("Has axes");
                paintAxes(g);
            } else {
                System.err.println("Not has axes");
            }
            paintScale(g);
        } else {
            System.err.println("vriGreyDisp.paint invoked but img is null.");
        }
        plotFocus(g);
    }

    public void loadImage(String filename) {
        System.err.println("Loading "+filename);
        try {
            imgURL = getClass().getResource(filename);
            System.err.println("imgURL:"+imgURL);
            MediaTracker tracker = new MediaTracker(this);
            img = Toolkit.getDefaultToolkit().getImage(imgURL);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } 
        catch (NullPointerException e) {
            System.err.println("Error with image load");
        }
        catch (Exception e) {
            System.err.println("Error with image load");
        }
    }

    public PixArray imgToPix(Image img) 
        throws EmptyImageException
    {
        if (img == null) {
            throw new EmptyImageException();
        } 
        int imh = img.getHeight(this);
        int imw = img.getWidth(this);
        System.err.println("Got "+this+"image: size = "+imw+"x"+imh);
        PixArray pix = new PixArray(imw, imh);
        try {
            PixelGrabber pg = new PixelGrabber(img, 0, 0, imw, imh, pix.data, 0, imw);
            pg.grabPixels(100);
        } catch (InterruptedException e){
            System.err.println("imgToPix failed");
        }
        return pix;
    }
}


