package com.example.application.data.entity;

import com.example.application.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class Send extends AbstractDictionary {
    public Send() {
    }

    public Send(String name, String code) {
        super(name, code);
    }
}
