package controllers;

import Gestion.models.Absence;
import Gestion.services.ServiceAbsence;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import java.sql.Date;
import java.time.LocalDate;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class MarquerAbsenceController {

    @FXML
    private ComboBox<Integer> employeIdComboBox;

    @FXML
    private DatePicker dateAbsencePicker;

    @FXML
    private TextField motifField;

    @FXML
    private CheckBox justifieCheckBox;

    @FXML
    private VBox cardContainer;

    private ServiceAbsence serviceAbsence;

    private boolean isManualRefresh = false;

    private Absence selectedAbsence; // Pour stocker l'absence sélectionnée
    private VBox selectedCard;

    @FXML
    public void initialize() {
        try {
            serviceAbsence = new ServiceAbsence();
            // Remplir le ComboBox avec les IDs d'employés
            employeIdComboBox.getItems().addAll(serviceAbsence.getEmployeIds());
            employeIdComboBox.setPromptText("ID Employé");
            rafraichirAbsence();
        } catch (Exception e) {
            showAlert("Erreur d'initialisation", "Erreur lors de l'initialisation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setStageIcon(Stage stage) {
        try {
            Image icon = new Image(getClass().getResourceAsStream("/images/app-icon.png"));
            if (icon.isError()) {
                System.err.println("Icon loading failed: Image is invalid.");
            } else {
                stage.getIcons().add(icon);
            }
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + e.getMessage());
        }
    }

    @FXML
    public void ajouterAbsence() {
        if (!validateInputs()) {
            return;
        }
        try {
            Integer employeId = employeIdComboBox.getValue();
            if (employeId == null) {
                showAlert("Champ manquant", "Veuillez sélectionner un ID d'employé.");
                return;
            }
            Date dateAbsence = Date.valueOf(dateAbsencePicker.getValue());
            String motif = motifField.getText().trim();
            boolean justifie = justifieCheckBox.isSelected();

            Absence absence = new Absence(0, employeId, dateAbsence, motif, justifie);
            serviceAbsence.ajouter(absence);

            rafraichirAbsence();
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout de l'absence : " + e.getMessage());
        }
    }

    @FXML
    public void modifierAbsence() {
        if (selectedAbsence == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une absence à modifier.");
            return;
        }
        if (!validateInputs()) {
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Modifier l'absence");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir modifier cette absence ?");
        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            Integer employeId = employeIdComboBox.getValue();
            if (employeId == null) {
                showAlert("Champ manquant", "Veuillez sélectionner un ID d'employé.");
                return;
            }
            Date dateAbsence = Date.valueOf(dateAbsencePicker.getValue());
            String motif = motifField.getText().trim();
            boolean justifie = justifieCheckBox.isSelected();

            Absence updatedAbsence = new Absence(selectedAbsence.getId(), employeId, dateAbsence, motif, justifie);
            serviceAbsence.modifier(updatedAbsence);

            rafraichirAbsence();
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification de l'absence : " + e.getMessage());
        }
    }

    @FXML
    public void supprimerAbsence() {
        if (selectedAbsence == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une absence à supprimer.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Supprimer l'absence");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette absence ?");
        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            serviceAbsence.supprimer(selectedAbsence.getId());
            rafraichirAbsence();
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la suppression de l'absence : " + e.getMessage());
        }
    }

    @FXML
    public void rafraichirAbsence() {
        try {
            cardContainer.getChildren().clear();
            for (Absence absence : serviceAbsence.afficher()) {
                VBox card = new VBox(5);
                card.getStyleClass().add("card");
                card.getChildren().addAll(
                        new Label("Employé ID: " + absence.getEmployeId()),
                        new Label("Date: " + absence.getDateAbsence()),
                        new Label("Motif: " + absence.getMotif()),
                        new Label("Justifiée?: " + absence.isJustifie())
                );
                // Ajouter un gestionnaire d'événements pour gérer la sélection
                card.setOnMouseClicked(event -> {
                    if (selectedCard != null) {
                        selectedCard.getStyleClass().remove("selected-card");
                    }
                    selectedAbsence = absence;
                    selectedCard = card;
                    selectedCard.getStyleClass().add("selected-card");
                    employeIdComboBox.setValue(absence.getEmployeId());
                    dateAbsencePicker.setValue(absence.getDateAbsence() != null ? absence.getDateAbsence().toLocalDate() : null);
                    motifField.setText(absence.getMotif() != null ? absence.getMotif() : "");
                    justifieCheckBox.setSelected(absence.isJustifie());
                });
                cardContainer.getChildren().add(card);
            }
            if (isManualRefresh) {
                showAlert("Succès", "La liste des absences a été rafraîchie avec succès.");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du rafraîchissement des absences : " + e.getMessage());
        } finally {
            isManualRefresh = false;
        }
    }

    @FXML
    public void onRafraichirButtonClicked() {
        isManualRefresh = true;
        rafraichirAbsence();
    }

    private boolean validateInputs() {
        Integer employeId = employeIdComboBox.getValue();
        if (employeId == null) {
            showAlert("Champ manquant", "Veuillez sélectionner un ID de l'employé.");
            return false;
        }

        LocalDate dateAbsence = dateAbsencePicker.getValue();
        if (dateAbsence == null) {
            showAlert("Champ manquant", "La date d'absence est requise.");
            return false;
        }

        String motif = motifField.getText();
        if (motif == null || motif.trim().isEmpty()) {
            showAlert("Champ manquant", "Le motif est requis.");
            return false;
        }

        return true;
    }

    private void clearFields() {
        employeIdComboBox.setValue(null);
        dateAbsencePicker.setValue(null);
        motifField.clear();
        justifieCheckBox.setSelected(false);
        selectedAbsence = null; // Réinitialiser la sélection
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(title.equals("Erreur") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}