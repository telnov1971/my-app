package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;
import org.hibernate.mapping.ToOne;

import javax.persistence.*;

@Table(name = "general")
@Entity
public class General extends AbstractEntity {
    @OneToOne
    private Demand demand;
    // число точек подключения
    @Column(name = "count_points")
    private Integer countPoints;
    @Column(name = "count_transformations")
    private String countTransformations;
    @Column(name = "count_generations")
    private String countGenerations;
    private String techminGeneration;
    private String reservation;

    public General() {
    }
    public Demand getDemand() {
        return demand;
    }
    public void setDemand(Demand demand) {
        this.demand = demand;
    }
    public Integer getCountPoints() {
        return countPoints;
    }
    public void setCountPoints(Integer countPoints) {
        this.countPoints = countPoints;
    }
    public String getCountTransformations() {
        return countTransformations;
    }
    public void setCountTransformations(String countTransformations) {
        this.countTransformations = countTransformations;
    }
    public String getCountGenerations() {
        return countGenerations;
    }
    public void setCountGenerations(String countGenerations) {
        this.countGenerations = countGenerations;
    }
    public String getTechminGeneration() {
        return techminGeneration;
    }
    public void setTechminGeneration(String techminGeneration) {
        this.techminGeneration = techminGeneration;
    }
    public String getReservation() {
        return reservation;
    }
    public void setReservation(String reservation) {
        this.reservation = reservation;
    }
}