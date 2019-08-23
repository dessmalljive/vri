package nl.jive.vri;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

class PixArray {
    int height, width;
    int[] data;
    
    public PixArray(int i, int j) {
        width=i;
        height=j;
        data = new int[width*height];
    }

    public void set(int i, int j, int val) {
        data[j*width+i] = val;
    }

    public int get(int i, int j) {
        return data[j*width+i];
    }

    public Image toImage(JApplet applet, int size) {
        return applet.createImage(new MemoryImageSource(width, height, data, 0, size));
    }

    public float meanEdge() {
        float mean = 0;
        int count = 0;
        for (int h = 0; h < height; h++) {
            int inc = width-1;
            if (h == 0 || h == (height-1)) inc = 1;
            for (int w = 0; w < width; w += inc) {
                mean += (float)(get(w, h) & 0x000000ff);  
                // Assume already greyscale -
                // i.e. red = green = blue
                count++;                                       
            }
        }
        mean /= (float)count;
        return mean;
    }

    public SquareArray toDat() {
        int size = vriUtils.getImsize(height, width);
        // find mean value along the edge (to make the padding realistic)
        float mean = meanEdge();
        SquareArray dat = new SquareArray(size);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                // Re-centre image.
                int i = (size/2 + w - width/2);
                int j = (size/2 + h - height/2);
                dat.set(i, j, (get(w, h) & 0x000000ff)-mean);
            }
        }
        return dat;
    }

    public void makeGrey() {
        // System.err.println("PixArray::greyPix: "+height+"x"+width);
        for (int i=0; i<height*width; i++) {
            int red   = (data[i] & 0x00ff0000) >> 16;
            int green = (data[i] & 0x0000ff00 >> 8);
            int blue  = (data[i] & 0x000000ff);
            int grey  = (red+green+blue)/3;
            data[i] = (data[i] & 0xff000000) | (grey << 16) | (grey << 8) | grey;
        }
    }
}

