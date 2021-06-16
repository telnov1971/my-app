package com.example.application.data.entity;

import com.example.application.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class Plan extends AbstractDictionary {
    public Plan() {
    }

    public Plan(String name, String code) {
        super(name, code);
    }
}
