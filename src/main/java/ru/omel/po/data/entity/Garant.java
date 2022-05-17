package ru.omel.po.data.entity;

import ru.omel.po.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class Garant extends AbstractDictionary {
    public Garant() {
    }

    public Garant(String name, String code) {
        super(name, code);
    }
}
