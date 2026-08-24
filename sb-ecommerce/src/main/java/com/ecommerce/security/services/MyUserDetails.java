package com.ecommerce.security.services;

import com.ecommerce.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public class MyUserDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;

    @JsonIgnore
    private String password;

    private String email;
    private Collection<? extends GrantedAuthority> authorities;

    public MyUserDetails(Long userId, String username, String password, String email, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
    }

    public static MyUserDetails build(User user) {
        if (user == null) {
            return null;
        }

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(
                role.getAppRole().name()
            ))
            .toList();

        return new MyUserDetails(
            user.getUserId(),
            user.getUsername(),
            user.getPassword(),
            user.getEmail(),
            authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
