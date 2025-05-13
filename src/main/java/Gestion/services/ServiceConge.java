package Gestion.services;

import Gestion.models.Conge;
import Gestion.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServiceConge implements IService<Conge> {

    private Connection conn = MyDataBase.getInstance().getConnection();

    @Override
    public void ajouter(Conge c) {
        String sql = "INSERT INTO conge (employe_id, date_debut, date_fin, type, statut) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, c.getEmployeId());
            pst.setDate(2, c.getDateDebut());
            pst.setDate(3, c.getDateFin());
            pst.setString(4, c.getType());
            pst.setString(5, c.getStatut());
            pst.executeUpdate();
            System.out.println("✅ Congé ajouté.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout congé : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Conge c) {
        String sql = "UPDATE conge SET employe_id = ?, date_debut = ?, date_fin = ?, type = ?, statut = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, c.getEmployeId());
            pst.setDate(2, c.getDateDebut());
            pst.setDate(3, c.getDateFin());
            pst.setString(4, c.getType());
            pst.setString(5, c.getStatut());
            pst.setInt(6, c.getId());
            pst.executeUpdate();
            System.out.println("✅ Congé modifié.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur modification : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM conge WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Congé supprimé.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Conge> afficher() {
        List<Conge> list = new ArrayList<>();
        String sql = "SELECT * FROM conge";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Conge(
                        rs.getInt("id"),
                        rs.getInt("employe_id"),
                        rs.getDate("date_debut"),
                        rs.getDate("date_fin"),
                        rs.getString("type"),
                        rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur affichage : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Conge> rechercherParEmploye(int employeId) {
        List<Conge> list = new ArrayList<>();
        String sql = "SELECT * FROM conge WHERE employe_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, employeId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new Conge(
                        rs.getInt("id"),
                        rs.getInt("employe_id"),
                        rs.getDate("date_debut"),
                        rs.getDate("date_fin"),
                        rs.getString("type"),
                        rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche : " + e.getMessage());
        }
        return list;
    }

    // Méthode ajoutée pour récupérer les IDs d'employés depuis la table employe
    public List<Integer> getEmployeIds() {
        List<Integer> employeIds = new ArrayList<>();
        String sql = "SELECT DISTINCT id FROM employe";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                employeIds.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des IDs d'employés : " + e.getMessage());
        }
        return employeIds;
    }

    // Méthodes Console
    public void ajouterDepuisConsole(Scanner sc) {
        try {
            System.out.print("ID employé : ");
            int emp = sc.nextInt();
            sc.nextLine();
            System.out.print("Date début (YYYY-MM-DD) : ");
            Date debut = Date.valueOf(sc.nextLine());
            System.out.print("Date fin (YYYY-MM-DD) : ");
            Date fin = Date.valueOf(sc.nextLine());
            System.out.print("Type : ");
            String type = sc.nextLine();
            System.out.print("Statut : ");
            String statut = sc.nextLine();
            ajouter(new Conge(0, emp, debut, fin, type, statut));
        } catch (Exception e) {
            System.err.println("❌ Erreur saisie : " + e.getMessage());
        }
    }

    public void modifierDepuisConsole(Scanner sc) {
        try {
            System.out.print("ID du congé à modifier : ");
            int id = sc.nextInt();
            sc.nextLine();
            System.out.print("Nouvel ID employé : ");
            int emp = sc.nextInt();
            sc.nextLine();
            System.out.print("Nouvelle date début : ");
            Date debut = Date.valueOf(sc.nextLine());
            System.out.print("Nouvelle date fin : ");
            Date fin = Date.valueOf(sc.nextLine());
            System.out.print("Type : ");
            String type = sc.nextLine();
            System.out.print("Statut : ");
            String statut = sc.nextLine();
            modifier(new Conge(id, emp, debut, fin, type, statut));
        } catch (Exception e) {
            System.err.println("❌ Erreur modification console : " + e.getMessage());
        }
    }

    public void supprimerDepuisConsole(Scanner sc) {
        System.out.print("ID du congé à supprimer : ");
        int id = sc.nextInt();
        supprimer(id);
    }

    public void rechercherParEmployeDepuisConsole(Scanner sc) {
        System.out.print("ID employé : ");
        int emp = sc.nextInt();
        rechercherParEmploye(emp).forEach(System.out::println);
    }
}