-- Create the formations table if it doesn't exist
CREATE TABLE IF NOT EXISTS formations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(100) NOT NULL,
    description TEXT,
    date_debut DATE NOT NULL,
    duree_jours INT NOT NULL,
    formateur_id INT NOT NULL,
    lieu VARCHAR(100) NOT NULL,
    places_max INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Delete existing records (optional, remove this if you want to keep existing data)
TRUNCATE TABLE formations;

-- Insert sample data into formations table
INSERT INTO formations (titre, description, date_debut, duree_jours, formateur_id, lieu, places_max) VALUES
('Développement Java Avancé', 'Formation approfondie sur les concepts avancés de Java incluant les streams, lambdas, et les nouvelles fonctionnalités de Java 17. Idéal pour les développeurs qui veulent perfectionner leurs compétences.', '2024-06-15', 5, 1, 'Salle 101, Centre de Formation Tunis', 20),

('Introduction au Machine Learning', 'Découvrez les fondamentaux du machine learning et ses applications pratiques. Cette formation couvre les algorithmes de base, la préparation des données et l''évaluation des modèles.', '2024-07-10', 3, 2, 'Laboratoire numérique, Technopole El Ghazala', 15),

('Angular pour les débutants', 'Apprenez à créer des applications web modernes avec Angular. Cette formation couvre la création de composants, services, et l''utilisation du routage pour créer des applications SPA.', '2024-06-20', 4, 3, 'Salle 205, Centre de Formation Sfax', 18),

('DevOps et CI/CD', 'Formation sur les pratiques DevOps et la mise en place de pipelines CI/CD avec Jenkins, GitLab CI et GitHub Actions. Apprenez à automatiser vos déploiements et tests.', '2024-08-05', 5, 4, 'Salle de conférence, Hôtel Golden Tulip Tunis', 12),

('React & Redux', 'Maîtrisez le développement d''applications web avec React et la gestion d''état avec Redux. Création de composants réutilisables et optimisation des performances.', '2024-07-25', 4, 3, 'Salle 102, Centre de Formation Tunis', 20),

('Administration de bases de données MySQL', 'Formation complète sur l''administration de bases de données MySQL, incluant l''optimisation des performances, la sécurité et la sauvegarde/restauration.', '2024-08-15', 3, 5, 'Laboratoire informatique, INSAT', 15),

('UX/UI Design Principles', 'Apprenez les principes fondamentaux du design d''interface utilisateur et de l''expérience utilisateur. Outils, méthodes et bonnes pratiques pour créer des interfaces intuitives.', '2024-09-01', 3, 6, 'Studio de design, El Menzah', 12),

('Spring Boot pour les applications d''entreprise', 'Développez des applications robustes avec Spring Boot. Cette formation couvre Spring MVC, Spring Data, Spring Security et les tests automatisés.', '2024-09-10', 5, 1, 'Salle 103, Centre de Formation Tunis', 18),

('Python pour la Data Science', 'Introduction à l''écosystème Python pour l''analyse de données avec NumPy, Pandas, Matplotlib et Scikit-learn. Apprenez à manipuler, visualiser et analyser des données.', '2024-10-05', 4, 2, 'Laboratoire numérique, Technopole El Ghazala', 16),

('Cybersécurité pour développeurs', 'Découvrez les vulnérabilités communes des applications web et comment les prévenir. Techniques de sécurisation du code, des APIs et des bases de données.', '2024-10-15', 3, 7, 'Salle 301, Centre de Formation Sousse', 15),

('Développement mobile avec Flutter', 'Apprenez à créer des applications mobiles multiplateformes avec Flutter. Cette formation couvre les widgets, la navigation, la gestion d''état et le déploiement.', '2024-11-01', 4, 8, 'Salle 204, Centre de Formation Sfax', 18),

('Cloud Computing avec AWS', 'Maîtrisez les services essentiels d''AWS pour déployer et gérer des applications dans le cloud. EC2, S3, RDS, Lambda et bien plus.', '2024-11-15', 5, 4, 'Salle de conférence, Hôtel Movenpick Tunis', 15);

-- Insert formateurs (trainers) if needed
CREATE TABLE IF NOT EXISTS formateurs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    specialite VARCHAR(100),
    email VARCHAR(100) UNIQUE
);

-- Delete existing records (optional)
TRUNCATE TABLE formateurs;

-- Insert sample data into formateurs table
INSERT INTO formateurs (nom, prenom, specialite, email) VALUES
('Ben Salah', 'Ahmed', 'Développement Java', 'ahmed.bensalah@example.com'),
('Trabelsi', 'Sonia', 'Data Science & IA', 'sonia.trabelsi@example.com'),
('Meddeb', 'Karim', 'Développement Web Frontend', 'karim.meddeb@example.com'),
('Gharbi', 'Nadia', 'DevOps & Cloud', 'nadia.gharbi@example.com'),
('Kammoun', 'Youssef', 'Bases de données', 'youssef.kammoun@example.com'),
('Ayari', 'Leila', 'UX/UI Design', 'leila.ayari@example.com'),
('Chaabane', 'Mehdi', 'Cybersécurité', 'mehdi.chaabane@example.com'),
('Sassi', 'Rim', 'Développement Mobile', 'rim.sassi@example.com');

-- Check if data was inserted successfully
SELECT COUNT(*) AS 'Nombre de formations' FROM formations;
SELECT COUNT(*) AS 'Nombre de formateurs' FROM formateurs; 