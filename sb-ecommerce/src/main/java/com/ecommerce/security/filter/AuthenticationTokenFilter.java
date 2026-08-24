package com.ecommerce.security.filter;


import com.ecommerce.security.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationTokenFilter.class);
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public AuthenticationTokenFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        logger.debug("AuthenticationTokenFilter is processing the request");

        // Extract the token from the request
        String tokenFromRequest = jwtUtils.getTokenFromRequest(request);

        // Check is null and valid
        if (tokenFromRequest != null && jwtUtils.validateToken(tokenFromRequest)) {
            logger.debug("Token is valid");

            // Get the username from the token
            String usernameFromToken = jwtUtils.getUsernameFromToken(tokenFromRequest);
            logger.debug("Username from token: {}", usernameFromToken);

            try {
                // Get the user from the database
                UserDetails userDetails = userDetailsService.loadUserByUsername(usernameFromToken);
                logger.debug("User details loaded from database");

                // Create a new authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );
                logger.debug("Authentication token created: {}", authentication);

                // Set the authentication token in the request
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.debug("Authentication details set in the request");

                // Set the authentication token in the security context
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                logger.debug("Authentication token set in the security context");
            } catch (UsernameNotFoundException e) {
                logger.warn("User not found: {}", e.getMessage());
            }
        }
        // Pass the request along the filter chain
        filterChain.doFilter(request, response);
    }
}
