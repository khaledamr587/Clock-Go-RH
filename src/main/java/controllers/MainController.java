package controllers;

import Gestion.models.Absence;
import Gestion.models.Conge;
import Gestion.services.ServiceAbsence;
import Gestion.services.ServiceConge;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private VBox mainVBox;

    @FXML
    private PieChart leavePieChart;

    @FXML
    private PieChart absencePieChart;

    private ServiceConge serviceConge;
    private ServiceAbsence serviceAbsence;

    @FXML
    public void initialize() {
        // Apply fade-in animation to the VBox
        FadeTransition ft = new FadeTransition(Duration.millis(1000), mainVBox);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        // Initialize services
        serviceConge = new ServiceConge();
        serviceAbsence = new ServiceAbsence();

        // Update pie charts
        updateLeavePieChart();
        updateAbsencePieChart();
    }

    private void updateLeavePieChart() {
        // Assume total leave units = 100
        double totalLeaveUnits = 100.0;
        long acceptedLeaves = serviceConge.afficher().stream()
                .filter(conge -> "accepté".equals(conge.getStatut()))
                .count();

        // Each accepted leave decrements by 1%
        double remainingPercentage = Math.max(0, 100.0 - acceptedLeaves); // 1% per accepted leave
        double usedPercentage = 100.0 - remainingPercentage;

        leavePieChart.getData().clear();
        leavePieChart.getData().add(new PieChart.Data("Restants", remainingPercentage));
        leavePieChart.getData().add(new PieChart.Data("Utilisés", usedPercentage));
    }

    private void updateAbsencePieChart() {
        long totalAbsences = serviceAbsence.afficher().size();
        long unjustifiedAbsences = serviceAbsence.afficher().stream()
                .filter(absence -> !absence.isJustifie())
                .count();

        double unjustifiedPercentage = totalAbsences > 0 ? (unjustifiedAbsences * 100.0 / totalAbsences) : 0;
        double justifiedPercentage = totalAbsences > 0 ? 100.0 - unjustifiedPercentage : 100.0;

        absencePieChart.getData().clear();
        absencePieChart.getData().add(new PieChart.Data("Non Justifiées", unjustifiedPercentage));
        absencePieChart.getData().add(new PieChart.Data("Justifiées", justifiedPercentage));
    }

    @FXML
    public void showCongeView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/demander_conge.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Demande de Congé");
            setStageIcon(stage);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement de la page Demande de Congé : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void showAbsenceView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/justifier_absence.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Justification des Absences");
            setStageIcon(stage);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement de la page Justification des Absences : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setStageIcon(Stage stage) {
        try {
            Image icon = new Image(getClass().getResourceAsStream("/images/app-icon.png"));
            if (icon.isError()) {
                System.err.println("Icon loading failed: Image is invalid.");
            } else {
                stage.getIcons().add(icon);
            }
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                title.equals("Erreur") ? javafx.scene.control.Alert.AlertType.ERROR : javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}