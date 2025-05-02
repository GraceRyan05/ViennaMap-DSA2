package com.example.gr_dsa2_asgn2;

import java.util.ArrayList;
import java.util.List;

//Represents a physical station

//Fields:
//Connections - List of routes to other stations
//distance/previous - used by algorithms
//xcord/ycord - screen coords for visualising
public class Station {
    private String name;
    private double latitude;
    private double longitude;
    private List<Connection> connections;
    private double distance; // For Dijkstra's algorithm
    private Station previous; // For path reconstruction

    private double xCoord; //pixel x coordinate on map
    private double yCoord; //pixel y coordinate on map

    public Station(String name, double xCoord, double yCoord) {
        this.name = name;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.connections = new ArrayList<>();
        this.distance = Double.MAX_VALUE; // Initialize to infinity
        this.previous = null;
    }

    // Getters and setters

    public double getXCoord(){ return xCoord; }
    public double getYCoord() {return yCoord; }
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