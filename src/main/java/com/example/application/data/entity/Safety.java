package com.example.application.data.entity;

import com.example.application.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class Safety extends AbstractDictionary {
    public Safety() {
    }

    public Safety(String name, String code) {
        super(name, code);
    }
}
