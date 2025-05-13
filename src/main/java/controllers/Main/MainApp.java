package controllers.Main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.net.URL;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Show splash screen
        Stage splash = new Stage(StageStyle.UNDECORATED);
        ProgressIndicator pi = new ProgressIndicator();
        splash.setScene(new Scene(new StackPane(pi), 300, 200));
        splash.show();

        // Load main UI in a separate thread
        new Thread(() -> {
            try {
                Thread.sleep(50); // Simulate loading time
                Platform.runLater(() -> {
                    try {
                        URL fxmlLocation = getClass().getResource("/fxml/consultation_absence_et_conge.fxml");
                        if (fxmlLocation == null) {
                            throw new RuntimeException("Fichier FXML 'consultation_absence_et_conge.fxml' non trouvé dans les ressources.");
                        }
                        Parent root = FXMLLoader.load(fxmlLocation);
                        primaryStage.setTitle("Consultation des congés et absences");
                        primaryStage.setScene(new Scene(root, 800, 600));

                        Image icon = new Image(getClass().getResourceAsStream("/images/app-icon.png"));
                        if (icon.isError()) {
                            System.err.println("❌ Erreur chargement icône : Image could not be loaded.");
                        } else {
                            primaryStage.getIcons().add(icon);
                        }

                        splash.close();
                        primaryStage.show();
                    } catch (Exception e) {
                        System.err.println("❌ Erreur lancement application Menu : " + e.getMessage());
                        Platform.runLater(() -> {
                            showAlert("Erreur", "Erreur lors du lancement de l'application : " + e.getMessage());
                        });
                    }
                });
            } catch (Exception e) {
                System.err.println("❌ Erreur splash screen : " + e.getMessage());
            }
        }).start();
    }

    private void showAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}