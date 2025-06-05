package Gestion.services;

import Gestion.models.Formation;
import Gestion.utils.MyDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class FormationService {
    
    public ObservableList<Formation> getAllFormations() throws SQLException {
        ObservableList<Formation> formations = FXCollections.observableArrayList();
        String query = "SELECT * FROM formations";
        
        try (Connection conn = MyDataBase.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Formation formation = new Formation(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getInt("duree_jours"),
                    rs.getInt("formateur_id"),
                    rs.getString("lieu"),
                    rs.getInt("places_max")
                );
                formations.add(formation);
            }
        }
        return formations;
    }
    
    public void addFormation(Formation formation) throws SQLException {
        String query = "INSERT INTO formations (titre, description, date_debut, duree_jours, formateur_id, lieu, places_max) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = MyDataBase.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, formation.getTitre());
            pstmt.setString(2, formation.getDescription());
            pstmt.setDate(3, Date.valueOf(formation.getDateDebut()));
            pstmt.setInt(4, formation.getDureeJours());
            pstmt.setInt(5, formation.getFormateurId());
            pstmt.setString(6, formation.getLieu());
            pstmt.setInt(7, formation.getPlacesMax());
            
            pstmt.executeUpdate();
        }
    }
    
    public void updateFormation(Formation formation) throws SQLException {
        String query = "UPDATE formations SET titre=?, description=?, date_debut=?, " +
                      "duree_jours=?, formateur_id=?, lieu=?, places_max=? WHERE id=?";
        
        try (Connection conn = MyDataBase.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, formation.getTitre());
            pstmt.setString(2, formation.getDescription());
            pstmt.setDate(3, Date.valueOf(formation.getDateDebut()));
            pstmt.setInt(4, formation.getDureeJours());
            pstmt.setInt(5, formation.getFormateurId());
            pstmt.setString(6, formation.getLieu());
            pstmt.setInt(7, formation.getPlacesMax());
            pstmt.setInt(8, formation.getId());
            
            try {
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected == 0) {
                    throw new SQLException("La formation avec l'ID " + formation.getId() + " n'existe pas ou n'a pas pu être mise à jour.");
                }
            } catch (SQLException e) {
                // Check for specific errors
                if (e.getErrorCode() == 1452) {
                    throw new SQLException("Le formateur spécifié n'existe pas dans la base de données.", e);
                } else {
                    throw new SQLException("Erreur lors de la mise à jour de la formation (Code: " + e.getErrorCode() + "): " + e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in updateFormation: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public void deleteFormation(int id) throws SQLException {
        String query = "DELETE FROM formations WHERE id=?";
        
        try (Connection conn = MyDataBase.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            try {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                // Check for foreign key constraint violation (MySQL error code 1451)
                if (e.getErrorCode() == 1451) {
                    throw new SQLException("Impossible de supprimer cette formation car elle est référencée par d'autres enregistrements (inscriptions ou participants). Veuillez d'abord supprimer ces références.", e);
                } else {
                    throw new SQLException("Erreur lors de la suppression de la formation (Code: " + e.getErrorCode() + "): " + e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in deleteFormation: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public Formation getFormationById(int id) throws SQLException {
        String query = "SELECT * FROM formations WHERE id=?";
        
        try (Connection conn = MyDataBase.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Formation(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getInt("duree_jours"),
                    rs.getInt("formateur_id"),
                    rs.getString("lieu"),
                    rs.getInt("places_max")
                );
            }
        }
        return null;
    }
    
    public ObservableList<Formation> searchFormations(String searchTerm) throws SQLException {
        ObservableList<Formation> formations = FXCollections.observableArrayList();
        String query = "SELECT * FROM formations WHERE titre LIKE ? OR description LIKE ?";
        
        try (Connection conn = MyDataBase.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Formation formation = new Formation(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getInt("duree_jours"),
                        rs.getInt("formateur_id"),
                        rs.getString("lieu"),
                        rs.getInt("places_max")
                    );
                    formations.add(formation);
                }
            }
        }
        return formations;
    }
} 