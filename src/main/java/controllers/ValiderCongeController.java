package controllers;

import Gestion.models.Conge;
import Gestion.services.ServiceConge;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Date;

public class ValiderCongeController {

    @FXML
    private TableView<Conge> tableConge;

    @FXML
    private TableColumn<Conge, Integer> colEmpId;

    @FXML
    private TableColumn<Conge, Date> colDebut;

    @FXML
    private TableColumn<Conge, Date> colFin;

    @FXML
    private TableColumn<Conge, String> colType;

    @FXML
    private TableColumn<Conge, String> colStatut;

    private ServiceConge serviceConge;

    @FXML
    public void initialize() {
        serviceConge = new ServiceConge();

        // Enable single selection in TableView
        tableConge.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Configure table columns
        colEmpId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getEmployeId()).asObject());
        colDebut.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateDebut()));
        colFin.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateFin()));
        colType.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
        colStatut.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut()));

        // Load data into table
        refreshTable();
    }

    @FXML
    public void accepterConge() {
        Conge selectedConge = tableConge.getSelectionModel().getSelectedItem();
        if (selectedConge == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner un congé à accepter.");
            return;
        }

        // Confirm action with the user
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Accepter le congé");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir accepter ce congé ?");
        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            selectedConge.setStatut("accepté");
            serviceConge.modifier(selectedConge);
            refreshTable();
            showAlert("Succès", "Le congé a été accepté avec succès.");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'acceptation du congé : " + e.getMessage());
        }
    }

    @FXML
    public void refuserConge() {
        Conge selectedConge = tableConge.getSelectionModel().getSelectedItem();
        if (selectedConge == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner un congé à refuser.");
            return;
        }

        // Confirm action with the user
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Refuser le congé");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir refuser ce congé ?");
        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            selectedConge.setStatut("refusée");
            serviceConge.modifier(selectedConge);
            refreshTable();
            showAlert("Succès", "Le congé a été refusé avec succès.");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du refus du congé : " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            // Clear current items to force a refresh
            tableConge.getItems().clear();
            // Fetch fresh data from the database
            tableConge.setItems(FXCollections.observableArrayList(serviceConge.afficher()));
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du rafraîchissement des congés : " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(title.equals("Erreur") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}