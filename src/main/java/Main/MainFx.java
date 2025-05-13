package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFx extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {


            Parent root = FXMLLoader.load(getClass().getResource("/PageAccueil.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Page Accueil");
            primaryStage.setScene(scene);
            primaryStage.show();

            /*Parent root = FXMLLoader.load(getClass().getResource("/listeOffres.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("listeOffres");
            primaryStage.setScene(scene);
            primaryStage.show();*/



        } catch (IOException e) {
            System.err.println("Erreur de chargement des fichiers FXML : assurez-vous que les chemins sont corrects."+ e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
