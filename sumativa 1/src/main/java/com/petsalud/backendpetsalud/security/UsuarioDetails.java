package com.petsalud.backendpetsalud.security;

import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UsuarioDetails implements UserDetails {

    private final Usuario usuario;

    public UsuarioDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Long getId() {
        return usuario.getId();
    }

    public Rol getRol() {
        return usuario.getRol();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        Rol rol = usuario.getRol();

        if (rol != null) {
            String roleName = rol.name();
            // Soporte tanto para hasAuthority('...') como hasRole('...')
            authorities.add(new SimpleGrantedAuthority(roleName));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));

            // Equivalencias jerarquicas y de compatibilidad
            if (rol == Rol.VETERINARIO) {
                authorities.add(new SimpleGrantedAuthority(Rol.EMPLEADO.name()));
                authorities.add(new SimpleGrantedAuthority("ROLE_" + Rol.EMPLEADO.name()));
            } else if (rol == Rol.EMPLEADO) {
                authorities.add(new SimpleGrantedAuthority("VETERINARIO"));
                authorities.add(new SimpleGrantedAuthority("ROLE_VETERINARIO"));
            } else if (rol == Rol.ADMIN || rol == Rol.GERENTE) {
                authorities.add(new SimpleGrantedAuthority(Rol.GERENTE.name()));
                authorities.add(new SimpleGrantedAuthority("ROLE_" + Rol.GERENTE.name()));
                authorities.add(new SimpleGrantedAuthority("ADMIN"));
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                // El gerente tiene acceso jerarquico a funciones de empleado
                authorities.add(new SimpleGrantedAuthority(Rol.EMPLEADO.name()));
                authorities.add(new SimpleGrantedAuthority("ROLE_" + Rol.EMPLEADO.name()));
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
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
        return true;
    }
}
