package com.example.application.data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractDictionary extends AbstractEntity {
    @Column(nullable = false)
    private String name;
    private String code;
    private boolean active;

    public AbstractDictionary() {
        this.active = true;
    }

    public AbstractDictionary(String name, String code) {
        super();
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
