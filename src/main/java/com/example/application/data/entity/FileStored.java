package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Table(name = "file_storage")
@Entity
public class FileStored extends AbstractEntity {
    private LocalDate createdate;

    @Column(name = "name")
    @Length(max = 2048)
    private String name;

    @Column(name = "link")
    private String link;

    @Column(name = "load1c")
    private Boolean load1c;

    @ManyToOne
    @JoinColumn(name = "demand_id")
    private Demand demand;

    public FileStored() {
    }

    public FileStored(String name, String link, Demand demand) {
        createdate = LocalDate.now();
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
}