package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Table(name = "notes")
@Entity
public class Note extends AbstractEntity {
    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "note")
    @Size(max = 1024)
    private String note;

    @Column(name = "client")
    private Boolean client;

    @ManyToOne
    @JoinColumn(name = "demand_id")
    private Demand demand;

    public Note() {
    }

    public Note(Demand demand, LocalDateTime dateTime, String note, Boolean client) {
        this.demand = demand;
        this.dateTime = dateTime;
        this.note = note;
        this.client = client;
    }

    public Note(Demand demand, String note, Boolean client) {
        this.demand = demand;
        this.client = client;
        this.dateTime = LocalDateTime.now();
        this.note = note;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getClient() {
        return client;
    }

    public void setClient(Boolean client) {
        this.client = client;
    }

    public Demand getDemand() {
        return demand;
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }
}