module org.example.petshelterv0 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires org.json;
    requires java.sql;


    opens org.example.petshelterv0 to javafx.fxml;
    exports org.example.petshelterv0;
}