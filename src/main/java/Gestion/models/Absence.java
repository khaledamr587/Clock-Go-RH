package Gestion.models;

import java.sql.Date;

public class Absence {
    private int id;
    private int employeId;
    private Date dateAbsence;
    private String motif;
    private boolean justifie;

    public Absence() {}

    public Absence(int param1, int param2, java.sql.Date param3, String param4) {
        this.id = param1;
        this.employeId = param2;
        this.dateAbsence = param3;
        this.motif = param4;
        this.justifie = false; // Default value
    }

    public Absence(int id, int employeId, Date dateAbsence, String motif, boolean justifie) {
        this.id = id;
        this.employeId = employeId;
        this.dateAbsence = dateAbsence;
        this.motif = motif;
        this.justifie = justifie;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getEmployeId() {
        return employeId;
    }

    public Date getDateAbsence() {
        return dateAbsence;
    }

    public String getMotif() {
        return motif;
    }

    public boolean isJustifie() {
        return justifie;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setEmployeId(int employeId) {
        this.employeId = employeId;
    }

    public void setDateAbsence(Date dateAbsence) {
        this.dateAbsence = dateAbsence;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public void setJustifie(boolean justifie) {
        this.justifie = justifie;
    }

    @Override
    public String toString() {
        return "Absence{" +
                "id=" + id +
                ", employeId=" + employeId +
                ", dateAbsence=" + dateAbsence +
                ", motif='" + motif + '\'' +
                ", justifie=" + justifie +
                '}';
    }
}