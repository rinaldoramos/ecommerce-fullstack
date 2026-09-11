package com.ecommerce.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private static final String AUTHORITIES_CLAIM = "authorities";
    private static final String COOKIE_PATH = "/api";

    private final JwtParser jwtParser;
    private final SecretKey secretKey;
    private final Duration tokenValidity;
    private final String issuer;
    private final Clock clock;
    private final String jwtCookie;
    private final Boolean cookieSecure;

    public JwtUtils(JwtProperties jwtProperties, Clock clock) {
        this.issuer = jwtProperties.issuer();
        this.tokenValidity = jwtProperties.expiration();
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.secret()));
        this.clock = clock;
        this.jwtCookie = jwtProperties.jwtCookie();
        this.cookieSecure = jwtProperties.cookieSecure();

        this.jwtParser = Jwts.parser()
            .verifyWith(this.secretKey)
            .requireIssuer(this.issuer)
            .clockSkewSeconds(30)
            .build();

        logger.debug("JwtUtils initialized successfully");
    }

    // A client can hold several cookies under this name when they were stored with
    // different paths, so pick the first one that actually carries a value instead of
    // whichever the client happened to send first.
    public Optional<String> extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
            .filter(cookie -> this.jwtCookie.equals(cookie.getName()))
            .map(Cookie::getValue)
            .filter(StringUtils::hasText)
            .findFirst();
    }

    public ResponseCookie generateCookie(UserDetails userDetails) {
        return cookieBuilder(generateToken(userDetails))
            .maxAge(this.tokenValidity)
            .build();
    }

    public ResponseCookie generateCleanCookie() {
        return cookieBuilder("")
            .maxAge(0)
            .build();
    }

    // A cookie is identified by (name, domain, path). The expiring cookie has to carry the
    // same attributes as the one handed out at login, otherwise the client stores a second,
    // differently scoped cookie instead of overwriting the existing one.
    private ResponseCookie.ResponseCookieBuilder cookieBuilder(String value) {
        return ResponseCookie.from(this.jwtCookie, value)
            .path(COOKIE_PATH)
            .httpOnly(true)
            .secure(this.cookieSecure)
            .sameSite("Lax");
    }

    // generate a jwt token from user details
    public String generateToken(UserDetails userDetails) {

        logger.info("Generating token for user: {}", userDetails.getUsername());

        Instant now = clock.instant();

        List<String> authorities = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        return Jwts.builder()
            .issuer(this.issuer)
            .subject(userDetails.getUsername())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(this.tokenValidity)))
            .claim(AUTHORITIES_CLAIM, authorities)
            .signWith(this.secretKey)
            .compact();
    }

    // extract claims from the token
    public Optional<Claims> parseClaims(String token) {
        try {
            return Optional.of(this.jwtParser.parseSignedClaims(token).getPayload());
        } catch (JwtException | IllegalArgumentException e) {
            logger.debug("Error extracting claims from token: {}", e.getClass().getSimpleName());
            return Optional.empty();
        }
    }

    // extract username from the claims
    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    // extract authorities from the claims
    public List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
        if (!(claims.get(AUTHORITIES_CLAIM) instanceof List<?> authorities)) {
            return List.of();
        }

        return authorities.stream()
            .map(String::valueOf)
            .map(SimpleGrantedAuthority::new)
            .toList();
    }
}
