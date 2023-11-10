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
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.util.Date;
import java.util.*;
import java.beans.*;

public class vrijr extends vri
{

	 vrijr() {
		  super();
	 }

	 public static void main(String args[]) {
		  System.out.println("Standalone java program");
		  System.out.println("VRI Junior Edition");

		  JFrame f = new JFrame("VRIjr");
		  vrijr vriTest = new vrijr();
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

		  gbc.gridx = 0;
		  gbc.gridy = 0;
		  gbc.gridwidth = 3;
		  allImgPanel.add(new JLabel("Array"), gbc);
		  
		  arrDisp = new ArrayDisplays.vriFlatLatLonArrDisp(obs);
		  arrDisp.setGeometry(geom.geomap.get("IYA"));
        // FIXME: There must be a correct order to create these such that the reconstructed image comes out right.
        // Original image:
		  imgDisp = new vriImgDisp(this);
        // UV plot of original image:
		  UVpDisp = new vriUVpDisp(this);
        // UV coverage:
		  UVcDisp = new vriUVcDisp(obs, aux);
		  UVcDisp.setPlotScale(Scale.EARTH);
		  UVcCtrl= new vriUVcZoomChooser("?", UVcDisp);
        // Multiplied:
		  UVpConvDisp = new vriUVpDisp(this);
        // Reconstructed image:
        imgDisp2 = new vriImgDisp(this);

        gbc.gridx = 0;
		  gbc.gridy = 1;
		  gbc.gridwidth = 3;
		  allImgPanel.add(arrDisp, gbc);

		  gbc.gridx = 0;
		  gbc.gridy = 2;
		  gbc.gridwidth = 1;
		  allImgPanel.add(new JLabel("Source Image"), gbc);

        gbc.gridx = 1;
		  gbc.gridy = 2;
		  allImgPanel.add(new JLabel("Array UV Coverage"), gbc);

		  gbc.gridx = 2;
		  gbc.gridy = 2;
		  allImgPanel.add(new JLabel("Reconstructed Image"), gbc); 

        
        // Third row
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
		  gbc.gridy = 3;
		  allImgPanel.add(imgDisp, gbc);

		  gbc.gridx = 1;
		  gbc.gridy = 3;
		  allImgPanel.add(UVcDisp, gbc);
		  
		  
		  gbc.gridx = 2;
		  gbc.gridy = 3;
		  allImgPanel.add(imgDisp2, gbc);


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
		  UVpConvDisp.addPropertyChangeListener(imgDisp2); // -> fftconv
		  UVpDisp.addPropertyChangeListener(UVpConvDisp); // -> FFT
		  UVcDisp.addPropertyChangeListener(UVpConvDisp); // -> uvcov
		  imgDisp.addPropertyChangeListener(UVpDisp); // -> dat
		  imgDisp.addPropertyChangeListener(UVcDisp); // -> fftsize
		  arrDisp.addPropertyChangeListener(UVcDisp); // -> active antenna
	 }

	 public void init() {
		  System.out.println("VRIjr, Virtual Radio Interferometer");
		  setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		  setBackground(Color.lightGray);
		  obsman = new vriObservatoryManager();
		  obs = obsman.select("IYA");
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
		  if (imgDisp==null) {
				System.err.println("imgDisp doesn't really exist");
		  } else {
				System.err.println("imgDisp really isn't null!");
		  }
        
		  imgEdit.setImage("Medium double");
		  arrDisp.setObservatory(obs);
		  auxEdit.setObservatory(obs);

        setVisible(true);
	 }

	 void setArrDisp(vriObservatory<?> o) {
		  try {
                      System.err.println("Vrijr: Changing observatory");
                      UVcDisp.setObservatory(o);
                      arrDisp.setObservatory(o);
                      arrDisp.repaint();
		  } catch (java.lang.ClassCastException e) {
                      System.err.println("Tried to set a small obs in vrijr.");
		  }
	 }
}



