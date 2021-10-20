package com.example.application.data.entity;

import com.example.application.data.AbstractDictionary;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Status extends AbstractDictionary {
    public enum EState {EDIT,ADD,NOTE,FREEZE}

    @Column(name = "state")
    private EState state;

    public Status() {
    }

    public Status(String name, String code) {
        super(name, code);
        state = EState.EDIT;
    }

    public EState getState() {
        return state;
    }
}
