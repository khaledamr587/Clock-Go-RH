package rh.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import rh.Utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AcceuilAdmin implements Initializable{
    @FXML
    private VBox vbox;
    @FXML
    private AnchorPane profilebox;
    private Boolean profileStatus;

    public void setProfileStatus(Boolean profileStatus) {
        this.profileStatus = profileStatus;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FXMLLoader fxl=new FXMLLoader();
        fxl.setLocation(getClass().getResource("/rh/sidebar.fxml"));
        Parent root= null;
        try {
            root = fxl.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        vbox.getChildren().add(root);
        setProfileStatus(false);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        UserSession userSession =UserSession.getInstance();
        userSession.logout();
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/rh/login.fxml"));
        Parent root=loader.load();
        vbox.getScene().setRoot(root);
    }

    public void OpenProfile(ActionEvent actionEvent) throws IOException {
        setProfileStatus(!this.profileStatus);
        profilebox.getChildren().clear();

        if (profileStatus) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/profilecard.fxml"));
            AnchorPane profilePane = loader.load();
            profilebox.getChildren().add(profilePane);
        }
    }

    @FXML
    public void goToRecruitment(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/PageAccueil.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("FXML file not found. Trying alternative path...");
            }
            Parent root = loader.load();
            vbox.getScene().setRoot(root);
        } catch (IOException e) {
            // Try alternative path
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/PageAccueil.fxml"));
                if (loader.getLocation() == null) {
                    throw new IOException("FXML file not found in any location");
                }
                Parent root = loader.load();
                vbox.getScene().setRoot(root);
            } catch (IOException e2) {
                System.err.println("Error loading recruitment page: " + e2.getMessage());
                throw e2;
            }
        }
    }

}
