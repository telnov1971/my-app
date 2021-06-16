package com.example.application.data.entity;

import com.example.application.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class Region extends AbstractDictionary {
    public Region() {
    }

    public Region(String name, String code) {
        super(name, code);
    }
}
