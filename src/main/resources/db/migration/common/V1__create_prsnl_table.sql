CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS prsnl (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150)  NOT NULL,
    phone VARCHAR(50),
    active BOOLEAN DEFAULT true,
    created_by UUID,
    created_date BIGINT NOT NULL,
    resto_code UUID,  -- Changé de VARCHAR(50) à UUID
    owner_code UUID,  -- Nouvelle colonne ajoutée
    role VARCHAR(255) NOT NULL,
    modified_by UUID,
    modified_date BIGINT NOT NULL,
    version INT NOT NULL DEFAULT 0
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_prsnl_resto_code ON prsnl(resto_code);
CREATE INDEX IF NOT EXISTS idx_prsnl_owner_code ON prsnl(owner_code);
CREATE INDEX IF NOT EXISTS idx_prsnl_phone ON prsnl(phone);
CREATE INDEX IF NOT EXISTS idx_prsnl_active ON prsnl(active);
CREATE INDEX IF NOT EXISTS idx_prsnl_role ON prsnl(role);
CREATE INDEX IF NOT EXISTS idx_prsnl_email ON prsnl(email);

-- Contraintes de clés étrangères (si les tables restaurant et owner existent)
-- ALTER TABLE prsnl ADD CONSTRAINT fk_prsnl_resto FOREIGN KEY (resto_code) REFERENCES restaurant(id) ON DELETE SET NULL;
-- ALTER TABLE prsnl ADD CONSTRAINT fk_prsnl_owner FOREIGN KEY (owner_code) REFERENCES owner(id) ON DELETE SET NULL;

-- Commentaires pour la documentation
COMMENT ON TABLE prsnl IS 'Table des personnels du restaurant avec gestion des accès multi-restaurants';
COMMENT ON COLUMN prsnl.id IS 'Identifiant unique du personnel';
COMMENT ON COLUMN prsnl.firstname IS 'Prénom du personnel';
COMMENT ON COLUMN prsnl.lastname IS 'Nom du personnel';
COMMENT ON COLUMN prsnl.password IS 'Mot de passe crypté';
COMMENT ON COLUMN prsnl.email IS 'Email unique du personnel';
COMMENT ON COLUMN prsnl.phone IS 'Numéro de téléphone';
COMMENT ON COLUMN prsnl.active IS 'Statut actif/inactif';
COMMENT ON COLUMN prsnl.created_by IS 'Créé par';
COMMENT ON COLUMN prsnl.created_date IS 'Date de création en timestamp';
COMMENT ON COLUMN prsnl.resto_code IS 'Référence vers le restaurant auquel le personnel est associé';
COMMENT ON COLUMN prsnl.owner_code IS 'Référence vers le propriétaire (pour accès multi-restaurants)';
COMMENT ON COLUMN prsnl.role IS 'Rôle du personnel (ROLE_MANAGER, ROLE_SERVEUR, etc.)';
COMMENT ON COLUMN prsnl.modified_by IS 'Modifié par';
COMMENT ON COLUMN prsnl.modified_date IS 'Date de modification en timestamp';
COMMENT ON COLUMN prsnl.version IS 'Version pour l''optimistic locking';


