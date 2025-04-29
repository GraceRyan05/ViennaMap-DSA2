package com.example.gr_dsa2_asgn2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.*;
import java.util.stream.Collectors;

//main class
public class HelloController {
    @FXML private ComboBox<String> startComboBox;
    @FXML private ComboBox<String> endComboBox;
    @FXML private ListView<String> waypointsList;
    @FXML private ListView<String> avoidList;
    @FXML private Slider penaltySlider;
    @FXML private Button findRouteButton;
    @FXML private Button clearButton;
    @FXML private ImageView mapView;
    @FXML private Canvas routeCanvas;
    @FXML private TextArea resultArea;

    private StationGraph graph;
    private Map<String, Color> lineColors = Map.of(
            "1", Color.RED,
            "2", Color.PURPLE,
            "3", Color.ORANGE,
            "4", Color.GREEN,
            "6", Color.BROWN
    );

    // Constants for map scaling
    private static final double MIN_LONGITUDE = 16.2;
    private static final double MAX_LONGITUDE = 16.5;
    private static final double MIN_LATITUDE = 48.1;
    private static final double MAX_LATITUDE = 48.3;

    public void initialize() throws IOException {
        // Initialize map and graph
        mapView.setImage(new Image(getClass().getResourceAsStream("/vienna_ubahn_map.png")));
        graph = new StationGraph();

        // Setup canvas transparency
        routeCanvas.getGraphicsContext2D().setFill(new Color(0, 0, 0, 0.1));

        // Load data and setup UI
        loadCSVData();
        setupComboBoxes();
        setupRouteListView();
        setupButtons();
    }

    private void loadCSVData() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/ubahn_data.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            // Skip header
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String from = parts[0].trim();
                    String to = parts[1].trim();
                    String lineName = parts[2].trim();
                    double distance = 1.0; // Default distance

                    // Add stations if they don't exist
                    if (graph.getStation(from) == null) {
                        graph.addStation(from, getRandomLatitude(), getRandomLongitude());
                    }
                    if (graph.getStation(to) == null) {
                        graph.addStation(to, getRandomLatitude(), getRandomLongitude());
                    }

