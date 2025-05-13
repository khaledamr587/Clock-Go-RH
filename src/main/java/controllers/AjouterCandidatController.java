package controllers;

import Models.Candidat;
import Services.QRCodeGenerator;
import Services.ServiceCandidat;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AjouterCandidatController implements Initializable {

    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private DatePicker dateCandidaturePicker;
    @FXML private ImageView qrImageView;
    @FXML private Button btnGenererQR;

    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnLire;
    @FXML private Button btnValiderDemande;

    @FXML private VBox cardContainer;

    private ServiceCandidat serviceCandidat;
    private Candidat selectedCandidat;
    private int selectedOfferId;
    private int selectedCandidateId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceCandidat = new ServiceCandidat();
        dateCandidaturePicker.setValue(LocalDate.now());
        reloadTable();

        setupCarets(txtNom);
        setupCarets(txtPrenom);
        setupCarets(txtEmail);
        setupCarets(txtTelephone);

    }


    private void setupCarets(TextField tf) {
        tf.focusedProperty().addListener((obs, WasF, isNowF) -> {
            if (isNowF) {
                tf.positionCaret(tf.getText().length());
            }
        });
        tf.setOnMouseClicked(e -> tf.positionCaret(tf.getText().length()));
    }




    /** Reconstruit toutes les cartes à partir de la base. */
    private void reloadTable() {
        cardContainer.getChildren().clear();
        List<Candidat> list = serviceCandidat.getAll();

        for (Candidat c : list) {
            HBox card = new HBox(10);
            card.setPadding(new Insets(10));
            card.getStyleClass().add("card");  // Appliquer la classe CSS "card"

            VBox info = new VBox(4);
            info.getChildren().addAll(
                    new Label("ID: "    + c.getIdCandidat()),
                    new Label("Nom: "   + c.getNom() + " " + c.getPrenom()),
                    new Label("Email: " + c.getEmail()),
                    new Label("Tél: "   + c.getTel())
            );
            card.getChildren().add(info);

            // Gestion du clic sur la carte
            card.setOnMouseClicked(e -> {
                selectedCandidat = c;
                selectedCandidateId = c.getIdCandidat();

                txtNom.setText(c.getNom());
                txtPrenom.setText(c.getPrenom());
                txtEmail.setText(c.getEmail());
                txtTelephone.setText(c.getTel());
                dateCandidaturePicker.setValue(LocalDate.now());

                // Supprimer la sélection précédente
                for (Node node : cardContainer.getChildren()) {
                    node.getStyleClass().remove("selected-card");
                }

                // Appliquer le style sélectionné
                card.getStyleClass().add("selected-card");
            });

            cardContainer.getChildren().add(card);
        }
    }

    @FXML
    private void onGenererQR(ActionEvent event) {
        try {

            String data = "C" + selectedCandidateId + "|O" + selectedOfferId;
            Image qr = QRCodeGenerator.generateQRCodeImage(data, 200, 200);
            qrImageView.setImage(qr);   // <<< utiliser setImage(Image)
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Impossible de générer le QR : " + e.getMessage()).showAndWait();
        }
    }


    @FXML
    private void Ajouter(ActionEvent event) {
        Candidat c = new Candidat(
                0,
                txtNom.getText(),
                txtPrenom.getText(),
                txtEmail.getText(),
                txtTelephone.getText()
        );
        serviceCandidat.add(c);
        reloadTable();
        clearFields();
    }

    @FXML
    private void Supprimer(ActionEvent event) {
        if (selectedCandidat != null) {
            serviceCandidat.delete(selectedCandidat);
            reloadTable();
            clearFields();
        }
    }

    @FXML
    private void Modifier(ActionEvent event) {
        if (selectedCandidat != null) {
            selectedCandidat.setNom(txtNom.getText());
            selectedCandidat.setPrenom(txtPrenom.getText());
            selectedCandidat.setEmail(txtEmail.getText());
            selectedCandidat.setTel(txtTelephone.getText());
            serviceCandidat.update(selectedCandidat);
            reloadTable();
            clearFields();
        }
    }



    @FXML
    private void ValiderDemande(ActionEvent event) {
        String nom    = txtNom.getText();
        String prenom = txtPrenom.getText();
        String email  = txtEmail.getText();
        String tel    = txtTelephone.getText();
        String date   = dateCandidaturePicker.getValue().toString();

        Candidat existant = serviceCandidat.findByEmail(email);
        int idCandidat;
        if (existant != null) {
            idCandidat = existant.getIdCandidat();
        } else {

            Candidat nouveau = new Candidat(0, nom, prenom, email, tel);
            serviceCandidat.add(nouveau);
            idCandidat = nouveau.getIdCandidat();
        }

        selectedCandidateId = idCandidat;

        // 2) On crée la candidature en réutilisant idCandidat et l'id de l'offre
        serviceCandidat.postuler(idCandidat, selectedOfferId, date);

        Notifications.create()
                .title("Candidature enregistrée")
                .text("Votre candidature est passée avec succès !")
                .position(Pos.TOP_RIGHT)
                .showInformation();

        new Alert(Alert.AlertType.INFORMATION, "Candidature enregistrée !").showAndWait();
        //((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    /** Vide le formulaire et réinitialise la surbrillance. */
    private void clearFields() {
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        txtTelephone.clear();
        dateCandidaturePicker.setValue(LocalDate.now());
        selectedCandidat = null;
        cardContainer.getChildren().forEach(node -> {
            node.getStyleClass().remove("selected-card");
            if (!node.getStyleClass().contains("card")) {
                node.getStyleClass().add("card");
            }
        });
    }

    public void setSelectedOfferId(int offerId) {
        this.selectedOfferId = offerId;
    }
    public void setSelectedCandidateId(int candId) {
        this.selectedCandidateId = candId;

}
    @FXML
    void Fermer(ActionEvent event) {

        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();

    }


}
