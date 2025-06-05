package Gestion.controllers;

import Gestion.models.Formation;
import Gestion.services.FormationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class FormationDetailsController {
    
    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label durationLabel;
    @FXML private Label locationLabel;
    @FXML private Label placesLabel;
    @FXML private Label trainerLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Button closeButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button registerButton;
    
    private Formation formation;
    private FormationService formationService;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private boolean isAdmin = true; // For demo purposes, set to true (would normally be based on user role)
    
    @FXML
    private void initialize() {
        formationService = new FormationService();
        
        closeButton.setOnAction(event -> closeWindow());
        editButton.setOnAction(event -> editFormation());
        deleteButton.setOnAction(event -> deleteFormation());
        registerButton.setOnAction(event -> registerForFormation());
        
        // Show/hide admin buttons based on user role
        updateUIForRole();
    }
    
    public void setFormation(Formation formation) {
        this.formation = formation;
        populateFormationDetails();
    }
    
    private void populateFormationDetails() {
        if (formation == null) return;
        
        titleLabel.setText(formation.getTitre());
        dateLabel.setText(dateFormatter.format(formation.getDateDebut()));
        durationLabel.setText(formation.getDureeJours() + " jours");
        locationLabel.setText(formation.getLieu());
        placesLabel.setText(formation.getPlacesMax() + " places");
        
        // Fetch trainer name based on ID if needed
        trainerLabel.setText("ID: " + formation.getFormateurId()); // Replace with actual trainer name when available
        
        descriptionArea.setText(formation.getDescription());
    }
    
    private void updateUIForRole() {
        // Hide admin buttons if user is not admin
        editButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);
        
        // Disable register button if formation is full
        // This would require additional checks with participant count
    }
    
    @FXML
    public void closeWindow() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
    
    private void editFormation() {
        if (!isAdmin) {
            showAlert(Alert.AlertType.WARNING, "Accès Refusé", "Seuls les administrateurs peuvent modifier les formations.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_formation.fxml"));
            Parent root = loader.load();
            
            AddFormationController controller = loader.getController();
            controller.setFormation(formation); // Pass the formation to edit
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier la Formation");
            stage.setScene(new Scene(root, 600, 650));
            
            // When the edit window closes, refresh the details view
            stage.setOnHidden(e -> refreshFormationDetails());
            
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void refreshFormationDetails() {
        try {
            Formation refreshedFormation = formationService.getFormationById(formation.getId());
            if (refreshedFormation != null) {
                setFormation(refreshedFormation);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du rafraîchissement des détails: " + e.getMessage());
        }
    }
    
    private void deleteFormation() {
        if (!isAdmin) {
            showAlert(Alert.AlertType.WARNING, "Accès Refusé", "Seuls les administrateurs peuvent supprimer des formations.");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmer la suppression");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cette formation ?");
        
        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                formationService.deleteFormation(formation.getId());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Formation supprimée avec succès.");
                closeWindow();
            } catch (SQLException e) {
                String errorMessage = e.getMessage();
                String displayMessage = "Erreur lors de la suppression: ";
                
                // Extract the most user-friendly message
                if (errorMessage.contains("référencée par d'autres enregistrements")) {
                    displayMessage += "Cette formation a des participants inscrits. Veuillez d'abord supprimer ces inscriptions.";
                } else if (errorMessage.contains("Communications link failure")) {
                    displayMessage += "Problème de connexion à la base de données. Veuillez vérifier votre connexion.";
                } else {
                    displayMessage += errorMessage;
                }
                
                showAlert(Alert.AlertType.ERROR, "Erreur", displayMessage);
                System.err.println("Full error details: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void registerForFormation() {
        // TODO: Implement registration functionality
        // This would require participant registration service
        showAlert(Alert.AlertType.INFORMATION, "Fonctionnalité à venir", "L'inscription aux formations sera disponible prochainement.");
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 