package ru.omel.po.data.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, GARANT, ADMIN, ANONYMOUS;

    @Override
    public String getAuthority() {
        return name();
    }

}