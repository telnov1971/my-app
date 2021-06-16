package com.example.application.data.entity;

import javax.persistence.Entity;

import com.example.application.data.AbstractEntity;
import java.time.LocalDate;

@Entity
public class Demand extends AbstractEntity {

    private LocalDate createdate;
    private String object;
    private String address;
    private Integer points;
    private boolean done;

    public LocalDate getCreatedate() {
        return createdate;
    }
    public void setCreatedate(LocalDate createdate) {
        this.createdate = createdate;
    }
    public String getObject() {
        return object;
    }
    public void setObject(String object) {
        this.object = object;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Integer getPoints() {
        return points;
    }
    public void setPoints(Integer points) {
        this.points = points;
    }
    public boolean isDone() {
        return done;
    }
    public void setDone(boolean done) {
        this.done = done;
    }

}
