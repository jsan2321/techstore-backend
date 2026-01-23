package com.ecoapi.goodshopping.user.infrastructure.security;

import com.ecoapi.goodshopping.user.application.port.out.TokenProviderPort;
import com.ecoapi.goodshopping.user.domain.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JWT Token Provider Adapter
 * Implements token generation for authentication using JWT
 * Migrated from old JwtUtils class
 */
@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {
    
    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;
    
    @Value("${auth.token.expirationInMils}")
    private int expirationTime;
    
    @Override
    public String generateToken(User user) {
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());
        
        return Jwts.builder()
                .subject(user.getEmail().value())
                .claim("id", user.getId().value())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + expirationTime))
                .signWith(key())
                .compact();
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | 
                 SignatureException | IllegalArgumentException e) {
            return false;
        }
    }
    
    @Override
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        Object idClaim = claims.get("id");
        if (idClaim instanceof String) {
            return (String) idClaim;
        }
        return String.valueOf(idClaim);
    }
    
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
