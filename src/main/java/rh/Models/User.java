package rh.Models;

public class User {
    public enum Role {
        Admin,
        Employe,
        Comptable
    }
    private int id;
    private String email;
    private String password;
    private String nom;
    private String prenom;
    private Role role;
    private boolean status;
    private String googleId;

    public User() {
    }

    public User(int id, String email, String password, String nom, String prenom, Role role, boolean status, String googleId) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.status = status;
        this.googleId = googleId;
    }

    public User(String email, String password, String nom, String prenom, Role role, boolean status, String googleId) {
        this.email = email;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.status = status;
        this.googleId = googleId;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Role getRole() {
        return role;
    }

    public boolean isStatus() {
        return status;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
}