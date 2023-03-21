package ru.omel.po.data.entity;

import ru.omel.po.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class Price extends AbstractDictionary {
    public Price() {
    }

    public Price(String name, String code) {
        super(name, code);
    }
}
