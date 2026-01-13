CREATE TABLE IF NOT EXISTS tables (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    position_x INTEGER DEFAULT 0,
    position_y INTEGER DEFAULT 0,
    shape VARCHAR(20) NOT NULL CHECK (shape IN ('rectangle', 'circle', 'square')),
    status VARCHAR(20) NOT NULL DEFAULT 'available' CHECK (status IN ('available', 'occupied', 'reserved', 'cleaning')),
    -- Remplacement de restaurant_id par resto_code
    resto_code UUID NOT NULL,
    
    -- Autres colonnes
    owner_code UUID,
    active BOOLEAN NOT NULL DEFAULT true,
    created_by UUID,
    created_date BIGINT NOT NULL,
    modified_by UUID,
    modified_date BIGINT NOT NULL,
    version INTEGER DEFAULT 0)
    