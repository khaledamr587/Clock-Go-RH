package rh.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import rh.Utils.UserSession;

import java.io.IOException;


public class HeaderCompt {

    @FXML
    private Button btn;

    public void GoToProfile(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/rh/profile.fxml"));
        Parent root=loader.load();
        btn.getScene().setRoot(root);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        UserSession userSession =UserSession.getInstance();
        userSession.logout();
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/rh/login.fxml"));
        Parent root=loader.load();
        btn.getScene().setRoot(root);
    }

    public void GoToSalaire(ActionEvent actionEvent) {
    }
}
