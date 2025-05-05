package Gestion.services;

import Gestion.models.Absence;
import Gestion.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServiceAbsence implements IService<Absence> {

    private Connection conn = MyDataBase.getInstance().getConnection();

    @Override
    public void ajouter(Absence a) {
        String sql = "INSERT INTO absence (employe_id, date_absence, motif, justifie) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, a.getEmployeId());
            pst.setDate(2, a.getDateAbsence());
            pst.setString(3, a.getMotif());
            pst.setBoolean(4, a.isJustifie());
            pst.executeUpdate();
            System.out.println("✅ Absence ajoutée.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout absence : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Absence a) {
        String sql = "UPDATE absence SET employe_id = ?, date_absence = ?, motif = ?, justifie = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, a.getEmployeId());
            pst.setDate(2, a.getDateAbsence());
            pst.setString(3, a.getMotif());
            pst.setBoolean(4, a.isJustifie());
            pst.setInt(5, a.getId());
            pst.executeUpdate();
            System.out.println("✅ Absence modifiée.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur modification absence : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM absence WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Absence supprimée.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression absence : " + e.getMessage());
        }
    }

    @Override
    public List<Absence> afficher() {
        List<Absence> list = new ArrayList<>();
        String sql = "SELECT * FROM absence";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Absence(
                        rs.getInt("id"),
                        rs.getInt("employe_id"),
                        rs.getDate("date_absence"),
                        rs.getString("motif"),
                        rs.getBoolean("justifie")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur affichage absences : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Absence> rechercherParEmploye(int employeId) {
        List<Absence> list = new ArrayList<>();
        String sql = "SELECT * FROM absence WHERE employe_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, employeId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new Absence(
                        rs.getInt("id"),
                        rs.getInt("employe_id"),
                        rs.getDate("date_absence"),
                        rs.getString("motif"),
                        rs.getBoolean("justifie")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche absences : " + e.getMessage());
        }
        return list;
    }

    // Méthodes Console
    public void ajouterDepuisConsole(Scanner sc) {
        try {
            System.out.print("ID employé : ");
            int emp = sc.nextInt();
            sc.nextLine();
            System.out.print("Date absence (YYYY-MM-DD) : ");
            Date dateAbsence = Date.valueOf(sc.nextLine());
            System.out.print("Motif : ");
            String motif = sc.nextLine();
            System.out.print("Justifié (true/false) : ");
            boolean justifie = sc.nextBoolean();
            ajouter(new Absence(0, emp, dateAbsence, motif, justifie));
        } catch (Exception e) {
            System.err.println("❌ Erreur saisie : " + e.getMessage());
        }
    }

    public void modifierDepuisConsole(Scanner sc) {
        try {
            System.out.print("ID de l'absence à modifier : ");
            int id = sc.nextInt();
            sc.nextLine();
            System.out.print("Nouvel ID employé : ");
            int emp = sc.nextInt();
            sc.nextLine();
            System.out.print("Nouvelle date absence (YYYY-MM-DD) : ");
            Date dateAbsence = Date.valueOf(sc.nextLine());
            System.out.print("Nouveau motif : ");
            String motif = sc.nextLine();
            System.out.print("Justifié (true/false) : ");
            boolean justifie = sc.nextBoolean();
            modifier(new Absence(id, emp, dateAbsence, motif, justifie));
        } catch (Exception e) {
            System.err.println("❌ Erreur modification console : " + e.getMessage());
        }
    }

    public void supprimerDepuisConsole(Scanner sc) {
        System.out.print("ID de l'absence à supprimer : ");
        int id = sc.nextInt();
        supprimer(id);
    }

    public void rechercherParEmployeDepuisConsole(Scanner sc) {
        System.out.print("ID employé : ");
        int emp = sc.nextInt();
        rechercherParEmploye(emp).forEach(System.out::println);
    }
}