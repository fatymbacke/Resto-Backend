 -- Script d'insertion des données de sécurité pour une application de restauration
-- Version adaptée à la structure existante de la base de données - Sans rôles

-- Insertion des modules de base
INSERT INTO modules (id, name, description, created_date, modified_date, version) VALUES
-- Module Sécurité
(gen_random_uuid(), 'SÉCURITÉ', 'Gestion des accès et permissions', 
 extract(epoch from current_timestamp) * 1000, extract(epoch from current_timestamp) * 1000, 1),
 
 -- Module Dashboard (Tableau de bord)
(gen_random_uuid(), 'DASHBOARD', 'Tableau de bord et vue d''ensemble', 
 extract(epoch from current_timestamp) * 1000, extract(epoch from current_timestamp) * 1000, 1),

-- Module Réservations
(gen_random_uuid(), 'RÉSERVATIONS', 'Gestion des réservations de tables', 
 extract(epoch from current_timestamp) * 1000, extract(epoch from current_timestamp) * 1000, 1),

-- Module Menus
(gen_random_uuid(), 'MENUS', 'Gestion des menus et cartes', 
 extract(epoch from current_timestamp) * 1000, extract(epoch from current_timestamp) * 1000, 1),

-- Module Clients
(gen_random_uuid(), 'CLIENTS', 'Gestion de la clientèle', 
 extract(epoch from current_timestamp) * 1000, extract(epoch from current_timestamp) * 1000, 1),

-- Module Factures
(gen_random_uuid(), 'FACTURES', 'Gestion des factures et documents comptables', 
 extract(epoch from current_timestamp) * 1000, extract(epoch from current_timestamp) * 1000, 1),
-- Module Personnels
(gen_random_uuid(), 'PERSONNELS', 'Gestion du personnel et des équipes', 
 extract(epoch from current_timestamp) * 1000, extract(epoch from current_timestamp) * 1000, 1),
-- Module Tables
(gen_random_uuid(), 'TABLES', 'Gestion des tables et disposition', 
 extract(epoch from current_timestamp) * 1000, extract(epoch from current_timestamp) * 1000, 1),

-- Module Paiements
(gen_random_uuid(), 'PAIEMENTS', 'Gestion des moyens de paiement', 
 extract(epoch from current_timestamp) * 1000, extract(epoch from current_timestamp) * 1000, 1),




-- Module Commandes
(gen_random_uuid(), 'COMMANDES', 'Gestion des commandes clients', 
 extract(epoch from current_timestamp) * 1000, extract(epoch from current_timestamp) * 1000, 1),


-- Module Caisse
(gen_random_uuid(), 'CAISSE', 'Gestion de la caisse et paiements', 
 extract(epoch from current_timestamp) * 1000, extract(epoch from current_timestamp) * 1000, 1),

-- Module Rapports
(gen_random_uuid(), 'RAPPORTS', 'Rapports et statistiques', 
 extract(epoch from current_timestamp) * 1000, extract(epoch from current_timestamp) * 1000, 1),



-- Module Livraison (NOUVEAU)
(gen_random_uuid(), 'LIVRAISON', 'Gestion des livraisons et livreurs', 
 extract(epoch from current_timestamp) * 1000, extract(epoch from current_timestamp) * 1000, 1);

-- Insertion des permissions pour chaque module
DO $$
DECLARE
    module_securite UUID;
    module_commandes UUID;
    module_caisse UUID;
    module_rapports UUID;
    module_livraison UUID; -- NOUVEAU
    module_dashboard UUID;
    module_reservations UUID;
    module_menus UUID;
    module_clients UUID;
    module_factures UUID;
    module_personnels UUID;
    module_tables UUID;
    module_paiements UUID;
    current_epoch BIGINT;
