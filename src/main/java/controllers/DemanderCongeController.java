package controllers;

import Gestion.models.Conge;
import Gestion.services.ServiceConge;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Optional;
import javafx.scene.layout.VBox;
import java.sql.Date;

public class DemanderCongeController {

    @FXML
    private VBox cardContainer;

    @FXML
    private Button ajouterCongeButton;

    @FXML
    private Button modifierCongeButton;

    @FXML
    private Button annulerCongeButton;

    private ServiceConge serviceConge;
    private Conge selectedConge;
    private VBox selectedCard;

    @FXML
    public void initialize() {
        serviceConge = new ServiceConge();
        // Vérifier l'initialisation des boutons
        if (ajouterCongeButton == null) {
            System.err.println("Erreur : Le bouton 'Ajouter' n'est pas correctement initialisé.");
        } else {
            System.out.println("Bouton 'Ajouter' bien initialisé.");
            ajouterCongeButton.setVisible(true);
        }
        if (modifierCongeButton == null || annulerCongeButton == null) {
            System.err.println("Erreur : Les boutons 'Modifier' ou 'Annuler' ne sont pas correctement initialisés.");
        } else {
            System.out.println("Boutons 'Modifier' et 'Annuler' bien initialisés.");
            modifierCongeButton.setVisible(true);
            annulerCongeButton.setVisible(true);
            modifierCongeButton.setDisable(true);
            annulerCongeButton.setDisable(true);
        }
        refreshTable();
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
                card.setOnMouseClicked(event -> {
                    if (selectedCard != null) {
                        selectedCard.getStyleClass().remove("selected-card");
                    }
                    selectedConge = conge;
                    selectedCard = card;
                    selectedCard.getStyleClass().add("selected-card");
                    if (modifierCongeButton != null && annulerCongeButton != null) {
                        modifierCongeButton.setDisable(false);
                        annulerCongeButton.setDisable(false);
                        System.out.println("Carte sélectionnée : Employé ID " + conge.getEmployeId());
                    }
                });
                cardContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du rafraîchissement des congés : " + e.getMessage());
            System.err.println("Exception dans refreshTable : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void ajouterConge() {
        // Créer une boîte de dialogue pour saisir les détails du congé
        Dialog<Conge> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une demande de congé");
        dialog.setHeaderText("Entrez les détails de votre demande");

        // Définir les champs de saisie
        ComboBox<Integer> employeIdComboBox = new ComboBox<>();
        employeIdComboBox.getItems().addAll(serviceConge.getEmployeIds());
        employeIdComboBox.setPromptText("Employé ID");
        DatePicker dateDebutPicker = new DatePicker();
        dateDebutPicker.setPromptText("Date de début");
        DatePicker dateFinPicker = new DatePicker();
        dateFinPicker.setPromptText("Date de fin");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Maladie", "Annuel", "Sans solde");
        typeComboBox.setPromptText("Sélectionnez un type");

        VBox dialogContent = new VBox(10);
        dialogContent.getChildren().addAll(
                new Label("Employé ID:"), employeIdComboBox,
                new Label("Date de début:"), dateDebutPicker,
                new Label("Date de fin:"), dateFinPicker,
                new Label("Type de congé:"), typeComboBox
        );
        dialog.getDialogPane().setContent(dialogContent);

        // Ajouter les boutons OK et Annuler
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);

        // Convertir le résultat en objet Conge
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                Integer employeId = employeIdComboBox.getValue();
                if (employeId == null) {
                    showAlert("Erreur", "Veuillez sélectionner un Employé ID.");
                    return null;
                }
                Date dateDebut = dateDebutPicker.getValue() != null ? Date.valueOf(dateDebutPicker.getValue()) : null;
                Date dateFin = dateFinPicker.getValue() != null ? Date.valueOf(dateFinPicker.getValue()) : null;
                String type = typeComboBox.getValue();
                if (type == null || type.isEmpty()) {
                    showAlert("Erreur", "Veuillez sélectionner un type de congé.");
                    return null;
                }
                return new Conge(0, employeId, dateDebut, dateFin, type, "en attente");
            }
            return null;
        });

        Optional<Conge> result = dialog.showAndWait();
        result.ifPresent(conge -> {
            try {
                serviceConge.ajouter(conge);
                refreshTable();
                showAlert("Succès", "Demande de congé ajoutée avec succès.");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'ajout de la demande : " + e.getMessage());
                System.err.println("Exception dans ajouterConge : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void modifierConge() {
        if (selectedConge == null || !selectedConge.getStatut().equals("en attente")) {
            showAlert("Erreur", "Veuillez sélectionner une demande en attente à modifier.");
            return;
        }

        // Créer une boîte de dialogue pour modifier les détails du congé
        Dialog<Conge> dialog = new Dialog<>();
        dialog.setTitle("Modifier la demande de congé");
        dialog.setHeaderText("Modifiez les détails de votre demande");

        // Définir les champs de saisie avec les valeurs actuelles
        DatePicker dateDebutPicker = new DatePicker(selectedConge.getDateDebut() != null ? selectedConge.getDateDebut().toLocalDate() : null);
        dateDebutPicker.setPromptText("Date de début");
        DatePicker dateFinPicker = new DatePicker(selectedConge.getDateFin() != null ? selectedConge.getDateFin().toLocalDate() : null);
        dateFinPicker.setPromptText("Date de fin");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Maladie", "Annuel", "Sans solde");
        typeComboBox.setValue(selectedConge.getType());
        typeComboBox.setPromptText("Sélectionnez un type");

        VBox dialogContent = new VBox(10);
        dialogContent.getChildren().addAll(
                new Label("Date de début:"), dateDebutPicker,
                new Label("Date de fin:"), dateFinPicker,
                new Label("Type de congé:"), typeComboBox
        );
        dialog.getDialogPane().setContent(dialogContent);

        // Ajouter les boutons OK et Annuler
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);

        // Convertir le résultat en objet Conge
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                Date dateDebut = dateDebutPicker.getValue() != null ? Date.valueOf(dateDebutPicker.getValue()) : selectedConge.getDateDebut();
                Date dateFin = dateFinPicker.getValue() != null ? Date.valueOf(dateFinPicker.getValue()) : selectedConge.getDateFin();
                String type = typeComboBox.getValue();
                if (type == null || type.isEmpty()) {
                    showAlert("Erreur", "Veuillez sélectionner un type de congé.");
                    return null;
                }
                Conge updatedConge = new Conge(
                        selectedConge.getId(),
                        selectedConge.getEmployeId(),
                        dateDebut,
                        dateFin,
                        type,
                        selectedConge.getStatut()
                );
                return updatedConge;
            }
            return null;
        });

        Optional<Conge> result = dialog.showAndWait();
        result.ifPresent(updatedConge -> {
            try {
                serviceConge.modifier(updatedConge);
                refreshTable();
                showAlert("Succès", "Demande modifiée avec succès.");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la modification : " + e.getMessage());
                System.err.println("Exception dans modifierConge : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void annulerConge() {
        if (selectedConge == null || !selectedConge.getStatut().equals("en attente")) {
            showAlert("Erreur", "Veuillez sélectionner une demande en attente à annuler.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Annuler la demande");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir annuler cette demande de congé ?");
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                serviceConge.supprimer(selectedConge.getId());
                refreshTable();
                showAlert("Succès", "Demande annulée avec succès.");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'annulation : " + e.getMessage());
                System.err.println("Exception dans annulerConge : " + e.getMessage());
                e.printStackTrace();
            }
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