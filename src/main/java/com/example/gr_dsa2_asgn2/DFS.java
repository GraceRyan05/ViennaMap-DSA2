package com.example.gr_dsa2_asgn2;

import java.util.*;

public class DFS {
    private StationGraph graph;

    public DFS(StationGraph graph) {
        this.graph = graph;
    }

    public List<List<Station>> findAllRoutes(String start, String end) {
        List<List<Station>> routes = new ArrayList<>();
        Set<Station> visited = new HashSet<>();
        Station startStation = graph.getStation(start);
        Station endStation = graph.getStation(end);

        if (startStation == null || endStation == null) {
            return routes; // return empty list if stations don't exist
        }

        dfsHelper(startStation, endStation, visited, new ArrayList<>(), routes);
        return routes;
    }

    private void dfsHelper(Station current, Station end, Set<Station> visited,
                           List<Station> path, List<List<Station>> routes) {
        visited.add(current);
        path.add(current);

        if (current.equals(end)) {
            routes.add(new ArrayList<>(path));
        } else {
            for (Connection conn : current.getConnections()) {
                Station neighbor = conn.getToStation(); // Changed from getStation() to getToStation()
                if (!visited.contains(neighbor)) {
                    dfsHelper(neighbor, end, visited, path, routes);
                }
            }
        }

        visited.remove(current);
        path.remove(path.size() - 1);
    }
}