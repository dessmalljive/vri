package nl.jive.vri;

import java.util.*;

class vriObservatoryManager {
	 HashMap<String, vriObservatory<? extends isVisible>> observatories;
	 
	 vriObservatoryManager()
	 {
		  double[] cs, cu;
		  int[] csi;
		  int num_antennas, num_stations;
		  observatories = new HashMap<String, vriObservatory<? extends isVisible>>();
	 
		  vriSmallObservatory atca = new vriSmallObservatory();
		  atca.menu_name = "ATCA";
		  atca.full_name = "Australia Telescope Compact Array";
		  atca.latitude = -0.529059644;
		  atca.longitude = Math.toRadians(149.581328);

		  atca.ant_diameter = 22.0;
		  atca.ant_el_limit = 12.0 * Math.PI / 180.0;
		  atca.ref = new vriLocation();
		  atca.lengthScale = 20e3;

		  // Stations
		  csi = new int[] {0, 2, 4, 6, 8, 10, 12, 14, 16, 32, 45, 64, 84, 98, 100, 
								 102, 109, 110, 111, 112, 113, 128, 129, 140, 147, 148, 
								 163, 168, 172, 173, 182, 189, 190, 195, 196, 388, 392};
		  atca.stations = new ArrayList<vriLocation>();
		  for (int i = 0; i < csi.length; i++) {
				double space = (6000.0/392.0);
				int incr = csi[i];
				int ref = csi[34];
				atca.stations.add(new vriLocation(0.0, (ref-incr)*space, 0.0));
		  }

		  // Track
		  atca.trk = new vriTrack[2];
		  atca.trk[0] = new vriTrack(atca.stations.get(0), 
											  atca.stations.get(34));
		  atca.trk[1] = new vriTrack(atca.stations.get(35),
											  atca.stations.get(36));

		  // Configurations
		  atca.cfg_stations = new int[][] {
				{ 3, 11, 16, 30, 34, 37},     // 6A     
				{ 2, 12, 25, 31, 35, 37},     // 6B     
				{ 1,  6, 21, 24, 31, 37},     // 6C     
				{ 5, 10, 13, 28, 30, 37},     // 6D     
				{15, 18, 25, 28, 35, 37},     // 1.5A  
				{19, 21, 27, 31, 34, 37},     // 1.5B  
				{14, 22, 30, 33, 34, 37},     // 1.5C  
				{16, 17, 24, 31, 35, 37},     // 1.5D  
				{25, 27, 29, 33, 34, 37},     // 750A  
				{14, 17, 21, 24, 26, 37},     // 750B  
				{12, 13, 15, 18, 21, 37},     // 750C  
				{15, 16, 22, 24, 25, 37},     // 750D  
				{ 2,  6,  8,  9, 10, 37},     // 375    
				{14, 15, 16, 17, 20, 37},     // 210   
				{ 5,  6,  7,  8,  9, 37}      // 122B   
		  };

		  atca.cfg_name = new String[] {
				"6A", "6B", "6C", "6D",
				"1.5A", "1.5B", "1.5C", "1.5D",
				"750A", "750B", "750C", "750D",
				"375", "210", "122B"
		  };


		  num_antennas = atca.cfg_stations[0].length;
		  atca.makeAntennas(num_antennas);
		  for (vriLocation loc : atca.stations) {
				atca.antennas.add(loc);
		  }

		  observatories.put(atca.menu_name, atca);
		  // End ATCA

		  vriSmallObservatory natca = new vriSmallObservatory();
		  
		  natca.menu_name = "New ATCA";
		  natca.full_name = "Modified Australia Telescope Compact Array";
		  natca.latitude = -0.529059644;
		  natca.longitude = Math.toRadians(149.581328);
		  natca.lengthScale = 20e3;
		  natca.ant_diameter = 22.0;
		  natca.ant_el_limit = 12.0 * Math.PI / 180.0;
		  natca.ref = new vriLocation();

		  // Stations
		  cs = new double[] {0.0,   2.0,   4.0,   6.0,   8.0,  // CS01-05
									10.0,  12.0,  14.0,  16.0,  32.0,  // CS06-10
									45.0,  64.0,  84.0,  98.0, 100.0,  // CS11-15
									102.0, 109.0, 110.0, 111.0, 112.0,  // CS16-20
									113.0, 128.0, 129.0, 140.0, 147.0,  // CS21-25
									148.0, 163.0, 168.0, 172.0, 173.0,  // CS26-30
									182.0, 189.0, 190.0, 195.0, 196.0, 388.0, 392.0,  // -37
									104.0, 106.0, 124.0, 125.0,         // CS38-41
									106.0, 106.0, 106.0, 106.0,         // CS42-45
									106.0, 106.0, 106.0, 106.0};        // CS46-49

		  cu = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
									0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
									0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
									0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
									0.0, 0.0, 0.0, 0.0, 2.0, 5.0, 7.0, 11.0,
									14.0, 19.0, 23.0, 24.0 };

