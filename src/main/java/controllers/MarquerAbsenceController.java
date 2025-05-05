package controllers;

import Gestion.models.Absence;
import Gestion.services.ServiceAbsence;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Date;
import java.time.LocalDate;

public class MarquerAbsenceController {

    @FXML
    private TextField employeIdField;

    @FXML
    private DatePicker dateAbsencePicker;

    @FXML
    private TextField motifField;

    @FXML
    private CheckBox justifieCheckBox;

    @FXML
    private TableView<Absence> tableAbsence;

    @FXML
    private TableColumn<Absence, Integer> colEmpId;

    @FXML
    private TableColumn<Absence, Date> colDate;

    @FXML
    private TableColumn<Absence, String> colMotif;

    @FXML
    private TableColumn<Absence, Boolean> colJustifie;

    private ServiceAbsence serviceAbsence;

    private boolean isManualRefresh = false; // Flag to track manual refresh

    @FXML
    public void initialize() {
        serviceAbsence = new ServiceAbsence();

        // Enable single selection in TableView
        tableAbsence.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Configure table columns
        colEmpId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getEmployeId()).asObject());
        colDate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateAbsence()));
        colMotif.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMotif()));
        colJustifie.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isJustifie()).asObject());

        // Load data into table without showing success banner
        rafraichirAbsence();

        // Add listener to pre-fill fields when a row is selected
        tableAbsence.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                employeIdField.setText(String.valueOf(newSelection.getEmployeId()));
                dateAbsencePicker.setValue(newSelection.getDateAbsence() != null ? newSelection.getDateAbsence().toLocalDate() : null);
                motifField.setText(newSelection.getMotif() != null ? newSelection.getMotif() : "");
                justifieCheckBox.setSelected(newSelection.isJustifie());
            } else {
                clearFields();
            }
        });
    }

    @FXML
    public void ajouterAbsence() {
        if (!validateInputs()) {
            return;
        }
        try {
            int employeId = Integer.parseInt(employeIdField.getText().trim());
            Date dateAbsence = Date.valueOf(dateAbsencePicker.getValue());
            String motif = motifField.getText().trim();
            boolean justifie = justifieCheckBox.isSelected();

            Absence absence = new Absence(0, employeId, dateAbsence, motif, justifie);
            serviceAbsence.ajouter(absence);

            // Refresh table and clear fields
            rafraichirAbsence();
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout de l'absence : " + e.getMessage());
        }
    }

    @FXML
    public void modifierAbsence() {
        Absence selectedAbsence = tableAbsence.getSelectionModel().getSelectedItem();
        if (selectedAbsence == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une absence à modifier.");
            return;
        }
        if (!validateInputs()) {
            return;
        }

        // Confirm modification with the user
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Modifier l'absence");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir modifier cette absence ?");
        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            int employeId = Integer.parseInt(employeIdField.getText().trim());
            Date dateAbsence = Date.valueOf(dateAbsencePicker.getValue());
            String motif = motifField.getText().trim();
            boolean justifie = justifieCheckBox.isSelected();

            Absence updatedAbsence = new Absence(selectedAbsence.getId(), employeId, dateAbsence, motif, justifie);
            serviceAbsence.modifier(updatedAbsence);

            // Refresh table and clear fields
            rafraichirAbsence();
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification de l'absence : " + e.getMessage());
        }
    }

    @FXML
    public void supprimerAbsence() {
        Absence selectedAbsence = tableAbsence.getSelectionModel().getSelectedItem();
        if (selectedAbsence == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une absence à supprimer.");
            return;
        }

        // Confirm deletion with the user
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
            // Clear current items to force a refresh
            tableAbsence.getItems().clear();
            // Fetch fresh data from the database
            tableAbsence.setItems(FXCollections.observableArrayList(serviceAbsence.afficher()));
            // Show success banner only for manual refresh (via Rafraîchir button)
            if (isManualRefresh) {
                showAlert("Succès", "La liste des absences a été rafraîchie avec succès.");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du rafraîchissement des absences : " + e.getMessage());
        } finally {
            // Reset the flag after each refresh
            isManualRefresh = false;
        }
    }

    // Wrapper method for the Rafraîchir button to set the flag
    @FXML
    public void onRafraichirButtonClicked() {
        isManualRefresh = true;
        rafraichirAbsence();
    }

    private boolean validateInputs() {
        // Check if employeIdField is empty or not a valid integer
        String employeIdText = employeIdField.getText();
        if (employeIdText == null || employeIdText.trim().isEmpty()) {
            showAlert("Champ manquant", "L'ID de l'employé est requis.");
            return false;
        }
        try {
            int employeId = Integer.parseInt(employeIdText.trim());
            if (employeId <= 0) {
                showAlert("Entrée invalide", "L'ID de l'employé doit être un nombre positif.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Entrée invalide", "L'ID de l'employé doit être un nombre entier valide.");
            return false;
        }

        // Check if dateAbsencePicker is empty
        LocalDate dateAbsence = dateAbsencePicker.getValue();
        if (dateAbsence == null) {
            showAlert("Champ manquant", "La date d'absence est requise.");
            return false;
        }

        // Check if motifField is empty
        String motif = motifField.getText();
        if (motif == null || motif.trim().isEmpty()) {
            showAlert("Champ manquant", "Le motif est requis.");
            return false;
        }

        return true;
    }

    private void clearFields() {
        employeIdField.clear();
        dateAbsencePicker.setValue(null);
        motifField.clear();
        justifieCheckBox.setSelected(false);
        tableAbsence.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(title.equals("Erreur") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}