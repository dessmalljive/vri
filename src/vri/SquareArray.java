package nl.jive.vri;

class SquareArray {
    int size;
    float data[];

    public SquareArray(int i) {
        size = i;
        data = new float[size*size];
    }
    public SquareArray(int i, float[] d) {
        size = i;
        data = d;
    }

    public void set(int i, int j, float val) {
        data[j*size+i] = val;
    }

    public float get(int i, int j) {
        return data[j*size+i];
    }

    float[] minmax() {
        float min = data[0];
        float max = data[0];
        for (int i=1; i < data.length; i++) {
            if (data[i] < min) min = data[i];
            if (data[i] > max) max = data[i];
        }
        float[] res = {min, max};
        return res;
    }

    void scale() {
        float[] minmaxv = minmax();
        float min = minmaxv[0];
        float max = minmaxv[1];
        int mean=0;
        for (int i = 0; i < data.length; i++) mean += data[i];
        // System.err.print("Limits = ["+min+","+max+"], sum = "+mean);
        mean /= (float) data.length;
        // System.err.print(", mean = "+mean);
        for (int i = 0; i < data.length; i++) {
            data[i] = (data[i] - mean) / (max - min);
        }
    }

    public PixArray toPix() {
        // System.err.println("Converting (datToPix)");
        // determine the scale of the data
        float[] minmaxv = vriUtils.minmax(data);
        float min = minmaxv[0];
        float max = minmaxv[1];

        PixArray pix = new PixArray(size, size);
        for (int y = 0; y < size; y++) {
            for  (int x = 0; x < size; x++) {
                int x1, y1;
                x1 = x; y1 = y;
                int grey = (int) (255 * (get(x1, y1) - min) / (max - min));
                pix.set(x, y, 0xff000000 | (grey << 16) | (grey << 8) | grey);
            }
        }
        return pix;
    }
}
