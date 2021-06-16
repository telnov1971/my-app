package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Table(name = "EXPIRATION")
@Entity
public class Expiration extends AbstractEntity {
    @ManyToOne
    private Demand demand;

    private String step;
    private LocalDate projectDate;
    private LocalDate usageDate;
    private Double powerMax;
    @ManyToOne
    @JoinColumn(name = "safety_id")
    private Safety safety;

    public Expiration() {
    }

    public Expiration(String step, LocalDate projectDate, LocalDate usageDate, Double powerMax, Safety safety) {
        this.step = step;
        this.projectDate = projectDate;
        this.usageDate = usageDate;
        this.powerMax = powerMax;
        this.safety = safety;
    }
    public String getStep() {
        return step;
    }
    public void setStep(String step) {
        this.step = step;
    }
    public LocalDate getProjectDate() {
        return projectDate;
    }
    public void setProjectDate(LocalDate projectDate) {
        this.projectDate = projectDate;
    }
    public LocalDate getUsageDate() {
        return usageDate;
    }
    public void setUsageDate(LocalDate usageDate) {
        this.usageDate = usageDate;
    }
    public Double getPowerMax() {
        return powerMax;
    }
    public void setPowerMax(Double powerMax) {
        this.powerMax = powerMax;
    }
    public Safety getSafety() {
        return safety;
    }
    public void setSafety(Safety safety) {
        this.safety = safety;
    }
}