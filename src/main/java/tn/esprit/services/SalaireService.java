package tn.esprit.services;

import tn.esprit.entities.Prime;
import tn.esprit.entities.Salaire;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaireService implements IService<Salaire> {
    private Connection connection;
    private PrimeService primeService;

    public SalaireService() {
        connection = MyDataBase.getInstance().getCnx();
        primeService = new PrimeService();
    }

    @Override
    public void add(Salaire salaire) {
        String query = "INSERT INTO salaire (montant_base, date_versement, employe) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, salaire.getMontantBase());
            ps.setDate(2, salaire.getDateVersement());
            ps.setString(3, salaire.getEmploye());
            ps.executeUpdate();
            
            // Get generated ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    salaire.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding salaire: " + e.getMessage());
        }
    }

    @Override
    public List<Salaire> getAll() {
        List<Salaire> salaires = new ArrayList<>();
        String query = "SELECT * FROM salaire";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Salaire salaire = new Salaire(
                        rs.getInt("id"),
                        rs.getDouble("montant_base"),
                        rs.getDate("date_versement"),
                        rs.getString("employe")
                );
                // Fetch associated primes
                salaire.setPrimes(getPrimesForSalaire(salaire.getId()));
                salaires.add(salaire);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving salaires: " + e.getMessage());
        }
        return salaires;
    }

    @Override
    public void update(Salaire salaire) {
        String query = "UPDATE salaire SET montant_base = ?, date_versement = ?, employe = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDouble(1, salaire.getMontantBase());
            ps.setDate(2, salaire.getDateVersement());
            ps.setString(3, salaire.getEmploye());
            ps.setInt(4, salaire.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating salaire: " + e.getMessage());
        }
    }

    @Override
    public void delete(Salaire salaire) {
        String query = "DELETE FROM salaire WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, salaire.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting salaire: " + e.getMessage());
        }
    }

    public Salaire getById(int id) {
        String query = "SELECT * FROM salaire WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Salaire salaire = new Salaire(
                            rs.getInt("id"),
                            rs.getDouble("montant_base"),
                            rs.getDate("date_versement"),
                            rs.getString("employe")
                    );
                    // Fetch associated primes
                    salaire.setPrimes(getPrimesForSalaire(salaire.getId()));
                    return salaire;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching salaire by ID: " + e.getMessage());
        }
        return null;
    }

    private List<Prime> getPrimesForSalaire(int salaireId) {
        return primeService.getBySalaireId(salaireId);
    }

    // Calculate the total salary including all bonuses
    public double calculerSalaire(int salaireId) {
        Salaire salaire = getById(salaireId);
        if (salaire == null) {
            return 0;
        }
        
        double montantTotal = salaire.getMontantBase();
        List<Prime> primes = salaire.getPrimes();
        
        if (primes != null) {
            for (Prime prime : primes) {
                montantTotal += prime.getMontant();
            }
        }
        
        return montantTotal;
    }

    // Generate a salary slip with all details
    public String genererBulletin(int salaireId) {
        Salaire salaire = getById(salaireId);
        if (salaire == null) {
            return "Salaire non trouvé";
        }
        
        StringBuilder bulletin = new StringBuilder();
        bulletin.append("BULLETIN DE PAIE\n");
        bulletin.append("------------------\n");
        bulletin.append("Employé: ").append(salaire.getEmploye()).append("\n");
        bulletin.append("Date de versement: ").append(salaire.getDateVersement()).append("\n");
        bulletin.append("Salaire de base: ").append(salaire.getMontantBase()).append(" €\n");
        bulletin.append("\nPrimes:\n");
        
        List<Prime> primes = salaire.getPrimes();
        if (primes != null && !primes.isEmpty()) {
            for (Prime prime : primes) {
                bulletin.append("- ").append(prime.getType()).append(": ").append(prime.getMontant()).append(" €\n");
            }
        } else {
            bulletin.append("Aucune prime\n");
        }
        
        bulletin.append("\nTotal: ").append(calculerSalaire(salaireId)).append(" €\n");
        
        return bulletin.toString();
    }
} 