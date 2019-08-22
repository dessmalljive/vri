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

	 String[] cfg_name;

	 double getLengthScale() {
		  return lengthScale;
	 }

	 abstract int numberOfAntennas();
	 abstract ArrayList<Baseline<T> > getBaselines();
	 abstract boolean isAntennaInBaseline(int i, Baseline<T> b);
	 abstract String defaultConfig();
	 abstract boolean setConfig(String cfg_str);  // BOOlean?!
	 abstract void selectNumAnt(int i);
	 abstract void stationLock();
	 abstract ArrayDisplays.vriArrDisp getArrDisp();
	 abstract vriUVcDisp getUVcDisp(vriAuxiliary aux);
	 abstract DisplayControls.vriDisplayCtrl getDispCtrl(ArrayDisplays.vriArrDisp ad);
}



