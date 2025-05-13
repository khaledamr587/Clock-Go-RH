package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.esprit.entities.Salaire;
import tn.esprit.services.SalaireService;
import tn.esprit.utils.EmailService;
import tn.esprit.utils.SmsService;
import java.util.regex.Pattern;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class SalaireController implements Initializable {

    @FXML
    private TextField txtEmploye;

    @FXML
    private TextField txtMontantBase;

    @FXML
    private DatePicker dateVersement;

    @FXML
    private Label lblEmployeError;

    @FXML
    private Label lblMontantError;

    @FXML
    private Label lblDateError;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button btnRetour;

    @FXML
    private TableView<Salaire> tableSalaires;

    @FXML
    private TableColumn<Salaire, Integer> colId;

    @FXML
    private TableColumn<Salaire, String> colEmploye;

    @FXML
    private TableColumn<Salaire, Double> colMontant;

    @FXML
    private TableColumn<Salaire, Date> colDate;

    @FXML
    private VBox salaireCardsContainer;

    @FXML
    private TextField txtEmail;

    @FXML
    private Label lblEmailError;

    @FXML
    private TextField txtPhone;

    @FXML
    private Label lblPhoneError;

    private SalaireService salaireService;
    private ObservableList<Salaire> salairesObservableList;
    private Salaire selectedSalaire;
    private EmailService emailService;
    private SmsService smsService;
    private static final String GMAIL_USERNAME = "hamzaznaidi539@gmail.com";
    private static final String GMAIL_APP_PASSWORD = "xxgn diez viub wcrw";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final String TWILIO_SID = "ACd60448024735d32fa9cb49cbb9182e82";
    private static final String TWILIO_TOKEN = "69743123c495dab9b8b3c95b3d014e70";
    private static final String TWILIO_MESSAGING_SID = "MGd074e4551d8b7d4e64ead0bc23eae2c7";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        salaireService = new SalaireService();
        
        // Initialize date picker formatter
        dateVersement.setConverter(new StringConverter<>() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate date) {
                return date != null ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() 
                        ? LocalDate.parse(string, dateFormatter) 
                        : null;
            }
        });
        
        // Set up table columns (keeping this for potential future use)
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEmploye.setCellValueFactory(new PropertyValueFactory<>("employe"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montantBase"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateVersement"));
        
        // Load salaries into the card view
        loadSalaires();
        
        // Clear error labels initially
        resetErrorLabels();
        
        emailService = new EmailService(GMAIL_USERNAME, GMAIL_APP_PASSWORD);
        smsService = new SmsService(TWILIO_SID, TWILIO_TOKEN, TWILIO_MESSAGING_SID);
    }
    
    private void loadSalaires() {
        List<Salaire> salaires = salaireService.getAll();
        salairesObservableList = FXCollections.observableArrayList(salaires);
        
        // Clear existing cards
        if (salaireCardsContainer != null) {
            salaireCardsContainer.getChildren().clear();
            
            // Create card for each salaire
            for (Salaire salaire : salaires) {
                salaireCardsContainer.getChildren().add(createSalaireCard(salaire));
            }
        } else {
            System.err.println("Warning: salaireCardsContainer is null in SalaireController");
        }
    }
    
    private Pane createSalaireCard(Salaire salaire) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #253c5c; -fx-background-radius: 8;");
        card.setPrefWidth(400);
        
        // Add shadow effect
        card.setEffect(new javafx.scene.effect.DropShadow(5, Color.rgb(0, 0, 0, 0.5)));
        
        // ID and Employee in header
        HBox header = new HBox();
        header.setSpacing(10);
        
        Label lblId = new Label("#" + salaire.getId());
        lblId.setStyle("-fx-text-fill: #ee8913; -fx-font-weight: bold;");
        lblId.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Label lblEmployee = new Label(salaire.getEmploye());
        lblEmployee.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        lblEmployee.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        header.getChildren().addAll(lblId, lblEmployee);
        
        // Separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #ee8913;");
        
        // Montant
        HBox montantBox = new HBox();
        montantBox.setSpacing(10);
        
        Label lblMontantTitle = new Label("Montant de base:");
        lblMontantTitle.setStyle("-fx-text-fill: #ee8913;");
        
        Label lblMontantValue = new Label(String.format("%.2f €", salaire.getMontantBase()));
        lblMontantValue.setStyle("-fx-text-fill: white;");
        
        montantBox.getChildren().addAll(lblMontantTitle, lblMontantValue);
        
        // Date
        HBox dateBox = new HBox();
        dateBox.setSpacing(10);
        
        Label lblDateTitle = new Label("Date de versement:");
        lblDateTitle.setStyle("-fx-text-fill: #ee8913;");
        
        Label lblDateValue = new Label(salaire.getDateVersement().toString());
        lblDateValue.setStyle("-fx-text-fill: white;");
        
        dateBox.getChildren().addAll(lblDateTitle, lblDateValue);
        
        // Action buttons
        HBox actionsBox = new HBox();
        actionsBox.setSpacing(10);
        
        Button btnEdit = new Button("Modifier");
        btnEdit.setStyle("-fx-background-color: #ee8913; -fx-text-fill: white;");
        btnEdit.setOnAction(e -> {
            selectSalaire(salaire);
        });
        
        Button btnDelete = new Button("Supprimer");
        btnDelete.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white;");
        btnDelete.setOnAction(e -> {
            selectedSalaire = salaire;
            handleSupprimer(e);
        });
        
        Button btnPrimes = new Button("Voir Primes");
        btnPrimes.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");
        btnPrimes.setOnAction(e -> {
            // TODO: Show primes for this salaire
            showAlert(Alert.AlertType.INFORMATION, "Information", "Cette fonctionnalité sera disponible prochainement.");
        });
        
        actionsBox.getChildren().addAll(btnEdit, btnDelete, btnPrimes);
        
        // Add all elements to card
        card.getChildren().addAll(header, separator, montantBox, dateBox, actionsBox);
        
        return card;
    }
    
    private void selectSalaire(Salaire salaire) {
        selectedSalaire = salaire;
        txtEmploye.setText(salaire.getEmploye());
        txtMontantBase.setText(String.valueOf(salaire.getMontantBase()));
        dateVersement.setValue(salaire.getDateVersement().toLocalDate());
        
        // Change button text to show we're in edit mode
        btnAjouter.setText("Annuler");
        btnModifier.setDisable(false);
    }

    @FXML
    void handleAjouter(ActionEvent event) {
        // If we're in edit mode, this button acts as a cancel button
        if (btnAjouter.getText().equals("Annuler")) {
            clearFields();
            btnAjouter.setText("Ajouter");
            btnModifier.setDisable(false);
            return;
        }
        
        if (!validateInputs()) {
            return;
        }
        
        try {
            String employe = txtEmploye.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            double montantBase = Double.parseDouble(txtMontantBase.getText().trim());
            LocalDate localDate = dateVersement.getValue();
            
            Salaire salaire = new Salaire(montantBase, Date.valueOf(localDate), employe);
            salaireService.add(salaire);
            
            // Envoyer l'email de confirmation
            String subject = "Confirmation d'ajout de salaire - DOC4U";
            String body = String.format("Cher(e) %s,\n\n" +
                    "Nous vous confirmons l'ajout de votre salaire de base d'un montant de %.2f € pour la période du %s.\n\n" +
                    "Cordialement,\n" +
                    "L'équipe DOC4U", 
                    employe, montantBase, localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            boolean emailSent = emailService.sendEmail(email, subject, body);
            String smsBody = String.format("Bonjour %s, votre salaire de %.2f € a été ajouté pour la date %s.", employe, montantBase, localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            boolean smsSent = smsService.sendSms(phone, smsBody);
            
            clearFields();
            loadSalaires();
            
            if (emailSent && smsSent) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Salaire ajouté, email et SMS envoyés.");
            } else if (emailSent) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Salaire ajouté, email envoyé mais SMS échoué.");
            } else if (smsSent) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Salaire ajouté, SMS envoyé mais email échoué.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Attention", "Salaire ajouté mais email et SMS échoués.");
            }
            
        } catch (NumberFormatException e) {
            lblMontantError.setText("Le montant doit être un nombre");
            lblMontantError.setVisible(true);
        }
    }

    @FXML
    void handleModifier(ActionEvent event) {
        if (selectedSalaire == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un salaire à modifier.");
            return;
        }
        
        if (!validateInputs()) {
            return;
        }
        
        try {
            String employe = txtEmploye.getText().trim();
            double montantBase = Double.parseDouble(txtMontantBase.getText().trim());
            LocalDate localDate = dateVersement.getValue();
            
            selectedSalaire.setEmploye(employe);
            selectedSalaire.setMontantBase(montantBase);
            selectedSalaire.setDateVersement(Date.valueOf(localDate));
            
            salaireService.update(selectedSalaire);
            
            clearFields();
            loadSalaires();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Salaire modifié avec succès.");
            
        } catch (NumberFormatException e) {
            lblMontantError.setText("Le montant doit être un nombre");
            lblMontantError.setVisible(true);
        }
    }

    @FXML
    void handleSupprimer(ActionEvent event) {
        if (selectedSalaire == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un salaire à supprimer.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Supprimer le salaire");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce salaire ?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            salaireService.delete(selectedSalaire);
            clearFields();
            loadSalaires();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Salaire supprimé avec succès.");
        }
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
    
    private boolean validateInputs() {
        boolean isValid = true;
        resetErrorLabels();
        
        // Validate employé
        if (txtEmploye.getText().trim().isEmpty()) {
            lblEmployeError.setText("Le nom de l'employé est requis");
            lblEmployeError.setVisible(true);
            isValid = false;
        } else if (txtEmploye.getText().trim().length() < 3) {
            lblEmployeError.setText("Le nom doit contenir au moins 3 caractères");
            lblEmployeError.setVisible(true);
            isValid = false;
        }
        
        // Validate montant
        try {
            double montant = Double.parseDouble(txtMontantBase.getText().trim());
            if (montant <= 0) {
                lblMontantError.setText("Le montant doit être positif");
                lblMontantError.setVisible(true);
                isValid = false;
            }
        } catch (NumberFormatException e) {
            lblMontantError.setText("Veuillez entrer un montant valide");
            lblMontantError.setVisible(true);
            isValid = false;
        }
        
        // Validate date
        if (dateVersement.getValue() == null) {
            lblDateError.setText("La date de versement est requise");
            lblDateError.setVisible(true);
            isValid = false;
        } else if (dateVersement.getValue().isAfter(LocalDate.now())) {
            lblDateError.setText("La date ne peut pas être dans le futur");
            lblDateError.setVisible(true);
            isValid = false;
        }
        
        // Validate email
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            lblEmailError.setText("L'email est requis");
            lblEmailError.setVisible(true);
            isValid = false;
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            lblEmailError.setText("Format d'email invalide");
            lblEmailError.setVisible(true);
            isValid = false;
        }
        
        // Validate phone
        String phone = txtPhone.getText().trim();
        if (phone.isEmpty() || !phone.matches("^\\+\\d{8,15}$")) {
            lblPhoneError.setText("Numéro invalide");
            lblPhoneError.setVisible(true);
            isValid = false;
        }
        
        return isValid;
    }
    
    private void resetErrorLabels() {
        lblEmployeError.setVisible(false);
        lblMontantError.setVisible(false);
        lblDateError.setVisible(false);
        lblEmailError.setVisible(false);
        lblPhoneError.setVisible(false);
    }
    
    private void clearFields() {
        txtEmploye.clear();
        txtMontantBase.clear();
        dateVersement.setValue(null);
        selectedSalaire = null;
        btnAjouter.setText("Ajouter");
        btnModifier.setDisable(false);
        resetErrorLabels();
        txtEmail.clear();
        txtPhone.clear();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 