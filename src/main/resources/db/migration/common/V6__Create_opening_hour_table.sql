-- ============================================================
-- V6__Create_opening_hour_table.sql
-- ============================================================

-- TABLE : opening_hour
CREATE TABLE opening_hour (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    days INTEGER NOT NULL CHECK (days BETWEEN 1 AND 7), -- 1 = Lundi, 7 = Dimanche
    open VARCHAR(10),
    close VARCHAR(10),
    is_closed BOOLEAN NOT NULL DEFAULT false
);

-- Index pour accélérer les recherches
CREATE INDEX idx_opening_hour_days ON opening_hour(days);
CREATE INDEX idx_opening_hour_is_closed ON opening_hour(is_closed);


-- TABLE : restaurant_opening_hour (table d’association)
CREATE TABLE restaurant_opening_hour (
    restaurant_id UUID NOT NULL,
    opening_hour_id UUID NOT NULL,
    PRIMARY KEY (restaurant_id, opening_hour_id),
    CONSTRAINT fk_roh_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurant(id) ON DELETE CASCADE,
    CONSTRAINT fk_roh_opening_hour FOREIGN KEY (opening_hour_id) REFERENCES opening_hour(id) ON DELETE CASCADE
);
