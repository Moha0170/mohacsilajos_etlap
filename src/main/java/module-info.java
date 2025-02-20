module com.etlap.mohacsilajos_etlap {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.etlap.mohacsilajos_etlap to javafx.fxml;
    exports com.etlap.mohacsilajos_etlap;
}