package controllers;

import Gestion.models.Absence;
import Gestion.services.ServiceAbsence;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Date;
import javafx.stage.FileChooser;
import java.io.File;

public class JustifierAbsenceController {

    @FXML
    private TableView<Absence> tableAbsence;

    @FXML
    private TableColumn<Absence, Integer> colEmpId;

    @FXML
    private TableColumn<Absence, Date> colDateAbsence;

    @FXML
    private TableColumn<Absence, String> colMotif;

    @FXML
    private TableColumn<Absence, Boolean> colJustifie;

    @FXML
    private Button uploadButton;

    @FXML
    private Button justifyButton;

    private ServiceAbsence serviceAbsence;
    private File selectedImageFile;

    @FXML
    public void initialize() {
        serviceAbsence = new ServiceAbsence();

        // Enable single selection in TableView
        tableAbsence.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Configure table columns
        colEmpId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getEmployeId()).asObject());
        colDateAbsence.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateAbsence()));
        colMotif.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMotif()));
        colJustifie.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isJustifie()).asObject());

        // Load data into table
        refreshTable();

        // Add listener to enable justifyButton based on selection and image upload
        tableAbsence.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateButtonState();
        });
    }

    @FXML
    public void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        selectedImageFile = fileChooser.showOpenDialog(tableAbsence.getScene().getWindow());
        updateButtonState();
    }

    @FXML
    public void justifyAbsence() {
        Absence selectedAbsence = tableAbsence.getSelectionModel().getSelectedItem();
        if (selectedAbsence == null || selectedImageFile == null) {
            showAlert("Erreur", "Veuillez sélectionner une absence et uploader une image.");
            return;
        }

        // Confirm action with the user
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Justifier l'absence");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir justifier cette absence ?");
        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            selectedAbsence.setJustifie(true);
            serviceAbsence.modifier(selectedAbsence);
            refreshTable();
            selectedImageFile = null; // Reset the selected image
            updateButtonState();
            showAlert("Succès", "L'absence a été justifiée avec succès.");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la justification de l'absence : " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            tableAbsence.getItems().clear();
            tableAbsence.setItems(FXCollections.observableArrayList(serviceAbsence.afficher()));
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du rafraîchissement des absences : " + e.getMessage());
        }
    }

    private void updateButtonState() {
        boolean isRowSelected = tableAbsence.getSelectionModel().getSelectedItem() != null;
        boolean isImageUploaded = selectedImageFile != null;
        justifyButton.setDisable(!(isRowSelected && isImageUploaded));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(title.equals("Erreur") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}