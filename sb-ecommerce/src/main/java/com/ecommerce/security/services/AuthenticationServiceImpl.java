package com.ecommerce.security.services;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.DuplicateResourceException;
import com.ecommerce.mappers.UserMapper;
import com.ecommerce.models.AppRole;
import com.ecommerce.models.Role;
import com.ecommerce.models.User;
import com.ecommerce.security.dto.*;
import com.ecommerce.security.repositories.RoleRepository;
import com.ecommerce.security.repositories.UserRepository;
import com.ecommerce.security.utils.JwtUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Validated
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    @Override
    public LoginCookieResponse signing(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password())
        );

        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();

        List<String> authorities = myUserDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        ResponseCookie responseCookie = jwtUtils.generateCookie(myUserDetails);

        return new LoginCookieResponse(
            new LoginResponse(myUserDetails.getUsername(), authorities),
            responseCookie
        );
    }

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        if (userRepository.existsByUsername(signupRequest.username())) {
            throw new DuplicateResourceException("Username :: " + signupRequest.username() + " already exists", "username", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new DuplicateResourceException("Email :: " + signupRequest.email() + " already exists", "email", HttpStatus.CONFLICT);
        }

        Role byAppRole = roleRepository.findByAppRole(AppRole.ROLE_USER)
            .orElseGet(() -> {
                Role role = new Role();
                role.setAppRole(AppRole.ROLE_USER);
                return roleRepository.save(role);
            });

        User savedUser;

        try {
            savedUser = createUser(signupRequest, byAppRole);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Username or email already exists", "Username or email", HttpStatus.CONFLICT);
        }

        return userMapper.toSignupResponse(savedUser);
    }

    @Override
    public UserInfoResponse getUserRole(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new APIException("Authentication is required", HttpStatus.UNAUTHORIZED);
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new APIException("User not found", HttpStatus.NOT_FOUND));

        return new UserInfoResponse(
            user.getUserId(),
            user.getUsername(),
            user.getRoles().stream().map(role -> role.getAppRole().toString()).toList()
        );
    }

    @Override
    public ResponseCookie signout() {
        return jwtUtils.generateCleanCookie();
    }

    private User createUser(SignupRequest signupRequest, Role byAppRole) {
        User user = new User();
        user.setUsername(signupRequest.username());
        user.setPassword(passwordEncoder.encode(signupRequest.password()));
        user.setEmail(signupRequest.email());

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(byAppRole);
        user.setRoles(userRoles);
        return userRepository.save(user);
    }
}