BEGIN
    current_epoch := extract(epoch from current_timestamp) * 1000;
    
    -- Récupérer les IDs des modules
    SELECT id INTO module_securite FROM modules WHERE name = 'SÉCURITÉ';
    SELECT id INTO module_restaurant FROM modules WHERE name = 'RESTAURANT';
    SELECT id INTO module_commandes FROM modules WHERE name = 'COMMANDES';
    SELECT id INTO module_caisse FROM modules WHERE name = 'CAISSE';
    SELECT id INTO module_rapports FROM modules WHERE name = 'RAPPORTS';
    SELECT id INTO module_parametres FROM modules WHERE name = 'PARAMÈTRES';
    SELECT id INTO module_livraison FROM modules WHERE name = 'LIVRAISON'; -- NOUVEAU
    SELECT id INTO module_dashboard FROM modules WHERE name = 'DASHBOARD'; -- NOUVEAU
    SELECT id INTO module_reservations FROM modules WHERE name = 'RÉSERVATIONS'; -- NOUVEAU
    SELECT id INTO module_menus FROM modules WHERE name = 'MENUS'; -- NOUVEAU
    SELECT id INTO module_clients FROM modules WHERE name = 'CLIENTS'; -- NOUVEAU
    SELECT id INTO module_factures FROM modules WHERE name = 'FACTURES'; -- NOUVEAU
    SELECT id INTO module_personnels FROM modules WHERE name = 'PERSONNELS'; -- NOUVEAU
    SELECT id INTO module_tables FROM modules WHERE name = 'TABLES'; -- NOUVEAU
    SELECT id INTO module_paiements FROM modules WHERE name = 'PAIEMENTS'; -- NOUVEAU


    -- Permissions du module SÉCURITÉ
    INSERT INTO permissions (id, name, code, description, module_id, created_date, modified_date, version) VALUES
    (gen_random_uuid(), 'Voir les utilisateurs', 'SECURITY_USER_READ', 'Permission de voir la liste des utilisateurs', module_securite, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Créer utilisateur', 'SECURITY_USER_CREATE', 'Permission de créer un utilisateur', module_securite, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Modifier utilisateur', 'SECURITY_USER_UPDATE', 'Permission de modifier un utilisateur', module_securite, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Supprimer utilisateur', 'SECURITY_USER_DELETE', 'Permission de supprimer un utilisateur', module_securite, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer les rôles', 'SECURITY_ROLE_MANAGE', 'Permission de gérer les rôles', module_securite, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Voir les permissions', 'SECURITY_PERMISSION_READ', 'Permission de voir les permissions', module_securite, current_epoch, current_epoch, 1);

 
    -- Permissions du module COMMANDES
    INSERT INTO permissions (id, name, code, description, module_id, created_date, modified_date, version) VALUES
    (gen_random_uuid(), 'Voir les commandes', 'ORDER_READ', 'Permission de voir les commandes', module_commandes, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Créer commande', 'ORDER_CREATE', 'Permission de créer une commande', module_commandes, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Modifier commande', 'ORDER_UPDATE', 'Permission de modifier une commande', module_commandes, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Annuler commande', 'ORDER_CANCEL', 'Permission d''annuler une commande', module_commandes, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer statut commande', 'ORDER_STATUS_MANAGE', 'Permission de changer le statut des commandes', module_commandes, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Voir historique commandes', 'ORDER_HISTORY_READ', 'Permission de voir l''historique des commandes', module_commandes, current_epoch, current_epoch, 1);
    -- Permissions du module DASHBOARD
    INSERT INTO permissions (id, name, code, description, module_id, created_date, modified_date, version) VALUES
    (gen_random_uuid(), 'Accéder au tableau de bord', 'DASHBOARD_VIEW', 'Permission d''accéder au tableau de bord principal', module_dashboard, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Voir les statistiques globales', 'DASHBOARD_STATS_VIEW', 'Permission de voir les statistiques globales', module_dashboard, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Voir les alertes', 'DASHBOARD_ALERTS_VIEW', 'Permission de voir les alertes et notifications', module_dashboard, current_epoch, current_epoch, 1);
    -- Permissions du module RÉSERVATIONS
    INSERT INTO permissions (id, name, code, description, module_id, created_date, modified_date, version) VALUES
    (gen_random_uuid(), 'Voir les réservations', 'RESERVATION_VIEW', 'Permission de voir la liste des réservations', module_reservations, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Créer réservation', 'RESERVATION_CREATE', 'Permission de créer une nouvelle réservation', module_reservations, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Modifier réservation', 'RESERVATION_UPDATE', 'Permission de modifier une réservation', module_reservations, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Annuler réservation', 'RESERVATION_CANCEL', 'Permission d''annuler une réservation', module_reservations, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Confirmer réservation', 'RESERVATION_CONFIRM', 'Permission de confirmer une réservation', module_reservations, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer calendrier', 'RESERVATION_CALENDAR_MANAGE', 'Permission de gérer le calendrier des réservations', module_reservations, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Voir historique réservations', 'RESERVATION_HISTORY_VIEW', 'Permission de voir l''historique des réservations', module_reservations, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Supprimer réservation', 'RESERVATION_DELETE', 'Permission de supprimer une réservation', module_reservations, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer statut réservations', 'RESERVATION_STATUS_MANAGE', 'Permission de gérer le statut des réservations', module_reservations, current_epoch, current_epoch, 1);
  
    
    -- Permissions du module MENUS
    INSERT INTO permissions (id, name, code, description, module_id, created_date, modified_date, version) VALUES
    (gen_random_uuid(), 'Voir les menus', 'MENU_VIEW', 'Permission de voir les menus', module_menus, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Créer menu', 'MENU_CREATE', 'Permission de créer un nouveau menu', module_menus, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Modifier menu', 'MENU_UPDATE', 'Permission de modifier un menu existant', module_menus, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Supprimer menu', 'MENU_DELETE', 'Permission de supprimer un menu', module_menus, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer les catégories', 'MENU_CATEGORY_MANAGE', 'Permission de gérer les catégories de plats', module_menus, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer les menus', 'MENU_MANAGE', 'Permission de gérer les menus', module_menus, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Voir les catégories', 'CATEGORY_VIEW', 'Permission de voir les catégories', module_menus, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Créer catégorie', 'CATEGORY_CREATE', 'Permission de créer un nouveau catégorie', module_menus, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Modifier catégorie', 'CATEGORY_UPDATE', 'Permission de modifier une catégories existante', module_menus, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Supprimer catégorie', 'CATEGORY_DELETE', 'Permission de supprimer une catégories', module_menus, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer statut tables', 'CATEGORY_STATUS_MANAGE', 'Permission de gérer le statut des catégories', module_tables, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer statut tables', 'MENU_STATUS_MANAGE', 'Permission de gérer le statut des menus', module_tables, current_epoch, current_epoch, 1);

    
    
    -- Permissions du module FACTURES
    INSERT INTO permissions (id, name, code, description, module_id, created_date, modified_date, version) VALUES
    (gen_random_uuid(), 'Voir les factures', 'INVOICE_VIEW', 'Permission de voir la liste des factures', module_factures, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Créer facture', 'INVOICE_CREATE', 'Permission de créer une nouvelle facture', module_factures, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Modifier facture', 'INVOICE_UPDATE', 'Permission de modifier une facture', module_factures, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Supprimer facture', 'INVOICE_DELETE', 'Permission de supprimer une facture', module_factures, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Générer facture', 'INVOICE_GENERATE', 'Permission de générer une facture', module_factures, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Envoyer facture', 'INVOICE_SEND', 'Permission d''envoyer une facture par email', module_factures, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Imprimer facture', 'INVOICE_PRINT', 'Permission d''imprimer une facture', module_factures, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer avoirs', 'INVOICE_CREDIT_MANAGE', 'Permission de gérer les avoirs', module_factures, current_epoch, current_epoch, 1);
    -- Permissions du module PERSONNELS
    INSERT INTO permissions (id, name, code, description, module_id, created_date, modified_date, version) VALUES
    (gen_random_uuid(), 'Voir le personnel', 'STAFF_VIEW', 'Permission de voir la liste du personnel', module_personnels, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Créer membre personnel', 'STAFF_CREATE', 'Permission d''ajouter un membre du personnel', module_personnels, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Modifier personnel', 'STAFF_UPDATE', 'Permission de modifier les informations du personnel', module_personnels, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Supprimer personnel', 'STAFF_DELETE', 'Permission de supprimer un membre du personnel', module_personnels, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer horaires personnel', 'STAFF_SCHEDULE_MANAGE', 'Permission de gérer les horaires du personnel', module_personnels, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer congés personnel', 'STAFF_LEAVE_MANAGE', 'Permission de gérer les congés du personnel', module_personnels, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Voir performances personnel', 'STAFF_PERFORMANCE_VIEW', 'Permission de voir les performances du personnel', module_personnels, current_epoch, current_epoch, 1);

    -- Permissions du module TABLES
    INSERT INTO permissions (id, name, code, description, module_id, created_date, modified_date, version) VALUES
    (gen_random_uuid(), 'Voir les tables', 'TABLE_VIEW', 'Permission de voir la disposition des tables', module_tables, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Créer table', 'TABLE_CREATE', 'Permission de créer une nouvelle table', module_tables, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Modifier table', 'TABLE_UPDATE', 'Permission de modifier une table', module_tables, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Supprimer table', 'TABLE_DELETE', 'Permission de supprimer une table', module_tables, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer disposition tables', 'TABLE_LAYOUT_MANAGE', 'Permission de gérer la disposition des tables', module_tables, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer statut tables', 'TABLE_STATUS_MANAGE', 'Permission de gérer le statut des tables', module_tables, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Assigner table', 'TABLE_ASSIGN', 'Permission d''assigner une table à une réservation', module_tables, current_epoch, current_epoch, 1);

    -- Permissions du module PAIEMENTS
    INSERT INTO permissions (id, name, code, description, module_id, created_date, modified_date, version) VALUES
    (gen_random_uuid(), 'Voir méthodes paiement', 'PAYMENT_METHOD_VIEW', 'Permission de voir les méthodes de paiement', module_paiements, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer méthodes paiement', 'PAYMENT_METHOD_MANAGE', 'Permission de gérer les méthodes de paiement', module_paiements, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Configurer paiements', 'PAYMENT_CONFIG_MANAGE', 'Permission de configurer les paramètres de paiement', module_paiements, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer terminaux paiement', 'PAYMENT_TERMINAL_MANAGE', 'Permission de gérer les terminaux de paiement', module_paiements, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Voir transactions', 'PAYMENT_TRANSACTION_VIEW', 'Permission de voir les transactions', module_paiements, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer frais de service', 'PAYMENT_SERVICE_FEE_MANAGE', 'Permission de gérer les frais de service', module_paiements, current_epoch, current_epoch, 1);

    
    
    
    -- Permissions du module CLIENTS
    INSERT INTO permissions (id, name, code, description, module_id, created_date, modified_date, version) VALUES
    (gen_random_uuid(), 'Voir les clients', 'CLIENT_VIEW', 'Permission de voir la liste des clients', module_clients, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Créer client', 'CLIENT_CREATE', 'Permission de créer un nouveau client', module_clients, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Modifier client', 'CLIENT_UPDATE', 'Permission de modifier les informations d''un client', module_clients, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Supprimer client', 'CLIENT_DELETE', 'Permission de supprimer un client', module_clients, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Voir historique client', 'CLIENT_HISTORY_VIEW', 'Permission de voir l''historique d''un client', module_clients, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer fidélité', 'CLIENT_LOYALTY_MANAGE', 'Permission de gérer le programme de fidélité', module_clients, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Exporter liste clients', 'CLIENT_EXPORT', 'Permission d''exporter la liste des clients', module_clients, current_epoch, current_epoch, 1);

  
    
    
    
    
    
    
    -- Permissions du module CAISSE
    INSERT INTO permissions (id, name, code, description, module_id, created_date, modified_date, version) VALUES
    (gen_random_uuid(), 'Encaisser commande', 'CASHIER_CHECKOUT', 'Permission d''encaisser les commandes', module_caisse, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Voir les ventes', 'CASHIER_SALES_READ', 'Permission de voir les ventes', module_caisse, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer les paiements', 'CASHIER_PAYMENT_MANAGE', 'Permission de gérer les modes de paiement', module_caisse, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Ouvrir/fermer caisse', 'CASHIER_REGISTER_MANAGE', 'Permission d''ouvrir/fermer la caisse', module_caisse, current_epoch, current_epoch, 1);

    -- Permissions du module RAPPORTS
    INSERT INTO permissions (id, name, code, description, module_id, created_date, modified_date, version) VALUES
    (gen_random_uuid(), 'Voir rapports ventes', 'REPORT_SALES_READ', 'Permission de voir les rapports de ventes', module_rapports, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Voir rapports stock', 'REPORT_STOCK_READ', 'Permission de voir les rapports de stock', module_rapports, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Voir statistiques', 'REPORT_STATS_READ', 'Permission de voir les statistiques', module_rapports, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Exporter rapports', 'REPORT_EXPORT', 'Permission d''exporter les rapports', module_rapports, current_epoch, current_epoch, 1);

    
    
    -- PERMISSIONS DU MODULE LIVRAISON (NOUVEAU)
    INSERT INTO permissions (id, name, code, description, module_id, created_date, modified_date, version) VALUES
    (gen_random_uuid(), 'Voir les livraisons', 'DELIVERY_READ', 'Permission de voir les livraisons', module_livraison, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Créer livraison', 'DELIVERY_CREATE', 'Permission de créer une livraison', module_livraison, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Modifier livraison', 'DELIVERY_UPDATE', 'Permission de modifier une livraison', module_livraison, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Annuler livraison', 'DELIVERY_CANCEL', 'Permission d''annuler une livraison', module_livraison, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer statut livraison', 'DELIVERY_STATUS_MANAGE', 'Permission de changer le statut des livraisons', module_livraison, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Assigner livreur', 'DELIVERY_ASSIGN_DRIVER', 'Permission d''assigner un livreur à une livraison', module_livraison, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer les livreurs', 'DELIVERY_DRIVER_MANAGE', 'Permission de gérer les livreurs', module_livraison, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Voir historique livraisons', 'DELIVERY_HISTORY_READ', 'Permission de voir l''historique des livraisons', module_livraison, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer zones de livraison', 'DELIVERY_ZONE_MANAGE', 'Permission de gérer les zones de livraison', module_livraison, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer frais de livraison', 'DELIVERY_FEE_MANAGE', 'Permission de gérer les frais de livraison', module_livraison, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Suivre livraison en temps réel', 'DELIVERY_TRACKING', 'Permission de suivre les livraisons en temps réel', module_livraison, current_epoch, current_epoch, 1),
    (gen_random_uuid(), 'Gérer véhicules', 'DELIVERY_VEHICLE_MANAGE', 'Permission de gérer les véhicules de livraison', module_livraison, current_epoch, current_epoch, 1);

END $$;

-- Message de confirmation
DO $$
BEGIN
    RAISE NOTICE 'Données de sécurité insérées avec succès!';
    RAISE NOTICE 'Modules créés: 9';
    RAISE NOTICE 'Permissions créées: %', (SELECT COUNT(*) FROM permissions);
    RAISE NOTICE 'Aucun rôle inséré (opération ignorée)';
    RAISE NOTICE 'Données disponibles pour tous les tenants';
END $$;