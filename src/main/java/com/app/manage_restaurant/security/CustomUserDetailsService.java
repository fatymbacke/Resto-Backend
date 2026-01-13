package com.app.manage_restaurant.security;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.app.manage_restaurant.repositories.PrsnlRepository;

import reactor.core.publisher.Mono;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final PrsnlRepository repository;

    public CustomUserDetailsService(PrsnlRepository repository) {
        this.repository = repository;
    }

    public PrsnlRepository getRepository() {
        return repository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        // On renvoie directement le Person qui implémente UserDetails
        return repository.findByUsernanme(username)
                .cast(UserDetails.class);
    }
}
