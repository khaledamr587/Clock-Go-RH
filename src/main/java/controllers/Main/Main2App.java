package controllers.Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.net.URL;

public class Main2App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlLocation = getClass().getResource("/fxml/gestion_absence_et_conge.fxml");
            if (fxmlLocation == null) {
                throw new RuntimeException("Unable to find FXML file at /fxml/gestion_absence_et_conge.fxml. Ensure the file exists in src/main/resources/fxml/");
            }
            Parent root = FXMLLoader.load(fxmlLocation);
            primaryStage.setTitle("Gestion des congés et absences");
            primaryStage.setScene(new Scene(root, 800, 600)); // Explicitly set scene size

            // Set the application icon
            Image icon = new Image(getClass().getResourceAsStream("/images/app-icon.png"));
            if (icon.isError()) {
                System.err.println("❌ Erreur chargement icône : Image could not be loaded.");
            } else {
                primaryStage.getIcons().add(icon);
            }

            primaryStage.show();
        } catch (Exception e) {
            System.err.println("❌ Erreur lancement application Menu : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}