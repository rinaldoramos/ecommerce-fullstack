package com.ecommerce.security.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // added jwtExpirationMs to set the expiration time
    private final Duration jwtExpirationMs;

    // added JwtParser to validate the token
    private final JwtParser jwtParser;

    // Cache the SecretKey to avoid re-decoding Base64 on every request
    private final SecretKey key;

    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtExpirationMs = jwtProperties.expiration();
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.secret()));
        this.jwtParser = Jwts.parser().verifyWith(this.key).build();
    }

    // Extracting the token from the request
    public String getTokenFromRequest(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            logger.debug("Authorization header with Bearer token present in request");
            return authorization.substring(7);
        }
        return null;
    }

    // Generating the token from the username
    public String generateToken(String username) {
        Instant now = Instant.now();

        // 3. Updated to modern JJWT 0.12+ non-deprecated API (subject, issuedAt, expiration)
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(jwtExpirationMs)))
            .signWith(key)
            .compact();
    }

    // Generating the username from the token
    public String getUsernameFromToken(String token) {
        return jwtParser
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    // Validate the token
    public boolean validateToken(String token) {
        try {
            jwtParser
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}