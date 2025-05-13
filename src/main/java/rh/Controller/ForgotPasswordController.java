package rh.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import rh.Models.User;
import rh.Service.EmailService;
import rh.Service.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private Label messageLabel;

    private UserService userService;
    private EmailService emailService;

    public ForgotPasswordController() {
        userService = new UserService();
        emailService = new EmailService();
    }

    @FXML
    void handleSendLinkAction(ActionEvent event) {
        String email = emailField.getText();
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            messageLabel.setText("Please enter a valid email address.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            User user = userService.findByEmail(email);
            if (user != null) {
                String token = userService.generatePasswordResetToken(email);
                if (token != null) {
                    boolean emailSent = emailService.sendPasswordResetEmail(email, user.getNom(), token);
                    if (emailSent) {
                        messageLabel.setText("Password reset link sent to your email.");
                        messageLabel.setStyle("-fx-text-fill: green;");
                        // Navigate to VerifyToken screen
                        navigateToVerifyToken(event, email); // Pass email to pre-fill if needed
                    } else {
                        messageLabel.setText("Failed to send reset email. Try again.");
                        messageLabel.setStyle("-fx-text-fill: red;");
                    }
                } else {
                    messageLabel.setText("Failed to generate reset token. Try again.");
                    messageLabel.setStyle("-fx-text-fill: red;");
                }
            } else {
                messageLabel.setText("No user found with this email address.");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    private void navigateToVerifyToken(ActionEvent event, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/verify_token.fxml"));
            Parent root = loader.load();

            // Pass the email to the VerifyTokenController if needed
            VerifyTokenController controller = loader.getController();
            controller.setUserEmail(email);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            // Assuming login.fxml dimensions were, for example, 600x400. Adjust as necessary.
            // stage.setMinWidth(600);
            // stage.setMinHeight(400);
            // stage.setMaxWidth(600);
            // stage.setMaxHeight(400);
            stage.setScene(scene);
            stage.setTitle("Verify Token");
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Error loading verification page: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
    
    @FXML
    void handleBackToLoginAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/rh/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login"); // Set title back to Login
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading login page.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
