package rh.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class MainWrapper {

    public static void loadMainView(Stage stage) {
        try {
            URL resourceUrl = MainWrapper.class.getResource("/fxml/main.fxml");
            if (resourceUrl == null) {
                System.err.println("ERROR: Could not find /fxml/main.fxml");
                
                // Try with alternative paths
                resourceUrl = MainWrapper.class.getResource("/resources/fxml/main.fxml");
                if (resourceUrl == null) {
                    System.err.println("ERROR: Could not find /resources/fxml/main.fxml");
                    
                    // Try with absolute path
                    resourceUrl = MainWrapper.class.getClassLoader().getResource("fxml/main.fxml");
                    if (resourceUrl == null) {
                        System.err.println("ERROR: All attempts to find main.fxml failed");
                        return;
                    }
                }
            }
            
            System.out.println("Found main.fxml at: " + resourceUrl);
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);
            stage.setScene(scene);
            stage.setTitle("Formation Management System");
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading main.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 