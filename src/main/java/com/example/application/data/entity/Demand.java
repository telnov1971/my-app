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

    @OneToMany(mappedBy = "demand", cascade = CascadeType.ALL)
    private List<Expiration> expirations = new ArrayList<>();

    private Integer count;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "garant_id")
    private Garant garant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id")
    private Plan plan;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "price_id")
    private Price price;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "send_id")
    private Send send;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dtype_id")
    private DemandType demandType;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private Status status;
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
    public String getDemander() {
        return demander;
    }
    public void setDemander(String demander) {
        this.demander = demander;
    }
    public String getPassportSerries() {
        return passportSerries;
    }
    public void setPassportSerries(String passportSerries) {
        this.passportSerries = passportSerries;
    }
    public String getPassportNumber() {
        return passportNumber;
    }
    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }
    public String getPasportIssued() {
        return pasportIssued;
    }
    public void setPasportIssued(String pasportIssued) {
        this.pasportIssued = pasportIssued;
    }
    public String getInn() {
        return inn;
    }
    public void setInn(String inn) {
        this.inn = inn;
    }
    public String getAddressRegistration() {
        return addressRegistration;
    }
    public void setAddressRegistration(String addressRegistration) {
        this.addressRegistration = addressRegistration;
    }
    public String getAddressActual() {
        return addressActual;
    }
    public void setAddressActual(String addressActual) {
        this.addressActual = addressActual;
    }
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public List<Point> getPoints() {
        return points;
    }
    public void setPoints(List<Point> points) {
        this.points = points;
    }
    public List<Expiration> getExpirations() {
        return expirations;
    }
    public void setExpirations(List<Expiration> expirations) {
        this.expirations = expirations;
    }
    public Integer getCount() {
        return count;
    }
    public void setCount(Integer count) {
        this.count = count;
    }
    public Garant getGarant() {
        return garant;
    }
    public void setGarant(Garant garant) {
        this.garant = garant;
    }
    public DemandType getDemandType() {
        return demandType;
    }
    public void setDemandType(DemandType demandType) {
        this.demandType = demandType;
    }
    public Plan getPlan() {
        return plan;
    }
    public void setPlan(Plan plan) {
        this.plan = plan;
    }
    public Price getPrice() {
        return price;
    }
    public void setPrice(Price price) {
        this.price = price;
    }
    public Send getSend() {
        return send;
    }
    public void setSend(Send send) {
        this.send = send;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
}
