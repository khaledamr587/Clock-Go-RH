package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main2Controller {

    @FXML
    public void goToValiderConge() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/valider_conge.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Valider les Congés");
            setStageIcon(stage);
            stage.setScene(new Scene(root, 440, 650));
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement de la page Valider les Congés : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void goToMarquerAbsence() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/marquer_absence.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Marquer les Absences");
            setStageIcon(stage);
            stage.setScene(new Scene(root, 440, 650));
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement de la page Marquer les Absences : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setStageIcon(Stage stage) {
        try {
            Image icon = new Image(getClass().getResourceAsStream("/images/app-icon.png"));
            if (icon.isError()) {
                System.err.println("Icon loading failed: Image is invalid.");
            } else {
                stage.getIcons().add(icon);
            }
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + e.getMessage());
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