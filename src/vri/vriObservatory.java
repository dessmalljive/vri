/*
 * vriObservatory.java
 *
 * Used in the Virtual Radio Interferometer
 *
 * 06/Jan/1998 Nuria McKay - Extracted from vri.java
 *
 */

package nl.jive.vri;

import java.util.*;
import java.util.stream.*;
import nl.jive.earth.*;
import nl.jive.earth.Component;
import java.beans.*;
import java.awt.geom.*;


abstract class vriObservatory<T extends isVisible> 
{
	 String menu_name;      // Name in observatory pop-up menu
	 String full_name;      // Proper name of obs.
	 double lengthScale;    // of observatory, in m
	 double latitude;
	 double longitude;
	 double ant_diameter;
	 double ant_el_limit;
	 boolean zoomer;
    TList<T> antennas;
    
	 String[] cfg_name;

	 double getLengthScale() {
		  return lengthScale;
	 }

	 abstract int numberOfAntennas();
    
	 abstract ArrayList<Baseline<T> > getBaselines();
    
    List<GeneralPath> getActivePaths(int i, double ha1, double ha2, double dec, double scale) {
        ArrayList<Baseline<T> > bl = getBaselines();
        java.util.List<GeneralPath> activePaths = bl.stream()
            .filter(b -> isAntennaInBaseline(i, b))
            .map(b -> b.makeUVGeneralPath(ha1, ha2, dec, scale))
            .collect(Collectors.toList());
        return activePaths;
    }
    
    List<GeneralPath> getOtherPaths(int i, double ha1, double ha2, double dec, double scale) {
        ArrayList<Baseline<T> > bl = getBaselines();
        java.util.List<GeneralPath> otherPaths = bl.stream()
            .filter(b -> !isAntennaInBaseline(i, b))
            .map(b -> b.makeUVGeneralPath(ha1, ha2, dec, scale))
            .collect(Collectors.toList());
        return otherPaths;
    }

    List<ArrayList<UV> > makeUVTracks(double h1, double h2, double dec, double s) {
        ArrayList<Baseline<T> > bl = getBaselines();
        List<ArrayList<UV> > tracks = bl.stream()
            .map(b -> b.makeUVPoints(h1, h2, dec, s))
            .collect(Collectors.toList());
        return tracks;
    }
	 abstract boolean isAntennaInBaseline(int i, Baseline<T> b);
	 abstract String defaultConfig();
	 abstract boolean setConfig(String cfg_str);  // BOOlean?!
	 abstract void selectNumAnt(int i);
	 abstract void stationLock();
	 abstract ArrayDisplays.vriArrDisp getArrDisp();
	 abstract vriUVcDisp getUVcDisp(vriAuxiliary aux);
	 abstract DisplayControls.vriDisplayCtrl getDispCtrl(ArrayDisplays.vriArrDisp ad);
}



