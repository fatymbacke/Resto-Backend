-- Création de la table payment_methods
CREATE TABLE payment_methods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    is_enabled BOOLEAN DEFAULT TRUE,
    processing_fee DECIMAL(5,2) DEFAULT 0.00,
    requires_authentication BOOLEAN DEFAULT FALSE,
    description TEXT,
    resto_code UUID NOT NULL,
    owner_code UUID,
    configuration JSONB,
    active BOOLEAN DEFAULT TRUE,
    created_by UUID,
    created_date BIGINT,
    modified_by UUID,
    modified_date BIGINT
);

-- Contrainte d'unicité pour le nom par restaurant
ALTER TABLE payment_methods ADD CONSTRAINT unique_resto_name UNIQUE (resto_code, name);

-- Index pour améliorer les performances des recherches
CREATE INDEX idx_payment_methods_resto_code ON payment_methods(resto_code);
CREATE INDEX idx_payment_methods_resto_enabled ON payment_methods(resto_code, is_enabled);
CREATE INDEX idx_payment_methods_resto_name ON payment_methods(resto_code, name);
CREATE INDEX idx_payment_methods_active ON payment_methods(active);
CREATE INDEX idx_payment_methods_created_date ON payment_methods(created_date);

-- Commentaires sur la table et les colonnes
COMMENT ON TABLE payment_methods IS 'Table des méthodes de paiement configurées pour chaque restaurant';
COMMENT ON COLUMN payment_methods.id IS 'Identifiant unique de la méthode de paiement';
COMMENT ON COLUMN payment_methods.name IS 'Nom de la méthode de paiement';
COMMENT ON COLUMN payment_methods.type IS 'Type de paiement (credit_card, debit_card, cash, mobile_money, bank_transfer, digital_wallet)';
COMMENT ON COLUMN payment_methods.is_enabled IS 'Indique si la méthode de paiement est activée';
COMMENT ON COLUMN payment_methods.processing_fee IS 'Frais de traitement en pourcentage';
COMMENT ON COLUMN payment_methods.requires_authentication IS 'Indique si une authentification est requise';
COMMENT ON COLUMN payment_methods.description IS 'Description de la méthode de paiement';
COMMENT ON COLUMN payment_methods.resto_code IS 'Référence vers le restaurant';
COMMENT ON COLUMN payment_methods.owner_code IS 'Référence vers le propriétaire';
COMMENT ON COLUMN payment_methods.configuration IS 'Configuration spécifique au processeur de paiement (clés API, etc.)';
COMMENT ON COLUMN payment_methods.active IS 'Indique si l''enregistrement est actif';
COMMENT ON COLUMN payment_methods.created_by IS 'Utilisateur ayant créé l''enregistrement';
COMMENT ON COLUMN payment_methods.created_date IS 'Date de création en timestamp';
COMMENT ON COLUMN payment_methods.modified_by IS 'Utilisateur ayant modifié l''enregistrement';
COMMENT ON COLUMN payment_methods.modified_date IS 'Date de modification en timestamp';