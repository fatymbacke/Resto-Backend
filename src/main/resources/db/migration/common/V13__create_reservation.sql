-- Script de création des reservation pour le système de gestion de restaurant
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table des commandes (reservation)
CREATE TABLE reservation (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID,
    reservation_number VARCHAR(50) UNIQUE NOT NULL,
    capacity INTEGER DEFAULT 0,
    reservation_date BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',

    -- Informations client
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_email VARCHAR(255),
    reservation_time VARCHAR(10)  NULL,
    commentaire TEXT,    
    -- Champs d'audit
    active BOOLEAN DEFAULT true,
    created_by UUID,
    created_date BIGINT NOT NULL,
    modified_by UUID,
    modified_date BIGINT NOT NULL,
    resto_code UUID NOT NULL,
    owner_code UUID NOT NULL,
    
    -- Contraintes
    CONSTRAINT fk_reservation_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE SET NULL
);


-- Index pour les performances
CREATE INDEX idx_reservation_reservation_id ON reservation(id);
CREATE INDEX idx_reservation_resto_code ON reservation(resto_code);