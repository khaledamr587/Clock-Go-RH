package Services;

import Interfaces.IService;
import Models.Offre;
import Utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceOffre implements IService<Offre> {
    private final Connection con;
    public Connection getConnection() {
        return con;
    }


    public ServiceOffre() {
        con = Database.getInstance().getConnection();
    }

    @Override
    public void add(Offre offre) {

        String sql = "INSERT INTO offre (titre, description, localisation, datePublication) VALUES (?, ?, ?, ?)";
        try {
             PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, offre.getTitre());
            ps.setString(2, offre.getDescription());
            ps.setString(3, offre.getLocalisation());
            ps.setString(4, offre.getDatePublication());

            ps.executeUpdate();
            System.out.println("Offre ajoutée avec succès.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public List<Offre> getAll() {
        List<Offre> offres = new ArrayList<>();
        String sql = "SELECT * FROM offre";
        try {
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Offre offre = new Offre();
                offre.setIdoffre(rs.getInt("idoffre"));
                offre.setTitre(rs.getString("titre"));
                offre.setDescription(rs.getString("description"));
                offre.setLocalisation(rs.getString("localisation"));
                offre.setDatePublication(rs.getString("datePublication"));

                offres.add(offre);
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return offres;
    }



    public List<Offre> getOffresByCandidat(int idCandidat) {
        List<Offre> offres = new ArrayList<>();
        String sql =
                "SELECT o.* " + "  FROM offre o " + "  JOIN candidature ca ON o.idoffre = ca.idoffre " + " WHERE ca.idcandidat = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idCandidat);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Offre offre = new Offre();
                    offre.setIdoffre(rs.getInt("idoffre"));
                    offre.setTitre(rs.getString("titre"));
                    offre.setDescription(rs.getString("description"));
                    offre.setLocalisation(rs.getString("localisation"));
                    offre.setDatePublication(rs.getString("datePublication"));
                    offres.add(offre);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL getOffresByCandidat : " + e.getMessage());
        }
        return offres;
    }




    @Override

    public void update(Offre offre) {
        String sql = "UPDATE offre SET titre = ?, description = ?, localisation = ?, datePublication = ? WHERE idoffre = ?";
        try {
             PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, offre.getTitre());
            ps.setString(2, offre.getDescription());
            ps.setString(3, offre.getLocalisation());
            ps.setString(4, offre.getDatePublication());
            ps.setInt(5, offre.getIdoffre());
            ps.executeUpdate();
            System.out.println("Offre modifiée avec succès.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void delete(Offre offre) {
        String sql = "DELETE FROM offre WHERE idoffre = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offre.getIdoffre());
            ps.executeUpdate();
            System.out.println("Offre supprimée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'offre : " + e.getMessage());
        }
    }





}
