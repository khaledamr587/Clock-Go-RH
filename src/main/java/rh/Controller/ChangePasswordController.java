package rh.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import rh.Service.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class ChangePasswordController {

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private int userId;
    private String token;
    private UserService userService;

    public ChangePasswordController() {
        userService = new UserService();
    }

    // Method to receive user ID and token from VerifyTokenController
    public void setUserData(int userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    @FXML
    void handleChangePasswordAction(ActionEvent event) {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword == null || newPassword.trim().isEmpty()) {
            messageLabel.setText("New password cannot be empty.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        if (newPassword.length() < 6) { // Example: enforce minimum password length
            messageLabel.setText("Password must be at least 6 characters long.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            boolean success = userService.resetPassword(userId, newPassword, token);
            if (success) {
                messageLabel.setText("Password changed successfully! You can now login.");
                messageLabel.setStyle("-fx-text-fill: green;");
                // Optionally, disable fields and button or navigate away after a delay
                // For now, we will just show a success message and let user click back to login
                newPasswordField.setDisable(true);
                confirmPasswordField.setDisable(true);
            } else {
                messageLabel.setText("Failed to change password. Please try again.");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
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
            // stage.setMinWidth(600); // Optional: maintain size
            // stage.setMinHeight(400);
            // stage.setMaxWidth(600);
            // stage.setMaxHeight(400);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Error loading login page: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}
