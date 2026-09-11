package com.ecommerce.security.services;

import com.ecommerce.models.AppRole;
import com.ecommerce.models.Role;
import com.ecommerce.models.User;
import com.ecommerce.security.repositories.RoleRepository;
import com.ecommerce.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Profile("local")
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String @NonNull ... args) throws Exception {
        seed();
    }

    private void seed() {
        Role userRole = roleRepository.findByAppRole(AppRole.ROLE_USER)
            .orElseGet(() -> {
                Role newUserRole = new Role();
                newUserRole.setAppRole(AppRole.ROLE_USER);
                return roleRepository.save(newUserRole);
            });

        Role sellerRole = roleRepository.findByAppRole(AppRole.ROLE_SELLER)
            .orElseGet(() -> {
                Role newSellerRole = new Role();
                newSellerRole.setAppRole(AppRole.ROLE_SELLER);
                return roleRepository.save(newSellerRole);
            });

        Role adminRole = roleRepository.findByAppRole(AppRole.ROLE_ADMIN)
            .orElseGet(() -> {
                Role newAdminRole = new Role();
                newAdminRole.setAppRole(AppRole.ROLE_ADMIN);
                return roleRepository.save(newAdminRole);
            });

        Set<Role> userRoles = new HashSet<>(Set.of(userRole));
        Set<Role> sellerRoles = new HashSet<>(Set.of(sellerRole));
        Set<Role> adminRoles = new HashSet<>(Set.of(userRole, sellerRole, adminRole));


        // Create users if not already present
        if (!userRepository.existsByUsername("user1")) {
            User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
            userRepository.save(user1);
        }

        if (!userRepository.existsByUsername("seller1")) {
            User seller1 = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
            userRepository.save(seller1);
        }

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
            userRepository.save(admin);
        }

        // Update roles for existing users
        userRepository.findByUsername("user1").ifPresent(user -> {
            user.setRoles(userRoles);
            userRepository.save(user);
        });

        userRepository.findByUsername("seller1").ifPresent(seller -> {
            seller.setRoles(sellerRoles);
            userRepository.save(seller);
        });

        userRepository.findByUsername("admin").ifPresent(admin -> {
            admin.setRoles(adminRoles);
            userRepository.save(admin);
        });
    }
}
