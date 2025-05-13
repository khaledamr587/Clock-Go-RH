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
        List<Conge> conges = serviceConge.afficher();

        Map<String, Long> typeCounts = conges.stream()
                .collect(Collectors.groupingBy(Conge::getType, Collectors.counting()));

        leavePieChart.getData().clear();
        for (Map.Entry<String, Long> entry : typeCounts.entrySet()) {
            leavePieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
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
}
