package tn.esprit.entities;

public class Prime {
    private int id;
    private String type;
    private double montant;
    private int salaireId;

    public Prime() {
    }

    public Prime(int id, String type, double montant, int salaireId) {
        this.id = id;
        this.type = type;
        this.montant = montant;
        this.salaireId = salaireId;
    }

    public Prime(String type, double montant, int salaireId) {
        this.type = type;
        this.montant = montant;
        this.salaireId = salaireId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public int getSalaireId() {
        return salaireId;
    }

    public void setSalaireId(int salaireId) {
        this.salaireId = salaireId;
    }

    @Override
    public String toString() {
        return "Prime{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", montant=" + montant +
                ", salaireId=" + salaireId +
                '}';
    }
} 