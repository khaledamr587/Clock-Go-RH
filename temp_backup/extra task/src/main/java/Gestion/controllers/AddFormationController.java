package Gestion.controllers;

import Gestion.models.Formation;
import Gestion.services.FormationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.time.LocalDate;

public class AddFormationController {
    
    @FXML private TextField titreField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private TextField dureeField;
    @FXML private ComboBox<FormateurItem> formateurCombo;
    @FXML private TextField lieuField;
    @FXML private TextField placesMaxField;
    @FXML private TextArea descriptionArea;
    @FXML private Button closeButton;
    @FXML private Label statusLabel;
    
    private FormationService formationService;
    private Formation existingFormation; // Will be null for new formations, populated for edits
    private boolean isEditMode = false;
    
    // Simple class to represent a formateur in the combo box
    private static class FormateurItem {
        private final int id;
        private final String displayName;
        
        public FormateurItem(int id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }
        
        public int getId() {
            return id;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    @FXML
    private void initialize() {
        formationService = new FormationService();
        
        // Set today's date as default
        dateDebutPicker.setValue(LocalDate.now());
        
        // Setup formateur combo box
        setupFormateurComboBox();
        
        // Add numeric validation to appropriate fields
        addNumericValidation(dureeField);
        addNumericValidation(placesMaxField);
    }
    
    private void setupFormateurComboBox() {
        // For simplicity, we'll add some dummy formateurs
        // In a real app, these would come from a FormateurService
        ObservableList<FormateurItem> formateurs = FXCollections.observableArrayList(
            new FormateurItem(1, "Jean Dupont"),
            new FormateurItem(2, "Sophie Martin"),
            new FormateurItem(3, "Alexandre Bernard"),
            new FormateurItem(4, "Isabelle Petit"),
            new FormateurItem(5, "Thomas Lambert")
        );
        
        formateurCombo.setItems(formateurs);
        formateurCombo.getSelectionModel().select(0); // Select first by default
    }
    
    private void addNumericValidation(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    public void setFormation(Formation formation) {
        this.existingFormation = formation;
        isEditMode = true;
        
        // Populate form fields with existing formation data
        titreField.setText(formation.getTitre());
        dateDebutPicker.setValue(formation.getDateDebut());
        dureeField.setText(String.valueOf(formation.getDureeJours()));
        lieuField.setText(formation.getLieu());
        placesMaxField.setText(String.valueOf(formation.getPlacesMax()));
        descriptionArea.setText(formation.getDescription());
        
        // Select the correct formateur in the combo box
        int formateurId = formation.getFormateurId();
        for (FormateurItem item : formateurCombo.getItems()) {
            if (item.getId() == formateurId) {
                formateurCombo.getSelectionModel().select(item);
                break;
            }
        }
    }
    
    @FXML
    public void saveFormation() {
        if (!validateForm()) {
            return;
        }
        
        try {
            Formation formation = createFormationFromForm();
            
            if (isEditMode) {
                formation.setId(existingFormation.getId());
                formationService.updateFormation(formation);
                statusLabel.setText("Formation mise à jour avec succès");
            } else {
                formationService.addFormation(formation);
                statusLabel.setText("Formation ajoutée avec succès");
                clearForm(); // Only clear the form for new formations, not edits
            }
            
            // Close the window after successful save
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
            
        } catch (SQLException e) {
            String errorMessage = e.getMessage();
            statusLabel.setText("Erreur: " + errorMessage);
            
            // Extract the most user-friendly message
            String displayMessage = "Erreur: ";
            if (errorMessage.contains("formateur spécifié n'existe pas")) {
                displayMessage += "Le formateur sélectionné n'existe pas. Veuillez en choisir un autre.";
            } else if (errorMessage.contains("Communications link failure")) {
                displayMessage += "Problème de connexion à la base de données. Veuillez vérifier votre connexion.";
            } else if (errorMessage.contains("référencée par d'autres enregistrements")) {
                displayMessage += "Des participants sont inscrits à cette formation. Modifications restreintes.";
            } else {
                displayMessage += errorMessage;
            }
            
            showAlert(Alert.AlertType.ERROR, "Erreur", displayMessage);
            System.err.println("Full error details: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Formation createFormationFromForm() {
        String titre = titreField.getText().trim();
        LocalDate dateDebut = dateDebutPicker.getValue();
        int dureeJours = Integer.parseInt(dureeField.getText().trim());
        int formateurId = formateurCombo.getValue().getId();
        String lieu = lieuField.getText().trim();
        int placesMax = Integer.parseInt(placesMaxField.getText().trim());
        String description = descriptionArea.getText().trim();
        
        Formation formation = new Formation();
        formation.setTitre(titre);
        formation.setDescription(description);
        formation.setDateDebut(dateDebut);
        formation.setDureeJours(dureeJours);
        formation.setFormateurId(formateurId);
        formation.setLieu(lieu);
        formation.setPlacesMax(placesMax);
        
        return formation;
    }
    
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        
        if (titreField.getText().trim().isEmpty()) {
            errors.append("- Le titre est obligatoire\n");
        }
        
        if (dateDebutPicker.getValue() == null) {
            errors.append("- La date de début est obligatoire\n");
        } else if (dateDebutPicker.getValue().isBefore(LocalDate.now())) {
            errors.append("- La date de début ne peut pas être dans le passé\n");
        }
        
        if (dureeField.getText().trim().isEmpty()) {
            errors.append("- La durée est obligatoire\n");
        } else {
            try {
                int duree = Integer.parseInt(dureeField.getText().trim());
                if (duree <= 0 || duree > 30) {
                    errors.append("- La durée doit être entre 1 et 30 jours\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- La durée doit être un nombre entier\n");
            }
        }
        
        if (formateurCombo.getValue() == null) {
            errors.append("- Le formateur est obligatoire\n");
        }
        
        if (lieuField.getText().trim().isEmpty()) {
            errors.append("- Le lieu est obligatoire\n");
        }
        
        if (placesMaxField.getText().trim().isEmpty()) {
            errors.append("- Le nombre de places est obligatoire\n");
        } else {
            try {
                int places = Integer.parseInt(placesMaxField.getText().trim());
                if (places <= 0 || places > 100) {
                    errors.append("- Le nombre de places doit être entre 1 et 100\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Le nombre de places doit être un nombre entier\n");
            }
        }
        
        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Formulaire incomplet", "Veuillez corriger les erreurs suivantes:\n" + errors.toString());
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        titreField.clear();
        dateDebutPicker.setValue(LocalDate.now());
        dureeField.clear();
        formateurCombo.getSelectionModel().selectFirst();
        lieuField.clear();
        placesMaxField.clear();
        descriptionArea.clear();
    }
    
    @FXML
    public void closeWindow() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 