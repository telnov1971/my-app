package com.example.application.data.entity;

import javax.persistence.*;

import com.example.application.data.AbstractEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Demand extends AbstractEntity {

    private LocalDate createdate;

    private String demander;
    @Column(name = "pasSer")
    private String passportSerries;
    @Column(name = "pasNum")
    private String passportNumber;
    @Column(name = "pasIss")
    private String pasportIssued;
    private String inn;
    @Column(name = "addReg")
    private String addressRegistration;
    @Column(name = "addAct")
    private String addressActual;
    private String contact;

    private String reason;
    private String object;
    private String address;

    @OneToMany(mappedBy = "demand", cascade = CascadeType.ALL)
    private List<Point> points = new ArrayList<>();

    private Integer count;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "garant_id")
    private Garant garant;

    private LocalDate expiration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dtype_id")
    private DemandType demandType;
    private boolean done;

    public Demand() {
    }

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
    public boolean isDone() {
        return done;
    }
    public void setDone(boolean done) {
        this.done = done;
    }

}
