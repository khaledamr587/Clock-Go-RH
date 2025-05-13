package controllers;

import Services.Admin;
import Utils.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminCandidature implements Initializable {

    @FXML
    private VBox cardContainer;

    @FXML
    private Button btnAfficher, btnAccepter, btnRefuser;

    private Admin adminService;
    private Connection con;

    private int selectedIdCandidat = -1;
    private int selectedIdOffre = -1;
    private HBox selectedCard = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        con = Database.getInstance().getConnection();
        adminService = new Admin(con);
        loadCandidatures();
    }

    @FXML
    private void afficherCandidatures(ActionEvent event) {
        loadCandidatures();
    }

    private void loadCandidatures() {
        cardContainer.getChildren().clear();
        String sql = """
        SELECT ca.idcandidat, ca.idoffre, c.nom, c.prenom, o.titre, ca.dateCandidature, ca.statut
        FROM candidature ca
        JOIN candidat c ON ca.idcandidat = c.idcandidat
        JOIN offre o ON ca.idoffre = o.idoffre
        """;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idC = rs.getInt("idcandidat");
                int idO = rs.getInt("idoffre");
                String nom     = rs.getString("nom");
                String prenom  = rs.getString("prenom");
                String titre   = rs.getString("titre");
                String date    = rs.getString("dateCandidature");
                String statut  = rs.getString("statut");


                HBox card = new HBox(10);
                card.getStyleClass().add("card");

                Label label = new Label(nom + " " + prenom + " - " + titre + " (" + statut + ") [" + date + "]");

                card.getChildren().add(label);
                card.setOnMouseClicked(e -> {
                    selectedIdCandidat = idC;
                    selectedIdOffre    = idO;
                    selectedCard       = card;

                    for (Node node : cardContainer.getChildren()) {
                        node.getStyleClass().remove("selected-card");
                    }
                    card.getStyleClass().add("selected-card");
                });

                cardContainer.getChildren().add(card);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        }
    }


    @FXML
    private void accepterCandidature(ActionEvent event) {
        updateStatut("Acceptée");
        Notifications.create()
                .title("Candidature acceptée")
                .text("Vous venez d’accepter cette candidature.")
                .position(Pos.TOP_RIGHT)
                .showConfirm();
    }

    @FXML
    private void refuserCandidature(ActionEvent event) {
        updateStatut("Refusée");
        Notifications.create()
                .title("Candidature refusée")
                .text("Vous venez de refuser cette candidature.")
                .position(Pos.TOP_RIGHT)
                .showError();
    }

    private void updateStatut(String statut) {
        if (selectedIdCandidat != -1 && selectedIdOffre != -1) {
            adminService.changerStatutCandidature(selectedIdCandidat, selectedIdOffre, statut);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Statut mis à jour : " + statut);
            loadCandidatures(); // recharge les cartes
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une candidature.");
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
            System.out.println("Erreur ouverture page accueil : " + e.getMessage());
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
