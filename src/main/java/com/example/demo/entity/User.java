package com.example.demo.entity;

import com.example.demo.Enum.Role;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "PMH_USERS")
public class User implements UserDetails {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME")
    private String userName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    private String passWord;

    @Column(nullable = false)
    @Builder.Default
    private String enabled = "Y";

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "PMH_USER_ROLES",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    @Builder.Default
    private Set<Roles> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(roles -> new SimpleGrantedAuthority(roles.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public @Nullable String getPassword() {
        return passWord;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "Y".equalsIgnoreCase(enabled);
    }
}
