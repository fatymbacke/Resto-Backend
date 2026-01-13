-- V6__Create_restaurant_table.sql

-- Création de la table restaurant avec resto_code et owner_code
CREATE TABLE restaurant (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL,
    cuisine VARCHAR(200)  NULL,
    city VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    currency VARCHAR(3) NOT NULL DEFAULT 'EUR',
    timezone VARCHAR(50) NOT NULL DEFAULT 'Europe/Paris',
    logo VARCHAR(500),
    cover_image VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT true,
    created_by UUID,
    created_date BIGINT DEFAULT (EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000)::BIGINT,
    resto_code UUID,  -- Nouvelle colonne pour le code restaurant (auto-référence ou hiérarchie)
    owner_code UUID,  -- Nouvelle colonne pour le propriétaire
    modified_date BIGINT DEFAULT (EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000)::BIGINT,
    modified_by UUID,
    version INTEGER DEFAULT 0,
    
    -- Contraintes de clés étrangères
    CONSTRAINT fk_restaurant_created_by FOREIGN KEY (created_by) REFERENCES prsnl(id),
    CONSTRAINT fk_restaurant_modified_by FOREIGN KEY (modified_by) REFERENCES prsnl(id),
    CONSTRAINT fk_restaurant_resto_code FOREIGN KEY (resto_code) REFERENCES restaurant(id) ON DELETE SET NULL,
    CONSTRAINT fk_restaurant_owner_code FOREIGN KEY (owner_code) REFERENCES prsnl(id) ON DELETE SET NULL,
    
    -- Contrainte pour éviter les références circulaires
    CONSTRAINT chk_resto_code_not_self CHECK (resto_code != id)
);

-- Index simples (recherches rapides mais autorisent les doublons)
CREATE INDEX idx_restaurant_name ON restaurant(name);
CREATE INDEX idx_restaurant_email ON restaurant(email);
CREATE INDEX idx_restaurant_phone ON restaurant(phone);
CREATE INDEX idx_restaurant_city ON restaurant(city);
CREATE INDEX idx_restaurant_active ON restaurant(active);
CREATE INDEX idx_restaurant_created_date ON restaurant(created_date);
CREATE INDEX idx_restaurant_cuisine ON restaurant(cuisine);

-- Nouveaux index pour resto_code et owner_code
CREATE INDEX idx_restaurant_resto_code ON restaurant(resto_code);
CREATE INDEX idx_restaurant_owner_code ON restaurant(owner_code);

-- Index composite pour les recherches par propriétaire et statut
CREATE INDEX idx_restaurant_owner_active ON restaurant(owner_code, active);

-- Trigger pour la mise à jour automatique du modified_date
CREATE OR REPLACE FUNCTION update_restaurant_modified_date()
RETURNS TRIGGER AS $$
BEGIN
    NEW.modified_date = (EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000)::BIGINT;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_restaurant_modified_date_trigger
    BEFORE UPDATE ON restaurant
    FOR EACH ROW EXECUTE FUNCTION update_restaurant_modified_date();

-- Trigger pour s'assurer qu'au moins un code (resto_code ou owner_code) est présent lors de l'insertion
CREATE OR REPLACE FUNCTION check_restaurant_codes()
RETURNS TRIGGER AS $$
BEGIN
    -- Vérifier qu'au moins un code est présent
    IF NEW.resto_code IS NULL AND NEW.owner_code IS NULL THEN
        RAISE EXCEPTION 'Au moins un code (resto_code ou owner_code) doit être spécifié';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_restaurant_codes_trigger
    BEFORE INSERT OR UPDATE ON restaurant
    FOR EACH ROW EXECUTE FUNCTION check_restaurant_codes();

