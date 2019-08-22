package nl.jive.vri;

import java.util.*;
import nl.jive.earth.*;
import nl.jive.earth.Component;
import java.beans.*;
import java.awt.geom.*;

class vriSmallObservatory extends vriObservatory<vriLocation> {
	 private static PropertyChangeSupport propChanges;

	 ArrayList<vriLocation> stations; 
	 vriTrack[] trk; 
	 vriLocation ref;
	 int[][] cfg_stations;  // Array of station numbers of configs
	 NSEWTelescopeList antennas;      
		  
	 vriSmallObservatory() {
		  zoomer = true; // default
	 }

	 int numberOfAntennas() {
		  return antennas.size();
	 }

	 void makeAntennas(int n) {
		  antennas = new NSEWTelescopeList(n);
	 }

	 ArrayDisplays.vriArrDisp getArrDisp() {
		  return new ArrayDisplays.vriNSEWArrDisp(this);
	 }

	 DisplayControls.vriDisplayCtrl getDispCtrl(ArrayDisplays.vriArrDisp ad) {
		  return new DisplayControls.vriDisplayZoomCtrl(ad);
	 }

	 vriUVcDisp getUVcDisp(vriAuxiliary a) {
		  return new vriUVcDisp(this, a);
	 }

    ArrayList<Baseline<vriLocation> > getBaselines() {
		  return antennas.getBaselines();
	 }

    boolean isAntennaInBaseline(int i, Baseline<vriLocation> b) {
		  return antennas.isAntennaInBaseline(i, b);
	 }

	 void selectNumAnt(int n) {
		  int num_antennas = n;
		  antennas.subList(0, n-1).clear();
	 }

	 String defaultConfig() {
		  int[] config = cfg_stations[0];
		  for (int j = 0; j < antennas.size(); j++) {
				if (j < config.length) {
					 vriLocation station = stations.get(config[j]-1);
					 antennas.get(j).moveTo(station);
				}
		  }
		  return cfg_name[0];
	 }

	 boolean setConfig(String cfg_str) {
		  for (int i = 0; i < cfg_stations.length; i++) {
				if (cfg_name[i].equals(cfg_str)) {
					 for (int j = 0; j < antennas.size(); j++) {
						  if (j < cfg_stations[i].length) {
								vriLocation station = stations.get(cfg_stations[i][j]-1);
								antennas.get(j).moveTo(station);
						  }
					 }
					 return true;
				} 
		  }
		  return false;
	 }

	 void stationLock() {
		  int s = 0;
		  double dist;

		  for (int i = 0; i < antennas.size(); i++) {
				vriLocation a = antennas.get(i);
				double bestdist = Double.MAX_VALUE;
				for (int j = 0; j < stations.size(); j++) {
					 vriLocation station = stations.get(j);
					 dist = vriLocation.dist2(a, station);
					 if (dist < bestdist) {
						  s = j;
						  bestdist = dist;
					 }
				}
				antennas.get(i).moveTo(stations.get(s));
		  }
	 }


	 void report() {
		  System.out.println ("\nObservatory report... "+full_name);
		  for (int i = 0; i < antennas.size(); i++) {
				System.out.println ("Antenna "+i+
										  " NS = "+antennas.get(i).NS+
										  " EW = "+antennas.get(i).EW+
										  " UD = "+antennas.get(i).UD );
		  }
	 }
}
