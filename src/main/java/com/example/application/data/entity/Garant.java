package com.example.application.data.entity;

import com.example.application.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class Garant extends AbstractDictionary {
    public Garant() {
    }

    public Garant(String name, String code) {
        super(name, code);
    }
}
