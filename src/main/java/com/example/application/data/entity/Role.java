package com.example.application.data.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, GARANT, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }

}