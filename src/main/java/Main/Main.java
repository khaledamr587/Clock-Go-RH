package Main;

import Models.Candidat;
import Models.Offre;
import Services.Admin;
import Services.ServiceCandidat;
import Services.ServiceOffre;
import Utils.Database;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ServiceOffre offreDAO = new ServiceOffre();
        ServiceCandidat candidatDAO = new ServiceCandidat();
        Connection connection = Database.getInstance().getConnection(); // Connexion partagée
        Admin admin = new Admin(connection);
        boolean continueLoop = true;

        while (continueLoop) {
            System.out.println("\nMenu principal - Choisissez une option :");
            System.out.println("1. Voir les offres disponibles");
            System.out.println("2. Postuler à une offre (Guest)");
            System.out.println("3. Voir les candidatures par offre ou par candidat");
            System.out.println("4. Gérer les candidatures (Admin)");
            System.out.println("5. Quitter");
            System.out.print("Votre choix: ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1 -> {
                    List<Offre> offres = offreDAO.getAll();
                    if (offres.isEmpty()) {
                        System.out.println("Aucune offre disponible.");
                    } else {
                        offres.forEach(o -> System.out.printf("ID: %d | %s — %s%n",
                                o.getIdoffre(), o.getTitre(), o.getDescription()));
                    }
                }
                case 2 -> postulerEnTantQueGuest(scanner, candidatDAO, offreDAO);
                case 3 -> menuCandidatures(scanner, offreDAO, candidatDAO);
                case 4 -> menuAdmin(scanner, admin);
                case 5 -> continueLoop = false;
                default -> System.out.println("Choix invalide.");
            }
        }
        scanner.close();
    }

    private static void postulerEnTantQueGuest(Scanner scanner, ServiceCandidat candidatDAO, ServiceOffre offreDAO) {
        System.out.println("\n--- POSTULER EN TANT QUE GUEST ---");
        System.out.println("Veuillez saisir vos informations pour créer votre profil de candidat :");
        System.out.print("Nom: ");
        String nom = scanner.nextLine();
        System.out.print("Prénom: ");
        String prenom = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Téléphone: ");
        String tel = scanner.nextLine();
        Candidat c = new Candidat(0, nom, prenom, email, tel);
        candidatDAO.add(c);
        int idCandidat = c.getIdCandidat();


        System.out.println("\n--- Liste des offres disponibles ---");
        List<Offre> offres = offreDAO.getAll();
        if (offres.isEmpty()) {
            System.out.println("Aucune offre disponible.");
            return;
        }
        offres.forEach(o -> System.out.printf("ID: %d | %s — %s%n",
                o.getIdoffre(), o.getTitre(), o.getDescription()));

        System.out.print("ID de l'offre à laquelle vous souhaitez postuler: ");
        int idOffre = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Date de candidature (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        candidatDAO.postuler(idCandidat, idOffre, date);
        System.out.println("Votre candidature a été enregistrée.");
    }

    private static void menuCandidatures(Scanner scanner, ServiceOffre offreDAO, ServiceCandidat candidatDAO) {
        boolean loop = true;
        while (loop) {
            System.out.println("\n--- CONSULTATION DES CANDIDATURES ---");
            System.out.println("1. Voir offres postulées par un candidat");
            System.out.println("2. Voir candidats ayant postulé à une offre");
            System.out.println("3. Retour");
            System.out.print("Choix: ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1 -> {
                    System.out.print("ID du candidat : ");
                    int idC = scanner.nextInt();
                    scanner.nextLine();
                    List<Offre> offres = offreDAO.getOffresByCandidat(idC);
                    if (offres.isEmpty()) {
                        System.out.println("Ce candidat n'a postulé à aucune offre.");
                    } else {
                        offres.forEach(o -> System.out.printf("Titre: %s | Description: %s | Localisation: %s | Date: %s%n",
                                o.getTitre(), o.getDescription(), o.getLocalisation(), o.getDatePublication()));
                    }
                }
                case 2 -> {
                    System.out.print("ID de l'offre: ");
                    int idO = scanner.nextInt();
                    List<Candidat> candidats = candidatDAO.getCandidatsByOffre(idO);
                    if (candidats.isEmpty()) {
                        System.out.println("Aucun candidat n'a postulé à cette offre.");
                    } else {
                        candidats.forEach(c -> System.out.printf("Nom: %s | Prénom: %s | Email: %s | Téléphone: %s%n",
                                c.getNom(), c.getPrenom(), c.getEmail(), c.getTel()));
                    }
                }
                case 3 -> loop = false;
                default -> System.out.println("Option invalide.");
            }
        }
    }

    private static void menuAdmin(Scanner scanner, Admin admin) {
        boolean loop = true;
        while (loop) {
            System.out.println("\n--- GESTION ADMIN DES CANDIDATURES ---");
            System.out.println("1. Voir toutes les candidatures");
            System.out.println("2. Accepter ou refuser une candidature");
            System.out.println("3. Retour");
            System.out.print("Choix: ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1 -> admin.afficherToutesLesCandidatures();
                case 2 -> {
                    System.out.print("ID du candidat : ");
                    int idCandidat = scanner.nextInt();
                    System.out.print("ID de l'offre  : ");
                    int idOffre = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Nouveau statut (Acceptée/Refusée) : ");
                    String statut = scanner.nextLine();
                    if (statut.equalsIgnoreCase("Acceptée") || statut.equalsIgnoreCase("Refusée")) {
                        admin.changerStatutCandidature(idCandidat, idOffre, statut);
                    } else {
                        System.out.println("Statut invalide. Entrez 'Acceptée' ou 'Refusée'.");
                    }
                }
                case 3 -> loop = false;
                default -> System.out.println("Option invalide.");
            }
        }
    }
}
