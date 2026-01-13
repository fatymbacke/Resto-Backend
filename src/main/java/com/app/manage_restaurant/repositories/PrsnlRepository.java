package com.app.manage_restaurant.repositories;

import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.entities.EnumPerson;
import com.app.manage_restaurant.entities.Person;
import com.app.manage_restaurant.entities.Prsnl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface PrsnlRepository extends BaseRepository<Prsnl, UUID> {
    Mono<Person> findByPhoneAndRestoCodeAndType(String phone, String restoCode,EnumPerson type);    
    Mono<Prsnl> findByUsernanme(String phone);   

    Mono<Long> counts();
    Mono<Boolean> existsByPhoneAndRestoCode(String phone, String restoCode);
    Mono<Prsnl> update(Prsnl prsnl);
     Flux<Prsnl> findByResto(UUID resto,boolean active);
    
  
}
