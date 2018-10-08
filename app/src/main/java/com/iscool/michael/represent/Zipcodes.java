package com.iscool.michael.represent;

public class Zipcodes {

    private static int length = 41335;
    private static String[] zipcodes;

    public static String generateRandom() {
        int index = (int) Math.random()*length;
        return zipcodes[index];
    }

}
