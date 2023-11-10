/**
	VRI jr, a kid's version of the Big VRI.
 **/


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

public class vriSource extends vri
{

	 vriSource() {
		  super();
	 }

	 public static void main(String args[]) {
		  System.out.println("Standalone java program");
		  System.out.println("VRI Source Edition");

		  JFrame f = new JFrame("VRI Source");
		  vriSource vriTest = new vriSource();
		  vriTest.init();
		  f.add("Center", vriTest);
		  f.setSize(900, 800);
		  f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  f.setVisible(true);
	 }


	 void makeImagePane() {
		  allImgPanel = new JPanel();
		  GridBagLayout gbl = new GridBagLayout();
		  GridBagConstraints gbc = new GridBagConstraints();
		  allImgPanel.setLayout(gbl);

		  gbc.gridwidth = 1;

		  gbc.gridx = 0;
		  gbc.gridy = 0;
		  allImgPanel.add(new JLabel("Source Image"), gbc);

		  imgDisp = new vriImgDisp(this);
		  gbc.gridx = 0;
		  gbc.gridy = 1;
		  allImgPanel.add(imgDisp, gbc);

		  gbc.gridx = 0;
		  gbc.gridy = 2;
		  allImgPanel.add(new JLabel("UV image"), gbc);

		  UVpDisp = new vriUVpDisp(this);
        gbc.gridx = 0;
        gbc.gridy = 3;
        allImgPanel.add(UVpDisp, gbc);

		  // End of gridbagging

        // things that we don't use but have to have
        // because WidgetManager connects them to each other.
		  arrDisp = new ArrayDisplays.vriFlatLatLonArrDisp(obs);
		  arrDisp.setGeometry(geom.geomap.get("IYA"));
		  imgDisp2 = new vriImgDisp(this);
		  UVpConvDisp = new vriUVpDisp(this);
		  UVcDisp = new vriUVcDisp(obs, aux);

		  add(allImgPanel);

	 }

	 void makeControls() {
		  JPanel obsEditPane = new JPanel();
		  obsEditPane.add(imgEdit);
		  obsEditPane.add(obsEdit);
		  obsEditPane.add(auxEdit);
		  add(obsEditPane);
	 }

 	 void makeListeners() {
		  imgDisp.addPropertyChangeListener(UVpDisp); // -> dat
	 }

	 public void init() {
		  System.out.println("VRI Source, Virtual Radio Interferometer");
		  setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		  setBackground(Color.lightGray);
		  obsman = new vriObservatoryManager();
		  obs = obsman.select("EVN");
		  aux = new vriAuxiliary();

		  obsEdit = new WidgetEditors.vriObsEdit(this, new String[] {"IYA", "EVN"}); 
		  imgEdit = new WidgetEditors.vriImgEdit(this); 
		  auxEdit = new WidgetEditors.vriAuxEditJr(this, aux, obs);

		  String obsname = obsEdit.getObservatory();
		  String imgname = imgEdit.getImage();

		  // Place the components on the screen
		  
		  makeControls();
		  makeImagePane();
		  makeListeners();

		  arrDisp.setObservatory(obs);
		  // new
		  auxEdit.setObservatory(obs);

		  if (imgDisp==null) {
				System.err.println("imgDisp doesn't really exist");
		  } else {
				System.err.println("imgDisp really isn't null!");
		  }
		  imgEdit.setImage("Medium double");
		  setVisible(true);
	 }

    
	 void setArrDisp(vriObservatory<?> o) {
		  try {
            System.err.println("Vrijr: Changing observatory");
            arrDisp.setObservatory(o);
            arrDisp.repaint();
		  } catch (java.lang.ClassCastException e) {
            System.err.println("Tried to set a small obs in vrijr.");
		  }
	 }
}



