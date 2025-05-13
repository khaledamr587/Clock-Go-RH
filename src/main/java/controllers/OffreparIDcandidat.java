package controllers;

import Models.Offre;
import Services.ServiceOffre;
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

public class OffreparIDcandidat implements Initializable {

    @FXML private TextField idCandidatField;
    @FXML private VBox cardContainer;

    private ServiceOffre serviceOffre;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceOffre = new ServiceOffre();
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
    private void handleAfficherOffres(ActionEvent event) {
        cardContainer.getChildren().clear();

        String text = idCandidatField.getText().trim();
        int idC;
        try {
            idC = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "ID invalide, entrez un nombre entier.").showAndWait();
            return;
        }

        List<Offre> offres = serviceOffre.getOffresByCandidat(idC);
        if (offres.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Aucune offre trouvée pour ce candidat.").showAndWait();
            return;
        }


        for (Offre o : offres) {
            HBox card = new HBox(10);
            card.setPadding(new Insets(10));
            card.getStyleClass().add("card");

            VBox info = new VBox(4);
            info.getChildren().addAll(
                    new Label("ID Offre      : " + o.getIdoffre()),
                    new Label("Titre         : " + o.getTitre()),
                    new Label("Description   : " + o.getDescription()),
                    new Label("Localisation  : " + o.getLocalisation()),
                    new Label("Date Publi.   : " + o.getDatePublication())
            );

            card.getChildren().add(info);

            card.setOnMouseClicked(e -> {
                cardContainer.getChildren().forEach(node -> node.getStyleClass().remove("selected-card")
                );

                card.getStyleClass().add("selected-card");
            });
            cardContainer.getChildren().add(card);
        }

    }
}
