package com.example.gr_dsa2_asgn2;

import java.util.*;

//BFS returns the shortes path - Fast - With fewest transfers



//Checks all the neighbouring lines at current depth before moving deeper
//will find the path with fewest stops

public class BFS {
    private StationGraph graph;

    public BFS(StationGraph graph) {
        this.graph = graph;
    }

    /**
     * Finds a valid route between two stations with fewest stops using BFS
     * @param start Name of starting station
     * @param end Name of destination station
     * @return List of stations representing the route (empty if no path exists)
     */
    public List<Station> findRoute(String start, String end) {
        // Input validation
        if (start == null || end == null) {
            return Collections.emptyList();
        }

        Station startStation = graph.getStation(start);
        Station endStation = graph.getStation(end);

        if (startStation == null || endStation == null) {
            return Collections.emptyList();
        }

        // Special case: start and end are the same
        if (startStation.equals(endStation)) {
            return Collections.singletonList(startStation);
        }

        // BFS setup
        Queue<Station> queue = new LinkedList<>();
        Map<Station, Station> previous = new HashMap<>(); // For path reconstruction
        Set<Station> visited = new HashSet<>();

        queue.add(startStation);
        visited.add(startStation);
        previous.put(startStation, null);

        // BFS algorithm
        while (!queue.isEmpty()) {
            Station current = queue.poll();

            // Check if we've reached the destination
            if (current.equals(endStation)) {
                return reconstructPath(previous, endStation);
            }

            // Explore all neighbors
            for (Connection conn : current.getConnections()) {
                Station neighbor = conn.getToStation();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        // No path found
        return Collections.emptyList();
    }

    private List<Station> reconstructPath(Map<Station, Station> previous, Station end) {
        List<Station> path = new LinkedList<>();
        Station current = end;

        while (current != null) {
            path.add(0, current); // Add to beginning to reverse the order
            current = previous.get(current);
        }

        return path;
    }
}