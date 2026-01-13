package com.app.manage_restaurant.repositories.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.entities.MenuTag;
import com.app.manage_restaurant.repositories.MenuTagRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MenuTagRepositoryImpl extends BaseRepositoryImpl<MenuTag, UUID> implements MenuTagRepository {
    
    public MenuTagRepositoryImpl(R2dbcEntityTemplate template) {
        super(template, MenuTag.class);
    }

    @Override
    public Flux<MenuTag> findByMenuId(UUID menuId) {
        Query query = Query.query(
            Criteria.where("menu_id").is(menuId)
                    .and("active").is(true)
        );
        return template.select(query, MenuTag.class);
    }

    @Override
    public Mono<Void> deleteByMenuId(UUID menuId) {
        Query query = Query.query(Criteria.where("menu_id").is(menuId));
        return template.delete(query, MenuTag.class)
                .then();
    }

    public Mono<Long> countByMenuId(UUID menuId) {
        Query query = Query.query(
            Criteria.where("menu_id").is(menuId)
                    .and("active").is(true)
        );
        return template.count(query, MenuTag.class);
    }

    public Mono<Boolean> existsByMenuIdAndTag(UUID menuId, String tag) {
        Query query = Query.query(
            Criteria.where("menu_id").is(menuId)
                    .and("tag").is(tag)
                    .and("active").is(true)
        );
        return template.exists(query, MenuTag.class);
    }

    public Mono<Void> deleteByMenuIdAndTag(UUID menuId, String tag) {
        Query query = Query.query(
            Criteria.where("menu_id").is(menuId)
                    .and("tag").is(tag)
        );
        return template.delete(query, MenuTag.class)
                .then();
    }

    public Flux<MenuTag> findByMenuIdAndTagIn(UUID menuId, java.util.List<String> tags) {
        Query query = Query.query(
            Criteria.where("menu_id").is(menuId)
                    .and("tag").in(tags)
                    .and("active").is(true)
        );
        return template.select(query, MenuTag.class);
    }

    public Flux<MenuTag> findByTag(String tag) {
        Query query = Query.query(
            Criteria.where("tag").is(tag)
                    .and("active").is(true)
        );
        return template.select(query, MenuTag.class);
    }

    public Flux<String> findDistinctTags() {
        String sql = "SELECT DISTINCT tag FROM menu_tags WHERE active = true ORDER BY tag";
        return template.getDatabaseClient()
                .sql(sql)
                .map((row, metadata) -> row.get("tag", String.class))
                .all();
    }

    public Flux<MenuTag> findByMenuIdOrderByTag(UUID menuId) {
        Query query = Query.query(
            Criteria.where("menu_id").is(menuId)
                    .and("active").is(true)
        ).sort(org.springframework.data.domain.Sort.by("tag").ascending());
        
        return template.select(query, MenuTag.class);
    }

	@Override
	public Flux<MenuTag> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}
}