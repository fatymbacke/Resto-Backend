CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS customer (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150)   NULL,
    phone VARCHAR(50),
    active BOOLEAN DEFAULT true,
    address TEXT,
    city VARCHAR(100),
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
CREATE INDEX IF NOT EXISTS idx_customer_resto_code ON customer(resto_code);
CREATE INDEX IF NOT EXISTS idx_customer_owner_code ON customer(owner_code);
CREATE INDEX IF NOT EXISTS idx_customer_phone ON customer(phone);
CREATE INDEX IF NOT EXISTS idx_customer_active ON customer(active);
CREATE INDEX IF NOT EXISTS idx_customer_role ON customer(role);
CREATE INDEX IF NOT EXISTS idx_customer_email ON customer(email);

-- Contraintes de clés étrangères (si les tables restaurant et owner existent)
-- ALTER TABLE customer ADD CONSTRAINT fk_customer_resto FOREIGN KEY (resto_code) REFERENCES restaurant(id) ON DELETE SET NULL;
-- ALTER TABLE customer ADD CONSTRAINT fk_customer_owner FOREIGN KEY (owner_code) REFERENCES owner(id) ON DELETE SET NULL;

-- Contrainte d'unicité du téléphone par restaurant
ALTER TABLE customer ADD CONSTRAINT uk_customer_phone_resto UNIQUE (phone, resto_code);

-- Commentaires pour la documentation
COMMENT ON TABLE customer IS 'Table des personnels du restaurant avec gestion des accès multi-restaurants';
COMMENT ON COLUMN customer.id IS 'Identifiant unique du personnel';
COMMENT ON COLUMN customer.firstname IS 'Prénom du personnel';
COMMENT ON COLUMN customer.lastname IS 'Nom du personnel';
COMMENT ON COLUMN customer.password IS 'Mot de passe crypté';
COMMENT ON COLUMN customer.email IS 'Email unique du personnel';
COMMENT ON COLUMN customer.phone IS 'Numéro de téléphone';
COMMENT ON COLUMN customer.active IS 'Statut actif/inactif';
COMMENT ON COLUMN customer.created_by IS 'Créé par';
COMMENT ON COLUMN customer.created_date IS 'Date de création en timestamp';
COMMENT ON COLUMN customer.resto_code IS 'Référence vers le restaurant auquel le personnel est associé';
COMMENT ON COLUMN customer.owner_code IS 'Référence vers le propriétaire (pour accès multi-restaurants)';
COMMENT ON COLUMN customer.role IS 'Rôle du personnel (ROLE_MANAGER, ROLE_SERVEUR, etc.)';
COMMENT ON COLUMN customer.modified_by IS 'Modifié par';
COMMENT ON COLUMN customer.modified_date IS 'Date de modification en timestamp';
COMMENT ON COLUMN customer.version IS 'Version pour l''optimistic locking';


