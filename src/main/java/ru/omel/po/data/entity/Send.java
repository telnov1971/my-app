package ru.omel.po.data.entity;

import ru.omel.po.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class Send extends AbstractDictionary {
    public Send() {
    }

    public Send(String name, String code) {
        super(name, code);
    }
}
