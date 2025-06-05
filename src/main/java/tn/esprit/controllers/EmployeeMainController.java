package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import rh.Utils.UserSession;

import java.io.IOException;

public class EmployeeMainController {

    @FXML
    private Button btnSalaires;

    @FXML
    private Button btnFormations;
    
    @FXML
    private Button btnNotifications;

    @FXML
    private Button btnretour;

    @FXML
    void handleSalaires(ActionEvent event) {
        loadView("/views/EmployeeSalaireView.fxml");
    }

    @FXML
    void handleFormations(ActionEvent event) {
        loadView("/fxml/employee_formations.fxml");
    }
    
    @FXML
    void handleNotifications(ActionEvent event) {
        loadView("/fxml/employee_notifications.fxml");
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
            showAlert("Erreur", "Erreur lors du chargement de la vue : " + e.getMessage());
        }
    }
    
    public void retour(ActionEvent actionEvent) throws IOException {
        try {
            UserSession userSession = UserSession.getInstance();
            userSession.logout();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/login.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("FXML file '/rh/login.fxml' not found.");
            }
            Parent root = loader.load();
            btnretour.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la déconnexion : " + e.getMessage());
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