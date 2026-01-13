package com.app.manage_restaurant.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;

import com.app.manage_restaurant.exceptions.entities.EntityAlreadyExistsException;
import com.app.manage_restaurant.security.SecurityUser;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GenericDuplicateChecker {

    private final R2dbcEntityTemplate template;

    public GenericDuplicateChecker(R2dbcEntityTemplate template) {
        this.template = template;
    }

    // Vérification doublons pour insert
    public <T> Mono<Void> checkDuplicates(String table, Map<String, Object> fields, Class<T> entityClass,UUID restoCodeR,UUID ownerCodeR) {
        return checkDuplicatesInternal(table, fields, entityClass, null, restoCodeR, ownerCodeR);
    }

    // Vérification doublons pour update (ignore l'ID courant)
    public <T> Mono<Void> checkDuplicatesForUpdate(String table, Map<String, Object> fields, Object currentId, Class<T> entityClass,UUID restoCodeR,UUID ownerCodeR) {
        return checkDuplicatesInternal(table, fields, entityClass, currentId, restoCodeR, ownerCodeR);
    }

    private <T> Mono<Void> checkDuplicatesInternal(String table, Map<String, Object> fields, Class<T> entityClass, Object excludeId,UUID restoCodeR,UUID ownerCodeR) {
        return Mono.deferContextual(ctx -> {
            SecurityUser securityUser = ctx.getOrDefault("CURRENT_USER", null);
            
            if (securityUser == null) {
                return Mono.error(new IllegalStateException("Authentication context not available"));
            }
            
            UUID ownerCode =ownerCodeR !=null ? ownerCodeR: securityUser.getOwnerCode();
            UUID restoCode =restoCodeR !=null ? restoCodeR : securityUser.getRestoCode();
            
            if (ownerCode == null && restoCode == null) {
                return Mono.error(new IllegalStateException("Resto code or Owner code not found in security context"));
            }

            // Si plusieurs champs, construire une condition AND
            if (fields.size() > 1) {
                return checkMultipleFieldsDuplicate(table, fields, entityClass, excludeId, restoCode, ownerCode);
            } else {
                // Un seul champ
                return checkSingleFieldDuplicate(table, fields, entityClass, excludeId, restoCode, ownerCode);
            }
        });
    }

    private <T> Mono<Void> checkSingleFieldDuplicate(String table, Map<String, Object> fields, Class<T> entityClass, 
            Object excludeId, UUID restoCode, UUID ownerCode) {
return Mono.defer(() -> {
Criteria criteria = Criteria.empty();

// Ajouter le champ unique du map
Map.Entry<String, Object> entry = fields.entrySet().iterator().next();
criteria = criteria.and(entry.getKey()).is(entry.getValue());

// Gestion conditionnelle de resto_code
if (restoCode != null) {
criteria = criteria.and("resto_code").is(restoCode);
} else {
criteria = criteria.and("resto_code").isNull();
}

// Gestion conditionnelle de owner_code
if (ownerCode != null) {
criteria = criteria.and("owner_code").is(ownerCode);
} else {
criteria = criteria.and("owner_code").isNull();
}

criteria = criteria.and("active").is(true);

// Exclusion de l'ID courant si fourni
if (excludeId != null) {
criteria = criteria.and("id").not(excludeId);
}

return template.select(entityClass)
.matching(Query.query(criteria))
.first()
.hasElement()
.flatMap(exists -> {
if (exists) {
return Mono.error(new EntityAlreadyExistsException(
entityClass.getSimpleName(),
fields,
entityClass
));
}
return Mono.empty();
});
});
}

    private <T> Mono<Void> checkMultipleFieldsDuplicate(String table, Map<String, Object> fields, Class<T> entityClass, 
            Object excludeId, UUID restoCode, UUID ownerCode) {
return Mono.defer(() -> {
Criteria criteria = Criteria.empty();

// Ajouter tous les champs du map
for (Map.Entry<String, Object> entry : fields.entrySet()) {
criteria = criteria.and(entry.getKey()).is(entry.getValue());
}

// Gestion conditionnelle de resto_code
if (restoCode != null) {
criteria = criteria.and("resto_code").is(restoCode);
} else {
criteria = criteria.and("resto_code").isNull();
}

// Gestion conditionnelle de owner_code
if (ownerCode != null) {
criteria = criteria.and("owner_code").is(ownerCode);
} else {
criteria = criteria.and("owner_code").isNull();
}

criteria = criteria.and("active").is(true);

// Exclusion de l'ID courant si fourni
if (excludeId != null) {
criteria = criteria.and("id").not(excludeId);
}

return template.select(entityClass)
.matching(Query.query(criteria))
.first()
.hasElement()
.flatMap(exists -> {
if (exists) {
return Mono.error(new EntityAlreadyExistsException(
entityClass.getSimpleName(),
fields,
entityClass
));
}
return Mono.empty();
});
});
}

    /**
     * Supprime toutes les entités qui ont des valeurs en doublon avant update.
     */
    public <T> Mono<Void> deleteDuplicatesBeforeUpdate(String table, Map<String, Object> fields, Object currentId, Class<T> entityClass) {
        return Flux.fromIterable(fields.entrySet())
                .concatMap(entry -> {
                    String field = entry.getKey();
                    Object value = entry.getValue();

                    String sql = String.format("DELETE FROM %s WHERE %s = $1 AND id <> $2", table, field);

                    return template.getDatabaseClient()
                            .sql(sql)
                            .bind("$1", value)
                            .bind("$2", currentId)
                            .fetch()
                            .rowsUpdated()
                            .then();
                })
                .then();
    }
}
