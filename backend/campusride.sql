-- Base de données pour CampusRide (Application mobile de covoiturage universitaire)
-- Encodage: utf8mb4_unicode_ci

CREATE DATABASE IF NOT EXISTS campusride CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campusride;

-- 1. Table utilisateur
CREATE TABLE IF NOT EXISTS utilisateur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email_universitaire VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    note_moyenne DECIMAL(2,1) DEFAULT 5.0,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Table trajet
CREATE TABLE IF NOT EXISTS trajet (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_conducteur INT NOT NULL,
    ville_depart VARCHAR(100) NOT NULL,
    ville_arrivee VARCHAR(100) NOT NULL,
    date_depart DATE NOT NULL,
    heure_depart TIME NOT NULL,
    places_totales INT NOT NULL,
    places_restantes INT NOT NULL,
    prix DECIMAL(5,2) NOT NULL,
    statut ENUM('disponible', 'complet', 'annule') DEFAULT 'disponible',
    date_publication TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_conducteur) REFERENCES utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. Table demande
CREATE TABLE IF NOT EXISTS demande (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_trajet INT NOT NULL,
    id_passager INT NOT NULL,
    statut ENUM('en_attente', 'acceptee', 'refusee', 'annulee') DEFAULT 'en_attente',
    date_demande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_trajet) REFERENCES trajet(id) ON DELETE CASCADE,
    FOREIGN KEY (id_passager) REFERENCES utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Table evaluation
CREATE TABLE IF NOT EXISTS evaluation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_trajet INT NOT NULL,
    id_evaluateur INT NOT NULL,
    note INT CHECK (note >= 1 AND note <= 5),
    commentaire TEXT,
    date_evaluation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_trajet) REFERENCES trajet(id) ON DELETE CASCADE,
    FOREIGN KEY (id_evaluateur) REFERENCES utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Données de test initiales (mot de passe haché pour 'password123')
INSERT INTO utilisateur (id, nom, prenom, email_universitaire, mot_de_passe, ville, telephone, note_moyenne) VALUES
(1, 'Ben Ali', 'Mohamed', 'mohamed.benali@isetn.rnu.tn', '$2y$10$w82bO04P1Z4J1WfZoOqOtuU1Q0hVp5Ew52sN5m1G.gQ76DkVH3h6q', 'Nabeul', '98123456', 4.9),
(2, 'Trabelsi', 'Sarra', 'sarra.trabelsi@univ.tn', '$2y$10$w82bO04P1Z4J1WfZoOqOtuU1Q0hVp5Ew52sN5m1G.gQ76DkVH3h6q', 'Tunis', '21456789', 4.8),
(3, 'Gharbi', 'Yassine', 'yassine.gharbi@enit.utm.tn', '$2y$10$w82bO04P1Z4J1WfZoOqOtuU1Q0hVp5Ew52sN5m1G.gQ76DkVH3h6q', 'Hammamet', '55123987', 4.7)
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO trajet (id, id_conducteur, ville_depart, ville_arrivee, date_depart, heure_depart, places_totales, places_restantes, prix, statut) VALUES
(1, 1, 'Nabeul', 'ISET Nabeul', CURDATE() + INTERVAL 1 DAY, '07:45:00', 3, 2, 2.50, 'disponible'),
(2, 2, 'Tunis', 'Nabeul', CURDATE() + INTERVAL 1 DAY, '08:00:00', 4, 3, 7.00, 'disponible'),
(3, 1, 'Nabeul', 'Hammamet', CURDATE() + INTERVAL 1 DAY, '17:30:00', 3, 1, 3.00, 'disponible')
ON DUPLICATE KEY UPDATE id=id;
