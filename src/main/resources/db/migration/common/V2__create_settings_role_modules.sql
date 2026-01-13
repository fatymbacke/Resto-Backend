-- V1__Create_security_tables.sql

-- Table des modules
CREATE TABLE modules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    active BOOLEAN DEFAULT true,
    created_by UUID,
    created_date BIGINT NOT NULL,
    resto_code UUID,
    owner_code UUID,
    modified_by UUID,
    modified_date BIGINT NOT NULL,
    version INT NOT NULL DEFAULT 0
);

-- Table des permissions
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(200) NOT NULL UNIQUE,
    description VARCHAR(500),
    module_id UUID NOT NULL,
    active BOOLEAN DEFAULT true,
    created_by UUID,
    created_date BIGINT NOT NULL,
    resto_code UUID,
    owner_code UUID,
    modified_by UUID,
    modified_date BIGINT NOT NULL,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_permission_module 
        FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE
);

-- Table des rôles
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL ,
    description VARCHAR(500),
    is_default BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT true,
    created_by UUID,
    created_date BIGINT NOT NULL,
    resto_code UUID,
    owner_code UUID,
    modified_by UUID,
    modified_date BIGINT NOT NULL,
    version INT NOT NULL DEFAULT 0
);

-- Table de liaison entre rôles et permissions (Many-to-Many)
CREATE TABLE role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    created_date BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000),
    created_by UUID,
    resto_code UUID,
    owner_code UUID,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rolepermission_role 
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_rolepermission_permission 
        FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Mise à jour de la table prsnl pour utiliser role_id au lieu de role
ALTER TABLE prsnl 
ADD COLUMN IF NOT EXISTS role_id UUID,
ADD CONSTRAINT fk_prsnl_role 
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE SET NULL;

-- Conserver la colonne role pour la compatibilité, mais elle devient dépréciée
COMMENT ON COLUMN prsnl.role IS 'Déprécié - Utiliser role_id à la place';

-- Créer les index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_permissions_module_id ON permissions(module_id);
CREATE INDEX IF NOT EXISTS idx_permissions_code ON permissions(code);
CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id ON role_permissions(permission_id);
CREATE INDEX IF NOT EXISTS idx_prsnl_role_id ON prsnl(role_id);
CREATE INDEX IF NOT EXISTS idx_modules_name ON modules(name);
CREATE INDEX IF NOT EXISTS idx_roles_name ON roles(name);
CREATE INDEX IF NOT EXISTS idx_roles_is_default ON roles(is_default);

-- Index pour les nouveaux codes
CREATE INDEX IF NOT EXISTS idx_modules_resto_code ON modules(resto_code);
CREATE INDEX IF NOT EXISTS idx_modules_owner_code ON modules(owner_code);
CREATE INDEX IF NOT EXISTS idx_permissions_resto_code ON permissions(resto_code);
CREATE INDEX IF NOT EXISTS idx_permissions_owner_code ON permissions(owner_code);
CREATE INDEX IF NOT EXISTS idx_roles_resto_code ON roles(resto_code);
CREATE INDEX IF NOT EXISTS idx_roles_owner_code ON roles(owner_code);
CREATE INDEX IF NOT EXISTS idx_role_permissions_resto_code ON role_permissions(resto_code);
CREATE INDEX IF NOT EXISTS idx_role_permissions_owner_code ON role_permissions(owner_code);

-- Commentaires pour la documentation
COMMENT ON TABLE modules IS 'Table des modules du système avec gestion multi-restaurants';
COMMENT ON TABLE permissions IS 'Table des permissions par module avec gestion multi-restaurants';
COMMENT ON TABLE roles IS 'Table des rôles utilisateur avec gestion multi-restaurants';
COMMENT ON TABLE role_permissions IS 'Table de liaison entre rôles et permissions avec gestion multi-restaurants';

COMMENT ON COLUMN modules.resto_code IS 'Restaurant associé au module (pour segmentation)';
COMMENT ON COLUMN modules.owner_code IS 'Propriétaire associé au module (pour accès multi-restaurants)';
COMMENT ON COLUMN permissions.resto_code IS 'Restaurant associé à la permission';
COMMENT ON COLUMN permissions.owner_code IS 'Propriétaire associé à la permission';
COMMENT ON COLUMN roles.resto_code IS 'Restaurant associé au rôle';
COMMENT ON COLUMN roles.owner_code IS 'Propriétaire associé au rôle';
COMMENT ON COLUMN role_permissions.resto_code IS 'Restaurant associé à l''association rôle-permission';
COMMENT ON COLUMN role_permissions.owner_code IS 'Propriétaire associé à l''association rôle-permission';