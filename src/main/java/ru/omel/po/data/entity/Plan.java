package ru.omel.po.data.entity;

import ru.omel.po.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class Plan extends AbstractDictionary {
    public Plan() {
    }

    public Plan(String name, String code) {
        super(name, code);
    }
}
