package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main2Controller {

    @FXML
    public void goToValiderConge() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/valider_conge.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Valider les Congés");
            stage.setScene(new Scene(root, 440, 650));
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement de la page Valider les Congés : " + e.getMessage());
        }
    }

    @FXML
    public void goToMarquerAbsence() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/marquer_absence.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Marquer les Absences");
            stage.setScene(new Scene(root, 440, 650));
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement de la page Marquer les Absences : " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                title.equals("Erreur") ? javafx.scene.control.Alert.AlertType.ERROR : javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}