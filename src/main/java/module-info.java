module com.example.gr_dsa2_asgn2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gr_dsa2_asgn2 to javafx.fxml;
    exports com.example.gr_dsa2_asgn2;
}