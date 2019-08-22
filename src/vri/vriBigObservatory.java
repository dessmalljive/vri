package nl.jive.vri;

import java.util.*;
import nl.jive.earth.*;
import nl.jive.earth.Component;

class vriBigObservatory extends vriObservatory<Telescope> {
	 
	 LatLonTelescopeList antennas;
	 vriBigObservatory() {
		  zoomer = false;
	 }

	 int numberOfAntennas() {
		  return antennas.size();
	 }

    ArrayDisplays.vriArrDisp getArrDisp() {
		  return new ArrayDisplays.vriFlatLatLonArrDisp(this);
	 }

	 DisplayControls.vriDisplayCtrl getDispCtrl(ArrayDisplays.vriArrDisp ad) {
		  if (zoomer) {
				return new DisplayControls.vriDisplayZoomCtrl(ad);
		  } else {
				return new DisplayControls.vriDisplayRotateCtrl(ad);
		  }
	 }

	 vriUVcDisp getUVcDisp(vriAuxiliary aux) {
		  return new vriUVcDisp(this, aux);
	 }
	 
	 String defaultConfig() {
		  return "Default";
	 }

	 boolean setConfig(String cfg_str) {
		  return true;
	 }

    ArrayList<Baseline<Telescope>> getBaselines() {
		  return antennas.getBaselines();
	 }

	boolean isAntennaInBaseline(int i, Baseline<Telescope> b) {
		  return antennas.isAntennaInBaseline(i, b);
	 }

	 void selectNumAnt(int i) {
	 }

	 void stationLock() {

	 }
}

