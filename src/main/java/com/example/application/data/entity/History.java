package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Table(name = "history")
@Entity
public class History extends AbstractEntity {
    private LocalDateTime createdate;
    private Boolean client;
    @Size(max = 2048)
    private String history;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "demand_id")
    Demand demand;
    @ColumnDefault("false")
    @Column(name = "it_load1c")
    private boolean load1c = false;

    public History() {
        this.createdate = LocalDateTime.now();
        this.history = "";
        this.client = true;
    }

    public History(Demand demand, String history) {
        this.createdate = LocalDateTime.now();
        this.demand = demand;
        this.history = history;
        this.client = true;
    }

    public LocalDateTime getCreateDate() {
        return createdate;
    }
    public void setCreateDate(LocalDateTime createdate) {
        this.createdate = createdate;
    }
    public String getHistory() {
        return history;
    }
    public void setHistory(String history) {
        this.history = history;
    }
    public Demand getDemand() {
        return demand;
    }
    public void setDemand(Demand demand) {
        this.demand = demand;
    }
    public Boolean getClient() {
        return client;
    }
    public void setClient(Boolean client) {
        this.client = client;
    }
}