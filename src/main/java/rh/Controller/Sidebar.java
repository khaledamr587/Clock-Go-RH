package rh.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.lang.Class;
import java.lang.reflect.Method;
import java.lang.ProcessBuilder;
import java.lang.Process;

public class Sidebar {

    @FXML
    private Button btn2;

    public void goToAcceuil(ActionEvent actionEvent) throws IOException {
        loadFXML("/rh/acceuilAdmin.fxml");
    }

    public void goToUsers(ActionEvent actionEvent) throws IOException {
        loadFXML("/rh/users.fxml");
    }

    public void GoToFormation(ActionEvent actionEvent) throws IOException {
        try {
            MainWrapper.loadMainView((Stage) btn2.getScene().getWindow());
        } catch (Exception e) {
            System.err.println("Error in GoToFormation: " + e.getMessage());
            e.printStackTrace();
            // Fallback to the old method if the new one fails
            loadFXML("/fxml/main.fxml");
        }
    }

    public void GoToDemandeDeCandidature(ActionEvent actionEvent) throws IOException {
        loadFXML("/PageAccueil.fxml");
    }


    public void GoToMain2App(ActionEvent actionEvent) throws IOException {
        loadFXML("/fxml/gestion_absence_et_conge.fxml");
    }

    private void loadFXML(String fxmlPath) throws IOException {
        try {
            System.out.println("Attempting to load: " + fxmlPath);
            URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                System.err.println("ResourceUrl is null for: " + fxmlPath);
                throw new IOException("FXML file '" + fxmlPath + "' not found at " + fxmlPath);
            }
            System.out.println("Found resource at: " + resourceUrl);
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            btn2.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            throw e;
        }
    }
}