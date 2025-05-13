package controllers;

import Models.Candidat;
import Services.ServiceCandidat;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CandidatparIDoffre implements Initializable {

    @FXML private TextField idOffreField;
    @FXML private VBox cardContainer;

    private ServiceCandidat serviceCandidat;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceCandidat = new ServiceCandidat();
    }

    @FXML
    void RetourAceuil(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/PageAccueil.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Page Accueil");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur ouverture page accueil : " + e.getMessage());
        }

    }

    @FXML
    private void handleAfficherCandidats(ActionEvent event) {
        cardContainer.getChildren().clear();

        int idOffre;
        try {
            idOffre = Integer.parseInt(idOffreField.getText().trim());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Veuillez entrer un entier valide pour l'ID de l'offre.").showAndWait();
            return;
        }

        List<Candidat> candidats = serviceCandidat.getCandidatsByOffre(idOffre);
        if (candidats.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Aucun candidat trouvé pour cette offre.").showAndWait();
            return;
        }

        for (Candidat c : candidats) {
            HBox card = new HBox(10);
            card.setPadding(new Insets(10));
            card.getStyleClass().add("card"); // Appliquer la classe CSS

            VBox info = new VBox(4);
            info.getChildren().addAll(
                    new Label("ID       : " + c.getIdCandidat()),
                    new Label("Nom      : " + c.getNom()),
                    new Label("Prénom   : " + c.getPrenom()),
                    new Label("Email    : " + c.getEmail()),
                    new Label("Téléphone: " + c.getTel())
            );
            card.getChildren().add(info);

            cardContainer.getChildren().add(card);
        }
    }

}
