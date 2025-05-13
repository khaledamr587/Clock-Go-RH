package tn.esprit.services;

import tn.esprit.entities.Prime;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrimeService implements IService<Prime> {
    private Connection connection;

    public PrimeService() {
        connection = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void add(Prime prime) {
        String query = "INSERT INTO prime (type, montant, salaire_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, prime.getType());
            ps.setDouble(2, prime.getMontant());
            ps.setInt(3, prime.getSalaireId());
            ps.executeUpdate();
            
            // Get generated ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    prime.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding prime: " + e.getMessage());
        }
    }

    @Override
    public List<Prime> getAll() {
        List<Prime> primes = new ArrayList<>();
        String query = "SELECT * FROM prime";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Prime prime = new Prime(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getDouble("montant"),
                        rs.getInt("salaire_id")
                );
                primes.add(prime);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving primes: " + e.getMessage());
        }
        return primes;
    }

    @Override
    public void update(Prime prime) {
        String query = "UPDATE prime SET type = ?, montant = ?, salaire_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, prime.getType());
            ps.setDouble(2, prime.getMontant());
            ps.setInt(3, prime.getSalaireId());
            ps.setInt(4, prime.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating prime: " + e.getMessage());
        }
    }

    @Override
    public void delete(Prime prime) {
        String query = "DELETE FROM prime WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, prime.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting prime: " + e.getMessage());
        }
    }

    public Prime getById(int id) {
        String query = "SELECT * FROM prime WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Prime(
                            rs.getInt("id"),
                            rs.getString("type"),
                            rs.getDouble("montant"),
                            rs.getInt("salaire_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving prime by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Prime> getBySalaireId(int salaireId) {
        List<Prime> primes = new ArrayList<>();
        String query = "SELECT * FROM prime WHERE salaire_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, salaireId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prime prime = new Prime(
                            rs.getInt("id"),
                            rs.getString("type"),
                            rs.getDouble("montant"),
                            rs.getInt("salaire_id")
                    );
                    primes.add(prime);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving primes for salaire ID: " + e.getMessage());
        }
        return primes;
    }
} 