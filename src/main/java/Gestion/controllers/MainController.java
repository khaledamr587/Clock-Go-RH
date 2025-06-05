package Gestion.controllers;

import Gestion.models.Formation;
import Gestion.services.FormationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;
import java.io.IOException;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

public class MainController {
    @FXML
    private FlowPane formationsContainer;
    @FXML
    private Label statusLabel;
    @FXML
    private TextField searchField;

    private ObservableList<Formation> formations = FXCollections.observableArrayList();
    private ObservableList<Formation> allFormations = FXCollections.observableArrayList();
    private boolean isAdmin = true; // For demo purposes, set to true
    private FormationService formationService;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        formationService = new FormationService();
        refreshCards();
        updateUIForRole();
        
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
            
            // Title with header bar containing title and options menu
            HBox headerBox = new HBox();
            headerBox.setAlignment(Pos.CENTER_LEFT);
            headerBox.setSpacing(10);
            
            VBox titleBox = new VBox();
            titleBox.setMinHeight(60);
            titleBox.setMaxWidth(240);
            HBox.setHgrow(titleBox, Priority.ALWAYS);
            
            Label titleLabel = new Label(formation.getTitre());
            titleLabel.getStyleClass().add("title-main1");
            titleLabel.setWrapText(true);
            titleLabel.setMaxWidth(240);
            titleBox.getChildren().add(titleLabel);
            
            // Options menu button
            MenuButton optionsButton = new MenuButton();
            optionsButton.getStyleClass().add("transparent-menu-button");
            FontAwesomeIconView ellipsisIcon = new FontAwesomeIconView(FontAwesomeIcon.ELLIPSIS_V);
            ellipsisIcon.setSize("16");
            optionsButton.setGraphic(ellipsisIcon);
            
            MenuItem editItem = new MenuItem("Modifier");
            editItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
            editItem.setOnAction(e -> editFormation(formation));
            
            MenuItem deleteItem = new MenuItem("Supprimer");
            deleteItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
            deleteItem.setOnAction(e -> deleteFormation(formation));
            
            optionsButton.getItems().addAll(editItem, deleteItem);
            
