package com.example.GestionBanque.models;

import com.example.GestionBanque.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Utilisateur implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;
    private String nomUtilisateur;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private  String motDePasse;

    private String email;

    @Column(name = "actif", columnDefinition = "VARCHAR(5)")
    @Convert(converter = org.hibernate.type.YesNoConverter.class)
    public  Boolean actif;

    @Enumerated(EnumType.STRING)
    public Role role;

    @OneToOne(mappedBy = "utilisateur") // Un utilisateur peut être associé à un seul client
    @JsonManagedReference
    private Client client;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+this.role.name()));
    }

    @Override
    public @Nullable String getPassword() {
        return this.motDePasse;
    }

    @Override
    public String getUsername() {
        return this.nomUtilisateur;
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
        return this.actif;
    }
}
