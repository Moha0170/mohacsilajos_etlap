package com.etlap.mohacsilajos_etlap;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class HelloController {
    @FXML public TableColumn<Etel, String> nev;
    @FXML public TableColumn<Etel, String> kat;
    @FXML public TableColumn<Etel, Integer> ar;
    @FXML public TableView<Etel> tabla;
    @FXML public Label leiras;

    private EtelService service;

    public void initialize() {
        nev.setCellValueFactory(new PropertyValueFactory<>("nev"));
        kat.setCellValueFactory(new PropertyValueFactory<>("kategoria"));
        ar.setCellValueFactory(new PropertyValueFactory<>("ar"));

        tabla.getSelectionModel().selectedItemProperty().addListener((observableValue, oldEtel, newEtel) -> {
            if (newEtel != null) leiras.setText(newEtel.getLeiras());
        });

        try {
            service = new EtelService();
            ListEtel();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Sikertelen csatlakozás az adatbázishoz.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            Platform.exit();
        }
    }

    public void ListEtel() throws SQLException {
        tabla.getItems().clear();
        tabla.getItems().addAll(service.getAll());
    }

    @FXML
    public void addButton(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("add-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 300);
            AddEtelController controller = fxmlLoader.getController();
            controller.setService(service);
            stage.setTitle("Új étel hozzáadása");
            stage.setScene(scene);
            stage.setOnHiding(e -> {
                try {
                    ListEtel();
                } catch (SQLException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hiba!");
                    alert.setHeaderText("Nem sikerült lekérni az adatbázisból.");
                    alert.setContentText(ex.getMessage());
                    Platform.exit();
                }
            });
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba!");
            alert.setHeaderText("Nem sikerült megnyitni az ablakot.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void deleteButton(ActionEvent actionEvent)  {
        Etel selected = tabla.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Nincs kiválasztott étel.");
            alert.showAndWait();
            return;
        }
        try {
            if (!service.delete(selected.getId())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hiba!");
                alert.setHeaderText("Sikertelen törlés.");
            } else {
                ListEtel();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba!");
            alert.setHeaderText("Sikertelen törlés.");
            alert.setContentText(e.getMessage());
        }

    }
}