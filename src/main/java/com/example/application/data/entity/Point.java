package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;

import javax.persistence.*;

@Table(name = "POINT")
@Entity
public class Point extends AbstractEntity {
    @ManyToOne
    private Demand demand;
    @Column(name = "powDem")
    private Double powerDemand = 0.0;
    @Column(name = "powCur")
    private Double powerCurrent = 0.0;
    @Column(name = "powMax")
    private Double powerMaximum = 0.0;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "volt_id")
    private Voltage voltage;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "safe_id")
    private Safety safety;
    @Column(name = "spec")
    private String specification;

    public Point() {}

    public Point(Double powerDemand, Double powerCurrent, Voltage voltage, Safety safety) {
        this.powerDemand = powerDemand;
        this.powerCurrent = powerCurrent;
        this.powerMaximum = this.powerCurrent + this.powerDemand;
        this.voltage = voltage;
        this.safety = safety;
    }

    public Double getPowerDemand() {
        return powerDemand;
    }
    public void setPowerDemand(Double powerDemanded) {
        this.powerDemand = powerDemanded;
        this.powerMaximum = this.powerCurrent + this.powerDemand;
    }
    public Double getPowerCurrent() {
        return powerCurrent;
    }
    public void setPowerCurrent(Double powerCurrent) {
        this.powerCurrent = powerCurrent;
        this.powerMaximum = this.powerCurrent + this.powerDemand;
    }
    public Voltage getVoltage() {
        return voltage;
    }
    public void setVoltage(Voltage voltage) {
        this.voltage = voltage;
    }
    public Safety getSafety() {
        return safety;
    }
    public void setSafety(Safety safety) {
        this.safety = safety;
    }
    public String getSpecification() {
        return specification;
    }
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    public Double getPowerMaximum() {
        return powerMaximum;
    }
}