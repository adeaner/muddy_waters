package io.geobigdata.muddy;


import org.apache.commons.math3.ml.clustering.Clusterable;

public class ClusterablePixel implements Clusterable {

    private double[] pixel = null;

    public ClusterablePixel(double[] pixel) {
        this.pixel = pixel;
    }

    @Override
    public double[] getPoint() {
        return pixel;
    }

}