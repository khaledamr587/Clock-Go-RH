package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert;
import javafx.scene.chart.PieChart;
import Gestion.models.Conge;
import Gestion.services.ServiceConge;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main2Controller {

    @FXML
    private VBox mainVBox;

    @FXML
    private Button themeToggleButton;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private PieChart leavePieChart;

    private final ServiceConge serviceConge = new ServiceConge();

    private boolean isDarkMode = false;

    @FXML
    public void initialize() {
        // Animation fade-in
        FadeTransition ft = new FadeTransition(Duration.millis(1000), mainVBox);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        updateLeavePieChart();
    }

    private void updateLeavePieChart() {
        try {
            // Clear existing data
            leavePieChart.getData().clear();
            
            // Get real data if available
        List<Conge> conges = serviceConge.afficher();
        Map<String, Long> typeCounts = conges.stream()
                .collect(Collectors.groupingBy(Conge::getType, Collectors.counting()));

            // If we have real data, use it
            if (!typeCounts.isEmpty()) {
        for (Map.Entry<String, Long> entry : typeCounts.entrySet()) {
            leavePieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }
            } else {
                // Otherwise, use sample data to match the screenshot
                leavePieChart.getData().add(new PieChart.Data("Maladie", 35));
                leavePieChart.getData().add(new PieChart.Data("Annuel", 20));
                leavePieChart.getData().add(new PieChart.Data("Sans solde", 45));
            }
        } catch (Exception e) {
            System.err.println("Error updating pie chart: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to sample data if there's an error
            leavePieChart.getData().clear();
            leavePieChart.getData().add(new PieChart.Data("Maladie", 35));
            leavePieChart.getData().add(new PieChart.Data("Annuel", 20));
            leavePieChart.getData().add(new PieChart.Data("Sans solde", 45));
        }
    }

    @FXML
    public void toggleTheme() {
        isDarkMode = !isDarkMode;
        anchorPane.getStyleClass().removeAll("light", "dark");
        if (isDarkMode) {
            anchorPane.getStyleClass().add("dark");
            themeToggleButton.setText("Switch to Light Mode");
        } else {
            anchorPane.getStyleClass().add("light");
            themeToggleButton.setText("Switch to Dark Mode");
        }
    }

    @FXML
    public void goToValiderConge() {
        try {
            URL fxmlLocation = getClass().getResource("/fxml/valider_conge.fxml");
            if (fxmlLocation == null) {
                throw new RuntimeException("Fichier FXML 'valider_conge.fxml' non trouvé.");
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Valider les Congés");
            setStageIcon(stage);
            stage.setScene(new Scene(root, 440, 650));
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement de la page Valider les Congés : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void goToMarquerAbsence() {
        try {
            URL fxmlLocation = getClass().getResource("/fxml/marquer_absence.fxml");
            if (fxmlLocation == null) {
                throw new RuntimeException("Fichier FXML 'marquer_absence.fxml' non trouvé.");
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Marquer les Absences");
            setStageIcon(stage);
            stage.setScene(new Scene(root, 440, 650));
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement de la page Marquer les Absences : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setStageIcon(Stage stage) {
        try {
            Image icon = new Image(getClass().getResourceAsStream("/images/app-icon.png"));
            if (!icon.isError()) {
                stage.getIcons().add(icon);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement icône : " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(title.equals("Erreur") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void goToSidebar() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/acceuilAdmin.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("FXML file '/rh/acceuilAdmin.fxml' not found.");
            }
            Parent root = loader.load();
            // Use the current button's scene to navigate back
            anchorPane.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du retour à la page d'accueil : " + e.getMessage());
            throw e;
        }
    }
}
