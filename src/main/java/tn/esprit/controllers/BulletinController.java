package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.esprit.entities.Salaire;
import tn.esprit.services.SalaireService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.stage.FileChooser;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BulletinController implements Initializable {

    @FXML
    private ComboBox<Salaire> comboSalaire;

    @FXML
    private Button btnGenerer;

    @FXML
    private TextArea txtBulletin;

    @FXML
    private Button btnImprimer;

    @FXML
    private Button btnRetour;

    private SalaireService salaireService;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        salaireService = new SalaireService();
        
        // Load salaires into combo box
        loadSalaires();
        
        // Setup salaire combobox display
        comboSalaire.setConverter(new StringConverter<Salaire>() {
            @Override
            public String toString(Salaire salaire) {
                return salaire == null ? "" : salaire.getId() + " - " + salaire.getEmploye();
            }

            @Override
            public Salaire fromString(String string) {
                return null; // Not needed for this use case
            }
        });
    }
    
    private void loadSalaires() {
        List<Salaire> salaires = salaireService.getAll();
        comboSalaire.setItems(FXCollections.observableArrayList(salaires));
    }

    @FXML
    void handleGenerer(ActionEvent event) {
        Salaire salaire = comboSalaire.getValue();
        
        if (salaire == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un salaire.");
            return;
        }
        
        String bulletin = salaireService.genererBulletin(salaire.getId());
        txtBulletin.setText(bulletin);
    }

    @FXML
    void handleImprimer(ActionEvent event) {
        if (txtBulletin.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucun bulletin", "Veuillez d'abord générer un bulletin.");
            return;
        }
        
        // Simulation d'impression
        showAlert(Alert.AlertType.INFORMATION, "Impression", "Le bulletin a été envoyé à l'imprimante.");
    }

    @FXML
    void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleExporterExcel(ActionEvent event) {
        if (txtBulletin.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucun bulletin", "Veuillez d'abord générer un bulletin.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le bulletin en Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier Excel", "*.xlsx"));
        java.io.File file = fileChooser.showSaveDialog(txtBulletin.getScene().getWindow());
        if (file == null) return;
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Bulletin de Paie");
            String[] lines = txtBulletin.getText().split("\n");
            int rowNum = 0;
            for (String line : lines) {
                Row row = sheet.createRow(rowNum++);
                org.apache.poi.ss.usermodel.Cell cell = row.createCell(0);
                cell.setCellValue(line);
            }
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            showAlert(Alert.AlertType.INFORMATION, "Export réussi", "Le bulletin a été exporté en Excel.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'export: " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 