package Models;

import java.util.ArrayList;
import java.util.List;

public class Offre {
    private int idoffre;
    private String titre;
    private String description;
    private String localisation;
    private String datePublication;


    private List<Candidat> candidats = new ArrayList<>();

    public Offre(int idoffre, String titre, String description, String localisation, String datePublication) {
        this.idoffre = idoffre;
        this.titre = titre;
        this.description = description;
        this.localisation = localisation;
        this.datePublication = datePublication;
    }
    public Offre() {}
    public int getIdoffre() {
        return idoffre;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public String getLocalisation() {
        return localisation;
    }

    public String getDatePublication() {
        return datePublication;
    }

    public void setIdoffre(int idoffre) {
        this.idoffre = idoffre;
    }

    public void setTitre(String titre)
    { this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public void setDatePublication(String datePublication) {
        this.datePublication = datePublication;
    }


}


