package nl.jive.vri;

import java.util.*;
import java.awt.geom.*;
import nl.jive.earth.*;

abstract class TList<T extends isVisible> extends ArrayList<T>  {
	 
	 HashMap<Baseline<T>, Integer[]> bl2ants = 
		  new HashMap<Baseline<T>, Integer[]>();
    
	 ArrayList<Baseline<T>> getBaselines() {
		  ArrayList<Baseline<T>> baselines = new ArrayList<Baseline<T>>();
		  for (int i = 0; i < (this.size()-1); i++) {
				T ant1 = this.get(i);
				for (int j = i+1; j < (this.size()); j++) {
					 T ant2 = this.get(j);
					 Baseline<T> bl = makeBaseline(ant1, ant2);
					 bl2ants.put(bl, new Integer[] {i, j});
					 baselines.add(bl);
				}
		  }
		  return baselines;
	 }

	 abstract Baseline<T> makeBaseline(T ant1, T ant2);

    boolean isAntennaInBaseline(int i, Baseline<T> bl) {
		  Integer[] ind = bl2ants.get(bl);
		  if (ind==null) {
				return false;
		  } else {
				return (ind[0] == i || ind[1] == i);
		  }
	 }
}

