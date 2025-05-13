package controllers;

import Gestion.models.Absence;
import Gestion.services.ServiceAbsence;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import javafx.scene.shape.SVGPath;
import javafx.animation.RotateTransition;
import javafx.util.Duration;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class JustifierAbsenceController {

    @FXML
    private VBox cardContainer;

    @FXML
    private Button uploadButton;

    @FXML
    private Button justifyButton;

    @FXML
    private SVGPath checkmark;

    private ServiceAbsence serviceAbsence;
    private File selectedImageFile;
    private TrayIcon trayIcon;
    private Absence selectedAbsence; // Ajouté pour stocker l'absence sélectionnée
    private VBox selectedCard; // Ajouté pour gérer l'indicateur visuel
    private boolean isImageValid; // Ajouté pour suivre la validité de l'image

    @FXML
    public void initialize() {
        serviceAbsence = new ServiceAbsence();
        refreshTable();
        uploadButton.setOnAction(event -> chooseImage());
        justifyButton.setOnAction(event -> justifyAbsence());
        initializeSystemTray();
        justifyButton.setDisable(true); // Désactiver par défaut
        isImageValid = false; // Initialiser à false
    }

    private void initializeSystemTray() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            try {
                InputStream iconStream = getClass().getResourceAsStream("/images/app-icon.png");
                BufferedImage image = ImageIO.read(iconStream);
                trayIcon = new TrayIcon(image, "Gestion Absences");
                tray.add(trayIcon);
            } catch (AWTException | IOException e) {
                System.err.println("Erreur lors de l'initialisation du tray : " + e.getMessage());
            }
        }
    }

    @FXML
    public void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;

            // Vérification OCR
            Tesseract tesseract = new Tesseract();
            String tessDataPath = "C:/Users/khale/Downloads/tesseract-main/tessdata"; // Chemin vers le dossier tessdata
            Path tessDataDir = Path.of(tessDataPath);
            if (!Files.exists(tessDataDir) || !Files.isDirectory(tessDataDir)) {
                showAlert("Erreur", "Le dossier tessdata (" + tessDataPath + ") n'existe pas ou ne contient pas les fichiers de langue. Créez ce dossier et ajoutez fra.traineddata.");
                isImageValid = false;
                uploadButton.setStyle("-fx-background-color: #DC3545;");
                justifyButton.setDisable(true);
                return;
            }
            tesseract.setDatapath(tessDataPath);
            tesseract.setLanguage("fra"); // Utiliser le français
            try {
                String text = tesseract.doOCR(selectedImageFile);
                if (text == null || text.trim().isEmpty()) {
                    throw new TesseractException("Aucun texte détecté dans l'image.");
                }
                // Journaliser le texte extrait pour déboguer
                System.out.println("Texte extrait par OCR : " + text);

                // Normaliser le texte pour gérer les accents et problèmes d'encodage
                String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "")
                        .toLowerCase();
                System.out.println("Texte normalisé : " + normalizedText);

                // Assouplir les critères de validation
                boolean hasCertificat = normalizedText.contains("certificat");
                boolean hasMedical = normalizedText.contains("medical");
                boolean hasDocteur = normalizedText.contains("docteur") || normalizedText.contains("dr");
                // Accepter différents formats de date (ex. : 12/05/2025, 12-05-2025, 12.05.2025)
                boolean hasDate = Pattern.compile("\\d{2}[/\\-.]\\d{2}[/\\-.]\\d{4}")
                        .matcher(normalizedText)
                        .find();


                // Journaliser les résultats des vérifications
                System.out.println("Critères de validation : ");
                System.out.println("- Certificat détecté : " + hasCertificat);
                System.out.println("- Médical détecté : " + hasMedical);
                System.out.println("- Docteur détecté : " + hasDocteur);
                System.out.println("- Date détectée : " + hasDate);

                if (hasCertificat && hasMedical && hasDocteur && hasDate) {
                    isImageValid = true;
                    uploadButton.setStyle("-fx-background-color: #28A745;");
                    showAlert("Succès", "Certificat médical valide détecté.");
                    if (selectedAbsence != null) {
                        justifyButton.setDisable(false); // Activer le bouton si une absence est sélectionnée
                    }
                } else {
                    isImageValid = false;
                    uploadButton.setStyle("-fx-background-color: #DC3545;"); // Rouge pour indiquer une erreur
                    StringBuilder errorMessage = new StringBuilder("Le document uploadé ne semble pas être un certificat médical valide. Raisons :");
                    if (!hasCertificat) errorMessage.append("\n- Mot 'certificat' non détecté.");
                    if (!hasMedical) errorMessage.append("\n- Mot 'médical' ou 'medical' non détecté.");
                    if (!hasDocteur) errorMessage.append("\n- Mot 'docteur' ou 'Dr' non détecté.");
                    if (!hasDate) errorMessage.append("\n- Date au format jj/mm/aaaa, jj-mm-aaaa ou jj.mm.aaaa non détectée.");
                    showAlert("Erreur", errorMessage.toString());
                    justifyButton.setDisable(true); // Désactiver le bouton si l'image est invalide
                }
            } catch (TesseractException e) {
                isImageValid = false;
                uploadButton.setStyle("-fx-background-color: #DC3545;");
                showAlert("Erreur", "Erreur OCR : " + e.getMessage() + "\nVérifiez que le dossier tessdata (" + tessDataPath + ") contient fra.traineddata.");
                System.err.println("Exception OCR : " + e.getMessage());
                justifyButton.setDisable(true);
            }
        } else {
            isImageValid = false;
            uploadButton.setStyle("-fx-background-color: #007BFF;"); // Remettre à la couleur par défaut
            justifyButton.setDisable(true);
        }
    }

    @FXML
    public void justifyAbsence() {
        if (selectedAbsence == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une absence à justifier.");
            return;
        }
        if (selectedImageFile == null || !isImageValid) {
            showAlert("Erreur", "Veuillez uploader un certificat médical valide avant de justifier.");
            return;
        }

        try {
            selectedAbsence.setJustifie(true);
            serviceAbsence.modifier(selectedAbsence);
            refreshTable();
            showAlert("Succès", "Absence justifiée avec succès.");
            checkmark.setVisible(true);
            RotateTransition rotate = new RotateTransition(Duration.millis(500), checkmark);
            rotate.setByAngle(360);
            rotate.play();
            if (trayIcon != null) {
                trayIcon.displayMessage("Succès", "Absence justifiée avec succès.", TrayIcon.MessageType.INFO);
            }
            selectedAbsence = null; // Réinitialiser la sélection
            selectedImageFile = null; // Réinitialiser l'image
            isImageValid = false; // Réinitialiser la validité
            uploadButton.setStyle("-fx-background-color: #007BFF;"); // Remettre à la couleur par défaut
            justifyButton.setDisable(true);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la justification de l'absence : " + e.getMessage());
        }
    }

    @FXML
    public void exportToExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Absences");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Employé ID", "Date Absence", "Motif", "Justifiée?"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Absence absence : serviceAbsence.afficher()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(absence.getEmployeId());
                row.createCell(1).setCellValue(absence.getDateAbsence() != null ? absence.getDateAbsence().toString() : "");
                row.createCell(2).setCellValue(absence.getMotif());
                row.createCell(3).setCellValue(absence.isJustifie());
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
                document.add(new Paragraph("Liste des absences"));

                Table table = new Table(4);
                table.addCell("Employé ID");
                table.addCell("Date Absence");
                table.addCell("Motif");
                table.addCell("Justifiée?");

                for (Absence absence : serviceAbsence.afficher()) {
                    table.addCell(String.valueOf(absence.getEmployeId()));
                    table.addCell(absence.getDateAbsence() != null ? absence.getDateAbsence().toString() : "");
                    table.addCell(absence.getMotif());
                    table.addCell(String.valueOf(absence.isJustifie()));
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
            for (Absence absence : serviceAbsence.afficher()) {
                VBox card = new VBox(5);
                card.getStyleClass().add("card");
                card.getChildren().addAll(
                        new Label("Employé ID: " + absence.getEmployeId()),
                        new Label("Date Absence: " + absence.getDateAbsence()),
                        new Label("Motif: " + absence.getMotif()),
                        new Label("Justifiée?: " + absence.isJustifie())
                );
                // Ajouter un gestionnaire d'événements pour gérer la sélection
                card.setOnMouseClicked(event -> {
                    // Réinitialiser le style de la carte précédemment sélectionnée
                    if (selectedCard != null) {
                        selectedCard.getStyleClass().remove("selected-card");
                    }
                    // Mettre à jour la nouvelle sélection
                    selectedAbsence = absence;
                    selectedCard = card;
                    selectedCard.getStyleClass().add("selected-card");
                    if (selectedImageFile != null && isImageValid) {
                        justifyButton.setDisable(false);
                    } else {
                        justifyButton.setDisable(true);
                    }
                });
                cardContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du rafraîchissement des absences : " + e.getMessage());
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