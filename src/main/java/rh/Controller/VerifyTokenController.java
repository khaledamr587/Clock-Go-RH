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
import rh.Service.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class VerifyTokenController {

    @FXML
    private TextField tokenField;

    @FXML
    private Label messageLabel;

    private String userEmail; // To store the email from the previous screen
    private UserService userService;

    public VerifyTokenController() {
        userService = new UserService();
    }

    // Method to receive the email from ForgotPasswordController
    public void setUserEmail(String email) {
        this.userEmail = email;
        // Optionally, display a message like "Verifying token for userEmail"
    }

    @FXML
    void handleVerifyTokenAction(ActionEvent event) {
        String token = tokenField.getText();
        if (token == null || token.trim().length() != 6 || !token.matches("\\d{6}")) {
            messageLabel.setText("Please enter a valid 6-digit token.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            int userId = userService.verifyPasswordResetToken(token);
            if (userId > 0) { // Token is valid and user ID is returned
                messageLabel.setText("Token verified successfully!");
                messageLabel.setStyle("-fx-text-fill: green;");
                // Navigate to ChangePassword screen, passing userId and token
                navigateToChangePassword(event, userId, token);
            } else if (userId == -1) {
                messageLabel.setText("Invalid token. Please check and try again.");
                messageLabel.setStyle("-fx-text-fill: red;");
            } else if (userId == -2) {
                messageLabel.setText("Token has expired. Please request a new one.");
                messageLabel.setStyle("-fx-text-fill: red;");
            } else {
                messageLabel.setText("Token verification failed. Please try again.");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    private void navigateToChangePassword(ActionEvent event, int userId, String token) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/change_password.fxml"));
            Parent root = loader.load();

            ChangePasswordController controller = loader.getController();
            controller.setUserData(userId, token);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            // stage.setMinWidth(600); // Optional: maintain size
            // stage.setMinHeight(400);
            // stage.setMaxWidth(600);
            // stage.setMaxHeight(400);
            stage.setScene(scene);
            stage.setTitle("Change Password");
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Error loading change password page: " + e.getMessage());
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
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading login page.");
             messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
