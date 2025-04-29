package com.example.gr_dsa2_asgn2;

import java.util.*;

public class BFS {
    private StationGraph graph;

    public BFS(StationGraph graph) {
        this.graph = graph;
    }

    public List<Station> findShortestRoute(String start, String end) {
        Queue<Station> queue = new LinkedList<>();
        Map<Station, Station> previous = new HashMap<>();
        Station startStation = graph.getStation(start);
        Station endStation = graph.getStation(end);

        queue.add(startStation);
        previous.put(startStation, null);

        while (!queue.isEmpty()) {
            Station current = queue.poll();

            if (current.equals(endStation)) {
                return reconstructPath(previous, endStation);
            }

            for (Connection conn : current.getConnections()) {
                Station neighbor = conn.getToStation();
                if (!previous.containsKey(neighbor)) {
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return Collections.emptyList();
    }

    private List<Station> reconstructPath(Map<Station, Station> previous, Station end) {
        List<Station> path = new ArrayList<>();
        for (Station at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}