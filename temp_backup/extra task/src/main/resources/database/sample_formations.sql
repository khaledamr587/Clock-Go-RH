-- Sample SQL script to generate formation data
-- This script creates a formations table if it doesn't exist 
-- and inserts sample formation records

-- Create formations table if not exists
CREATE TABLE IF NOT EXISTS formations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    date_debut DATE NOT NULL,
    duree_jours INT NOT NULL,
    formateur_id INT,
    lieu VARCHAR(255) NOT NULL,
    places_max INT NOT NULL
);

-- Create formateurs table if not exists
CREATE TABLE IF NOT EXISTS formateurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    specialite VARCHAR(255),
    email VARCHAR(255) UNIQUE
);

-- Insert sample formateurs
INSERT INTO formateurs (nom, prenom, specialite, email) VALUES
('Dupont', 'Jean', 'Développement Java', 'jean.dupont@example.com'),
('Martin', 'Sophie', 'Base de données', 'sophie.martin@example.com'),
('Bernard', 'Alexandre', 'Développement Web', 'alex.bernard@example.com'),
('Petit', 'Isabelle', 'DevOps', 'isabelle.petit@example.com'),
('Lambert', 'Thomas', 'Sécurité Informatique', 'thomas.lambert@example.com');

-- Clear existing formations data
DELETE FROM formations;

-- Reset auto-increment
ALTER TABLE formations AUTO_INCREMENT = 1;

-- Insert sample formations with realistic data
INSERT INTO formations (titre, description, date_debut, duree_jours, formateur_id, lieu, places_max) VALUES
('Introduction à Java et JDBC', 'Apprenez les bases du langage Java et comment se connecter aux bases de données relationnelles avec JDBC. Cette formation couvre les concepts fondamentaux de la programmation orientée objet et les techniques pour interagir avec les systèmes de gestion de base de données.', '2023-11-15', 5, 1, 'Paris', 15),

('Développement avancé avec Java', 'Cette formation approfondie couvre les aspects avancés de Java incluant les streams, les lambdas, les modules, et les nouvelles fonctionnalités de Java 11 et 17. Vous apprendrez également les meilleures pratiques pour développer des applications robustes et maintenables.', '2023-11-25', 4, 1, 'Lyon', 12),

('Architecture des applications JavaFX', 'Découvrez comment concevoir et développer des interfaces utilisateur modernes avec JavaFX. Cette formation couvre le pattern MVC, les contrôles avancés, et les techniques d''animation et de styling pour créer des applications desktop attrayantes et fonctionnelles.', '2023-12-05', 3, 3, 'Marseille', 10),

('Maîtriser les bases de données avec MySQL', 'Formation complète sur MySQL couvrant la conception de base de données, l''optimisation des requêtes, l''indexation, et l''administration. Vous apprendrez à concevoir des schémas efficaces et à écrire des requêtes performantes pour vos applications.', '2023-12-10', 4, 2, 'Bordeaux', 15),

('Sécurité des applications Java', 'Apprenez à sécuriser vos applications Java contre les vulnérabilités courantes. Cette formation couvre l''authentification, l''autorisation, la protection contre les injections SQL, XSS, et d''autres risques de sécurité.', '2024-01-08', 3, 5, 'Toulouse', 12),

('Java EE et Jakarta EE pour le développement d''entreprise', 'Cette formation couvre les frameworks et technologies Java pour le développement d''applications d''entreprise. Vous découvrirez les servlets, JSP, EJB, JPA et comment ils s''intègrent dans l''écosystème Java.', '2024-01-15', 5, 1, 'Paris', 10),

('Gestion avancée des données avec JDBC et JPA', 'Maîtrisez la persistance des données en Java avec JDBC et JPA/Hibernate. Cette formation couvre les transactions, le mapping objet-relationnel, le caching, et l''optimisation des performances.', '2024-01-25', 4, 2, 'Nantes', 15),

('Développement d''API REST avec Spring Boot', 'Apprenez à développer des API REST modernes avec Spring Boot. Cette formation couvre la création de services web RESTful, la documentation avec Swagger, l''authentification et l''autorisation.', '2024-02-05', 4, 3, 'Lyon', 12),

('DevOps pour les développeurs Java', 'Découvrez les pratiques DevOps et comment les appliquer aux projets Java. Cette formation couvre Docker, Jenkins, GitLab CI, et les techniques de déploiement continu pour les applications Java.', '2024-02-15', 3, 4, 'Lille', 10),

('Tests automatisés en Java', 'Maîtrisez les techniques de test en Java avec JUnit, Mockito, et Selenium. Apprenez à écrire des tests unitaires, d''intégration et fonctionnels pour garantir la qualité de vos applications.', '2024-03-01', 3, 1, 'Paris', 15);

-- Display the inserted data
SELECT * FROM formations; 