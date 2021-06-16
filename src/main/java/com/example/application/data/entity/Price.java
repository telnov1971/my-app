package com.example.application.data.entity;

import com.example.application.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class Price extends AbstractDictionary {
    public Price() {
    }

    public Price(String name, String code) {
        super(name, code);
    }
}
