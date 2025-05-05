package controllers;

import Gestion.models.Conge;
import Gestion.services.ServiceConge;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DemanderCongeController {

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

    @FXML
    private TableColumn<Conge, String> colCongesRestants;

    @FXML
    private TextField empIdField;

    @FXML
    private DatePicker debutPicker;

    @FXML
    private DatePicker finPicker;

    @FXML
    private ComboBox<String> typeCombo;

    private ServiceConge serviceConge;
    private Map<Integer, Integer> congesRestantsMap; // Maps employeId to remaining congés

    @FXML
    public void initialize() {
        serviceConge = new ServiceConge();
        congesRestantsMap = new HashMap<>();

        // Enable single selection in TableView
        tableConge.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Configure table columns
        colEmpId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getEmployeId()).asObject());
        colDebut.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateDebut()));
        colFin.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateFin()));
        colType.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
        colStatut.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut()));
        colCongesRestants.setCellValueFactory(cellData -> {
            int employeId = cellData.getValue().getEmployeId();
            Integer remaining = congesRestantsMap.getOrDefault(employeId, 30);
            return new javafx.beans.property.SimpleStringProperty(remaining + "jrs");
        });

        // Load data into table
        refreshTable();
    }

    @FXML
    public void ajouterConge() {
        try {
            int employeId = Integer.parseInt(empIdField.getText());
            LocalDate debut = debutPicker.getValue();
            LocalDate fin = finPicker.getValue();
            String type = typeCombo.getValue();

            if (debut == null || fin == null || type == null || type.isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs.");
                return;
            }

            Conge conge = new Conge();
            conge.setEmployeId(employeId);
            conge.setDateDebut(Date.valueOf(debut));
            conge.setDateFin(Date.valueOf(fin));
            conge.setType(type);
            conge.setStatut("en attente");

            serviceConge.ajouter(conge);
            initializeCongesRestants(employeId);
            refreshTable();
            clearFields();
            showAlert("Succès", "Congé ajouté avec succès.");
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID de l'employé doit être un nombre.");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout du congé : " + e.getMessage());
        }
    }

    @FXML
    public void modifierConge() {
        Conge selectedConge = tableConge.getSelectionModel().getSelectedItem();
        if ( selectedConge == null) {
            showAlert("Erreur", "Veuillez sélectionner un congé à modifier.");
            return;
        }

        try {
            int employeId = Integer.parseInt(empIdField.getText());
            LocalDate debut = debutPicker.getValue();
            LocalDate fin = finPicker.getValue();
            String type = typeCombo.getValue();
            String previousStatut = selectedConge.getStatut();

            if (debut == null || fin == null || type == null || type.isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs.");
                return;
            }

            selectedConge.setEmployeId(employeId);
            selectedConge.setDateDebut(Date.valueOf(debut));
            selectedConge.setDateFin(Date.valueOf(fin));
            selectedConge.setType(type);

            serviceConge.modifier(selectedConge);
            updateCongesRestants(selectedConge, previousStatut);
            refreshTable();
            clearFields();
            showAlert("Succès", "Congé modifié avec succès.");
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID de l'employé doit être un nombre.");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification du congé : " + e.getMessage());
        }
    }

    @FXML
    public void supprimerConge() {
        Conge selectedConge = tableConge.getSelectionModel().getSelectedItem();
        if (selectedConge == null) {
            showAlert("Erreur", "Veuillez sélectionner un congé à supprimer.");
            return;
        }

        try {
            String previousStatut = selectedConge.getStatut();
            serviceConge.supprimer(selectedConge.getId());
            updateCongesRestantsAfterDeletion(selectedConge, previousStatut);
            refreshTable();
            clearFields();
            showAlert("Succès", "Congé supprimé avec succès.");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la suppression du congé : " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            tableConge.getItems().clear();
            tableConge.setItems(FXCollections.observableArrayList(serviceConge.afficher()));
            // Initialize remaining congés for all employees in the table
            for (Conge conge : tableConge.getItems()) {
                initializeCongesRestants(conge.getEmployeId());
                updateCongesRestants(conge, null);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du rafraîchissement des congés : " + e.getMessage());
        }
    }

    private void initializeCongesRestants(int employeId) {
        congesRestantsMap.putIfAbsent(employeId, 30);
    }

    private void updateCongesRestants(Conge conge, String previousStatut) {
        int employeId = conge.getEmployeId();
        String currentStatut = conge.getStatut();

        // If this is a modification and the status changed from non-"accepté" to "accepté"
        if (previousStatut != null && !previousStatut.equals("accepté") && currentStatut.equals("accepté")) {
            int currentConges = congesRestantsMap.getOrDefault(employeId, 30);
            if (currentConges > 0) {
                congesRestantsMap.put(employeId, currentConges - 1);
            }
        }
        // If this is an initial load or refresh, count all "accepté" congés
        else if (previousStatut == null && currentStatut.equals("accepté")) {
            int currentConges = congesRestantsMap.getOrDefault(employeId, 30);
            if (currentConges > 0) {
                congesRestantsMap.put(employeId, currentConges - 1);
            }
        }
    }

    private void updateCongesRestantsAfterDeletion(Conge conge, String previousStatut) {
        if (previousStatut.equals("accepté")) {
            int employeId = conge.getEmployeId();
            int currentConges = congesRestantsMap.getOrDefault(employeId, 30);
            congesRestantsMap.put(employeId, currentConges + 1);
        }
    }

    private void clearFields() {
        empIdField.clear();
        debutPicker.setValue(null);
        finPicker.setValue(null);
        typeCombo.setValue(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(title.equals("Erreur") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}