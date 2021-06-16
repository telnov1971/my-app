package com.example.application.data.entity;

import com.example.application.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class Voltage extends AbstractDictionary {
    public Voltage() {
    }

    public Voltage(String name, String code) {
        super(name, code);
    }
}