		  natca.stations = new ArrayList<vriLocation>(cs.length);
		  for (int i = 0; i < cs.length; i++) {
				// 4 doubles form of constructor
				double space = (6000.0/392.0);
				double ref = cs[15];
				double EWincr = cs[i];
				double NSincr = cu[i];
				natca.stations.add(new vriLocation(NSincr*space, (ref-EWincr)*space, 0.0));
		  }

		  // Track 
		  natca.trk = new vriTrack[3];
		  natca.trk[0] = new vriTrack(natca.stations.get(0),
												natca.stations.get(34));
		  natca.trk[1] = new vriTrack(natca.stations.get(35),
												natca.stations.get(36));
		  natca.trk[2] = new vriTrack(natca.stations.get(38), 
												natca.stations.get(48));

		  // Configurations
		  natca.cfg_stations = new int[][] {
				{14, 16, 38, 17, 20, 37},     // EWuc    New East-West configs
				{16, 38, 17, 20, 41, 37},     // EWvc1
				{38, 18, 21, 40, 22, 37},     // EWvc2

				{39, 42, 43, 44, 45, 37},     // NSuc1   New North-South configs
				{39, 42, 43, 45, 46, 37},     // NSuc2
				{39, 43, 44, 47, 48, 37},     // NSvc1
				{42, 43, 45, 47, 49, 37},     // NSvc2

				{38, 39, 18, 42, 43, 37},     // H75     New Hybrid configs
				{15, 38, 19, 44, 45, 37},     // H168
				{15, 38, 19, 44, 46, 37},     // H214
				{15, 17, 40, 46, 48, 37},     // H368

				{ 5,  6,  7,  8,  9, 37},     // 122B    Existing configs
				{14, 15, 16, 17, 20, 37},     // 210   
				{ 2,  6,  8,  9, 10, 37},     // 375    
				{25, 27, 29, 33, 34, 37},     // 750A  
				{14, 17, 21, 24, 26, 37},     // 750B  
				{12, 13, 15, 18, 21, 37},     // 750C  
				{15, 16, 22, 24, 25, 37},     // 750D  
				{15, 18, 25, 28, 35, 37},     // 1.5A  
				{19, 21, 27, 31, 34, 37},     // 1.5B  
				{14, 22, 30, 33, 34, 37},     // 1.5C  
				{16, 17, 24, 31, 35, 37},     // 1.5D  
				{ 3, 11, 16, 30, 34, 37},     // 6A     
				{ 2, 12, 25, 31, 35, 37},     // 6B     
				{ 1,  6, 21, 24, 31, 37},     // 6C     
				{ 5, 10, 13, 28, 30, 37}      // 6D     
		  };

		  natca.cfg_name = new String [] {
				"EWuc", "EWvc1", "EWvc2",
				"NSuc1", "NSuc2", "NSvc1", "NSvc2",
				"H75", "H168", "H214", "H368",
				"122B", "210", "375",
				"750A", "750B", "750C", "750D",
				"1.5A", "1.5B", "1.5C", "1.5D",
				"6A", "6B", "6C", "6D"
		  };

		  num_antennas = natca.cfg_stations[0].length;
		  natca.makeAntennas(num_antennas);
		  for (vriLocation loc : natca.stations) {
				natca.antennas.add(loc);
		  }

		  observatories.put(natca.menu_name, natca);
		  // End New ATCA
		  
		  vriSmallObservatory wsrt = new vriSmallObservatory();
		  wsrt.menu_name = "WSRT";
		  wsrt.full_name = "Westerbork Synthesis Radio Telescope";
		  wsrt.latitude = +0.923574484;
		  wsrt.lengthScale = 5e3;
		  wsrt.ant_diameter = 25.0;
		  wsrt.ant_el_limit = 0.0 * Math.PI / 180.0;
		  wsrt.ref = new vriLocation();
		  // Stations
		  csi = new int[] {0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 19, 20, 37, 38};
		  wsrt.stations = new ArrayList<vriLocation>();
		  for(int i = 0; i < csi.length; i++) {
				int incr = csi[i];
				int ref = csi[10];
				double space = -72.0;
				wsrt.stations.add(new vriLocation(0.0, (double)(ref-incr)*space, 0.0));
		  }

		  // Track
		  wsrt.trk = new vriTrack[2];
		  wsrt.trk[0] = new vriTrack(wsrt.stations.get(10), 
											  wsrt.stations.get(11));
		  wsrt.trk[1] = new vriTrack(wsrt.stations.get(12), 
											  wsrt.stations.get(13));

		  // Configurations
		  wsrt.cfg_stations = new int[][] {
				{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14}    // Default
		  };

		  wsrt.cfg_name = new String[] {
				"default"
		  };

		  num_antennas = wsrt.cfg_stations[0].length;
		  wsrt.makeAntennas(num_antennas); 
		  for (vriLocation loc : wsrt.stations) {
				wsrt.antennas.add(loc);
		  }

		  observatories.put(wsrt.menu_name, wsrt);
		  // End WSRT

		  vriSmallObservatory merlin = new vriSmallObservatory();
		  merlin.menu_name = "MERLIN";
		  merlin.full_name = "Multi Element Radio Linked Interferometer Network";
		  
