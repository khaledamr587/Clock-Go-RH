package Gestion.models;

import java.sql.Date;

public class Conge {
    private int id;
    private int employeId;
    private Date dateDebut;
    private Date dateFin;
    private String type; // "annuel", "maladie", etc.
    private String statut; // "en attente", "accepté", "refusé"

    public Conge() {}

    public Conge(int id, int employeId, Date dateDebut, Date dateFin, String type, String statut) {
        this.id = id;
        this.employeId = employeId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.type = type;
        this.statut = statut;
    }

    public Conge(int i, int empId, Date debut, Date fin, String type) {
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getEmployeId() {
        return employeId;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public String getType() {
        return type;
    }

    public String getStatut() {
        return statut;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setEmployeId(int employeId) {
        this.employeId = employeId;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Conge{" +
                "id=" + id +
                ", employeId=" + employeId +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", type='" + type + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}