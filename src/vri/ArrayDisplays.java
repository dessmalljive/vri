package nl.jive.vri;

import java.util.*;
import java.lang.*;
import java.lang.Math;
import java.beans.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.geom.Point2D;
//import java.applet.Applet;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;

import nl.jive.earth.*;
import nl.jive.earth.Component;
// import nl.jive.earth.Point2D;


class ArrayDisplays {
    public abstract static class vriArrDisp extends vriDisplay {
        AffineTransform trans;
        Image image;       // Image of antenna for ArrDisp
        Color bg = new Color(174, 255, 81);
        double latitude, longitude;  // In radians
        String unit="m";
        ArrayList<Component> geometry;

        vriArrDisp(vriObservatory<?> o) {
            super();
            setObservatory(o);
            latitude = o.latitude;
            longitude = o.longitude;
            loadImage();
            propChanges = new PropertyChangeSupport(this);
        }

        void loadImage() {
            // Now load in the antenna images/icons for display on the site map
            // Note that we wait for them to finish loading before proceding
            try {
                URL u = getClass().getResource("antenna.gif");
                image = Toolkit.getDefaultToolkit().getImage(u);
            } catch (Exception exc) {
                System.err.println("Error with antenna icon load");
            }
        }		  

        void setGeometry(ArrayList<Component> g) {
            geometry = g;
        }

        ArrayList<Contour2D> processComponents(ArrayList<Component> components) 
        {
            ArrayList<Contour2D> contours2D = new ArrayList<Contour2D>();
            for (Component comp : components) {
                if (comp!=null) {
                    ArrayList<Contour2D> cs = comp.rotateAndProject(Math.toDegrees(-longitude),
                                                                    Math.toDegrees(-latitude));
                    if (cs!=null) {
                        contours2D.addAll(cs);
                    }
                }
            }
            return contours2D;
        }

        static GeneralPath pointsToPolygon(LinkedList<Point2D.Float> pts, 
                                           AffineTransform t) {
            GeneralPath gp = new GeneralPath();

            Point2D.Float p = pts.getFirst();
            gp.moveTo(p.x, p.y);
            for (ListIterator<Point2D.Float> i=pts.listIterator(1); i.hasNext(); p=i.next()) {
                gp.lineTo(p.x, p.y);
            }
            gp.closePath();
            gp.transform(t);
            return gp;
        }


        abstract void paintBackground(Graphics g);

        abstract void paintAntennas(Graphics g);

        abstract void setObservatory(vriObservatory<?> o);

        void paintScale(Graphics g) {
            System.err.println("Painting scale");
            Graphics2D g2 = (Graphics2D) g;
            Rectangle r = getBounds();
            int width = getWidth();
            double displayScale = trans.getScaleX();
            g2.setColor(Color.blue);
            double l = roundPower((width - 20.0) / displayScale); 
            int m = (int) Math.round(l * displayScale); //
            String str = roundUnit(l, unit); //new String();
            Font font = g2.getFont();
            FontRenderContext frc = g2.getFontRenderContext();
            Rectangle2D bounds = font.getStringBounds(str, frc); 
            g2.setColor(Color.white);
            int w = Math.max((int)bounds.getWidth(), m);
            g2.fill(new Rectangle(10-4, r.height-12-(int)bounds.getHeight(), 
                                  w+8, (int)bounds.getHeight()+5));
            g2.setColor(Color.black);
            g2.drawString(str, 10, r.height-12);
            g.drawLine(10, r.height-10, 10+m, r.height-10);
        }

    }

    public static class vriNSEWArrDisp extends vriArrDisp 
    {
        vriSmallObservatory obs;  // Observatory being used
        int pick;    // Index of antenna selected by the mouse

        vriNSEWArrDisp(vriObservatory<?> o) {
            super(o);
            pick = -1;
            addMouseListener(new Mousey());
            addMouseMotionListener(new MoveyMousey());
            repaint();
        }

