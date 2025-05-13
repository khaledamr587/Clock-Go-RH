package rh.Controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import rh.Models.User;
import rh.Service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register {
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @FXML
    private Label emailcc;
    @FXML
    private Label pwdcc;
    @FXML
    private Label registercc;
    @FXML
    private Label nomcc;
    @FXML
    private Label prenomcc;
    @FXML
    private Label registerss;
    @FXML
    private TextField Email;
    @FXML
    private TextField pwd;
    @FXML
    private TextField nom;
    @FXML
    private TextField prenom;
    @FXML
    private Button googleRegisterButton;

    private final UserService userService = new UserService();

    public void GoToLogin(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/login.fxml"));
        Parent root = loader.load();
        Email.getScene().setRoot(root);
    }

    @FXML
    void handleGoogleRegister(ActionEvent event) {
        try {
            InputStream in = getClass().getResourceAsStream("/resources/client_secret.json");
            if (in == null) {
                registercc.setText("client_secrets.json not found in resources.");
                return;
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    clientSecrets,
                    Collections.singletonList("profile email openid")
            ).setAccessType("offline").build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            Oauth2 oauth2 = new Oauth2.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY, credential)
                    .setApplicationName("RH").build();
            Userinfo userInfo = oauth2.userinfo().get().execute();

            User user = userService.registerWithGoogle(userInfo.getEmail(), userInfo.getId(), userInfo.getFamilyName(), userInfo.getGivenName());
            if (user == null) {
                registercc.setText("Error registering with Google.");
                return;
            }
            registerss.setText("Félicitations, votre compte Google a été créé");
        } catch (Exception e) {
            registercc.setText("Google registration error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void register(ActionEvent actionEvent) throws SQLException, IOException {
        int t = 0;
        List<User> usersList = userService.getAllusers();
        if (Email.getText().isEmpty()) {
            t = 1;
            this.emailcc.setText("Email invalide");
        } else {
            String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
            Pattern pattern = Pattern.compile(emailPattern);
            Matcher matcher = pattern.matcher(Email.getText());
            if (!matcher.matches()) {
                t = 1;
                this.emailcc.setText("Format d'email invalide");
            } else {
                for (User user : usersList) {
                    if (user.getEmail().equalsIgnoreCase(Email.getText())) {
                        t = 1;
                        this.emailcc.setText("Email existe");
                        break;
                    }
                }
                if (t == 0) {
                    this.emailcc.setText("");
                }
            }
        }

        if (pwd.getText().isEmpty()) {
            t = 1;
            this.pwdcc.setText("Vous devez saisir votre mot de passe");
        } else {
            this.pwdcc.setText("");
        }
        if (nom.getText().isEmpty()) {
            t = 1;
            this.nomcc.setText("Vous devez saisir votre nom");
        } else {
            this.nomcc.setText("");
        }
        if (prenom.getText().isEmpty()) {
            t = 1;
            this.prenomcc.setText("Vous devez saisir votre prenom");
        } else {
            this.prenomcc.setText("");
        }
        if (t == 0) {
            User user = new User();
            user.setEmail(Email.getText());
            user.setPassword(pwd.getText());
            user.setNom(nom.getText());
            user.setPrenom(prenom.getText());
            user.setRole(User.Role.Employe);
            user.setStatus(false);
            t = userService.addUser(user);
            if (t == 0) {
                this.registercc.setText("desole un erreur a interrompu la requête");
            } else {
                this.registercc.setText("");
                this.registerss.setText("Félicitations, votre compte a été créé");
            }
        }
    }
}