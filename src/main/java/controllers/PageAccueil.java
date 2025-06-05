package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PageAccueil {

    @FXML
    private void ouvrirPageOffres(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdminOffres.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Gestion des Offres");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void ouvrirPageCandidatures(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdminCandidature.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Gestion des Candidats");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("erreur de l'ouvrirPageCandidatures"+e.getMessage());
        }
    }


    @FXML
    void candidatparidoffre(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/CandidatparIDoffre.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Candidat par ID offre");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("erreur de l'ouvrirPageCandidatures"+e.getMessage());
        }


    }


    @FXML
    void OffreparIDcandidat(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/OffreparIDcandidat.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Offre par ID candidat");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("erreur de l'ouvrirPageCandidatures"+e.getMessage());
        }
    }

    @FXML
    void retourAdmin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/rh/acceuilAdmin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Tableau de bord administrateur");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors du retour au tableau de bord admin: " + e.getMessage());
        }
    }
}
