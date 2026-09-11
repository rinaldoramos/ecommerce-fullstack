package com.ecommerce.security.filter;


import com.ecommerce.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationTokenFilter.class);
    private final JwtUtils jwtUtils;
    private final AuthenticationDetailsSource<HttpServletRequest, ?> detailsSource = new WebAuthenticationDetailsSource();

    public AuthenticationTokenFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        jwtUtils.extractTokenFromCookie(request)
            .flatMap(jwtUtils::parseClaims)
            .ifPresent(claims -> authenticate(claims, request));

        // Pass the request along the filter chain
        filterChain.doFilter(request, response);
    }

    private void authenticate(Claims claims, @NonNull HttpServletRequest request) {
        String username = jwtUtils.extractUsername(claims);
        List<SimpleGrantedAuthority> authorities = jwtUtils.extractAuthorities(claims);

        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(username, null, authorities);

        authentication.setDetails(detailsSource.buildDetails(request));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
