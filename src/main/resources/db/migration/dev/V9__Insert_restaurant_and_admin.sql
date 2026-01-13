-- ============================================================
-- Insertion du rôle SUPERADMIN et d'un utilisateur SUPERADMIN
-- ============================================================

DO $$
DECLARE
    super_admin_id UUID := 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a15';
    super_admin_role_id UUID := 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a16';
    current_epoch BIGINT;
    super_admin_password TEXT := '$2a$10$mCOckI8VOiEQkyQO3PIZH.bcQ23nhFQBlV1TzBBMWXuDRV2fDToi2'; -- "775073511" crypté
BEGIN
    current_epoch := EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000;

    -- Création du rôle SUPERADMIN (sans la colonne permissions)
    INSERT INTO roles (
        id, name, description, active, 
        created_date, modified_date, version
    ) VALUES (
        super_admin_role_id,
        'SUPERADMIN',
        'Administrateur suprême avec tous les droits sur le système',
        true,
        current_epoch,
        current_epoch,
        1
    );

    RAISE NOTICE 'Rôle SUPERADMIN créé avec ID: %', super_admin_role_id;

    -- Attribution de toutes les permissions au rôle SUPERADMIN via role_permissions
    INSERT INTO role_permissions (role_id, permission_id, created_date)
    SELECT super_admin_role_id, id, current_epoch FROM permissions;

    RAISE NOTICE 'Toutes les permissions attribuées au rôle SUPERADMIN';

    -- Création de l'utilisateur SUPERADMIN
    INSERT INTO prsnl (
        id,
        firstname,
        lastname,
        password,
        email,
        phone,
        active,
        created_date,
        resto_code,
        owner_code,
        role_id,
        role,
        modified_date,
        version
    ) VALUES (
        super_admin_id,
        'Super',
        'Admin',
        super_admin_password,
        'superadmin@system.fr',
        '+221775073511',
        true,
        current_epoch,
        NULL, -- SUPERADMIN n'est pas lié à un restaurant spécifique
        super_admin_id, -- Auto-référence comme propriétaire
        super_admin_role_id,
        'ROLE_SUPERADMIN',
        current_epoch,
        1
    );

    RAISE NOTICE 'Utilisateur SUPERADMIN créé avec ID: %', super_admin_id;
    RAISE NOTICE 'Email: superadmin@system.fr';
    RAISE NOTICE 'Téléphone: +221775073511';
    RAISE NOTICE 'Mot de passe: 775073511';

EXCEPTION
    WHEN unique_violation THEN
        RAISE EXCEPTION 'Erreur: Un enregistrement avec ces identifiants existe déjà';
    WHEN others THEN
        RAISE EXCEPTION 'Erreur lors de la création: %', SQLERRM;
END $$;

-- Vérification de la création
DO $$
DECLARE
    super_admin_role_id UUID;
BEGIN
    RAISE NOTICE 'VÉRIFICATION DU SUPERADMIN:';
    RAISE NOTICE '========================================';
    
    -- Récupérer l'ID du rôle SUPERADMIN
    SELECT id INTO super_admin_role_id FROM roles WHERE name = 'SUPERADMIN';
    
    -- Vérifier le rôle SUPERADMIN
    IF super_admin_role_id IS NOT NULL THEN
        RAISE NOTICE '✅ Rôle SUPERADMIN créé avec succès';
        RAISE NOTICE '   ID: %', super_admin_role_id;
        RAISE NOTICE '   Permissions attribuées: %', (SELECT COUNT(*) FROM role_permissions WHERE role_id = super_admin_role_id);
    ELSE
        RAISE NOTICE '❌ Rôle SUPERADMIN non trouvé';
    END IF;
    
    -- Vérifier l'utilisateur SUPERADMIN
    IF EXISTS (SELECT 1 FROM prsnl WHERE phone = '+221775073511') THEN
        RAISE NOTICE '✅ Utilisateur SUPERADMIN créé avec succès';
        RAISE NOTICE '   Nom: % %', 
            (SELECT firstname FROM prsnl WHERE phone = '+221775073511'),
            (SELECT lastname FROM prsnl WHERE phone = '+221775073511');
        RAISE NOTICE '   Téléphone: %', (SELECT phone FROM prsnl WHERE phone = '+221775073511');
        RAISE NOTICE '   Email: %', (SELECT email FROM prsnl WHERE phone = '+221775073511');
        RAISE NOTICE '   Rôle: %', (SELECT name FROM roles r JOIN prsnl p ON p.role_id = r.id WHERE p.phone = '+221775073511');
    ELSE
        RAISE NOTICE '❌ Utilisateur SUPERADMIN non trouvé';
    END IF;
    
END $$;


-- Insérer un restaurant parent avec resto_code NULL (exemple)
-- Note: owner_code doit exister dans la table prsnl
INSERT INTO restaurant (id,
    name, email, phone, address, city, description, 
    capacity, cuisine, active, created_by, owner_code
) VALUES (
'4b2326ec-e0e4-450b-8a41-84ee1effd9c9',
    'Restaurant Principal',
    'parent@example.com',
    '+33123456789',
    '123 Rue Principale',
    'Paris',
    'Notre restaurant principal',
    50,
    'Française',
    true,
    NULL, 
    'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a15')