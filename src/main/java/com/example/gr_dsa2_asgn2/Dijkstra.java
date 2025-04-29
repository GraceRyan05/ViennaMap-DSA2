package com.example.gr_dsa2_asgn2;

import java.util.*;

public class Dijkstra {
    private StationGraph graph;

    public Dijkstra(StationGraph graph) {
        this.graph = graph;
    }

    public List<Station> shortestPath(String start, String end, double changePenalty) {
        // Reset all stations
        graph.getAllStations().forEach(station -> {
            station.setDistance(Double.MAX_VALUE);
            station.setPrevious(null);
        });

        PriorityQueue<Station> queue = new PriorityQueue<>(Comparator.comparingDouble(Station::getDistance));
        Map<Station, String> lineUsed = new HashMap<>();

        Station startStation = graph.getStation(start);
        Station endStation = graph.getStation(end);

        if (startStation == null || endStation == null) {
            return Collections.emptyList();
        }

        startStation.setDistance(0);
        queue.add(startStation);

        while (!queue.isEmpty()) {
            Station current = queue.poll();

            if (current.equals(endStation)) {
                return reconstructPath(endStation);
            }

            for (Connection conn : current.getConnections()) {
                Station neighbor = conn.getToStation();
                double newDistance = current.getDistance() + conn.getDistance();

                // Apply penalty if changing lines
                if (lineUsed.get(current) != null &&
                        !lineUsed.get(current).equals(conn.getLineName())) {
                    newDistance += changePenalty;
                }

                if (newDistance < neighbor.getDistance()) {
                    neighbor.setDistance(newDistance);
                    neighbor.setPrevious(current);
                    lineUsed.put(neighbor, conn.getLineName());

                    // Update priority queue
                    queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return Collections.emptyList();
    }

    private List<Station> reconstructPath(Station end) {
        List<Station> path = new ArrayList<>();
        for (Station at = end; at != null; at = at.getPrevious()) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}