        void setObservatory(vriObservatory<?> aobs) {
            obs = (vriSmallObservatory) aobs;
            Dimension d = getSize();
            int w = d.width;
            int h = d.height;
            if (aff==null) {
                System.err.println("Null affine transform");
            }
            if (obs.ref==null) {
                System.err.println("Null obs.ref");				
                System.err.println("Latitude: "+obs.latitude);				
            }
            aff.translate((int)obs.ref.EW, (int)obs.ref.NS);
            defaultTransform = (AffineTransform) aff.clone();
            repaint();
            // new, to propagate to UVcDisp
            firePropertyChange("Observatory", null, obs);
        }


        void paintBackground(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Rectangle r = getBounds();
            double displayScale = getDisplayScale();
            if (geometry != null) {
                g2.setColor(Color.blue);
                g2.fillRect(0, 0, r.width-1, r.height-1);
                g2.setColor(bg);
                AffineTransform cache = g2.getTransform();
                g2.translate(getWidth()/2, getHeight()/2);
                ArrayList<Contour2D> contours2D = processComponents(geometry);
                for (Contour2D cont : contours2D) {
                    if (cont.isClosed()) {
                        LinkedList<Point2D.Float> pts = cont.getPoints();
                        GeneralPath poly = pointsToPolygon(pts, trans);
                        Rectangle rec = poly.getBounds();
                        // System.err.println(String.format("*** x %d y %d width %d height %d",
                        //									  rec.x, rec.y, rec.width, rec.height));
                        g2.fill(poly);
                    } else {
                        System.err.println("*** Unclosed contour, skipping");
                    }
                }
                g2.setTransform(cache);
            } else {
                System.err.println("No geography");
                g2.setColor(bg);
                g2.fillRect(0, 0, r.width-1, r.height-1);
            }
            plotFocus(g);
        }
		  
        void paintRef(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g.setColor(Color.black);
            // Plot observatory centre
            Point p1 = new Point((int)obs.ref.EW, (int)obs.ref.NS);
            Point p2 = new Point();
            trans.transform(p1, p2);
            g2.drawLine(p2.x, p2.y+2, p2.x, p2.y-2);
            g2.drawLine(p2.x+2, p2.y, p2.x-2, p2.y);

        }
	 
        void paintStations(Graphics g) {
            // Recall: Stations are configuration specific 
            // pre-allocated positions for antennas.
            // Not to be confused with antennas themselves.

            Graphics2D g2 = (Graphics2D) g;
            for (int j = 0; j < obs.stations.size(); j++) {
                vriLocation station = obs.stations.get(j);
                Point p1 = new Point((int)station.EW, (int)station.NS);
                Point p2 = new Point();
                trans.transform(p1, p2);
                g2.drawOval(p2.x-2, p2.y-2, 4, 4);
            }
        }

        void paintTracks(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g.setColor(Color.black);
            for (int j = 0; j < obs.trk.length; j++) {
                vriTrack track = obs.trk[j];
                Point p1 = new Point((int)track.start.EW, (int)track.start.NS);
                Point p2 = new Point((int)track.end.EW, (int)track.end.NS);
                Point p3 = new Point();
                Point p4 = new Point();
                trans.transform(p1, p3);
                trans.transform(p2, p4);
				
                g2.drawLine(p3.x, p3.y, p4.x, p4.y);
            }
        }

        void paintAntennas(Graphics g) {
            // Calculate antenna image sizes
            // use one image; all antennas look the same.
            Graphics2D g2 = (Graphics2D) g;

            double displayScale = getDisplayScale();
            int imgw = image.getWidth(this);
            int imgh = image.getHeight(this);
            double xoff = displayScale*imgw/2;
            double yoff = displayScale*imgw/2;
            for (int i = 0; i < obs.antennas.size(); i++) {
                vriLocation ant = obs.antennas.get(i);
                Point p1 = new Point((int)ant.EW, (int)ant.NS);
                Point p2 = new Point();
                trans.transform(p1, p2);
                g2.drawImage(image, p2.x-imgw/2, p2.y-imgh/2, this);
            }
        }

