package nl.jive.vri;

import java.util.*;
import nl.jive.earth.*;
import nl.jive.earth.Component;

class Geometry {
	 HashMap<String, ArrayList<Component>> geomap;
	 NationReader nr;
    
	 Geometry() 
	 {
		  geomap = new HashMap<String, ArrayList<Component>>();
		  try {
				nr = new NationReader("gis.json");
		  } catch (org.json.JSONException e) {
				;
		  } catch (java.io.FileNotFoundException e) {
            System.err.println("Geometry file not found");
		  } catch (java.io.IOException e) {
				;
		  }
		  if (nr == null) {
				System.err.println("Geometries not loaded");
		  } else {
				System.err.println("Geometries loaded");
		  }
		  try {
				geomap.put("ATCA", nr.getNation("Australia").getComponents());
				geomap.put("NATCA", nr.getNation("Australia").getComponents());
				
				Set<String> europe = nr.getNationNamesInRegion("Europe");
				europe.remove("Russia");
				ArrayList<Component> eurcomps = nr.getSelectedNations(europe);
				geomap.put("EVN", eurcomps);
				geomap.put("MERLIN", eurcomps);
				Set<String> iya_nations = new HashSet<String>();

				String[] world = new String[] {"Europe", "NorthAfrica", "Sub Saharan Africa",
														 "Asia", "North America", "Latin America",
														 "Antarctica", "Australia"};
				for (String s : world) {
					 iya_nations.addAll(nr.getNationNamesInRegion(s));
				}
				geomap.put("IYA", nr.getSelectedNations(iya_nations));
		  } catch (NameNotFoundException e) {
				System.err.println("Country or region not found");
		  }
	 }  
	 ArrayList<Component> getComponents(String s) {
		  return geomap.get(s);
	 }
}
