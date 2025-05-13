package controllers;

import Models.Offre;
import Services.ServiceCandidat;
import Services.ServiceOffre;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListeOffres implements Initializable {

    @FXML private VBox cardContainer;

    private final ServiceOffre serviceOffre     = new ServiceOffre();
    private final ServiceCandidat serviceCandidat = new ServiceCandidat();
    private Offre selectedOffre;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        afficherOffres();
    }

    /** Charge et affiche toutes les offres sous forme de cartes */
    private void afficherOffres() {
        List<Offre> offres = serviceOffre.getAll();
        cardContainer.getChildren().clear();

        for (Offre o : offres) {
            HBox card = new HBox(10);
            card.getStyleClass().add("card");

            VBox info = new VBox(4);
            info.getChildren().addAll(
                    new Label("Titre       : " + o.getTitre()),
                    new Label("Description : " + o.getDescription()),
                    new Label("Localisation: " + o.getLocalisation()),
                    new Label("Publié le   : " + o.getDatePublication())
            );

            card.getChildren().add(info);

            card.setOnMouseClicked(e -> {
                for (Node n : cardContainer.getChildren()) {
                    n.getStyleClass().remove("selected-card");
                }

                card.getStyleClass().add("selected-card");
                selectedOffre = o;
            });

            cardContainer.getChildren().add(card);
        }
    }


    @FXML
    void Fermer(ActionEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();

    }

    /** Ouvre le formulaire de candidature pour l'offre sélectionnée */
    @FXML
    private void Postuler(ActionEvent event) {
        if (selectedOffre == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une offre.").showAndWait();
            return;
        }

        try {
            // Débug : vérification du chemin
            URL fxmlUrl = getClass().getResource("/ajouterCandidat.fxml");
            System.out.println(">> FXML URL = " + fxmlUrl);
            if (fxmlUrl == null) {
                throw new IOException("FXML '/ajouterCandidat.fxml' introuvable !");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Transmettre l'ID
            controllers.AjouterCandidatController ctrl = loader.getController();
            ctrl.setSelectedOfferId(selectedOffre.getIdoffre());

            Stage stage = new Stage();
            stage.setTitle("Formulaire de candidature");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur chargement formulaire :");
            e.printStackTrace();
        }
    }

}
