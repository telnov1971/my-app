package com.example.application.data.entity;

import com.example.application.data.AbstractDictionary;
import com.example.application.data.AbstractEntity;

import javax.persistence.*;
import java.util.Set;

@Table(name = "reason")
@Entity
public class Reason extends AbstractDictionary {

    private Boolean temporal = false;
    @ElementCollection(targetClass = DType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "reason_dtype", joinColumns = @JoinColumn(name = "reason_id"))
    @Enumerated(EnumType.STRING)
    private Set<DType> dtype;

    public Reason() {
        this.temporal = false;
    }

    public Boolean getTemporal() {
        return temporal;
    }
    public void setTemporal(Boolean temporal) {
        this.temporal = temporal;
    }

    public Set<DType> getDtype() {
        return dtype;
    }
    public void setDtype(Set<DType> dtype) {
        this.dtype = dtype;
    }
}