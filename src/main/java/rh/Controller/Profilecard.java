package rh.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import rh.Models.User;
import rh.Utils.UserSession;

import java.net.URL;
import java.util.ResourceBundle;
public class Profilecard implements Initializable{

    @FXML
    private Label nom;
    @FXML
    private Label prenom;
    @FXML
    private Label email;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserSession userSession =UserSession.getInstance();
        User user=userSession.getUser();
        nom.setText(user.getNom());
        prenom.setText(user.getPrenom());
        email.setText(user.getEmail());
    }

}
