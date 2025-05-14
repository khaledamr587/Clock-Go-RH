package rh.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import rh.Utils.UserSession;

import java.io.IOException;

public class HeaderEmpl {
    @FXML
    private Button btn;

    public void GoToProfile(ActionEvent actionEvent) throws IOException {
        loadFXML("/rh/profile.fxml");
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        UserSession userSession = UserSession.getInstance();
        userSession.logout();
        loadFXML("/rh/login.fxml");
    }

    public void GoToFormation(ActionEvent actionEvent) throws IOException {

    }

    public void GoToabsenceetconge(ActionEvent actionEvent) throws IOException {
        loadFXML("/fxml/consultation_absence_et_conge.fxml");
    }

    private void loadFXML(String fxmlPath) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new IOException("FXML file '" + fxmlPath + "' not found in resources.");
            }
            Parent root = loader.load();
            btn.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            throw e;
        }
    }
}