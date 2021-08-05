package com.example.application.data.entity;

import com.example.application.data.AbstractDictionary;
import com.example.application.data.AbstractEntity;

import javax.persistence.*;

@Table(name = "reason")
@Entity
public class Reason extends AbstractDictionary {

    private Boolean temporal = false;

    public Reason() {
        this.temporal = false;
    }

    public Boolean getTemporal() {
        return temporal;
    }

    public void setTemporal(Boolean temporal) {
        this.temporal = temporal;
    }
}