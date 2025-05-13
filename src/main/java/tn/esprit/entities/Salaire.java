package tn.esprit.entities;

import java.sql.Date;
import java.util.List;

public class Salaire {
    private int id;
    private double montantBase;
    private Date dateVersement;
    private String employe;
    private List<Prime> primes;

    public Salaire() {
    }

    public Salaire(int id, double montantBase, Date dateVersement, String employe) {
        this.id = id;
        this.montantBase = montantBase;
        this.dateVersement = dateVersement;
        this.employe = employe;
    }

    public Salaire(double montantBase, Date dateVersement, String employe) {
        this.montantBase = montantBase;
        this.dateVersement = dateVersement;
        this.employe = employe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMontantBase() {
        return montantBase;
    }

    public void setMontantBase(double montantBase) {
        this.montantBase = montantBase;
    }

    public Date getDateVersement() {
        return dateVersement;
    }

    public void setDateVersement(Date dateVersement) {
        this.dateVersement = dateVersement;
    }

    public String getEmploye() {
        return employe;
    }

    public void setEmploye(String employe) {
        this.employe = employe;
    }

    public List<Prime> getPrimes() {
        return primes;
    }

    public void setPrimes(List<Prime> primes) {
        this.primes = primes;
    }

    @Override
    public String toString() {
        return "Salaire{" +
                "id=" + id +
                ", montantBase=" + montantBase +
                ", dateVersement=" + dateVersement +
                ", employe='" + employe + '\'' +
                '}';
    }
} 