package io.geobigdata.muddy.services.model;


import java.io.Serializable;

public class BoundingBox implements Serializable {
    private Double upperLeftLatitude;
    private Double upperLeftLongitude;

    private Double lowerRightLatitude;
    private Double lowerRightLongitude;

    public Double getUpperLeftLatitude() {
        return upperLeftLatitude;
    }

    public void setUpperLeftLatitude(Double upperLeftLatitude) {
        this.upperLeftLatitude = upperLeftLatitude;
    }

    public Double getUpperLeftLongitude() {
        return upperLeftLongitude;
    }

    public void setUpperLeftLongitude(Double upperLeftLongitude) {
        this.upperLeftLongitude = upperLeftLongitude;
    }

    public Double getLowerRightLatitude() {
        return lowerRightLatitude;
    }

    public void setLowerRightLatitude(Double lowerRightLatitude) {
        this.lowerRightLatitude = lowerRightLatitude;
    }

    public Double getLowerRightLongitude() {
        return lowerRightLongitude;
    }

    public void setLowerRightLongitude(Double lowerRightLongitude) {
        this.lowerRightLongitude = lowerRightLongitude;
    }

    @Override
    public String toString() {
        return "BoundingBox {" +
                "upperLeftLatitude=" + upperLeftLatitude +
                ", upperLeftLongitude=" + upperLeftLongitude +
                ", lowerRightLatitude=" + lowerRightLatitude +
                ", lowerRightLongitude=" + lowerRightLongitude +
                '}';
    }
}
