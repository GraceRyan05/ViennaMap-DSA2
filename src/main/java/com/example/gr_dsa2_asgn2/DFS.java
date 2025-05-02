package com.example.gr_dsa2_asgn2;

import java.util.*;

//DFS returns all possible paths - slow - Explores alternaive options

//follows each path as far as possible before backtracking
//will find all possible routes

public class DFS {
    private StationGraph graph;

    public DFS(StationGraph graph) {
        this.graph = graph;
    }

    /**
     * Finds all possible routes between two stations
     * @param start Name of starting station
     * @param end Name of destination station
     * @return List of all possible routes (each route is a list of stations)
     */
    public List<List<Station>> findAllRoutes(String start, String end) {
        List<List<Station>> allRoutes = new ArrayList<>();
        Station startStation = graph.getStation(start);
        Station endStation = graph.getStation(end);

        // Validate inputs
        if (startStation == null || endStation == null) {
            return allRoutes;
        }

        // Perform DFS search
        dfsSearch(startStation, endStation,
                new HashSet<>(), new ArrayList<>(), allRoutes);

        return allRoutes;
    }

    /**
     * Recursive DFS implementation
     */
    private void dfsSearch(Station current, Station end,
                           Set<Station> visited, List<Station> path,
                           List<List<Station>> routes) {
        // Mark current station as visited and add to path
        visited.add(current);
        path.add(current);

        // If we reached the destination, add this path to routes
        if (current.equals(end)) {
            routes.add(new ArrayList<>(path));
        } else {
            // Explore all connections
            for (Connection conn : current.getConnections()) {
                Station neighbor = conn.getToStation();

                // Only visit if not already visited
                if (!visited.contains(neighbor)) {
                    dfsSearch(neighbor, end, visited, path, routes);
                }
            }
        }

        // Backtrack
        visited.remove(current);
        path.remove(path.size() - 1);
    }

    /**
     * Finds all routes that pass through specified waypoints in order
     */
    public List<List<Station>> findAllRoutesWithWaypoints(String start, String end,
                                                          List<String> waypoints) {
        List<List<Station>> finalRoutes = new ArrayList<>();

        // If no waypoints, just find direct routes
        if (waypoints == null || waypoints.isEmpty()) {
            return findAllRoutes(start, end);
        }

        // Create full path including start, waypoints and end
        List<String> allPoints = new ArrayList<>();
        allPoints.add(start);
        allPoints.addAll(waypoints);
        allPoints.add(end);

        // Find routes between each consecutive pair of points
        List<List<List<Station>>> segmentRoutes = new ArrayList<>();
        for (int i = 0; i < allPoints.size() - 1; i++) {
            String segmentStart = allPoints.get(i);
            String segmentEnd = allPoints.get(i + 1);
            List<List<Station>> routes = findAllRoutes(segmentStart, segmentEnd);

            if (routes.isEmpty()) {
                return Collections.emptyList(); // No route exists for this segment
            }
            segmentRoutes.add(routes);
        }

        // Combine all possible segment combinations
        combineRoutes(segmentRoutes, 0, new ArrayList<>(), finalRoutes);
        return finalRoutes;
    }

    /**
     * Combines route segments recursively to form complete routes
     */
    private void combineRoutes(List<List<List<Station>>> segmentRoutes, int index,
                               List<Station> currentPath, List<List<Station>> finalRoutes) {
        if (index == segmentRoutes.size()) {
            finalRoutes.add(new ArrayList<>(currentPath));
            return;
        }

        for (List<Station> segment : segmentRoutes.get(index)) {
            List<Station> newPath = new ArrayList<>(currentPath);

            // Skip the first station of segment (same as last station of previous segment)
            if (!newPath.isEmpty()) {
                newPath.addAll(segment.subList(1, segment.size()));
            } else {
                newPath.addAll(segment);
            }

            combineRoutes(segmentRoutes, index + 1, newPath, finalRoutes);
        }
    }
}