-- Commentaires pour la documentation
COMMENT ON TABLE restaurant IS 'Table des restaurants avec gestion hiérarchique et multi-propriétaires';
COMMENT ON COLUMN restaurant.id IS 'Identifiant unique du restaurant';
COMMENT ON COLUMN restaurant.name IS 'Nom du restaurant';
COMMENT ON COLUMN restaurant.email IS 'Email de contact du restaurant';
COMMENT ON COLUMN restaurant.phone IS 'Téléphone du restaurant';
COMMENT ON COLUMN restaurant.address IS 'Adresse physique du restaurant';
COMMENT ON COLUMN restaurant.city IS 'Ville du restaurant';
COMMENT ON COLUMN restaurant.description IS 'Description du restaurant';
COMMENT ON COLUMN restaurant.capacity IS 'Capacité d''accueil du restaurant';
COMMENT ON COLUMN restaurant.currency IS 'Devise utilisée (EUR, USD, etc.)';
COMMENT ON COLUMN restaurant.timezone IS 'Fuseau horaire du restaurant';
COMMENT ON COLUMN restaurant.logo IS 'URL du logo du restaurant';
COMMENT ON COLUMN restaurant.cover_image IS 'URL de l''image de couverture';
COMMENT ON COLUMN restaurant.active IS 'Statut actif/inactif du restaurant';
COMMENT ON COLUMN restaurant.created_by IS 'Utilisateur ayant créé le restaurant';
COMMENT ON COLUMN restaurant.created_date IS 'Date de création en millisecondes';
COMMENT ON COLUMN restaurant.resto_code IS 'Code restaurant parent (pour hiérarchie ou regroupement)';
COMMENT ON COLUMN restaurant.owner_code IS 'Propriétaire du restaurant (référence vers prsnl)';
COMMENT ON COLUMN restaurant.modified_date IS 'Date de dernière modification en millisecondes';
COMMENT ON COLUMN restaurant.modified_by IS 'Utilisateur ayant modifié le restaurant';
COMMENT ON COLUMN restaurant.version IS 'Version pour le contrôle de concurrence';

-- Vue pour faciliter l'accès aux restaurants avec leurs propriétaires
CREATE OR REPLACE VIEW restaurant_details_view AS
SELECT 
    r.id,
    r.name,
    r.email,
    r.phone,
    r.address,
    r.city,
    r.description,
    r.capacity,
    r.currency,
    r.timezone,
    r.logo,
    r.cover_image,
    r.active,
    r.created_date,
    r.resto_code,
    r.owner_code,
    r.modified_date,
    -- Informations du propriétaire
    p_owner.firstname as owner_firstname,
    p_owner.lastname as owner_lastname,
    p_owner.email as owner_email,
    p_owner.phone as owner_phone,
    -- Informations du créateur
    p_creator.firstname as creator_firstname,
    p_creator.lastname as creator_lastname,
    -- Informations du modificateur
    p_modifier.firstname as modifier_firstname,
    p_modifier.lastname as modifier_lastname,
    -- Restaurant parent (si hiérarchie)
    r_parent.name as parent_restaurant_name
FROM restaurant r
LEFT JOIN prsnl p_owner ON r.owner_code = p_owner.id
LEFT JOIN prsnl p_creator ON r.created_by = p_creator.id
LEFT JOIN prsnl p_modifier ON r.modified_by = p_modifier.id
LEFT JOIN restaurant r_parent ON r.resto_code = r_parent.id;

-- Fonction utilitaire pour obtenir les restaurants d'un propriétaire
CREATE OR REPLACE FUNCTION get_restaurants_by_owner(owner_uuid UUID)
RETURNS TABLE(
    restaurant_id UUID,
    restaurant_name VARCHAR(100),
    restaurant_email VARCHAR(100),
    restaurant_phone VARCHAR(20),
    restaurant_city VARCHAR(100),
    restaurant_active BOOLEAN
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        r.id,
        r.name,
        r.email,
        r.phone,
        r.city,
        r.active
    FROM restaurant r
    WHERE r.owner_code = owner_uuid
    ORDER BY r.name;
END;
$$ LANGUAGE plpgsql;

-- Message de confirmation
DO $$
BEGIN
    RAISE NOTICE 'Table restaurant créée avec succès avec les colonnes resto_code et owner_code';
    RAISE NOTICE 'Triggers et index créés';
    RAISE NOTICE 'Vue restaurant_details_view créée';
END $$;