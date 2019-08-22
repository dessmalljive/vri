package nl.jive.vri;

import java.awt.Color;


class vriUtils {
   public static double logn(double x, double n) {
        return Math.log(x)/Math.log(n);
    }

    static float[] minmax(float arr[]) {
        float min = arr[0];
        float max = arr[0];
        for (int i=1; i<arr.length; i++) {
            if (arr[i] < min) min = arr[i];
            if (arr[i] > max) max = arr[i];
        }
        float[] res = {min, max};
        return res;
    }

    static public int getImsize(int imh, int imw) {
        // Determine minimum sized box that image could fit into
        int imsize = Math.max(imw, imh);
        int i = (int)logn(imsize, 2);
        if (i>=10) { // Abandon exercise if it is too big
            System.err.println("Error: image too large (max = 1024x1024)");
            return 0;
        } else {
            imsize = (int) Math.pow(2,i);
        }
        return imsize;
    }


    static PixArray fftToPix(FFTArray fft, vriGreyDisp.PlotTypes type) {
        float value;        // Quantity that is plotted to a pixel
        // determine the scale of the data (use a forced -180:180 range for phase
        int size = fft.size;
        float[] minmaxv = vriUtils.minmax(fft.data);
        float min = minmaxv[0];
        float max = minmaxv[1];
        if (type == vriGreyDisp.PlotTypes.PHASE) {
            min = (float)(-Math.PI / 2.0);
            max = (float)( Math.PI / 2.0);
        }

        PixArray pix = new PixArray(size, size);
        for (int y = 0; y < size; y++) {
            for  (int x = 0; x < size; x++) {
                int x1, y1;
                // shift origin to center of image
                x1 = (x + size/2) % size;
                y1 = (y + size/2) % size;

                // Depending on the "type" of display selected, we extract the 
                // relevant components of the fourier transform.
                switch (type) {
                case REAL: 
                    value = fft.getReal(x1, y1);
                    break;
                case IMAG:
                    value = fft.getImag(x1, y1);
                    break;
                case PHASE:
                    value = fft.getPhase(x1, y1); 
                    break;
                default:  // Used for Ampl., Colour and others that aren't handled
                    value = fft.getAmpl(x1, y1);
                }

                int grey = (int) (255.0 * (value - min) / (max - min) );
                pix.set(x, y, 0xff000000 | (grey << 16) | (grey << 8) | grey);

                if (type.equals("Colour")) {
                    double h = fft.getPhase(x1, y1) / Math.PI / 2.0;
                    if (h < 0.0) h += 1.0;
                    pix.set(x, y, Color.HSBtoRGB( (float) h, (float) 1.0,
                                                  (float) grey/(float)255.0));
                }  // End if(colour)
            }  // End for(x)
        }  // End for(y)
        return pix;
    }  
}

