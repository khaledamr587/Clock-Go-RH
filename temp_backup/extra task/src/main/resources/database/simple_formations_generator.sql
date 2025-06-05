USE 3b9;

-- Create the formations table if it doesn't exist
DROP TABLE IF EXISTS formations;
CREATE TABLE formations (
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

-- Insert sample data into formations table
INSERT INTO formations (titre, description, date_debut, duree_jours, formateur_id, lieu, places_max) VALUES
('Développement Java Avancé', 'Formation approfondie sur les concepts avancés de Java incluant les streams, lambdas, et les nouvelles fonctionnalités de Java 17.', '2024-06-15', 5, 1, 'Salle 101, Centre de Formation Tunis', 20),
('Introduction au Machine Learning', 'Découvrez les fondamentaux du machine learning et ses applications pratiques.', '2024-07-10', 3, 2, 'Laboratoire numérique, Technopole El Ghazala', 15),
('Angular pour les débutants', 'Apprenez à créer des applications web modernes avec Angular.', '2024-06-20', 4, 3, 'Salle 205, Centre de Formation Sfax', 18),
('DevOps et CI/CD', 'Formation sur les pratiques DevOps et la mise en place de pipelines CI/CD.', '2024-08-05', 5, 4, 'Salle de conférence, Hôtel Golden Tulip Tunis', 12),
('React & Redux', 'Maîtrisez le développement d''applications web avec React et la gestion d''état avec Redux.', '2024-07-25', 4, 3, 'Salle 102, Centre de Formation Tunis', 20),
('Administration de bases de données MySQL', 'Formation complète sur l''administration de bases de données MySQL.', '2024-08-15', 3, 5, 'Laboratoire informatique, INSAT', 15),
('UX/UI Design Principles', 'Apprenez les principes fondamentaux du design d''interface utilisateur et de l''expérience utilisateur.', '2024-09-01', 3, 6, 'Studio de design, El Menzah', 12),
('Spring Boot pour les applications d''entreprise', 'Développez des applications robustes avec Spring Boot.', '2024-09-10', 5, 1, 'Salle 103, Centre de Formation Tunis', 18),
('Python pour la Data Science', 'Introduction à l''écosystème Python pour l''analyse de données.', '2024-10-05', 4, 2, 'Laboratoire numérique, Technopole El Ghazala', 16),
('Cybersécurité pour développeurs', 'Découvrez les vulnérabilités communes des applications web et comment les prévenir.', '2024-10-15', 3, 7, 'Salle 301, Centre de Formation Sousse', 15),
('Développement mobile avec Flutter', 'Apprenez à créer des applications mobiles multiplateformes avec Flutter.', '2024-11-01', 4, 8, 'Salle 204, Centre de Formation Sfax', 18),
('Cloud Computing avec AWS', 'Maîtrisez les services essentiels d''AWS pour déployer et gérer des applications dans le cloud.', '2024-11-15', 5, 4, 'Salle de conférence, Hôtel Movenpick Tunis', 15); 