            headerBox.getChildren().addAll(titleBox, optionsButton);
            
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
            FontAwesomeIconView calendarIcon = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR);
            calendarIcon.setSize("14");
            calendarIcon.getStyleClass().add("glyph-icon");
            Label dateLabel = new Label(dateFormatter.format(formation.getDateDebut()));
            dateBox.getChildren().addAll(calendarIcon, dateLabel);
            
            // Duration
            HBox durationBox = new HBox(8);
            durationBox.setAlignment(Pos.CENTER_LEFT);
            FontAwesomeIconView clockIcon = new FontAwesomeIconView(FontAwesomeIcon.CLOCK_ALT);
            clockIcon.setSize("14");
            clockIcon.getStyleClass().add("glyph-icon");
            Label durationLabel = new Label("Durée: " + formation.getDureeJours() + " jours");
            durationBox.getChildren().addAll(clockIcon, durationLabel);
            
            // Location
            HBox locationBox = new HBox(8);
            locationBox.setAlignment(Pos.CENTER_LEFT);
            FontAwesomeIconView locationIcon = new FontAwesomeIconView(FontAwesomeIcon.MAP_MARKER);
            locationIcon.setSize("14");
            locationIcon.getStyleClass().add("glyph-icon");
            Label locationLabel = new Label(formation.getLieu());
            locationBox.getChildren().addAll(locationIcon, locationLabel);
            
            // Places
            HBox placesBox = new HBox(8);
            placesBox.setAlignment(Pos.CENTER_LEFT);
            FontAwesomeIconView usersIcon = new FontAwesomeIconView(FontAwesomeIcon.USERS);
            usersIcon.setSize("14");
            usersIcon.getStyleClass().add("glyph-icon");
            Label placesLabel = new Label(formation.getPlacesMax() + " places max");
            placesBox.getChildren().addAll(usersIcon, placesLabel);
            
            // Add info items to container
            infoBox.getChildren().addAll(dateBox, durationBox, locationBox, placesBox);
            
            // Add elements to card
            card.getChildren().addAll(
                headerBox,
                descBox,
                separator,
                infoBox
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/formation_details.fxml"));
            Parent root = loader.load();
            
            // Pass the formation to the controller
            FormationDetailsController controller = loader.getController();
            controller.setFormation(formation);
            
            Stage stage = new Stage();
            stage.setTitle("Détails de la Formation");
            stage.setScene(new Scene(root, 600, 650));
            
            // Refresh the formations list when the details window is closed
            stage.setOnHidden(e -> refreshCards());
            
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'affichage des détails: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateUIForRole() {
        // No need to hide anything since we're setting isAdmin to true for the demo
    }

    @FXML
    private void showAddFormationView() {
        if (!isAdmin) {
            showAlert(Alert.AlertType.WARNING, "Accès Refusé", "Seuls les administrateurs peuvent ajouter des formations.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_formation.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter une Formation");
            stage.setScene(new Scene(root, 600, 650));
            
            // Refresh the formations list when the add window is closed
            stage.setOnHidden(e -> refreshCards());
            
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la vue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showManageParticipantsView() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fonctionnalité non disponible");
        alert.setHeaderText(null);
        alert.setContentText("La gestion des participants n'est pas encore implémentée dans cette version.");
        alert.showAndWait();
    }

    @FXML
    private void showFormationListView() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/formation_list.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Formation List");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchFormations() {
        String searchQuery = searchField.getText().trim();
        
        if (searchQuery.isEmpty()) {
            refreshCards();
            return;
        }
        
        try {
            statusLabel.setText("Recherche des formations...");
            formations.clear();
            formations.addAll(formationService.searchFormations(searchQuery));
            createFormationCards();
            
            if (formations.isEmpty()) {
                statusLabel.setText("Aucune formation trouvée pour: \"" + searchQuery + "\"");
            } else {
                statusLabel.setText(formations.size() + " formation(s) trouvée(s) pour: \"" + searchQuery + "\"");
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la recherche des formations");
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshCards() {
        try {
            statusLabel.setText("Chargement des formations...");
            formations.clear();
            allFormations.clear();
            
            try {
            ObservableList<Formation> loadedFormations = formationService.getAllFormations();
            formations.addAll(loadedFormations);
            allFormations.addAll(loadedFormations);
            
            createFormationCards();
            statusLabel.setText("Formations chargées avec succès - " + formations.size() + " formation(s)");
            } catch (SQLException e) {
                System.err.println("Error loading formations: " + e.getMessage());
                e.printStackTrace();
                
                // Add a placeholder message and card when database access fails
                Label errorLabel = new Label("Impossible de charger les formations depuis la base de données");
                errorLabel.getStyleClass().add("error-label");
                formationsContainer.getChildren().clear();
                formationsContainer.getChildren().add(errorLabel);
                
                // Create sample formation data for display
                createSampleFormations();
                statusLabel.setText("Affichage des formations d'exemple - Base de données indisponible");
            }
            
            // Clear search field when refreshing
            searchField.clear();
        } catch (Exception e) {
            statusLabel.setText("Erreur lors du chargement des formations");
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des formations: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createSampleFormations() {
        // Create some sample formations for display when database is unavailable
        formations.clear();
        
        formations.add(new Formation(
            1,
            "Développement Java Avancé",
            "Formation approfondie sur Java, incluant les design patterns, la programmation concurrente, et les APIs modernes.",
            LocalDate.now().plusDays(30),
            5,
            1,
            "Paris",
            20
        ));
        
        formations.add(new Formation(
            2,
            "Introduction à JavaFX",
            "Apprenez à créer des interfaces graphiques modernes avec JavaFX, CSS et FXML.",
            LocalDate.now().plusDays(15),
            3,
            2,
            "Lyon",
            15
        ));
        
        formations.add(new Formation(
            3,
            "Spring Boot pour débutants",
            "Découvrez comment développer rapidement des applications web avec Spring Boot.",
            LocalDate.now().plusDays(45),
            4,
            3,
            "Marseille",
            25
        ));
        
        createFormationCards();
    }

    @FXML
    private void handleExit() {
        try {
            // Get the current stage
            Stage currentStage = (Stage) formationsContainer.getScene().getWindow();
            
            // Load the admin homepage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/acceuilAdmin.fxml"));
            Parent root = loader.load();
            
            // Set the scene
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Accueil Admin");
        } catch (Exception e) {
            System.err.println("Error going back to previous page: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour à la page précédente: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void editFormation(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_formation.fxml"));
            Parent root = loader.load();
            
            AddFormationController controller = loader.getController();
            controller.setFormation(formation); // Pass the formation to edit
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier la Formation");
            stage.setScene(new Scene(root, 600, 650));
            
            // When the edit window closes, refresh the cards
            stage.setOnHidden(e -> refreshCards());
            
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void deleteFormation(Formation formation) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmer la suppression");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cette formation ?");
        
        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                formationService.deleteFormation(formation.getId());
                refreshCards();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Formation supprimée avec succès.");
            } catch (SQLException e) {
                String errorMessage = e.getMessage();
                String displayMessage = "Erreur lors de la suppression: ";
                
                // Extract the most user-friendly message
                if (errorMessage.contains("référencée par d'autres enregistrements")) {
                    displayMessage += "Cette formation a des participants inscrits. Veuillez d'abord supprimer ces inscriptions.";
                } else if (errorMessage.contains("Communications link failure")) {
                    displayMessage += "Problème de connexion à la base de données. Veuillez vérifier votre connexion.";
                } else {
                    displayMessage += errorMessage;
                }
                
                showAlert(Alert.AlertType.ERROR, "Erreur", displayMessage);
                System.err.println("Full error details: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
} 