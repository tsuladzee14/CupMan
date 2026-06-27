module com.example.cupMan {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javafx.media;


    opens com.example.cupMan to javafx.fxml;
    exports com.example.cupMan;
}