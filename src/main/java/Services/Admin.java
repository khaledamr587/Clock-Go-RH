package Services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin {

    private Connection con;

    public Admin(Connection con) {
        this.con = con;
    }


    public void afficherToutesLesCandidatures() {
        String sql = """
            SELECT ca.idoffre, ca.idcandidat, c.nom, c.prenom, o.titre, ca.dateCandidature, ca.statut FROM candidature ca 
            JOIN candidat c ON ca.idcandidat = c.idcandidat
            JOIN offre o ON ca.idoffre = o.idoffre """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.printf(
                        "Candidat: %s %s | Offre: %s | Date: %s | Statut: %s%n",
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("titre"),
                        rs.getString("datecandidature"),
                        rs.getString("statut")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des candidatures : " + e.getMessage());
        }
    }

    // Modifier le statut d'une candidature
    public void changerStatutCandidature(int idCandidat, int idOffre, String nouveauStatut) {
        String sql = "UPDATE candidature SET statut = ? WHERE idcandidat = ? AND idoffre = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, nouveauStatut);
            stmt.setInt(2, idCandidat);
            stmt.setInt(3, idOffre);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Le statut a été mis à jour avec succès.");
            } else {
                System.out.println("Aucune candidature trouvée pour cette combinaison.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du statut : " + e.getMessage());
        }
    }
}

