package Gestion.models;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Formation {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty titre = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dateDebut = new SimpleObjectProperty<>();
    private final IntegerProperty dureeJours = new SimpleIntegerProperty();
    private final IntegerProperty formateurId = new SimpleIntegerProperty();
    private final StringProperty lieu = new SimpleStringProperty();
    private final IntegerProperty placesMax = new SimpleIntegerProperty();

    // Constructors
    public Formation() {}

    public Formation(int id, String titre, String description, LocalDate dateDebut, 
                    int dureeJours, int formateurId, String lieu, int placesMax) {
        setId(id);
        setTitre(titre);
        setDescription(description);
        setDateDebut(dateDebut);
        setDureeJours(dureeJours);
        setFormateurId(formateurId);
        setLieu(lieu);
        setPlacesMax(placesMax);
    }

    // ID
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    // Titre
    public String getTitre() { return titre.get(); }
    public void setTitre(String value) { titre.set(value); }
    public StringProperty titreProperty() { return titre; }

    // Description
    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }

    // DateDebut
    public LocalDate getDateDebut() { return dateDebut.get(); }
    public void setDateDebut(LocalDate value) { dateDebut.set(value); }
    public ObjectProperty<LocalDate> dateDebutProperty() { return dateDebut; }

    // DureeJours
    public int getDureeJours() { return dureeJours.get(); }
    public void setDureeJours(int value) { dureeJours.set(value); }
    public IntegerProperty dureeJoursProperty() { return dureeJours; }

    // FormateurId
    public int getFormateurId() { return formateurId.get(); }
    public void setFormateurId(int value) { formateurId.set(value); }
    public IntegerProperty formateurIdProperty() { return formateurId; }

    // Lieu
    public String getLieu() { return lieu.get(); }
    public void setLieu(String value) { lieu.set(value); }
    public StringProperty lieuProperty() { return lieu; }

    // PlacesMax
    public int getPlacesMax() { return placesMax.get(); }
    public void setPlacesMax(int value) { placesMax.set(value); }
    public IntegerProperty placesMaxProperty() { return placesMax; }
} 