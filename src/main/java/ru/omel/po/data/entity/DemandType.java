package ru.omel.po.data.entity;

import ru.omel.po.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class DemandType extends AbstractDictionary {
    public static final Long TO15 = 1L;
    public static final Long TO150 = 2L;
    public static final Long TEMPORAL = 3L;
    public static final Long GENERAL = 5L;

    public DemandType() {
    }

    public DemandType(String name, String code) {
        super(name, code);
    }
}
