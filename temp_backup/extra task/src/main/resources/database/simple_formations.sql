-- Simple SQL script to generate formation data
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

-- Clear existing formations data
DELETE FROM formations;

-- Reset auto-increment
ALTER TABLE formations AUTO_INCREMENT = 1;

-- Insert sample formations with realistic data
INSERT INTO formations (titre, description, date_debut, duree_jours, formateur_id, lieu, places_max) VALUES
('Introduction à Java et JDBC', 'Apprenez les bases du langage Java et comment se connecter aux bases de données relationnelles avec JDBC.', '2023-11-15', 5, 1, 'Paris', 15),

('Développement avancé avec Java', 'Cette formation approfondie couvre les aspects avancés de Java incluant les streams, les lambdas et les modules.', '2023-11-25', 4, 1, 'Lyon', 12),

('Architecture des applications JavaFX', 'Découvrez comment concevoir et développer des interfaces utilisateur modernes avec JavaFX.', '2023-12-05', 3, 2, 'Marseille', 10),

('Maîtriser les bases de données avec MySQL', 'Formation complète sur MySQL couvrant la conception de base de données et l''optimisation des requêtes.', '2023-12-10', 4, 2, 'Bordeaux', 15),

('Sécurité des applications Java', 'Apprenez à sécuriser vos applications Java contre les vulnérabilités courantes.', '2024-01-08', 3, 3, 'Toulouse', 12),

('Java EE et Jakarta EE', 'Cette formation couvre les frameworks et technologies Java pour le développement d''applications d''entreprise.', '2024-01-15', 5, 1, 'Paris', 10);

-- Display the inserted data
SELECT * FROM formations; 