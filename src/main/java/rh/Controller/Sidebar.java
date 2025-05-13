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
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/rh/acceuilAdmin.fxml"));
        Parent root=loader.load();
        btn.getScene().setRoot(root);
    }

    public void goToUsers(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/rh/users.fxml"));
        Parent root=loader.load();
        btn.getScene().setRoot(root);
    }
    public void GoToFormation(ActionEvent actionEvent) {
    }
    public void GoToDemandeDeCandidature(ActionEvent actionEvent) {
    }
    public void GoTopaiement(ActionEvent actionEvent) {
    }
    public void GoToCongeAndAbsence(ActionEvent actionEvent) {
    }
}
