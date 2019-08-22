package nl.jive.vri;

import java.applet.Applet;
import java.lang.Math;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.net.*;
import javax.swing.*;
import edu.emory.mathcs.jtransforms.fft.*;
import java.util.Arrays;

class FFTArray {
    int size;
    float data[];

    public FFTArray(int i) {
        size = i;
        data = new float[2*size*size];
    }

    static FFTArray fromSquareArray(SquareArray sa) {
        FFTArray result = new FFTArray(sa.size);
        for (int i=0; i<sa.size; i++) {
            for (int j=0; j<sa.size; j++) {
                result.set(i, j, sa.get(i, j), 0);
            }
        }
        return result;
    }

    public FFTArray(int i, float[] d) {
        size = i;
        data = d;
    }

    public void set(int i, int j, float real, float im) {
        data[j*size*2 + i*2] = real;
        data[j*size*2 + i*2 + 1] = im;
    }

    public float getReal(int i, int j) {
        return data[j*size*2 + i*2];
    }
    
    public float getImag(int i, int j) {
        return data[j*size*2 + i*2 + 1];
    }

    public float getPhase(int i, int j) {
        return (float) Math.atan2(data[j*size*2+i*2],data[j*size*2+i*2+1]);
    }

    public float getAmpl(int i, int j) {
        return (float) Math.sqrt(data[j*size*2+i*2] * data[j*size*2+i*2] +
                                 data[j*size*2+i*2+1] * data[j*size*2+i*2+1]);
    }

    public FFTArray fft() {
        // System.err.print("FFTArray: Doing NEW forward transform... ");
        float[] fft = Arrays.copyOf(data, data.length);
        FloatFFT_2D f = new FloatFFT_2D(size, size);
        f.complexForward(fft);
        return new FFTArray(size, fft);
    }

    public FFTArray invfft() {
        float[] dat2 = new float[data.length];
        for (int i = 0; i < dat2.length; i++) {
            dat2[i] = data[i]/size/size;
        }
        FloatFFT_2D f = new FloatFFT_2D(size, size);
        f.complexInverse(dat2, false);
        return new FFTArray(size, dat2);
    }

    public FFTArray multiply(SquareArray covArray) {
        // The FFT runs from 0 to 2*pi in both dimensions;
        // the coverage array is centred at (0,0) in the UV plane.
        //
        // So we shift the (source and target) FFT arrays in the loop
        // and leave the covariant indices alone.
        int s1 = covArray.size;
        if (s1 != size) {
            System.err.println("FFTArray: trying to multiply covArray of size " + s1 + " by FFT array of size " + size);
        } 
        System.err.println("FFTArray: data array length: " + size);
        System.err.println("FFTArray: data array length: " + data.length);
        float[] cov = covArray.data;
        float[] fft2 = new float[2*size*size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int x1 = (x >= size/2) ? x-size/2 : x+size/2;
                int y1 = (y >= size/2) ? y-size/2 : y+size/2;
                try {
                    fft2[y1*size*2 + x1*2] =  
                        data[y1*size*2 + x1*2] * cov[y*size+x];  // Real
                    fft2[y1*size*2 + x1*2 + 1] = 
                        data[y1*size*2 + x1*2 + 1]*cov[y*size+x];  // Imaginary
                } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("FFTArray: size: " + size);
                        System.err.println("FFTArray: x1, y1: " + x1 + "," + y1);
                        System.err.println("FFTArray: x, y: " + x + "," + y);
                        throw e;
                    }                        
            }
        }
        FFTArray fftconv = new FFTArray(size, fft2);
        return fftconv;
    }    

    public SquareArray extractReals() {
        SquareArray sa = new SquareArray(size);
        for (int i=0; i < size; i++) {
            for (int j=0; j<size; j++) {
                sa.set(i, j, getReal(i,j));
            }
        }
        return sa;
    }
}


