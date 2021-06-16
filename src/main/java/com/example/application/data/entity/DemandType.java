package com.example.application.data.entity;

import com.example.application.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class DemandType extends AbstractDictionary {
    public DemandType() {
    }

    public DemandType(String name, String code) {
        super(name, code);
    }
}
