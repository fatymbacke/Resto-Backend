-- Script de création des tables pour l'application de gestion de restaurant
-- Base de données : PostgreSQL

-- Table des catégories de menu
CREATE TABLE menucategory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    order_no INTEGER DEFAULT 0,
    menu_count BIGINT DEFAULT 0,
    
    -- Champs d'audit
    active BOOLEAN DEFAULT true,
    created_by UUID,
    created_date BIGINT NOT NULL,
    modified_by UUID,
    modified_date BIGINT NOT NULL,
    resto_code UUID,
    owner_code UUID,
    
    -- Contraintes
    CONSTRAINT uk_menu_category_name_resto UNIQUE (name, resto_code, owner_code),
    CONSTRAINT chk_menu_category_order_positive CHECK (order_no >= 0)
);

-- Table des menus
CREATE TABLE menus (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    category VARCHAR(100) NOT NULL,
    image_url VARCHAR(500),
    is_available BOOLEAN DEFAULT true,
    is_vegetarian BOOLEAN DEFAULT false,
    is_vegan BOOLEAN DEFAULT false,
    is_gluten_free BOOLEAN DEFAULT false,
    calories INTEGER CHECK (calories >= 0),
    preparation_time INTEGER CHECK (preparation_time >= 0),
    
    -- Champs d'audit
    active BOOLEAN DEFAULT true,
    created_by UUID,
    created_date BIGINT NOT NULL,
    modified_by UUID,
    modified_date BIGINT NOT NULL,
    resto_code UUID,
    owner_code UUID,
    
    -- Contraintes
    CONSTRAINT uk_menu_name_resto UNIQUE (name, resto_code, owner_code),
    CONSTRAINT chk_menu_price_positive CHECK (price >= 0),
    CONSTRAINT chk_menu_calories_positive CHECK (calories >= 0),
    CONSTRAINT chk_menu_prep_time_positive CHECK (preparation_time >= 0)
);

-- Table pour stocker les ingrédients (relation many-to-many avec menus)
CREATE TABLE menu_ingredients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    menu_id UUID NOT NULL,
    ingredient VARCHAR(100) NOT NULL,
    order_no INTEGER DEFAULT 0,
    
    -- Champs d'audit
    active BOOLEAN DEFAULT true,
    created_by UUID,
    created_date BIGINT,
    modified_by UUID,
    modified_date BIGINT,
    resto_code UUID,
    owner_code UUID,
    
    -- Contraintes
    CONSTRAINT fk_menu_ingredients_menu 
        FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE
    -- La contrainte d'unicité est supprimée pour permettre les doublons si nécessaire
);

-- Table pour stocker les tags (relation many-to-many avec menus)
CREATE TABLE menu_tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    menu_id UUID NOT NULL,
    tag VARCHAR(50) NOT NULL,
    
    -- Champs d'audit
    active BOOLEAN DEFAULT true,
    created_by UUID,
    created_date BIGINT,
    modified_by UUID,
    modified_date BIGINT,
    resto_code UUID,
    owner_code UUID,
    
    -- Contraintes
    CONSTRAINT fk_menu_tags_menu 
        FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE
    -- La contrainte d'unicité est supprimée pour permettre les doublons si nécessaire
);

-- Index pour améliorer les performances
CREATE INDEX idx_menucategory_order ON menucategory(order_no);
CREATE INDEX idx_menucategory_active ON menucategory(active);
CREATE INDEX idx_menucategory_resto ON menucategory(resto_code, owner_code);

CREATE INDEX idx_menus_category ON menus(category);
CREATE INDEX idx_menus_price ON menus(price);
CREATE INDEX idx_menus_is_available ON menus(is_available);
CREATE INDEX idx_menus_active ON menus(active);
CREATE INDEX idx_menus_resto ON menus(resto_code, owner_code);

CREATE INDEX idx_menu_ingredients_menu_id ON menu_ingredients(menu_id);
CREATE INDEX idx_menu_ingredients_active ON menu_ingredients(active);
CREATE INDEX idx_menu_ingredients_resto ON menu_ingredients(resto_code, owner_code);

CREATE INDEX idx_menu_tags_menu_id ON menu_tags(menu_id);
CREATE INDEX idx_menu_tags_active ON menu_tags(active);
CREATE INDEX idx_menu_tags_resto ON menu_tags(resto_code, owner_code);

-- Index pour les recherches textuelles
CREATE INDEX idx_menu_ingredients_ingredient ON menu_ingredients(ingredient);
CREATE INDEX idx_menu_tags_tag ON menu_tags(tag);