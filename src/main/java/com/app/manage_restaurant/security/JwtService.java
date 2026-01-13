package com.app.manage_restaurant.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.app.manage_restaurant.entities.EnumPerson;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey SECRET_KEY;
    private final long EXPIRATION_TIME; // 1 heures

    public JwtService(@Value("${security.jwt.secret}") String secretString, @Value("${security.jwt.expiration-ms}") long expiration_time) {
        // Convertir la string en SecretKey pour HS256
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
        this.EXPIRATION_TIME = expiration_time;
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRestoCode(String token) {
        return extractClaim(token, claims -> claims.get("resto_code", String.class));
    }

    public String extractOwnerCode(String token) {
        return extractClaim(token, claims -> claims.get("owner_code", String.class));
    }

    public EnumPerson extractType(String token) {
        return extractClaim(token, claims -> {
            String typeString = claims.get("type", String.class);
            return EnumPerson.valueOf(typeString); // Conversion manuelle de String vers EnumPerson
        });
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username, UUID restoCode, UUID ownerCode, String fullname,
                               UUID userId, String role,UUID role_id, EnumPerson type) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("resto_code", restoCode != null ? restoCode.toString() : null);
        claims.put("owner_code", ownerCode != null ? ownerCode.toString() : null);
        claims.put("fullname", fullname);
        claims.put("user_id", userId != null ? userId.toString() : null);
        claims.put("role", role);       
        claims.put("type", type.name()); // Stocker le nom de l'enum comme String        
        claims.put("role_id", role_id != null ? role_id.toString() : null);

        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Méthode utilitaire pour extraire tous les claims importants
    public JwtUserInfo extractUserInfo(String token) {
        Claims claims = extractAllClaims(token);
        return JwtUserInfo.builder()
                .username(claims.getSubject())
                .restoCode(claims.get("resto_code", String.class))
                .ownerCode(claims.get("owner_code", String.class))
                .fullname(claims.get("fullname", String.class))
                .userId(claims.get("user_id", String.class))
                .role(claims.get("role", String.class))
                .type(extractType(token)) // Utiliser la méthode extractType corrigée
                .build();
    }

    // Record pour transporter les infos utilisateur
    public static record JwtUserInfo(
        String username, 
        String restoCode, 
        String ownerCode,
        String fullname,
        String userId,
        String role,
        EnumPerson type
    ) {
        public static JwtUserInfoBuilder builder() {
            return new JwtUserInfoBuilder();
        }

        static class JwtUserInfoBuilder {
            private String username;
            private String restoCode;
            private String ownerCode;
            private String fullname;
            private String userId;
            private String role;
            private EnumPerson type;

            public JwtUserInfoBuilder username(String username) {
                this.username = username;
                return this;
            }

            public JwtUserInfoBuilder restoCode(String restoCode) {
                this.restoCode = restoCode;
                return this;
            }

            public JwtUserInfoBuilder ownerCode(String ownerCode) {
                this.ownerCode = ownerCode;
                return this;
            }

            public JwtUserInfoBuilder fullname(String fullname) {
                this.fullname = fullname;
                return this;
            }

            public JwtUserInfoBuilder userId(String userId) {
                this.userId = userId;
                return this;
            }

            public JwtUserInfoBuilder role(String role) {
                this.role = role;
                return this;
            }

            public JwtUserInfoBuilder type(EnumPerson type) {
                this.type = type;
                return this;
            }

            public JwtUserInfo build() {
                return new JwtUserInfo(username, restoCode, ownerCode, fullname, userId, role, type);
            }
        }
    }

    // Méthode de validation générale (sans username)
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Méthode pour extraire l'ID utilisateur
    public UUID extractUserId(String token) {
        String userIdString = extractClaim(token, claims -> claims.get("user_id", String.class));
        return userIdString != null ? UUID.fromString(userIdString) : null;
    }

    // Méthode pour extraire le nom complet
    public String extractFullname(String token) {
        return extractClaim(token, claims -> claims.get("fullname", String.class));
    }

    // Méthode pour extraire le rôle
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
}