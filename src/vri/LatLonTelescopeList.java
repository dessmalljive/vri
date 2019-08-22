package nl.jive.vri;

import java.util.*;
import java.awt.geom.*;
import nl.jive.earth.*;

class LatLonTelescopeList extends TList<Telescope> {
	 public ArrayList<ProjectedTelescope> rotateAndProject(double lon,  double lat) {
		  ArrayList<ProjectedTelescope> result = 
				new ArrayList<ProjectedTelescope>();
		  for (Telescope t : this) {
				Point3D p1 = Projectable.make3D(t.position);
				Point3D p2 = Projectable.rotate(lon, lat, p1);
				if (p2.y <= 0) continue;
				else {
					 Point2D.Double proj = new Point2D.Double(p2.x, p2.z);
					 result.add(new ProjectedTelescope(t, proj));
				}
		  }
		  return result;
	 }
    
    Baseline<Telescope> makeBaseline(Telescope ant1, Telescope ant2) {
        return Telescope.makeBaseline(ant1, ant2);
    }

}
