package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.entities.Prime;
import tn.esprit.entities.Salaire;
import tn.esprit.services.PrimeService;
import tn.esprit.services.SalaireService;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class MainFx extends Application {

    private static MainFx instance;
    private SalaireService salaireService;
    private PrimeService primeService;

    // Public constructor required by JavaFX
    public MainFx() {
        salaireService = new SalaireService();
        primeService = new PrimeService();
    }

    public static MainFx getInstance() {
        if (instance == null)
            instance = new MainFx();
        return instance;
    }

    // Calculer le salaire total d'un employé
    public double calculerSalaire(int salaireId) {
        return salaireService.calculerSalaire(salaireId);
    }

    // Ajouter un nouveau salaire
    public void ajouterSalaire(double montantBase, Date dateVersement, String employe) {
        Salaire salaire = new Salaire(montantBase, dateVersement, employe);
        salaireService.add(salaire);
    }

    // Ajouter une prime à un salaire
    public void ajouterPrime(String type, double montant, int salaireId) {
        Prime prime = new Prime(type, montant, salaireId);
        primeService.add(prime);
    }

    // Supprimer une prime
    public void supprimerPrime(int primeId) {
        Prime prime = primeService.getById(primeId);
        if (prime != null) {
            primeService.delete(prime);
        }
    }

    // Générer un bulletin de paie
    public String genererBulletin(int salaireId) {
        return salaireService.genererBulletin(salaireId);
    }

    // Récupérer tous les salaires
    public List<Salaire> getAllSalaires() {
        return salaireService.getAll();
    }

    // Récupérer toutes les primes
    public List<Prime> getAllPrimes() {
        return primeService.getAll();
    }

    // Récupérer les primes d'un salaire spécifique
    public List<Prime> getPrimesBySalaireId(int salaireId) {
        return primeService.getBySalaireId(salaireId);
    }

    // Mettre à jour un salaire
    public void mettreAJourSalaire(Salaire salaire) {
        salaireService.update(salaire);
    }

    // Mettre à jour une prime
    public void mettreAJourPrime(Prime prime) {
        primeService.update(prime);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/MainView.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Gestion de Paie");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Set instance for singleton access after JavaFX instantiates the class
            if (instance == null) {
                instance = this;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Main method for launching the application
    public static void main(String[] args) {
        launch(args);
    }
}
