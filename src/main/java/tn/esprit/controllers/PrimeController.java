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
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.esprit.entities.Prime;
import tn.esprit.entities.Salaire;
import tn.esprit.services.PrimeService;
import tn.esprit.services.SalaireService;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class PrimeController implements Initializable {

    @FXML
    private ComboBox<String> comboType;

    @FXML
    private TextField txtMontant;

    @FXML
    private ComboBox<Salaire> comboSalaire;

    @FXML
    private Label lblTypeError;

    @FXML
    private Label lblMontantError;

    @FXML
    private Label lblSalaireError;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button btnRetour;

    @FXML
    private TableView<Prime> tablePrimes;

    @FXML
    private TableColumn<Prime, Integer> colId;

    @FXML
    private TableColumn<Prime, String> colType;

    @FXML
    private TableColumn<Prime, Double> colMontant;

    @FXML
    private TableColumn<Prime, Integer> colSalaireId;

    @FXML
    private VBox primeCardsContainer;

    private PrimeService primeService;
    private SalaireService salaireService;
    private ObservableList<Prime> primesObservableList;
    private Prime selectedPrime;
    
    // Types de primes prédéfinis
    private final String[] TYPES_PRIMES = {"Performance", "Ancienneté", "Responsabilité", "Transport", "Logement", "Autre"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        primeService = new PrimeService();
        salaireService = new SalaireService();
        
        // Set up table columns (keeping this for potential future use)
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colSalaireId.setCellValueFactory(new PropertyValueFactory<>("salaireId"));
        
        // Fill combo boxes
        comboType.setItems(FXCollections.observableArrayList(Arrays.asList(TYPES_PRIMES)));
        
        loadSalaires();
        loadPrimes();
        
        // Clear error labels initially
        resetErrorLabels();
        
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
    
    private void loadPrimes() {
        List<Prime> primes = primeService.getAll();
        primesObservableList = FXCollections.observableArrayList(primes);
        
        // Clear existing cards
        if (primeCardsContainer != null) {
            primeCardsContainer.getChildren().clear();
            
            // Create card for each prime
            for (Prime prime : primes) {
                primeCardsContainer.getChildren().add(createPrimeCard(prime));
            }
        } else {
            System.err.println("Warning: primeCardsContainer is null in PrimeController");
        }
    }
    
    private Pane createPrimeCard(Prime prime) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #253c5c; -fx-background-radius: 8;");
        card.setPrefWidth(400);
        
        // Add shadow effect
        card.setEffect(new javafx.scene.effect.DropShadow(5, Color.rgb(0, 0, 0, 0.5)));
        
        // Header with ID and Type
        HBox header = new HBox();
        header.setSpacing(10);
        
        Label lblId = new Label("#" + prime.getId());
        lblId.setStyle("-fx-text-fill: #ee8913; -fx-font-weight: bold;");
        lblId.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Label lblType = new Label(prime.getType());
        lblType.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        lblType.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        header.getChildren().addAll(lblId, lblType);
        
        // Separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #ee8913;");
        
        // Montant
        HBox montantBox = new HBox();
        montantBox.setSpacing(10);
        
        Label lblMontantTitle = new Label("Montant:");
        lblMontantTitle.setStyle("-fx-text-fill: #ee8913;");
        
        Label lblMontantValue = new Label(String.format("%.2f €", prime.getMontant()));
        lblMontantValue.setStyle("-fx-text-fill: white;");
        
        montantBox.getChildren().addAll(lblMontantTitle, lblMontantValue);
        
        // Salaire associé
        HBox salaireBox = new HBox();
        salaireBox.setSpacing(10);
        
        Label lblSalaireTitle = new Label("ID Salaire:");
        lblSalaireTitle.setStyle("-fx-text-fill: #ee8913;");
        
        // Get employee name for this salaire ID
        String employeName = "Inconnu";
        for (Salaire salaire : comboSalaire.getItems()) {
            if (salaire.getId() == prime.getSalaireId()) {
                employeName = salaire.getEmploye();
                break;
            }
        }
        
        Label lblSalaireValue = new Label(prime.getSalaireId() + " - " + employeName);
        lblSalaireValue.setStyle("-fx-text-fill: white;");
        
        salaireBox.getChildren().addAll(lblSalaireTitle, lblSalaireValue);
        
        // Action buttons
        HBox actionsBox = new HBox();
        actionsBox.setSpacing(10);
        
        Button btnEdit = new Button("Modifier");
        btnEdit.setStyle("-fx-background-color: #ee8913; -fx-text-fill: white;");
        btnEdit.setOnAction(e -> {
            selectPrime(prime);
        });
        
        Button btnDelete = new Button("Supprimer");
        btnDelete.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white;");
        btnDelete.setOnAction(e -> {
            selectedPrime = prime;
            handleSupprimer(e);
        });
        
        actionsBox.getChildren().addAll(btnEdit, btnDelete);
        
        // Add all elements to card
        card.getChildren().addAll(header, separator, montantBox, salaireBox, actionsBox);
        
        return card;
    }
    
    private void selectPrime(Prime prime) {
        selectedPrime = prime;
        comboType.setValue(prime.getType());
        txtMontant.setText(String.valueOf(prime.getMontant()));
        
        // Find and select the corresponding salaire in combobox
        for (Salaire salaire : comboSalaire.getItems()) {
            if (salaire.getId() == prime.getSalaireId()) {
                comboSalaire.setValue(salaire);
                break;
            }
        }
        
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
            String type = comboType.getValue();
            double montant = Double.parseDouble(txtMontant.getText().trim());
            Salaire salaire = comboSalaire.getValue();
            
            Prime prime = new Prime(type, montant, salaire.getId());
            primeService.add(prime);
            
            clearFields();
            loadPrimes();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Prime ajoutée avec succès.");
            
        } catch (NumberFormatException e) {
            lblMontantError.setText("Le montant doit être un nombre");
            lblMontantError.setVisible(true);
        }
    }

    @FXML
    void handleModifier(ActionEvent event) {
        if (selectedPrime == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner une prime à modifier.");
            return;
        }
        
        if (!validateInputs()) {
            return;
        }
        
        try {
            String type = comboType.getValue();
            double montant = Double.parseDouble(txtMontant.getText().trim());
            Salaire salaire = comboSalaire.getValue();
            
            selectedPrime.setType(type);
            selectedPrime.setMontant(montant);
            selectedPrime.setSalaireId(salaire.getId());
            
            primeService.update(selectedPrime);
            
            clearFields();
            loadPrimes();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Prime modifiée avec succès.");
            
        } catch (NumberFormatException e) {
            lblMontantError.setText("Le montant doit être un nombre");
            lblMontantError.setVisible(true);
        }
    }

    @FXML
    void handleSupprimer(ActionEvent event) {
        if (selectedPrime == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner une prime à supprimer.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Supprimer la prime");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette prime ?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            primeService.delete(selectedPrime);
            clearFields();
            loadPrimes();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Prime supprimée avec succès.");
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
        
        // Validate type
        if (comboType.getValue() == null) {
            lblTypeError.setText("Veuillez sélectionner un type de prime");
            lblTypeError.setVisible(true);
            isValid = false;
        }
        
        // Validate montant
        try {
            double montant = Double.parseDouble(txtMontant.getText().trim());
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
        
        // Validate salaire
        if (comboSalaire.getValue() == null) {
            lblSalaireError.setText("Veuillez sélectionner un salaire");
            lblSalaireError.setVisible(true);
            isValid = false;
        }
        
        return isValid;
    }
    
    private void resetErrorLabels() {
        lblTypeError.setVisible(false);
        lblMontantError.setVisible(false);
        lblSalaireError.setVisible(false);
    }
    
    private void clearFields() {
        comboType.setValue(null);
        txtMontant.clear();
        comboSalaire.setValue(null);
        selectedPrime = null;
        btnAjouter.setText("Ajouter");
        btnModifier.setDisable(false);
        resetErrorLabels();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 