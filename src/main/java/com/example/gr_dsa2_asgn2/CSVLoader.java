package com.example.gr_dsa2_asgn2;

import java.io.*;
import java.util.Scanner;
public class CSVLoader {
    public static void loadData(StationGraph graph, String filePath) throws IOException {
        System.out.println("[DEBUG] Loading data from: " + filePath);

        try (InputStream is = CSVLoader.class.getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            // Skip header
            String header = br.readLine();
            System.out.println("[DEBUG] CSV Header: " + header);

            // Process each line
            String line;
            int stationsAdded = 0;
            int connectionsAdded = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    // Split on comma (your CSV uses commas)
                    String[] parts = line.split(",");
                    if (parts.length < 4) {
                        System.err.println("[WARN] Skipping line - not enough columns: " + line);
                        continue;
                    }

                    String fromStation = parts[0].trim();
                    String toStation = parts[1].trim();
                    String lineName = parts[2].trim();
                    String lineColor = parts[3].trim();

                    // Add stations if they don't exist
                    if (graph.getStation(fromStation) == null) {
                        graph.addStation(fromStation, 0, 0); // Temporary coordinates
                        stationsAdded++;
                        System.out.println("[DEBUG] Added station: " + fromStation);
                    }
                    if (graph.getStation(toStation) == null) {
                        graph.addStation(toStation, 0, 0); // Temporary coordinates
                        stationsAdded++;
                        System.out.println("[DEBUG] Added station: " + toStation);
                    }

                    // Add connection (distance = 1.0 temporarily)
                    graph.addConnection(fromStation, toStation, lineName, 1.0);
                    connectionsAdded++;

                } catch (Exception e) {
                    System.err.println("[ERROR] Failed to process line: " + line);
                    e.printStackTrace();
                }
            }

            System.out.printf("[DEBUG] Loaded %d stations and %d connections%n",
                    stationsAdded, connectionsAdded);

        } catch (NullPointerException e) {
            System.err.println("[ERROR] File not found: " + filePath);
            throw new FileNotFoundException("CSV file not found: " + filePath);
        }
    }

    private static double calculateDistance(String station1, String station2) {
        // Implement your distance calculation here
        // You might need a separate mapping of stations to coordinates
        return 1.0; // Default distance
    }
}
