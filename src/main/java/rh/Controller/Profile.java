package rh.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import rh.Models.User;
import rh.Service.UserService;
import rh.Utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile implements Initializable{

    @FXML
    private TextField email;
    @FXML
    private TextField prenom;
    @FXML
    private TextField nom;
    @FXML
    private TextField pwd;
    @FXML
    private HBox box;
    @FXML
    private Label nomcc;
    @FXML
    private Label prenomcc;
    @FXML
    private Label emailcc;
    @FXML
    private Label pwdcc;
    @FXML
    private Label submitcc;
    @FXML
    private Label submitss;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FXMLLoader fxl=new FXMLLoader();
        UserSession userSession =UserSession.getInstance();
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
            nom.setText(user.getNom());
            prenom.setText(user.getPrenom());
            email.setText(user.getEmail());
        }else{
            fxl.setLocation(getClass().getResource("/rh/headerComp.fxml"));
            Parent root= null;
            try {
                root = fxl.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            box.getChildren().add(root);
            nom.setText(user.getNom());
            prenom.setText(user.getPrenom());
            email.setText(user.getEmail());
        }
    }

    public void retour(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/rh/acceuil.fxml"));
        Parent root=loader.load();
        nom.getScene().setRoot(root);
    }

    public void submit(ActionEvent actionEvent) throws SQLException, IOException {
        int t=0;
        UserService userService=new UserService();
        if (email.getText().isEmpty()) {
            t = 1;
            this.emailcc.setText("Email invalide");
        } else {
            String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            Pattern pattern = Pattern.compile(emailPattern);
            Matcher matcher = pattern.matcher(email.getText());
            if (!matcher.matches()) {
                t = 1;
                this.emailcc.setText("Format d'email invalide");
            } else {
                this.emailcc.setText("");
            }
        }
        if (nom.getText().isEmpty()){
            t = 1;
            this.nomcc.setText("Vous devez saisir votre nom");
        } else {
            this.nomcc.setText("");
        }
        if (prenom.getText().isEmpty()){
            t = 1;
            this.prenomcc.setText("Vous devez saisir votre prenom");
        } else {
            this.prenomcc.setText("");
        }
        if (pwd.getText().isEmpty()){
            t = 1;
            this.pwdcc.setText("Vous devez saisir votre mot de passe");
        } else {
            this.pwdcc.setText("");
        }
        if(t==0){
            User user=new User();
            user.setEmail(email.getText());
            user.setNom(nom.getText());
            user.setPrenom(prenom.getText());
            user.setPassword(pwd.getText());
            user=userService.modifierUser(user,UserSession.getInstance().getUser().getEmail());
            if(user==null){
                this.submitcc.setText("Modification échoué");
            }else {
                this.submitcc.setText("");
                this.submitss.setText("Modifié avec succès");
                UserSession userSession =UserSession.getInstance();
                userSession.getUser().setNom(user.getNom());
                userSession.getUser().setPrenom(user.getPrenom());
                userSession.getUser().setEmail(user.getEmail());
                userSession.getUser().setPassword(user.getPassword());
                userSession.getUser().setEmail(user.getEmail());
            }

        }
    }

}
