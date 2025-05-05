package Gestion.services;

import java.util.List;

public interface IService<T> {
    void ajouter(T t);
    void modifier(T t);
    void supprimer(int id);
    List<T> afficher();
    List<T> rechercherParEmploye(int employeId);
}
