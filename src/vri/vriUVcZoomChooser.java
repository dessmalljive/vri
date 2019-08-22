/**
 * @(#)vri.java V.R.I.
 *
 * The Virtual Radio Interferometer
 *
 * This is a java applet (also runnable as a standalone java program) which
 * allows the simulation of various aspects of a radio interferometer.
 *
 * uvTest.java
 *
 * v1.0  05/Dec/1996  Derek McKay
 * v1.1  12/Dec/1996  Derek McKay & Nuria McKay
 * v1.2  13/Dec/1996  Derek McKay & Nuria McKay
 * v1.3  26/Dec/1996  Derek McKay, Mark Wieringa & Nuria McKay
 *
 * vri.java
 *
 * v2.0 06/Jan/1997   Derek McKay
 * v2.1 17/Mar/1997   Derek McKay
 * v2.2 07/Apr/1997   Derek McKay & Nuria McKay
 * v2.3 29/Jul/1997   Derek McKay & Nuria McKay
 * v2.4 10/Sep/1997   Nuria McKay (removed grid for ADASS)
 * v2.5 11/Sep/1997   Derek McKay (added new ATCA stations for JLC)
 *
 */


// http://jop46.jive.nl:8080/VRI/vri.jnlp

package nl.jive.vri;

import java.lang.*;
import java.lang.Math;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.applet.Applet;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.util.Date;
import java.util.*;
import java.beans.*;
import nl.jive.earth.Component;

class vriUVcZoomChooser extends JPanel {
	 vriUVcDisp disp;
	 Scale current;

	 void setDisplay(vriUVcDisp d) {
		  disp = d;
	 }

	 public vriUVcZoomChooser(String s, vriUVcDisp d) {
		  disp = d;
		  current = Scale.SPACE;
		  
		  setLayout(new GridLayout(0, 1));
		  String arrayString = "Array scale";
		  String earthString = "Earth scale";
		  String spaceString = "Source scale";


		  JRadioButton arrayButton = new JRadioButton(arrayString);
		  add(arrayButton);
		  JRadioButton earthButton = new JRadioButton(earthString);
		  add(earthButton);
		  JRadioButton spaceButton = new JRadioButton(spaceString);
		  spaceButton.setSelected(true);
		  add(spaceButton);

		  ButtonGroup group = new ButtonGroup();
		  group.add(arrayButton);
		  group.add(earthButton);
		  group.add(spaceButton);

		  arrayButton.addActionListener(new ActionListener() 
				{
					 public void actionPerformed(ActionEvent ae) {
						  System.err.println("Array button pressed");
						  current = Scale.ARRAY;
						  disp.setPlotScale(current);
						  disp.repaint();
					 }
				});
		  earthButton.addActionListener(new ActionListener() 
				{
					 public void actionPerformed(ActionEvent ae) {
						  System.err.println("Earth button pressed");
						  current = Scale.EARTH;
						  disp.setPlotScale(current);
						  disp.repaint();
					 }
				});
		  spaceButton.addActionListener(new ActionListener() 
				{
					 public void actionPerformed(ActionEvent ae) {
						  System.err.println("Space button pressed");
						  current = Scale.SPACE;
						  disp.setPlotScale(current);
						  disp.repaint();
					 }
				});
	 }
}
