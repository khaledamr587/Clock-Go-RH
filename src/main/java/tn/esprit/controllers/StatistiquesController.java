package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.esprit.entities.Salaire;
import tn.esprit.services.SalaireService;

import java.io.IOException;
import java.net.URL;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StatistiquesController implements Initializable {

    @FXML
    private PieChart pieChartSalaires;

    @FXML
    private BarChart<String, Number> barChartSalaires;

    @FXML
    private Label lblTotalSalaires;

    @FXML
    private Label lblSalaireMoyen;

    @FXML
    private Label lblSalaireMin;

    @FXML
    private Label lblSalaireMax;

    @FXML
    private Button btnRetour;

    private SalaireService salaireService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        salaireService = new SalaireService();
        
        // Charger les données
        loadStatistiques();
    }

    private void loadStatistiques() {
        List<Salaire> salaires = salaireService.getAll();
        
        if (salaires.isEmpty()) {
            return;
        }
        
        // 1. Statistiques de base
        DoubleSummaryStatistics stats = salaires.stream()
                .mapToDouble(Salaire::getMontantBase)
                .summaryStatistics();
        
        lblTotalSalaires.setText(String.format("%.2f €", stats.getSum()));
        lblSalaireMoyen.setText(String.format("%.2f €", stats.getAverage()));
        lblSalaireMin.setText(String.format("%.2f €", stats.getMin()));
        lblSalaireMax.setText(String.format("%.2f €", stats.getMax()));
        
        // 2. Diagramme circulaire - Répartition par tranches
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        long moins2000 = salaires.stream().filter(s -> s.getMontantBase() < 2000).count();
        long entre2000Et3000 = salaires.stream().filter(s -> s.getMontantBase() >= 2000 && s.getMontantBase() < 3000).count();
        long entre3000Et4000 = salaires.stream().filter(s -> s.getMontantBase() >= 3000 && s.getMontantBase() < 4000).count();
        long plus4000 = salaires.stream().filter(s -> s.getMontantBase() >= 4000).count();
        
        if (moins2000 > 0) pieChartData.add(new PieChart.Data("< 2000 €", moins2000));
        if (entre2000Et3000 > 0) pieChartData.add(new PieChart.Data("2000-3000 €", entre2000Et3000));
        if (entre3000Et4000 > 0) pieChartData.add(new PieChart.Data("3000-4000 €", entre3000Et4000));
        if (plus4000 > 0) pieChartData.add(new PieChart.Data("> 4000 €", plus4000));
        
        pieChartSalaires.setData(pieChartData);
        
        // 3. Diagramme à barres - Salaires par employé
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Salaire de base");
        
        // Limiter à 10 employés pour la lisibilité
        salaires.stream()
                .limit(10)
                .forEach(salaire -> series.getData().add(
                        new XYChart.Data<>(salaire.getEmploye(), salaire.getMontantBase())
                ));
        
        barChartSalaires.getData().add(series);
        
        // 4. Ajouter une série pour les primes si disponibles
        XYChart.Series<String, Number> seriesPrimes = new XYChart.Series<>();
        seriesPrimes.setName("Avec primes");
        
        // Pour chaque salaire, calculer le total avec primes
        salaires.stream()
                .limit(10)
                .forEach(salaire -> {
                    double totalAvecPrimes = salaireService.calculerSalaire(salaire.getId());
                    seriesPrimes.getData().add(
                            new XYChart.Data<>(salaire.getEmploye(), totalAvecPrimes)
                    );
                });
        
        barChartSalaires.getData().add(seriesPrimes);
    }

    @FXML
    void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 