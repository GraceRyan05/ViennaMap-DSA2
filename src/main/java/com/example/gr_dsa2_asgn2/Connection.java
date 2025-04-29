package com.example.gr_dsa2_asgn2;

public class Connection {
    private Station toStation;
    private String lineName;
    private double distance;

    public Connection(Station toStation, String lineName, double distance) {
        this.toStation = toStation;
        this.lineName = lineName;
        this.distance = distance;
    }

    // Getters
    public Station getToStation() { return toStation; }
    public String getLineName() { return lineName; }
    public double getDistance() { return distance; }
}