package ru.omel.po.data.entity;

import ru.omel.po.data.AbstractEntity;

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
    private Integer client;

    @ManyToOne
    @JoinColumn(name = "demand_id")
    private Demand demand;

    public Note() {
    }

    public Note(Demand demand, LocalDateTime dateTime, String note, Integer client) {
        this.demand = demand;
        this.dateTime = dateTime;
        this.note = note;
        this.client = client;
    }

    public Note(Demand demand, String note, Integer client) {
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

    public Integer getClient() {
        return client;
    }

    public void setClient(Integer client) {
        this.client = client;
    }

    public Demand getDemand() {
        return demand;
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }
}