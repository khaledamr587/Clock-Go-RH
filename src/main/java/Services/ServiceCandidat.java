package Services;

import Interfaces.IService;
import Models.Candidat;
import Utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCandidat implements IService<Candidat> {
    private final Connection con;

    public ServiceCandidat() {
        con = Database.getInstance().getConnection();
    }

    @Override
    public void add(Candidat candidat) {

        String sql = "INSERT INTO candidat(nom,prenom,email,tel) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, candidat.getNom());
            ps.setString(2, candidat.getPrenom());
            ps.setString(3, candidat.getEmail());
            ps.setString(4, candidat.getTel());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        candidat.setIdCandidat(generatedId);  // Met à jour l'ID généré
                        System.out.println("Candidat ajouté avec succès. ID: " + generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public List<Candidat> getAll() {
        List<Candidat> candidats = new ArrayList<>();
        String sql = "SELECT * FROM candidat";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Candidat candidat= new Candidat();
                candidat.setIdCandidat(rs.getInt("idcandidat"));
                candidat.setNom(rs.getString("nom"));
                candidat.setPrenom(rs.getString("prenom"));
                candidat.setEmail(rs.getString("email"));
                candidat.setTel(rs.getString("tel"));

                candidats.add(candidat);
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return candidats;
    }




    public List<Candidat> getCandidatsByOffre(int idOffre) {
        List<Candidat> candidats = new ArrayList<>();
        String sql = ""
                + "SELECT c.* "
                + "  FROM candidat c "
                + "  JOIN candidature ca ON c.idcandidat = ca.idcandidat "
                + " WHERE ca.idoffre = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idOffre);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Candidat c = new Candidat();
                    c.setIdCandidat(rs.getInt("idcandidat"));
                    c.setNom(rs.getString("nom"));
                    c.setPrenom(rs.getString("prenom"));
                    c.setEmail(rs.getString("email"));
                    c.setTel(rs.getString("tel"));
                    candidats.add(c);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL getCandidatsByOffre : " + e.getMessage());
        }
        return candidats;
    }






    public int postuler(int idCandidat, int idOffre, String dateCandidature) {
        String sql = "INSERT INTO candidature (idoffre, idcandidat, dateCandidature, statut) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idOffre);
            ps.setInt(2, idCandidat);
            ps.setString(3, dateCandidature);
            ps.setString(4, "En attente");

            int affected = ps.executeUpdate();
            if (affected == 0) {
                System.err.println("Aucune ligne insérée, échec de la création de la candidature.");
                return -1;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    System.out.println("Candidature enregistrée (id interne " + generatedId + ")");
                    return generatedId;
                } else {
                    System.err.println("Aucune clé générée retournée.");
                    return -1;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la candidature : " + e.getMessage());
            return -1;
        }
    }



    public Candidat findByEmail(String email) {
        String sql = "SELECT * FROM candidat WHERE email = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Candidat(
                        rs.getInt("idcandidat"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("tel")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByEmail: " + e.getMessage());
        }
        return null;
    }




    @Override

    public void update(Candidat candidat) {
        String sql = "UPDATE candidat SET nom = ?, prenom = ?, email = ?, tel = ? WHERE idCandidat = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, candidat.getNom());
            ps.setString(2, candidat.getPrenom());
            ps.setString(3, candidat.getEmail());
            ps.setString(4, candidat.getTel());
            ps.setInt(5, candidat.getIdCandidat());
            ps.executeUpdate();
            System.out.println("Candidat modifiée avec succès.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void delete(Candidat candidat ) {
        String sql = "DELETE FROM candidat WHERE idcandidat = ?";
        try (
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, candidat.getIdCandidat());
            ps.executeUpdate();
            System.out.println("Candidat supprimée avec succès.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