                    // Add connection
                    graph.addConnection(from, to, lineName, distance);
                }
            }
        }
    }

    // Helper methods for generating test coordinates
    private double getRandomLatitude() {
        return MIN_LATITUDE + (Math.random() * (MAX_LATITUDE - MIN_LATITUDE));
    }

    private double getRandomLongitude() {
        return MIN_LONGITUDE + (Math.random() * (MAX_LONGITUDE - MIN_LONGITUDE));
    }

    private void setupComboBoxes() {
        List<String> stationNames = graph.getAllStations().stream()
                .map(Station::getName)
                .sorted()
                .collect(Collectors.toList());

        startComboBox.getItems().addAll(stationNames);
        endComboBox.getItems().addAll(stationNames);
    }

    private void setupRouteListView() {
        waypointsList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    // Color code by line
                    if (item.contains("Line 1")) setStyle("-fx-text-fill: red;");
                    else if (item.contains("Line 2")) setStyle("-fx-text-fill: purple;");
                    else if (item.contains("Line 3")) setStyle("-fx-text-fill: orange;");
                    else if (item.contains("Line 4")) setStyle("-fx-text-fill: green;");
                    else if (item.contains("Line 6")) setStyle("-fx-text-fill: brown;");
                    else if (item.contains("TRANSFER")) {
                        setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupButtons() {
        findRouteButton.setOnAction(e -> findRoute());
        clearButton.setOnAction(e -> clearRoute());
    }

    @FXML
    private void findRoute() {
        String start = startComboBox.getValue();
        String end = endComboBox.getValue();

        if (start == null || end == null) {
            resultArea.setText("Please select both start and end stations");
            return;
        }

        Dijkstra dijkstra = new Dijkstra(graph);
        List<Station> route = dijkstra.shortestPath(start, end, penaltySlider.getValue());

        if (route.isEmpty()) {
            resultArea.setText("No route found between " + start + " and " + end);
            return;
        }

        displayRouteDetails(route);
        drawRouteOnMap(route);
    }

    private void displayRouteDetails(List<Station> route) {
        waypointsList.getItems().clear();
        resultArea.clear();

        StringBuilder routeSummary = new StringBuilder();
        routeSummary.append("Route from ").append(route.get(0).getName())
                .append(" to ").append(route.get(route.size()-1).getName())
                .append(":\n\n");

        String currentLine = null;
        double totalTime = 0;
        int transferCount = 0;

        for (int i = 0; i < route.size() - 1; i++) {
            Station current = route.get(i);
            Station next = route.get(i+1);
            Connection connection = findConnection(current, next);

            if (connection != null) {
                // Check for line change
                if (currentLine != null && !currentLine.equals(connection.getLineName())) {
                    String transferMsg = "TRANSFER at " + current.getName() + " to Line " + connection.getLineName();
                    waypointsList.getItems().add(transferMsg);
                    routeSummary.append(transferMsg).append("\n");
                    transferCount++;
                }

                currentLine = connection.getLineName();
                totalTime += connection.getDistance();

                String connectionInfo = String.format("%s → %s (Line %s, %.1f min)",
                        current.getName(), next.getName(), connection.getLineName(), connection.getDistance());

                waypointsList.getItems().add(connectionInfo);
                routeSummary.append(connectionInfo).append("\n");
            }
        }

        routeSummary.append("\nTotal journey time: ").append(String.format("%.1f", totalTime)).append(" minutes\n");
        routeSummary.append("Number of transfers: ").append(transferCount);

        resultArea.setText(routeSummary.toString());
    }

    private Connection findConnection(Station from, Station to) {
        return from.getConnections().stream()
                .filter(conn -> conn.getToStation().equals(to))
                .findFirst()
                .orElse(null);
    }

    private void drawRouteOnMap(List<Station> route) {
        GraphicsContext gc = routeCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, routeCanvas.getWidth(), routeCanvas.getHeight());

        // Draw connections between stations
        for (int i = 0; i < route.size() - 1; i++) {
            Station current = route.get(i);
            Station next = route.get(i + 1);
            Connection conn = findConnection(current, next);

            if (conn != null) {
                // Set line color based on metro line
                gc.setStroke(lineColors.getOrDefault(conn.getLineName(), Color.BLACK));
                gc.setLineWidth(4);

                double x1 = scaleLongitudeToX(current.getLongitude());
                double y1 = scaleLatitudeToY(current.getLatitude());
                double x2 = scaleLongitudeToX(next.getLongitude());
                double y2 = scaleLatitudeToY(next.getLatitude());

                gc.strokeLine(x1, y1, x2, y2);
            }
        }

        // Draw all station markers
        gc.setFill(Color.BLUE);
        for (Station station : route) {
            drawStationMarker(station, gc);
        }

        // Highlight start and end stations
        if (!route.isEmpty()) {
            drawSpecialStation(route.get(0), Color.GREEN, "START");
            drawSpecialStation(route.get(route.size() - 1), Color.RED, "END");
        }
    }

    private void drawStationMarker(Station station, GraphicsContext gc) {
        double x = scaleLongitudeToX(station.getLongitude());
        double y = scaleLatitudeToY(station.getLatitude());

        // Draw station circle
        gc.fillOval(x - 5, y - 5, 10, 10);

        // Draw outer ring
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeOval(x - 6, y - 6, 12, 12);
    }

    private void drawSpecialStation(Station station, Color color, String label) {
        GraphicsContext gc = routeCanvas.getGraphicsContext2D();
        double x = scaleLongitudeToX(station.getLongitude());
        double y = scaleLatitudeToY(station.getLatitude());

        // Draw larger circle
        gc.setFill(color);
        gc.fillOval(x - 8, y - 8, 16, 16);

        // Draw label
        gc.setFill(Color.BLACK);
        gc.fillText(label, x + 10, y);
    }

    private void clearRoute() {
        waypointsList.getItems().clear();
        resultArea.clear();
        routeCanvas.getGraphicsContext2D().clearRect(0, 0, routeCanvas.getWidth(), routeCanvas.getHeight());
    }

    private double scaleLongitudeToX(double longitude) {
        return ((longitude - MIN_LONGITUDE) / (MAX_LONGITUDE - MIN_LONGITUDE)) * routeCanvas.getWidth();
    }

    private double scaleLatitudeToY(double latitude) {
        return routeCanvas.getHeight() - ((latitude - MIN_LATITUDE) / (MAX_LATITUDE - MIN_LATITUDE)) * routeCanvas.getHeight();
    }
}