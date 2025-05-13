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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import rh.Models.User;
import rh.Service.UserService;
import rh.Utils.UserSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Collections;

public class Login {
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @FXML
    private Label emailcc;
    @FXML
    private Label pwdcc;
    @FXML
    private Label logincc;
    @FXML
    private TextField Email;
    @FXML
    private TextField pwd;
    @FXML
    private Button googleLoginButton;

    private final UserService userService = new UserService();

    @FXML
    void handleForgotPasswordAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/forgot_password.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) Email.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Forgot Password");
        stage.show();
    }

    @FXML
    void handleGoogleLogin(ActionEvent event) {
        try {
            InputStream in = getClass().getResourceAsStream("/resources/client_secrets.json");
            if (in == null) {
                logincc.setText("client_secrets.json not found in resources.");
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

            User user = userService.loginWithGoogle(userInfo.getEmail(), userInfo.getId());
            if (user == null) {
                logincc.setText("No account associated with this Google email.");
                return;
            }
            if (!user.isStatus()) {
                logincc.setText("Sorry, the administrator must approve your account.");
                return;
            }

            UserSession userSession = UserSession.getInstance();
            userSession.setId((long) user.getId());
            userSession.setUser(user);

            String fxmlPath = user.getRole().toString().equals("Admin") ? "/rh/acceuilAdmin.fxml" : "/rh/acceuil.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            googleLoginButton.getScene().setRoot(root);
        } catch (Exception e) {
            logincc.setText("Google login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void GoToRegister(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/register.fxml"));
        Parent root = loader.load();
        Email.getScene().setRoot(root);
    }

    public void connection(ActionEvent actionEvent) throws SQLException, IOException {
        int t = 0;
        if (Email.getText().isEmpty()) {
            t = 1;
            this.emailcc.setText("Vous devez saisir votre email");
        } else {
            this.emailcc.setText("");
        }
        if (pwd.getText().isEmpty()) {
            t = 1;
            this.pwdcc.setText("Vous devez saisir votre mot de passe");
        } else {
            this.pwdcc.setText("");
        }
        if (t == 0) {
            User user = new User();
            user.setPassword(pwd.getText());
            user.setEmail(Email.getText());
            user = userService.login(user);
            if (user == null)
                this.logincc.setText("Desole email ou mot de passe incorrect");
            else if (user.getId() == 0) {
                this.logincc.setText("Desole Mot de passe incorrect");
            } else if (!user.isStatus()) {
                this.logincc.setText("Desole l'administrateur doit vous accepter ");
            } else {
                UserSession userSession = UserSession.getInstance();
                userSession.setId((long) user.getId());
                userSession.setUser(user);
                if (user.getRole().toString().equals("Admin")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/acceuilAdmin.fxml"));
                    Parent root = loader.load();
                    logincc.getScene().setRoot(root);
                } else {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/acceuil.fxml"));
                    Parent root = loader.load();
                    logincc.getScene().setRoot(root);
                }
            }
        }
    }
}