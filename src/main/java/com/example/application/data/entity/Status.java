package com.example.application.data.entity;

import com.example.application.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class Status extends AbstractDictionary {
    public Status() {
    }

    public Status(String name, String code) {
        super(name, code);
    }
}
