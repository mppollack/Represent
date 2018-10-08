package com.iscool.michael.represent;

import java.io.Serializable;
import java.util.Random;

public class LatLong implements Serializable {

    /* This code is a modified version of a lat/long generator that I found at the following website:
    https://www.programcreek.com/java-api-examples/index.php?source_dir=simplelatlng-master/src/main/java/com/javadocmd/simplelatlng/LatLng.java
     */

    private static final long serialVersionUID = 7086953744720769418L;

    /**
     * Creates a random latitude and longitude. (Not inclusive of (-90, 0))
     *
     * @return the random LatLng.
     */
    public static LatLong random() {
        return random(new Random());
    }

    /**
     * Creates a random latitude and longitude. (Not inclusive of (-90, 0))
     *
     * @param r the random number generator to use, if you want to be
     *          specific or are creating many LatLngs at once.
     * @return the random LatLng.
     */
    public static LatLong random(Random r) {
        return new LatLong((r.nextDouble() * -180.0) + 90.0,
                (r.nextDouble() * -360.0) + 180.0);
    }

    /**
     * Tests whether two angles fall within the tolerance
     * allowed in. Ignores
     * NaN and infinite values, returning false in either case.
     *
     * @param degree1 one degree angle.
     * @param degree2 another degree angle.
     * @return true if they should be considered equal, false otherwise.
     */
    public static boolean degreesEqual(double degree1, double degree2) {
        if (Double.isNaN(degree1) || Double.isNaN(degree2))
            return false;
        if (Double.isInfinite(degree1) || Double.isInfinite(degree2))
            return false;
        return doubleToLong(degree1) == doubleToLong(degree2);
    }

    public static long doubleToLong(double value) {
        return (long) (value / 0.000001);
    }

    public static double longToDouble(long value) {
        return (double) value * 0.0000001;
    }

    private long latitude;
    private long longitude;

    /**
     * Creates a LatLng point.
     *
     * @param latitude  the latitude in degrees.
     * @param longitude the longitude in degrees.
     */
    public LatLong(double latitude, double longitude) {
        this.setLatitudeLongitude(latitude, longitude);
    }

    /**
     * Get latitude for this point in degrees.
     *
     * @return latitude in degrees.
     */
    public double getLatitude() {
        return longToDouble(latitude);
    }

    /**
     * Get the internal long representation of this point's latitude
     * in degrees. Intended for library use only.
     *
     * @return the internal representation of latitude in degrees.
     */
    public long getLatitudeInternal() {
        return latitude;
    }

    /**
     * Get longitude for this point in degrees.
     *
     * @return longitude in degrees.
     */
    public double getLongitude() {
        return longToDouble(longitude);
    }

    /**
     * Get the internal long representation of this point's longitude
     * in degrees. Intended for library use only.
     *
     * @return the internal representation of longitude in degrees.
     */
    public long getLongitudeInternal() {
        return longitude;
    }

    /**
     * Sets the latitude and longitude for this point.
     *
     * @param latitude  the latitude in degrees.
     * @param longitude the longitude in degrees.
     */
    public void setLatitudeLongitude(double latitude, double longitude) {
        this.setLatitude(latitude);
        if (Math.abs(this.latitude) == 90000000L) {
            // At the poles all longitudes intersect. Simplify for later comparison.
            this.setLongitude(0);
        } else {
            this.setLongitude(longitude);
        }
    }

    /**
     * Sets the latitude for this point.
     */
    private void setLatitude(double latitude) {
        double lat = normalizeLatitude(latitude);
        if (Double.isNaN(lat))
            throw new IllegalArgumentException("Invalid latitude given.");
        this.latitude = doubleToLong(lat);
    }

    /**
     * Sets the longitude for this point.
     */
    private void setLongitude(double longitude) {
        double lng = normalizeLongitude(longitude);
        if (Double.isNaN(lng))
            throw new IllegalArgumentException("Invalid longitude given.");
        this.longitude = doubleToLong(lng);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof LatLong))
            return false;

        LatLong latlng = (LatLong) obj;
        if (this.latitude != latlng.latitude) {
            return false;
        }

        return this.longitude == latlng.longitude;
    }



    public static double normalizeLatitude(double latitude) {
        if (Double.isNaN(latitude))
            return Double.NaN;
        if (latitude > 0) {
            return Math.min(latitude, 90.0);
        } else {
            return Math.max(latitude, -90.0);
        }
    }

    public static double normalizeLongitude(double longitude) {
        if (Double.isNaN(longitude) || Double.isInfinite(longitude))
            return Double.NaN;
        double longitudeResult = longitude % 360;
        if (longitudeResult > 180) {
            double diff = longitudeResult - 180;
            longitudeResult = -180 + diff;
        } else if (longitudeResult < -180) {
            double diff = longitudeResult + 180;
            longitudeResult = 180 + diff;
        }
        return longitudeResult;
    }
}