        public void paint(Graphics g) {
            System.err.println("NSEWArrDisp::paint called");

            double geoScale = getWidth()/obs.getLengthScale();
            AffineTransform geoTrans = 
                AffineTransform.getScaleInstance(geoScale, -geoScale);
            trans = (AffineTransform) geoTrans.clone();
            // obs.ref is guaranteed to be (0,0), but still:
            trans.concatenate(AffineTransform.getTranslateInstance(-obs.ref.EW, -obs.ref.NS)); 
            trans.preConcatenate(aff);

            Graphics2D g2 = (Graphics2D) g;	 
            paintBackground(g);
            AffineTransform a = g2.getTransform();
            g2.translate(getWidth()/2, getHeight()/2);

            paintRef(g);
            paintTracks(g);
            paintStations(g);
            paintAntennas(g);
            g2.setTransform(a);
            paintScale(g);
        }

        Point screenToGeom(Point p1) 
            throws NoninvertibleTransformException 
        {
            AffineTransform shiftOrigin = AffineTransform.getTranslateInstance(getWidth()/2, 
                                                                               getHeight()/2);
            Point p2 = new Point();				
            Point p3 = new Point();
            shiftOrigin.inverseTransform(p1, p2);
            trans.inverseTransform(p2, p3);
            return p3;
        }

        int pointToAntenna(Point p2) 
        {
            int ind = 0;
            double bestdist = Double.MAX_VALUE;
            for (int i = 0; i < obs.antennas.size(); i++) {
                vriLocation ant = obs.antennas.get(i);
                double dist = ((ant.EW - p2.x) * (ant.EW - p2.x) +
                               (ant.NS - p2.y) * (ant.NS - p2.y));
                if (dist < bestdist) {
                    ind = i;
                    bestdist = dist;
                }
            }
            return ind;
        }

        class Mousey extends MouseAdapter {
            public void mousePressed(MouseEvent e) {
                System.err.println("** MousePressed");
                Graphics2D g2 = (Graphics2D) getGraphics();
                Point p1 = e.getPoint();
                try {
                    Point p2 = screenToGeom(p1);
                    pick = pointToAntenna(p2);
                    // honestly! ns, ew
                    vriLocation l = new vriLocation(p2.y, p2.x, 0.0);
                    obs.antennas.set(pick, l);
                    repaint();
                    propChanges.firePropertyChange("active", -1, pick);
                } catch (NoninvertibleTransformException ex) {
                    System.err.println("Transformation not invertible");
                }
            }

            public void mouseReleased(MouseEvent e) {
                System.err.println("** MouseReleased");
                repaint();
                propChanges.firePropertyChange("active", pick, -1);
            }
        }

        class MoveyMousey extends MouseMotionAdapter {
            public void mouseDragged(MouseEvent e) {
                System.err.println("** MouseDragged");
                Graphics2D g2 = (Graphics2D) getGraphics();
                Point p1 = e.getPoint();
                try {
                    Point p2 = screenToGeom(p1);
                    // honestly! ns, ew
                    vriLocation l = new vriLocation(p2.y, p2.x, 0.0);
                    obs.antennas.set(pick, l);
                    repaint();
                    propChanges.firePropertyChange("active", null, pick); 
                    // The above used to be ("active", pick, pick), but nothing seemed to happen
                    // I suspect smartalecry that checks it is actually a change
                } catch (NoninvertibleTransformException ex) {
                    System.err.println("Transformation not invertible");
                }
            }
        }

    }

    public static class vriFlatLatLonArrDisp extends vriArrDisp 
    {
        vriBigObservatory obs;
        int pick;

        vriFlatLatLonArrDisp(vriObservatory<?> o) {
            super(o);
            // otherwise it gets set to something small
            // remember to flip y!
            trans = AffineTransform.getScaleInstance(1.0, -1.0); 
            addMouseListener(new Mousey());
            addMouseMotionListener(new MoveyMousey());
        }

        public Dimension getPreferredSize() {
            int width, height;
            width = 3*256+48;
            height = 256;
            return new Dimension(width, height);
        }

        void setObservatory(vriObservatory<?> aobs) {
            obs = (vriBigObservatory) aobs;
            // new, to propagate to UVcDisp
            System.err.println("FlatLatLonArrDisp changing observatory.");
            propChanges.firePropertyChange("Observatory", null, obs);
        }

        void setLongitude(double l) {
            longitude = l;
        }

