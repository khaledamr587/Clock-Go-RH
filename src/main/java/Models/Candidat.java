package Models;

import java.util.ArrayList;
import java.util.List;

public class Candidat {
    private int idcandidat;
    private String nom;
    private String prenom;
    private String email;
    private String tel;

    private List<Offre> offres = new ArrayList<>();

public Candidat (int idcandidat , String nom, String prenom, String email , String tel)
{
    this.idcandidat = idcandidat;
    this.nom = nom;
    this.prenom = prenom;
    this.email = email;
    this.tel = tel;
}
public Candidat(){}
    public int getIdCandidat()
    {
return idcandidat;
    }
    public void setIdCandidat(int idcandidat){
    this.idcandidat = idcandidat;
    }

    public String getNom(){
    return nom;
    }
    public void setNom(String nom){
    this.nom = nom;
    }

    public String getPrenom(){
    return prenom;
    }

    public void setPrenom(String prenom){
    this.prenom = prenom;
    }

    public String getEmail(){
    return email;
    }

    public void setEmail(String email){
    this.email = email;
    }

    public String getTel(){
    return tel;
    }
    public void setTel(String tel){
    this.tel = tel;
    }
}




