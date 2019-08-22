/*
 * vriUVpDisp.java
 *
 * Used in the Virtual Radio Interferometer.
 *
 * 06/Jan/1998 Nuria McKay - Extracted from vriGreyDisp.java
 *
 */
package nl.jive.vri;

import java.applet.Applet;
import java.beans.*;
import java.awt.*;

class vriUVpDisp extends vriGreyDisp 
    implements PropertyChangeListener
{
    FFTArray fft;
    SquareArray uvcov;	 

    public vriUVpDisp(Applet app) {
        super(app);
        setUnit("lambda");
        message = new String("No current transform");
        setHasAxes(true);
        axesLabels = new String[] {"U", "V"};
        propChanges = new PropertyChangeSupport(this);
    }

    void setFullScale(double s){
        fullScale = s;
        repaint();
    }

    public void propertyChange(PropertyChangeEvent e) {
        // uvcov and fft are for UVpConvDisp
        // dat is from the source image, for UVpDisp
        String pname = e.getPropertyName();
        if (pname.equals("uvcov")) {
            System.err.println("vriUVpDisp: "+
                               "UV coverage changed - updating convolution");
            uvcov =  (SquareArray) e.getNewValue(); 
            if (fft!=null) {
                System.err.println("vriUVpDisp: "+
                                   "fft size " + fft.size);
                applyUVc(uvcov, fft);
            } else {
                System.err.println("vriUVpDisp: Null fft");
            }
        } else if (pname.equals("fft")) {
            System.err.println("vriUVp(Conv)Disp: UVp fft changed");
            fft = (FFTArray) e.getNewValue();
            if (uvcov!=null) {
                applyUVc(uvcov, fft);
            } else {
                System.err.println("vriUVpDisp: Null uvcov");
            }
        } else if (pname.equals("dat")) {
            System.err.println("vriUVpDisp: img disp dat changed");
            SquareArray dat = (SquareArray) e.getNewValue();
            if (dat != null) {
                fft(dat);
            } else {
                System.err.println("vriUVpDisp: got null dat!");
            }
        } else {
            Boolean b = pname.equals("dat");
            System.err.println("vriUVpDisp got propertyChange for "+
                               pname+" ; ignoring it because "+b.toString());
        }
    }

    public void fft(SquareArray dat) {
        if (dat.data == null) {
            System.err.println("vriUVpDisp: dat not set");
            return;
        }
        System.err.println("vriUVpDisp: Fourier transforming...");
        message = new String("Fourier transforming...");
        super.repaint();
        FFTArray fdat = FFTArray.fromSquareArray(dat);
        fft = fdat.fft();
        fftToImg(fft);
        propChanges.firePropertyChange("fft", null, fft);
    }

    public void fftToImg(FFTArray fft) {
        PixArray pix = vriUtils.fftToPix(fft, type);
        System.err.println("vriUVpDisp: pix " + pix.toString());
        img = pix.toImage(applet, fft.size);
        message = null;
        repaint();
        System.err.println("vriUVpDisp: Repainted...");
    }

    // sets fft if this is the convolved class
    public void applyUVc(SquareArray cov, FFTArray fft0) {
        message = new String("Applying UV coverage...");
        repaint();
        System.err.println("vriUVpDisp: Applying UV coverage...");

        FFTArray fftconv = fft0.multiply(cov);
        fftToImg(fftconv);
        propChanges.firePropertyChange("fftconv", null, fftconv);
    }

    public void paint(Graphics g) {
        System.err.println("vriUVpDisp: Painting...");
        super.paint(g);
    }
        
}
