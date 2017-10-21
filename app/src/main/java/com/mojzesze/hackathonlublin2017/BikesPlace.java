package com.mojzesze.hackathonlublin2017;

/**
 * Created by sebas on 21.10.2017.
 */

public class BikesPlace {
    private double lat;
    private double lng;
    private String name;
    private int bikes;
    private int freeRacks;

    public BikesPlace(double lat, double lng, String name, int bikes, int freeRacks) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.bikes = bikes;
        this.freeRacks = freeRacks;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getName() {
        return name;
    }

    public int getBikes() {
        return bikes;
    }

    public int getFreeRacks() {
        return freeRacks;
    }
}
