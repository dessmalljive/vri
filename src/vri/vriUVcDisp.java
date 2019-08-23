/*
 * vriUVcDisp.java
 *
 * Used in the Virtual Radio Interferometer
 *
 * 06/Jan/1998 Nuria McKay - Extracted from vri.java
 *
 */

package nl.jive.vri;

import java.awt.*;
import java.beans.*;
import java.util.*;
import java.awt.geom.*;
import nl.jive.earth.*;


class vriUVcDisp extends vriDisplay 
    implements PropertyChangeListener 
{
	 String unit = "lambda";
	 double c = 299.792458; // in 1000km/s because freq is in MHz
	 double r_earth = 6.378137e6; // m
	 vriObservatory<? extends isVisible> obs;
	 vriAuxiliary aux;
	 double arrayScale;
	 double spaceScale;
	 double earthScale;
	 Scale plotScale;
	 AffineTransform aff, defaultTransform;
	 SquareArray uvcov;
	 int selectedAntenna;
    // FIXME: I am setting this to an initial value because that is
    // better than zero, which we had. It's meant to be set when you
    // load an image, but that isn't working.

	 int uvCoverageSize = 256; 

	 vriUVcDisp(vriObservatory<? extends isVisible> o, vriAuxiliary a) {
		  super();
		  selectedAntenna = -1;
		  aux = a;
		  setObservatory(o);
		  double lengthScale = obs.getLengthScale(); // in m
		  double scale = roundUpPower(lengthScale/getReferenceLambda());
		  aff = new AffineTransform();
		  defaultTransform = (AffineTransform) aff.clone();
		  System.err.println("lengthScale: "+lengthScale);
		  System.err.println("Default scale: "+scale);
		  System.err.println("Default lambda: "+getReferenceLambda());
		  arrayScale = obs.getLengthScale()/getReferenceLambda();
		  spaceScale = scale; 
		  earthScale = 2*r_earth/getReferenceLambda(); // full width
		  plotScale = Scale.SPACE;
		  propChanges = new PropertyChangeSupport(this);
	 }

	 public void setObservatory(vriObservatory<? extends isVisible> aobs) {
		  System.err.println("vriUVcDisp: setting observatory");
		  obs = aobs;
		  double lengthScale = obs.getLengthScale(); // in m
		  arrayScale = lengthScale/getLambda();
		  earthScale = 2*r_earth/getLambda();
		  repaint();
		  uvCoverage(); // also fires property change
	 }
	 
	 public void setPlotScale(Scale ps) {
		  plotScale = ps;
 	 }

	 public double getConvScale() {
		  return spaceScale;
	 }

	 public void setConvScale(double cs) {
		  spaceScale = cs;
	 }

	 double getLambda() {
		  return c/aux.freq;
	 }

	 double getReferenceLambda() {
		  return c/4800.0;
	 }

	 void paintScale(Graphics g) {
		  Graphics2D g2 = (Graphics2D) g;
		  Rectangle r = getBounds();
		  int width = getWidth();
		  g2.setColor(Color.white);

		  double displayScale;
		  switch (plotScale) {
		  case ARRAY:
				displayScale = 2.0*arrayScale/aff.getScaleX();
				break;
		  case EARTH:
				displayScale = 2.0*earthScale/aff.getScaleX();
				break;
		  case SPACE:
		  default: // AKA 
				displayScale = spaceScale/aff.getScaleX();
		  }
		  double l = roundPower(displayScale * (width - 20.0) / width);
		  int m = (int) Math.round(l * width / displayScale);
		  String str = roundUnit(l, "lambda");
		  paintRealScale(g2, r, str, m);
	 }

	 public SquareArray getUVCoverage() {
		  return uvcov;
	 }

	 public void uvCoverage() {
		  
		  Rectangle r = getBounds();
		  uvcov = new SquareArray(uvCoverageSize);
        // FIXME! spaceScale == 0;
		  double s = (uvCoverageSize) / (getLambda() * spaceScale);
		  System.err.println("vriUVcDisp: uvCoverageSize: " + uvCoverageSize);
		  System.err.println("vriUVcDisp: width: " + getWidth());
		  System.err.println("vriUVcDisp: earthScale: " + earthScale);
		  System.err.println("vriUVcDisp: spaceScale: " + spaceScale);
		  System.err.println("vriUVcDisp: arrayScale: " + arrayScale);
		  System.err.println("vriUVcDisp: s: "+s);
        System.err.println("vriUVcDisp: aux.dec: "+aux.dec);
		  System.err.println("vriUVcDisp: Obs.antennas length: "+ obs.numberOfAntennas());
        applyUV(aux.ha1, aux.ha2, aux.dec, s);
		  boolean nonzero = false;
		  for (int i=0; i < uvCoverageSize*uvCoverageSize; i++) {
				if (uvcov.data[i] != 0) {
					 nonzero=true;
					 break;
				}
		  }
		  if (!nonzero) {
				System.err.println("vriUVcDisp: uvcov is zero; not firing changes");
        } else {
            System.err.println("vriUVcDisp: UV coverage change firing");
            propChanges.firePropertyChange("uvcov", null, uvcov);
        }
	 }  // uvCoverage()

	 public void applyUV(double h1, double h2,
								double dec, double s) {
        
		  double uvscale = 1;
        double umax = 0;
        double vmax = 0;
		  java.util.List<ArrayList<UV> > uvTracks = obs.makeUVTracks(h1, h2, dec, s);
		  int size = uvcov.size;
		  for (ArrayList<UV> uvt : uvTracks) {
            for (UV uv : uvt) {
                if (Math.abs(uv.u)>umax) umax = Math.abs(uv.u);
                if (Math.abs(uv.v)>vmax) vmax = Math.abs(uv.v);
                int uc = (int)(uv.u*uvscale);
                int vc = (int)(uv.v*uvscale);
                if (uc > -size/2 && uc < size/2 && 
                    vc > -size/2 && vc < size/2) {
                    uvcov.set(uc+size/2, vc+size/2, 1);
                    uvcov.set(-uc+size/2, -vc+size/2, 1);
                }
            }
		  }
	 }  
	  
	 public void propertyChange(PropertyChangeEvent e) {
		  String pname = e.getPropertyName();
		  if (pname.equals("active")) {
				selectedAntenna = (Integer) e.getNewValue();
				System.err.println("vriUVcDisp: Got active: "+selectedAntenna);
				if (uvCoverageSize != 0) {
					 uvCoverage();
				}
				repaint();
		  } else if (pname.equals("fftsize")) {
				uvCoverageSize = (Integer) e.getNewValue();
				System.err.println("vriUVcDisp: Got fftsize: "+uvCoverageSize);
				if (uvCoverageSize>0) {
					 uvCoverage();
				}
		  } else if (pname.equals("this")) {
				System.err.println("vriUVcDisp: Got a this");				
		  } else {
				System.err.println("vriUVcDisp: Got propertyChange for "+
										 pname+
										 "; ignoring it");
		  }
	 }

	 double getActualScale() {
		  double s; // "actual scale" must be in m, so that we can calculate UV tracks
		  int width = getWidth();
		  switch (plotScale) {
		  case ARRAY:
				s = (width/2.0) / (getLambda() * arrayScale) * aff.getScaleX();
				break;
		  case EARTH:
				s = (width/2.0) / (getLambda() * earthScale) * aff.getScaleX();
				break;
		  case SPACE:
		  default:
				// Space scale is for full width of image
				s = (width) / (getLambda() * spaceScale) * aff.getScaleX();
		  }
		  return s;
	 }

	 public void paint(Graphics g) {
		  Graphics2D g2 = (Graphics2D) g;
		  Rectangle r = getBounds();
		  int tx, ty;
		  int tw, th;  // Used for the shadow plot
		  g2.setColor(Color.black);
		  g2.fillRect(1, 1, r.width-2, r.height-2);
		  g2.setColor(Color.darkGray);
		  g2.drawRect(0, 0, r.width-1, r.height-1);
		  // Calculate the metres-to-wavelengths-to-pixels conversion scale factor
		  double s = getActualScale();
		  System.err.println("vriUVcDisp: repainting");
		  System.err.println("Actual scale: "+s);
		  g2.translate(getWidth()/2.0, getHeight()/2.0);

		  if (plotScale == Scale.EARTH || plotScale == Scale.ARRAY) {
				double uv_earth = s*2*r_earth;
				// System.err.println("uv_earth: "+uv_earth);
				g2.setColor(new Color(0.0f, 0.0f, 1.0f, 0.5f));
				g2.fill(new Ellipse2D.Double(-uv_earth, -uv_earth, 2*uv_earth, 2*uv_earth));
		  
				g2.setColor(Color.darkGray);
		  }
		  // Do the shadow zone (use the tx/ty/tw/th parameters temporarily)
		  tx = ty = (int) (-s * obs.ant_diameter);
		  tw = th = (int) (s * 2.0 * obs.ant_diameter);
		  g2.fillOval(tx, ty, tw, th);
		  // System.err.println(String.format("UVc.Aux parameters: ha1 %f ha2 %f dec %f",  
		  // aux.ha1, aux.ha2, aux.dec));

        plotBaselineTracks(s, g2);
		  g2.translate(-getWidth()/2.0, -getHeight()/2.0);
		  paintScale(g);
		  plotFocus(g);
	 }  

    void plotBaselineTracks(double s, Graphics2D g2) {
        java.util.List<GeneralPath> activePaths = obs.getActivePaths(selectedAntenna, aux.ha1, aux.ha2, aux.dec, s);
        g2.setColor(Color.red);
        for (GeneralPath p: activePaths) {
            g2.draw(p);
        }
        java.util.List<GeneralPath> otherPaths = obs.getOtherPaths(selectedAntenna, aux.ha1, aux.ha2, aux.dec, s);
        g2.setColor(Color.blue);
        for (GeneralPath p: otherPaths) {
            g2.draw(p);
        }
        g2.setColor(Color.red);
    }
    
}

