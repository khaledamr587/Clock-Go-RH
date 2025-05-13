package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private Button btnSalaires;

    @FXML
    private Button btnPrimes;

    @FXML
    private Button btnBulletins;

    @FXML
    private Button btnStatistiques;

    @FXML
    void handleSalaires(ActionEvent event) {
        loadView("/views/SalaireView.fxml");
    }

    @FXML
    void handlePrimes(ActionEvent event) {
        loadView("/views/PrimeView.fxml");
    }

    @FXML
    void handleBulletins(ActionEvent event) {
        loadView("/views/BulletinView.fxml");
    }

    @FXML
    void handleStatistiques(ActionEvent event) {
        loadView("/views/StatistiquesView.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnSalaires.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 