		  merlin.latitude = +0.929160933;
		  merlin.lengthScale = 500.0e3;
		  //merlin.longitude = 0;
		  //merlin.latitude = Math.toRadians(53.0 + 30.0/60);
		  merlin.longitude = Math.toRadians(-1.0);
		  merlin.ant_diameter = 25.0;
		  merlin.ant_el_limit = 0.0 * Math.PI / 180.0;
		  merlin.ref = new vriLocation();

		  // Stations
		  merlin.stations = new ArrayList<vriLocation>();

		  merlin.stations.add(vriLocation.fromKM(   0.0,        0.0,        0.0 )); // Jb
		  merlin.stations.add(vriLocation.fromKM( -98.663272, 180.934874,  13.0 )); // Cm
		  merlin.stations.add(vriLocation.fromKM(  11.670062, -24.278806,  88.0 )); // Da
		  merlin.stations.add(vriLocation.fromKM( -28.996605, -67.835530, 109.0 )); // Kn
		  merlin.stations.add(vriLocation.fromKM(  26.381173, -14.973939,  77.0 )); // Ta
		  merlin.stations.add(vriLocation.fromKM(-105.641049,  12.157448,  67.0 )); // De

		  for (int i = 0; i < merlin.stations.size(); i++) 
				merlin.stations.get(i).EW -= 60000.0;  // Centre the array

		  // Track
		  merlin.trk = new vriTrack[5];
		  merlin.trk[0] = new vriTrack(merlin.stations.get(0), 
												 merlin.stations.get(2));
		  merlin.trk[1] = new vriTrack(merlin.stations.get(0), 
												 merlin.stations.get(3));
		  merlin.trk[2] = new vriTrack(merlin.stations.get(0), 
												 merlin.stations.get(4));
		  merlin.trk[3] = new vriTrack(merlin.stations.get(0),
												 merlin.stations.get(5));
		  merlin.trk[4] = new vriTrack(merlin.stations.get(5),
												 merlin.stations.get(1));

		  // Configurations
		  merlin.cfg_stations = new int[][] {
				{1, 2, 3, 4, 5, 6}    // Default
		  };

		  merlin.cfg_name = new String[] {"default"};
		  num_antennas = merlin.cfg_stations[0].length;
		  merlin.makeAntennas(num_antennas);
		  for (vriLocation loc : merlin.stations) {
				merlin.antennas.add(loc);
		  }

		  observatories.put(merlin.menu_name, merlin);
		  // End MERLIN

		  vriBigObservatory evn = new vriBigObservatory();
		  TelescopeReader tr = new TelescopeReader("evn.json");
		  LatLonTelescopeList tl = tr.getTelescopes();
		  
		  evn.zoomer = true; // override default
		  evn.antennas = tl;
		  evn.menu_name = "EVN";
		  evn.full_name = "EVN";
		  evn.lengthScale = 5e6;
		  // FIXME
		  evn.latitude = Math.toRadians(50);
		  evn.longitude = Math.toRadians(10);
		  evn.cfg_name = new String[]{"default"};
		  
		  observatories.put(evn.menu_name, evn);
		  
		  // Kn and Da are mostly used in combination with the MkII at Jodrell
		  // shanghai, atca ("ATCA01"), mopra, hobart, kashima, 
		  // tigo (using "TIGOCONC")
		  // , westford, 
		  // arecibo and metsahovi

		  // EVN: ira.cnr.it (Bologna=Medicina)
		  // Effelsburg, WSRT, Torun
		  // chalmers.se = Onsala
		  // bkg.bund.de = Wettzell(!)
		  // Metsähovi
		  // Also, Jodrell and Cambridge, with Kn as a possible extra if we feel Merlincasty
		  

		  // 2009-01-06
		  // WSRT, Effelsberg, Arecibo, Jodrell Bank, Cambridge, Onsala,
		  // Metsahovi, Torun, Tigo, Shanghai, Medicina, Urumqi,
		  // Westford, Kashima, Hobart, ATCA, Mopra.

		  vriBigObservatory iya = new vriBigObservatory();
		  TelescopeReader tr2 = new TelescopeReader("iya.json");
		  LatLonTelescopeList tl2 = tr2.getTelescopes();
		  
		  iya.antennas = tl2;
		  iya.menu_name = "IYA";
		  iya.full_name = "International Year of Astronomy Array";

		  double r_earth = 6.378137e6; // m
		  iya.lengthScale = 1.1*2*r_earth; // 10% bigger
		  // FIXME
		  iya.latitude = Math.toRadians(20);
		  iya.longitude = Math.toRadians(10);
		  iya.cfg_name = new String[]{"default"};
		  
		  observatories.put(iya.menu_name, iya);
	 }
	 
	 vriObservatory<?> select(String selection) {
		  // When a selection is made from the observatory pop-up menu,
		  // this method is called to load the values (from file or otherwise)
		  
		  System.out.println(String.format("Selected %s", selection));
		  vriObservatory<?> obs = observatories.get(selection);
		  return obs;
	 }	 
}