        void paintBackground(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Rectangle r = getBounds();
            double displayScale = getDisplayScale();
            if (geometry != null) {
                g2.setColor(Color.blue);
                g2.fillRect(0, 0, r.width-1, r.height-1);
                AffineTransform cache = g2.getTransform();

                g2.translate(getWidth()/2, getHeight()/2);				
                g2.setColor(bg);
                for (Component c : geometry) {
                    LinkedList<Point2D.Float> pts = c.getPoints2D();
                    GeneralPath poly = pointsToPolygon(pts, trans);
                    g2.fill(poly);
                }
                g2.setTransform(cache);
            } else {
                System.err.println("No geography");
                g2.setColor(bg);
                g2.fillRect(0, 0, r.width-1, r.height-1);
            }
            plotFocus(g);
        }

        void paintAntennas(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform cache = g2.getTransform();
            g2.translate(getWidth()/2, getHeight()/2);

            LatLonTelescopeList telescopes = obs.antennas;
            int imgw = image.getWidth(this);
            int imgh = image.getHeight(this);
            for (Telescope t : telescopes) {
                LatLon ll = t.position;
                Point2D.Float p1 = new Point2D.Float((float)ll.lon, 
                                                     (float)ll.lat);
                Point2D.Float p2 = new Point2D.Float();
                trans.transform(p1, p2);
                g2.drawImage(image, (int)(p2.x-imgw/2), (int)(p2.y-imgh/2), this);

            }
            g2.setTransform(cache);
        }

        public void paint(Graphics g) {
            System.err.println("FlatLatLonArrDisp::paint called");
            trans = AffineTransform.getScaleInstance((float)getWidth()/360.0,
                                                     (float)-getHeight()/180.0);
            trans.preConcatenate(aff);

            Graphics2D g2 = (Graphics2D) g;	 
            paintBackground(g);
            paintAntennas(g);
        }

        Point screenToGeom(Point p1) 
            throws NoninvertibleTransformException 
        {
            AffineTransform shiftOrigin = AffineTransform.getTranslateInstance(getWidth()/2, 
                                                                               getHeight()/2);
            Point p2 = new Point();				
            Point p3 = new Point();
            shiftOrigin.inverseTransform(p1, p2);
            trans.inverseTransform(p2, p3);
            return p3;
        }

        int pointToAntenna(Point p2) 
        {
            int ind = 0;
            double bestdist = Double.MAX_VALUE;
            for (int i = 0; i < obs.antennas.size(); i++) {
                LatLon pos = obs.antennas.get(i).position;
                double dist = ((pos.lon - p2.x) * (pos.lon - p2.x) +
                               (pos.lat - p2.y) * (pos.lat - p2.y));
                if (dist < bestdist) {
                    ind = i;
                    bestdist = dist;
                }
            }
            return ind;
        }

        class Mousey extends MouseAdapter {
            public void mousePressed(MouseEvent e) {
                System.err.println("** MousePressed");
                Graphics2D g2 = (Graphics2D) getGraphics();
                Point p1 = e.getPoint();
                try {
                    Point p2 = screenToGeom(p1);
                    pick = pointToAntenna(p2);
                    LatLon pos = obs.antennas.get(pick).position;
                    pos.lon = p2.x;
                    pos.lat = p2.y;
                    repaint();
                    propChanges.firePropertyChange("active", -1, pick);
                } catch (NoninvertibleTransformException ex) {
                    System.err.println("Transformation not invertible");
                }
            }

            public void mouseReleased(MouseEvent e) {
                System.err.println("** MouseReleased");
                repaint();
                propChanges.firePropertyChange("active", pick, -1);
            }
        }

        class MoveyMousey extends MouseMotionAdapter {
            public void mouseDragged(MouseEvent e) {
                System.err.println("** MouseDragged");
                Graphics2D g2 = (Graphics2D) getGraphics();
                Point p1 = e.getPoint();
                try {
                    Point p2 = screenToGeom(p1);
                    LatLon pos = obs.antennas.get(pick).position;
                    pos.lon = p2.x;
                    pos.lat = p2.y;
                    repaint();
                    propChanges.firePropertyChange("active", null, pick); 
                    // The above used to be ("active", pick, pick), but nothing seemed to happen
                    // I suspect smartalecry that checks it is actually a change
                } catch (NoninvertibleTransformException ex) {
                    System.err.println("Transformation not invertible");
                }
            }
        }
    }	 
}
