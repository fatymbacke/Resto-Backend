-- Script de création des tables pour le système de gestion de restaurant
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table des commandes (orders)
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    total_price DECIMAL(10,2) DEFAULT 0.00,
    total_items INTEGER DEFAULT 0,
    order_date BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    deliver_info UUID ,

    -- Informations client
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_email VARCHAR(255),
    delivery_address TEXT NOT NULL,
    delivery_city VARCHAR(100) NOT NULL,
    delivery_instructions TEXT,
    
    -- Champs d'audit
    active BOOLEAN DEFAULT true,
    created_by UUID,
    created_date BIGINT NOT NULL,
    modified_by UUID,
    modified_date BIGINT NOT NULL,
    resto_code UUID NOT NULL,
    owner_code UUID NOT NULL,
    
    -- Contraintes
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE SET NULL
);

-- Table des éléments de commande (order_items) - CORRIGÉE pour matcher l'entité Java
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL,
    menu_item_id VARCHAR(255) NOT NULL,  -- ✅ CORRIGÉ: product_id → menu_item_id (VARCHAR)
    menu_item_name VARCHAR(255) NOT NULL,  -- ✅ CORRIGÉ: product_name → menu_item_name
    menu_item_price DECIMAL(10,2) NOT NULL,  -- ✅ CORRIGÉ: unit_price → menu_item_price
    quantity INTEGER NOT NULL DEFAULT 1,
    category VARCHAR(100),  -- ✅ AJOUTÉ: champ manquant
    special_instructions TEXT,  -- ✅ AJOUTÉ: champ manquant (au lieu de notes)
    
    -- Champs d'audit
    active BOOLEAN DEFAULT true,
    created_by UUID,
    created_date BIGINT NOT NULL,
    modified_by UUID,
    modified_date BIGINT NOT NULL,
    resto_code UUID NOT NULL,
    owner_code UUID,  -- ✅ NULLABLE comme dans votre script
    
    -- Contraintes
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Index pour les performances
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_resto_code ON order_items(resto_code);