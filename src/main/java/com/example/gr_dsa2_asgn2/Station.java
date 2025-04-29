package com.example.gr_dsa2_asgn2;

import java.util.ArrayList;
import java.util.List;

public class Station {
    private String name;
    private double latitude;
    private double longitude;
    private List<Connection> connections;
    private double distance; // For Dijkstra's algorithm
    private Station previous; // For path reconstruction

    public Station(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.connections = new ArrayList<>();
        this.distance = Double.MAX_VALUE; // Initialize to infinity
        this.previous = null;
    }

    // Getters and setters
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public List<Connection> getConnections() { return new ArrayList<>(connections); }
    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
    public Station getPrevious() { return previous; }
    public void setPrevious(Station previous) { this.previous = previous; }

    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Station station = (Station) obj;
        return name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}