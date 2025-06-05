-- Create formations table if not exists
CREATE TABLE IF NOT EXISTS formations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(100) NOT NULL,
    description TEXT,
    date_debut DATE NOT NULL,
    duree_jours INT NOT NULL,
    formateur_id INT,
    lieu VARCHAR(100),
    places_max INT NOT NULL
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    role ENUM('ADMIN', 'EMPLOYEE') NOT NULL DEFAULT 'EMPLOYEE',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create inscriptions table (for managing formation registrations)
CREATE TABLE IF NOT EXISTS inscriptions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    formation_id INT,
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('EN_ATTENTE', 'CONFIRMEE', 'ANNULEE') DEFAULT 'EN_ATTENTE',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (formation_id) REFERENCES formations(id) ON DELETE CASCADE
);

-- Create formateurs table
CREATE TABLE IF NOT EXISTS formateurs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    specialite VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    telephone VARCHAR(20)
);

-- Add foreign key to formations table for formateur_id
ALTER TABLE formations
ADD FOREIGN KEY (formateur_id) REFERENCES formateurs(id);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, email, nom, prenom, role)
VALUES ('admin', '$2a$10$xP3Dj2pMw/dmHxVhh.z7IO8GEyHGbPaJUGczHGHqUp7JHBQjFPmrK', 'admin@company.com', 'Admin', 'User', 'ADMIN');

-- Add indexes for better performance
CREATE INDEX idx_formations_date ON formations(date_debut);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_inscriptions_status ON inscriptions(statut); 