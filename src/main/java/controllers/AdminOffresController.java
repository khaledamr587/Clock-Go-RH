package controllers;

import Models.Offre;
import Services.ServiceOffre;
import Utils.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AdminOffresController implements Initializable {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private TextField localisationField;
    @FXML private DatePicker datePublicationPicker;
    @FXML private VBox cardContainer;
    @FXML private Button btnAjouter, btnModifier, btnSupprimer, btnReset;

    private ServiceOffre serviceOffre;
    private Connection con;
    private Offre selectedOffre;
    private HBox selectedCard;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        con = Database.getInstance().getConnection();
        serviceOffre = new ServiceOffre();
        datePublicationPicker.setValue(LocalDate.now());
        loadOffres();
    }

    @FXML
    private void ajouterOffre(ActionEvent event) {
        Offre o = new Offre(0,
                titreField.getText(),
                descriptionField.getText(),
                localisationField.getText(),
                datePublicationPicker.getValue() != null ? datePublicationPicker.getValue().toString() : ""
        );
        serviceOffre.add(o);
        loadOffres();
        resetForm();
        showAlert(Alert.AlertType.INFORMATION, "Offre ajoutée", "L'offre a été ajoutée avec succès.");
    }

    @FXML
    private void modifierOffre(ActionEvent event) {
        if (selectedOffre != null) {
            selectedOffre.setTitre(titreField.getText());
            selectedOffre.setDescription(descriptionField.getText());
            selectedOffre.setLocalisation(localisationField.getText());
            String date = datePublicationPicker.getValue() != null ? datePublicationPicker.getValue().toString() : "";
            selectedOffre.setDatePublication(date);
            serviceOffre.update(selectedOffre);
            loadOffres();
            resetForm();
            showAlert(Alert.AlertType.INFORMATION, "Offre modifiée", "L'offre a été modifiée avec succès.");
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une offre.");
        }
    }

    @FXML
    private void supprimerOffre(ActionEvent event) {
        if (selectedOffre != null) {
            serviceOffre.delete(selectedOffre);
            loadOffres();
            resetForm();
            showAlert(Alert.AlertType.INFORMATION, "Offre supprimée", "L'offre a été supprimée avec succès.");
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une offre.");
        }
    }

    @FXML
    private void resetForm(ActionEvent event) {
        resetForm();
    }

    private void resetForm() {
        titreField.clear();
        descriptionField.clear();
        localisationField.clear();
        datePublicationPicker.setValue(null);
        selectedOffre = null;
        if (selectedCard != null) {
            selectedCard.getStyleClass().remove("selected-card");
            selectedCard = null;
        }
    }

    private void loadOffres() {
        cardContainer.getChildren().clear();
        String sql = "SELECT idoffre, titre, description, localisation, datePublication FROM offre";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idOffre = rs.getInt("idoffre");
                String titre = rs.getString("titre");
                String description = rs.getString("description");
                String localisation = rs.getString("localisation");
                String date = rs.getString("datePublication");

                HBox card = new HBox(10);
                card.getStyleClass().add("card");

                Label label = new Label(titre + " - " + localisation + " [" + date + "]\n" + description);
                label.getStyleClass().add("label");

                card.getChildren().add(label);


                card.setOnMouseClicked(e -> {
                    for (Node node : cardContainer.getChildren()) {
                        node.getStyleClass().remove("selected-card");
                    }

                    card.getStyleClass().add("selected-card");

                    selectedCard = card;
                    selectedOffre = new Offre(idOffre, titre, description, localisation, date);
                    populateForm(selectedOffre);
                });

                cardContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        }
    }


    private void populateForm(Offre o) {
        titreField.setText(o.getTitre());
        descriptionField.setText(o.getDescription());
        localisationField.setText(o.getLocalisation());
        if (o.getDatePublication() != null && !o.getDatePublication().isEmpty()) {
            datePublicationPicker.setValue(LocalDate.parse(o.getDatePublication()));
        } else {
            datePublicationPicker.setValue(null);
        }
    }

    @FXML
    void RetourpageAcceuil(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/PageAccueil.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Page Accueil");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
