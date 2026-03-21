package com.securebanksystem.uitl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static javax.crypto.Cipher.SECRET_KEY;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKey;

    // Helper method to get the signing key
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Add this above your createToken method
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        // You can add extra data to claims here later, like roles
        return createToken(claims, email);
    }
    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Use the Key object here
                .compact();
    }
}