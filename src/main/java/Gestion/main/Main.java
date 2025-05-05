package Gestion.main;

import Gestion.models.Conge;
import Gestion.services.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ServiceConge serviceConge = new ServiceConge();
        ServiceAbsence serviceAbsence = new ServiceAbsence();
        int choix;

        do {
            System.out.println("\n========== MENU PRINCIPAL ==========");
            System.out.println("------- GESTION DES CONGÉS ---------");
            System.out.println("1. Ajouter une demande de congé");
            System.out.println("2. Modifier une demande de congé");
            System.out.println("3. Supprimer une demande de congé");
            System.out.println("4. Afficher toutes les demandes de congé");
            System.out.println("5. Rechercher les demandes de congé par employé");

            System.out.println("------- GESTION DES ABSENCES -------");
            System.out.println("6. Enregistrer une absence");
            System.out.println("7. Modifier une absence");
            System.out.println("8. Supprimer une absence");
            System.out.println("9. Afficher toutes les absences");
            System.out.println("10. Rechercher les absences par employé");

            System.out.println("11. Quitter");
            System.out.println("====================================");
            System.out.print("Votre choix : ");

            choix = sc.nextInt();
            sc.nextLine(); // flush

            switch (choix) {
                // Congé
                case 1:
                    serviceConge.ajouterDepuisConsole(sc);
                    break;
                case 2:
                    serviceConge.modifierDepuisConsole(sc);
                    break;
                case 3:
                    serviceConge.supprimerDepuisConsole(sc);
                    break;
                case 4:
                    serviceConge.afficher().forEach(System.out::println);
                    break;
                case 5:
                    serviceConge.rechercherParEmployeDepuisConsole(sc);
                    break;

                // Absence
                case 6:
                    serviceAbsence.ajouterDepuisConsole(sc);
                    break;
                case 7:
                    serviceAbsence.modifierDepuisConsole(sc);
                    break;
                case 8:
                    serviceAbsence.supprimerDepuisConsole(sc);
                    break;
                case 9:
                    serviceAbsence.afficher().forEach(System.out::println);
                    break;
                case 10:
                    serviceAbsence.rechercherParEmployeDepuisConsole(sc);
                    break;

                // Quitter
                case 11:
                    System.out.println("Fin du programme. À bientôt !");
                    break;
                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
            }

        } while (choix != 11);

        sc.close();
    }
}
