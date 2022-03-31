package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "file_storage")
@Entity
public class FileStored extends AbstractEntity {
    private LocalDateTime createdate;

    @Column(name = "name")
    @Length(max = 2048)
    private String name;

    @Column(name = "link")
    private String link;

    @Column(name = "client")
    private Integer client;

    @ManyToOne
    @JoinColumn(name = "demand_id")
    private Demand demand;

    @ColumnDefault("false")
    @Column(name = "it_load1c")
    private boolean load1c = false;

    public FileStored() {
    }

    public FileStored(String name, String link, Demand demand) {
        createdate = LocalDateTime.now();
        this.load1c = false;
        this.name = name;
        this.link = link;
        this.demand = demand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getLoad1c() {
        return load1c;
    }

    public void setLoad1c(Boolean load1c) {
        this.load1c = load1c;
    }

    public Demand getDemand() {
        return demand;
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public Integer getClient() {
        return client;
    }

    public void setClient(Integer client) {
        this.client = client;
    }

    public LocalDateTime getCreatedate() {
        return createdate;
    }

    public void setCreatedate(LocalDateTime createdate) {
        this.createdate = createdate;
    }
}