package controllers;

import Models.Formation;
import Models.ParticipationRequest;
import Services.FormationService;
import Services.ParticipationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import rh.Utils.UserSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;
import java.io.IOException;
import javafx.scene.input.KeyCode;

public class EmployeeFormationsController {
    @FXML
    private FlowPane formationsContainer;
    @FXML
    private Label statusLabel;
    @FXML
    private TextField searchField;

    private ObservableList<Formation> formations = FXCollections.observableArrayList();
    private ObservableList<Formation> allFormations = FXCollections.observableArrayList();
    private FormationService formationService;
    private ParticipationService participationService;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        formationService = new FormationService();
        participationService = new ParticipationService();
        refreshCards();
        
        // Add listener for real-time search as user types
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                // If search field is cleared, show all formations
                formations.clear();
                formations.addAll(allFormations);
                createFormationCards();
            }
        });
        
        // Add Enter key event handler for search field
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchFormations();
            }
        });
    }

    private void createFormationCards() {
        formationsContainer.getChildren().clear();
        
        if (formations.isEmpty()) {
            Label noFormationsLabel = new Label("Aucune formation disponible");
            noFormationsLabel.getStyleClass().add("placeholder-label");
            formationsContainer.getChildren().add(noFormationsLabel);
            return;
        }
        
        for (Formation formation : formations) {
            // Create card
            VBox card = new VBox(12);
            card.getStyleClass().add("card");
            card.setPadding(new Insets(20));
            card.setPrefWidth(320);
            card.setMaxWidth(320);
            card.setMinHeight(280);
            
            // Title with header bar
            HBox headerBox = new HBox();
            headerBox.setAlignment(Pos.CENTER_LEFT);
            headerBox.setSpacing(10);
            
            VBox titleBox = new VBox();
            titleBox.setMinHeight(60);
            titleBox.setMaxWidth(320);
            HBox.setHgrow(titleBox, Priority.ALWAYS);
            
            Label titleLabel = new Label(formation.getTitre());
            titleLabel.getStyleClass().add("title-main1");
            titleLabel.setWrapText(true);
            titleLabel.setMaxWidth(320);
            titleBox.getChildren().add(titleLabel);
            
            headerBox.getChildren().add(titleBox);
            
            // Description (truncated) with fixed height
            VBox descBox = new VBox();
            descBox.setMinHeight(80);
            String desc = formation.getDescription();
            if (desc != null && desc.length() > 100) {
                desc = desc.substring(0, 97) + "...";
            }
            Label descLabel = new Label(desc);
            descLabel.setWrapText(true);
            descLabel.setMaxWidth(280);
            descBox.getChildren().add(descLabel);
            
            // Separator
            Separator separator = new Separator();
            separator.setPadding(new Insets(5, 0, 5, 0));
            
            // Info items container
            VBox infoBox = new VBox(8);
            
            // Date
            HBox dateBox = new HBox(8);
            dateBox.setAlignment(Pos.CENTER_LEFT);
            Label dateLabel = new Label("Date: " + dateFormatter.format(formation.getDateDebut()));
            dateBox.getChildren().add(dateLabel);
            
            // Duration
            HBox durationBox = new HBox(8);
            durationBox.setAlignment(Pos.CENTER_LEFT);
            Label durationLabel = new Label("Durée: " + formation.getDureeJours() + " jours");
            durationBox.getChildren().add(durationLabel);
            
            // Location
            HBox locationBox = new HBox(8);
            locationBox.setAlignment(Pos.CENTER_LEFT);
            Label locationLabel = new Label("Lieu: " + formation.getLieu());
            locationBox.getChildren().add(locationLabel);
            
            // Places
            HBox placesBox = new HBox(8);
            placesBox.setAlignment(Pos.CENTER_LEFT);
            Label placesLabel = new Label(formation.getPlacesMax() + " places max");
            placesBox.getChildren().add(placesLabel);
            
            // Add info items to container
            infoBox.getChildren().addAll(dateBox, durationBox, locationBox, placesBox);
            
            // Request participation button
            Button requestButton = new Button("Demander participation");
            requestButton.getStyleClass().add("button-green");
            requestButton.setMaxWidth(Double.MAX_VALUE);
            
            // Check if the user has already requested participation for this formation
            int userId = UserSession.getInstance().getUser().getId();
            try {
                boolean alreadyRequested = participationService.hasUserRequestedParticipation(userId, formation.getId());
                if (alreadyRequested) {
                    requestButton.setText("Demande en cours");
                    requestButton.setDisable(true);
                } else {
                    requestButton.setOnAction(e -> requestParticipation(formation));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                requestButton.setDisable(true);
            }
            
            // Add elements to card
            card.getChildren().addAll(
                headerBox,
                descBox,
                separator,
                infoBox,
                requestButton
            );
            
            // Only change cursor to hand on hover, no additional styling
            card.setOnMouseEntered(e -> card.setCursor(javafx.scene.Cursor.HAND));
            card.setOnMouseExited(e -> card.setCursor(javafx.scene.Cursor.DEFAULT));
            
            // Click handler
            card.setOnMouseClicked(event -> showFormationDetails(formation));
            
            // Add card to flow pane
            formationsContainer.getChildren().add(card);
        }
    }

    private void showFormationDetails(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/employee_formation_details.fxml"));
            Parent root = loader.load();
            
            EmployeeFormationDetailsController controller = loader.getController();
            controller.setFormation(formation);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Détails de la Formation");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh data after dialog is closed
            refreshCards();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de détails : " + e.getMessage());
        }
    }

    @FXML
    private void searchFormations() {
        String query = searchField.getText().trim();
        
        if (query.isEmpty()) {
            formations.clear();
            formations.addAll(allFormations);
        } else {
            try {
                formations.clear();
                formations.addAll(formationService.searchFormations(query));
                
                if (formations.isEmpty()) {
                    statusLabel.setText("Aucun résultat trouvé pour \"" + query + "\"");
                } else {
                    statusLabel.setText(formations.size() + " formation(s) trouvée(s)");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de recherche", "Erreur lors de la recherche : " + e.getMessage());
                statusLabel.setText("Erreur de recherche");
            }
        }
        
        createFormationCards();
    }

    @FXML
    private void refreshCards() {
        try {
            statusLabel.setText("Chargement des formations...");
            
            allFormations.clear();
            allFormations.addAll(formationService.getAllFormations());
            
            formations.clear();
            formations.addAll(allFormations);
            
            createFormationCards();
            
            statusLabel.setText(formations.size() + " formation(s) disponible(s)");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Erreur lors du chargement des formations : " + e.getMessage());
            statusLabel.setText("Erreur de chargement");
        }
    }

    @FXML
    private void handleReturn() {
        try {
            returnToDashboard();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au tableau de bord : " + e.getMessage());
        }
    }
    
    @FXML
    private void showMyRequests() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/employee_my_requests.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Mes demandes de participation");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh after closing
            refreshCards();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre des demandes : " + e.getMessage());
        }
    }
    
    private void requestParticipation(Formation formation) {
        int userId = UserSession.getInstance().getUser().getId();
        
        try {
            // Create a new participation request
            ParticipationRequest request = new ParticipationRequest();
            request.setUserId(userId);
            request.setFormationId(formation.getId());
            request.setRequestDate(LocalDate.now());
            request.setStatus("En attente");
            
            participationService.createParticipationRequest(request);
            
            showAlert(Alert.AlertType.INFORMATION, "Demande envoyée", 
                    "Votre demande de participation à la formation \"" + formation.getTitre() + "\" a été envoyée avec succès.");
            
            // Refresh to update UI
            refreshCards();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'envoyer la demande : " + e.getMessage());
        }
    }
    
    private void returnToDashboard() throws IOException {
        // Navigate back to the employee main view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EmployeeMainView.fxml"));
        Parent root = loader.load();
        formationsContainer.getScene().setRoot(root);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 