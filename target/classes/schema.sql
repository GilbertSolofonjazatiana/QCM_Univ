-- Création de la base de données QCM
-- Script d'initialisation du schéma

-- Table ETUDIANT
CREATE TABLE IF NOT EXISTS ETUDIANT (
    num_etudiant SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenoms VARCHAR(100) NOT NULL,
    niveau VARCHAR(50) NOT NULL,
    adr_email VARCHAR(150) UNIQUE NOT NULL
);

-- Table QCM (Questions à Choix Multiples)
CREATE TABLE IF NOT EXISTS QCM (
    num_question SERIAL PRIMARY KEY,
    question TEXT NOT NULL,
    reponse1 VARCHAR(255) NOT NULL,
    reponse2 VARCHAR(255) NOT NULL,
    reponse3 VARCHAR(255) NOT NULL,
    reponse4 VARCHAR(255) NOT NULL,
    bonne_reponse INTEGER NOT NULL CHECK (bonne_reponse IN (1, 2, 3, 4)),
    qcm_niveau VARCHAR(50) NOT NULL
);

-- Table LOGIN
CREATE TABLE IF NOT EXISTS LOGIN (
    code_log VARCHAR(150) PRIMARY KEY,
    num_etudiant INTEGER NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    FOREIGN KEY (num_etudiant) REFERENCES ETUDIANT(num_etudiant) ON DELETE CASCADE
);

-- Table EXAMEN
CREATE TABLE IF NOT EXISTS EXAMEN (
    num_examen SERIAL PRIMARY KEY,
    num_etudiant INTEGER NOT NULL,
    annee_univ VARCHAR(4) NOT NULL,
    note DECIMAL(5, 2),
    statut VARCHAR(20) NOT NULL CHECK (statut IN ('non_passe', 'en_cours', 'termine')),
    debut_examen TIMESTAMP NOT NULL,
    FOREIGN KEY (num_etudiant) REFERENCES ETUDIANT(num_etudiant) ON DELETE CASCADE,
    UNIQUE(num_etudiant, annee_univ)
);

-- Index pour les recherches fréquentes
CREATE INDEX IF NOT EXISTS idx_etudiant_email ON ETUDIANT(adr_email);
CREATE INDEX IF NOT EXISTS idx_qcm_niveau ON QCM(qcm_niveau);
CREATE INDEX IF NOT EXISTS idx_examen_etudiant ON EXAMEN(num_etudiant);
CREATE INDEX IF NOT EXISTS idx_examen_statut ON EXAMEN(statut);
CREATE INDEX IF NOT EXISTS idx_examen_annee ON EXAMEN(annee_univ);
