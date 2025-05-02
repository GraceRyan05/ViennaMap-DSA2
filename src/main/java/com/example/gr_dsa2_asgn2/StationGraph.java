package com.example.gr_dsa2_asgn2;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.*;

//Manages the entire network for the lines



public class StationGraph {
    private Map<String, Station> stations;
    private Map<String, String> nameMapping; // For case-insensitive lookup

    public StationGraph() {
        stations = new HashMap<>();
        nameMapping = new HashMap<>();
    }

    public void addStation(String name, double latitude, double longitude) {
        String normalized = name.trim().toUpperCase();
        if (!stations.containsKey(normalized)) {
            Station station = new Station(name, latitude, longitude);
            stations.put(normalized, station);
            nameMapping.put(normalized, name); // Preserve original case
            System.out.println("Added station: " + name); // Debug output
        }
    }

    public void addConnection(String from, String to, String line, double distance) {
        String fromKey = from.trim().toUpperCase();
        String toKey = to.trim().toUpperCase();

        Station fromStation = stations.get(fromKey);
        Station toStation = stations.get(toKey);

        if (fromStation != null && toStation != null) {
            fromStation.addConnection(new Connection(toStation, line, distance));
            toStation.addConnection(new Connection(fromStation, line, distance));
            System.out.println("Added connection: " + from + " <-> " + to); // Debug
        } else {
            System.err.println("Could not find stations for connection: "
                    + from + " to " + to);
        }
    }

    public Station getStation(String name) {
        return stations.get(name.trim().toUpperCase());
    }

    public List<Station> getAllStations() {
        return new ArrayList<>(stations.values());
    }
}