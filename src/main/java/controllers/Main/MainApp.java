package controllers.Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the main FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/consultation_absence_et_conge.fxml"));
            Parent root = loader.load();

            // Set up the scene
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("Consultation des congés et absences");
            primaryStage.setScene(scene);

            // Set the application icon
            Image icon = new Image(getClass().getResourceAsStream("/images/app-icon.png"));
            if (icon.isError()) {
                System.err.println("❌ Erreur chargement icône : Image could not be loaded.");
            } else {
                primaryStage.getIcons().add(icon);
            }

            primaryStage.show();
        } catch (Exception e) {
            System.err.println("❌ Erreur lancement application : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}