package rh.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import rh.Models.User;
import rh.Utils.UserSession;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Acceuil implements Initializable{
    @FXML
    private HBox box;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserSession userSession =UserSession.getInstance();
        FXMLLoader fxl=new FXMLLoader();
        User user=userSession.getUser();
        String role = user.getRole().toString();
        if(role.equals("Employe")){
            fxl.setLocation(getClass().getResource("/rh/headerEmpl.fxml"));
            Parent root= null;
            try {
                root = fxl.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            box.getChildren().add(root);
        }
        else {
            fxl.setLocation(getClass().getResource("/rh/headerComp.fxml"));
            Parent root= null;
            try {
                root = fxl.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            box.getChildren().add(root);
        }

    }
}
