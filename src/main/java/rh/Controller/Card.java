package rh.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import rh.Models.User;
import rh.Service.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class Card {

    @FXML
    private Label nom;
    @FXML
    private Label prenom;
    @FXML
    private Label role;
    @FXML
    private Label email;
    @FXML
    private Label statut;

    private  int id;
    private  User user;
    private String roleName;

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setdata(User u){
        nom.setText(u.getNom());
        prenom.setText(u.getPrenom());
        role.setText(u.getRole().toString());
        email.setText(u.getEmail());
        if(u.isStatus()){
            statut.setText("Actif");
        }else {
            statut.setText("Inactif");
        }
    }

    public void supprimer(ActionEvent actionEvent) throws SQLException, IOException {
        UserService userService=new UserService();
        int t;
        t=userService.supprimerUser(this.id);
        if(t==1){
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/rh/users.fxml"));
            Parent root=loader.load();
            statut.getScene().setRoot(root);
        }
    }

    public void toggle(ActionEvent actionEvent) throws SQLException, IOException {
        UserService userService=new UserService();
        int t;
        t=userService.toggleUserStatus(this.id);
        if(t==1){
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/rh/users.fxml"));
            Parent root=loader.load();
            statut.getScene().setRoot(root);
        }
    }
    public void updateRole()throws SQLException, IOException{
        UserService userService=new UserService();
        int t;
        t=userService.changeUserRole(this.roleName,this.id);
        if(t==1){
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/rh/users.fxml"));
            Parent root=loader.load();
            statut.getScene().setRoot(root);
        }
    }

    public void changeRole(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/changerRole.fxml"));
        Parent root = loader.load();

        ChangerRoleController controller = loader.getController();

        controller.setOnEmployeSelected(() -> {
            try {
                setRoleName("Employe");
                updateRole();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        controller.setOnComptableSelected(() -> {
            try {
                setRoleName("Comptable");
                updateRole();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Stage stage = new Stage();
        stage.setTitle("Changer le rôle");
        stage.setScene(new javafx.scene.Scene(root));
        stage.setResizable(false);
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

}
