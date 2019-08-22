package nl.jive.vri;

import nl.jive.earth.*;
import java.util.*; // Math


public class Telescope
	 implements isVisible
{
	 public String code, name;
	 public LatLon position;

	 Telescope(String acode, String aname, double alon, double alat) {
		  code = acode;
		  aname = aname;
		  position = new LatLon(alon, alat);
	 }

	 public boolean isVisible(double ha, double dec) {
		  Point3D p1 = Projectable.make3D(position);
		  Point3D p2 = Projectable.rotate(ha, dec, p1);
		  return (p2.y>0);
	 }
    
    public static Baseline<Telescope> makeBaseline(Telescope ant1, Telescope ant2) {
		  LatLon ll1 = ant1.position;
		  LatLon ll2 = ant2.position;
				
		  double rlon1 = Math.toRadians(ll1.lon);
		  double rlat1 = Math.toRadians(ll1.lat);
		  double rlon2 = Math.toRadians(ll2.lon);
		  double rlat2 = Math.toRadians(ll2.lat);

		  double bx = Constants.r_earth*(Math.sin(rlon1)*Math.cos(rlat1) - 
													Math.sin(rlon2)*Math.cos(rlat2));
		  double by = Constants.r_earth*(Math.cos(rlon2)*Math.cos(rlat2) - 
													Math.cos(rlon1)*Math.cos(rlat1));
		  double bz = Constants.r_earth*(Math.sin(rlat2) - Math.sin(rlat1));
		  return new Baseline<Telescope>(ant1, ant2, bx, by, bz);
	 }
}
