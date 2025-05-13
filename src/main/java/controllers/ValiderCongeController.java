package controllers;

import Gestion.models.Conge;
import Gestion.services.ServiceConge;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.sql.Date;
import javafx.scene.shape.SVGPath;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import java.io.File;

public class ValiderCongeController {

    @FXML
    private VBox cardContainer;

    @FXML
    private HBox buttonContainer; // Ajouté pour manipuler le HBox

    @FXML
    private Button accepterCongeButton;

    @FXML
    private Button refuserCongeButton;

    @FXML
    private SVGPath checkmark;

    private ServiceConge serviceConge;
    private TrayIcon trayIcon;
    private Conge selectedConge;
    private VBox selectedCard;

    @FXML
    public void initialize() {
        serviceConge = new ServiceConge();
        // Vérifier que les éléments sont bien initialisés
        if (buttonContainer == null) {
            System.err.println("Erreur : Le HBox 'buttonContainer' n'est pas correctement initialisé.");
        } else {
            System.out.println("HBox 'buttonContainer' bien initialisé.");
            buttonContainer.setVisible(true); // Forcer la visibilité
        }
        if (accepterCongeButton == null || refuserCongeButton == null) {
            System.err.println("Erreur : Les boutons 'Accepter' ou 'Refuser' ne sont pas correctement initialisés.");
        } else {
            System.out.println("Boutons 'Accepter' et 'Refuser' bien initialisés.");
            accepterCongeButton.setVisible(true);
            refuserCongeButton.setVisible(true);
        }
        refreshTable();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(30), event -> refreshTable())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        initializeSystemTray();
    }

    private void initializeSystemTray() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            try {
                InputStream iconStream = getClass().getResourceAsStream("/images/app-icon.png");
                BufferedImage image = ImageIO.read(iconStream);
                trayIcon = new TrayIcon(image, "Gestion Congés");
                tray.add(trayIcon);
            } catch (AWTException | IOException e) {
                System.err.println("Erreur lors de l'initialisation du tray : " + e.getMessage());
            }
        }
    }

    @FXML
    public void accepterConge() {
        if (selectedConge == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une demande de congé à accepter.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Accepter la demande");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir accepter cette demande de congé ?");
        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            selectedConge.setStatut("accepté");
            serviceConge.modifier(selectedConge);
            refreshTable();
            showAlert("Succès", "Demande acceptée avec succès.");
            checkmark.setVisible(true);
            RotateTransition rotate = new RotateTransition(Duration.millis(500), checkmark);
            rotate.setByAngle(360);
            rotate.play();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'acceptation de la demande : " + e.getMessage());
        }
    }

    @FXML
    public void refuserConge() {
        if (selectedConge == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une demande de congé à refuser.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Refuser la demande");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir refuser cette demande de congé ?");
        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            selectedConge.setStatut("refusé");
            serviceConge.modifier(selectedConge);
            refreshTable();
            showAlert("Succès", "Demande refusée avec succès.");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du refus de la demande : " + e.getMessage());
        }
    }

    @FXML
    public void exportToExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Congés");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Employé ID", "Début", "Fin", "Type", "Statut"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Conge conge : serviceConge.afficher()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(conge.getEmployeId());
                row.createCell(1).setCellValue(conge.getDateDebut() != null ? conge.getDateDebut().toString() : "");
                row.createCell(2).setCellValue(conge.getDateFin() != null ? conge.getDateFin().toString() : "");
                row.createCell(3).setCellValue(conge.getType());
                row.createCell(4).setCellValue(conge.getStatut());
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer en Excel");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    workbook.write(outputStream);
                    showAlert("Succès", "Exportation en Excel réussie.");
                }
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'exportation en Excel : " + e.getMessage());
        }
    }

    @FXML
    public void exportToPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer en PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);
                document.add(new Paragraph("Liste des demandes de congés"));

                Table table = new Table(5);
                table.addCell("Employé ID");
                table.addCell("Début");
                table.addCell("Fin");
                table.addCell("Type");
                table.addCell("Statut");

                for (Conge conge : serviceConge.afficher()) {
                    table.addCell(String.valueOf(conge.getEmployeId()));
                    table.addCell(conge.getDateDebut() != null ? conge.getDateDebut().toString() : "");
                    table.addCell(conge.getDateFin() != null ? conge.getDateFin().toString() : "");
                    table.addCell(conge.getType());
                    table.addCell(conge.getStatut());
                }

                document.add(table);
                document.close();
                showAlert("Succès", "Exportation en PDF réussie.");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'exportation en PDF : " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            cardContainer.getChildren().clear();
            for (Conge conge : serviceConge.afficher()) {
                VBox card = new VBox(5);
                card.getStyleClass().add("card");
                card.getChildren().addAll(
                        new Label("Employé ID: " + conge.getEmployeId()),
                        new Label("Début: " + conge.getDateDebut()),
                        new Label("Fin: " + conge.getDateFin()),
                        new Label("Type: " + conge.getType()),
                        new Label("Statut: " + conge.getStatut())
                );
                int finalId = conge.getId();
                card.setOnMouseClicked(event -> {
                    if (selectedCard != null) {
                        selectedCard.getStyleClass().remove("selected-card");
                    }
                    selectedConge = conge;
                    selectedCard = card;
                    selectedCard.getStyleClass().add("selected-card");
                    accepterCongeButton.setDisable(false);
                    refuserCongeButton.setDisable(false);
                });
                cardContainer.getChildren().add(card);
            }
            accepterCongeButton.setDisable(true);
            refuserCongeButton.setDisable(true);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du rafraîchissement des congés : " + e.getMessage());
            System.err.println("Exception dans refreshTable : " + e.getMessage());
            e.printStackTrace();
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