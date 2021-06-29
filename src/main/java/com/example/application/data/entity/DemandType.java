package com.example.application.data.entity;

import com.example.application.data.AbstractDictionary;

import javax.persistence.Entity;

@Entity
public class DemandType extends AbstractDictionary {
    public static final Long TO15 = 1L;
    public static final Long TO150 = 2L;
    public static final Long TEMPORARY = 3L;
    public static final Long RECIVER = 5L;

    public DemandType() {
    }

    public DemandType(String name, String code) {
        super(name, code);
    }
}
