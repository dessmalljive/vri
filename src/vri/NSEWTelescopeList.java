package nl.jive.vri;

import java.util.*;
import java.awt.geom.*;
import nl.jive.earth.*;

class NSEWTelescopeList extends TList<vriLocation>
{
	 double latitude;

	 NSEWTelescopeList(double aLatitude) {
		  latitude = aLatitude;
	 }

	 public Baseline<vriLocation> makeBaseline(vriLocation ant1, vriLocation ant2) {
		  double bx = (ant1.NS - ant2.NS) * Math.sin(latitude);
		  double by = (ant1.EW - ant2.EW);
		  double bz = (ant1.NS - ant2.NS) * Math.cos(latitude);
		  return new Baseline<vriLocation>(ant1, ant2, bx, by, bz);
	 }
}

