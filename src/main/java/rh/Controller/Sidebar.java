package rh.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import java.io.IOException;

public class Sidebar {

    @FXML
    private Button btn;

    public void goToAcceuil(ActionEvent actionEvent) throws IOException {
        loadFXML("/rh/acceuilAdmin.fxml");
    }

    public void goToUsers(ActionEvent actionEvent) throws IOException {
        loadFXML("/rh/users.fxml");
    }

    public void GoToFormation(ActionEvent actionEvent) throws IOException {
        // Add logic if needed
    }

    public void GoToDemandeDeCandidature(ActionEvent actionEvent) throws IOException {
        System.out.println("Attempting to load: /src/main/resources/views/PageAccueil.fxml");
        System.out.println("Resource URL: " + getClass().getResource("/src/main/resources/views/PageAccueil.fxml"));
        loadFXML("/src/main/resources/views/PageAccueil.fxml");
    }

    public void GoTopaiement(ActionEvent actionEvent) throws IOException {
        loadFXML("/views/MainView.fxml");
    }

    public void GoToMain2App(ActionEvent actionEvent) throws IOException {
        loadFXML("/fxml/gestion_absence_et_conge.fxml");
    }

    private void loadFXML(String fxmlPath) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new IOException("FXML file '" + fxmlPath + "' not found.");
            }
            Parent root = loader.load();
            btn.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            throw e;
        }
    }
}