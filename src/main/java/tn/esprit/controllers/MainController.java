package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private Button btnSalaires;


    @FXML
    private Button btnretour;


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
    public void retour(ActionEvent actionEvent) throws IOException {
        try {
            // Load the acceuil.fxml which will automatically show the appropriate header
            // based on the user's role (comptable)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/acceuil.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("FXML file '/rh/acceuil.fxml' not found.");
            }
            Parent root = loader.load();
            btnretour.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du retour à la page d'accueil : " + e.getMessage());
            throw e;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(
                title.equals("Erreur") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}