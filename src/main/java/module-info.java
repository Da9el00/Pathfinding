module com.example.astarpathfinding {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.astarpathfinding to javafx.fxml;
    exports com.example.astarpathfinding;
}