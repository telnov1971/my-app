package ru.omel.po.data.entity;

import org.hibernate.annotations.DynamicUpdate;
import ru.omel.po.data.AbstractEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Table(name = "POINT")
@Entity
@DynamicUpdate
public class Point extends AbstractEntity  implements Comparable {
    @Column(name = "number")
    private Integer number;
    @ManyToOne
    private Demand demand;
    @Column(name = "pow_dem")
    @Min(0)
    private Double powerDemand = 0.0;
    @Column(name = "pow_cur")
    @Min(0)
    private Double powerCurrent = 0.0;
    @Column(name = "pow_max")
    @Min(0)
    private Double powerMaximum = 0.0;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "volt_id")
    private Voltage voltage;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "volt_in_id")
    private Voltage voltageIn;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "safe_id")
    private Safety safety;
    @Column(name = "spec")
    private String specification;
    @ColumnDefault("false")
    @Column(name = "it_load1c")
    private boolean load1c = false;

    public Point() {}

    public Point(Integer number, Double powerDemand, Double powerCurrent,
                 Voltage voltage, Voltage voltageIn, Safety safety) {
        this.number = number;
        this.powerDemand = powerDemand;
        this.powerCurrent = powerCurrent;
        this.powerMaximum = this.powerCurrent + this.powerDemand;
        this.voltage = voltage;
        this.voltageIn = voltageIn;
        this.safety = safety;
    }

    public Demand getDemand() {
        return demand;
    }
    public void setDemand(Demand demand) {
        this.demand = demand;
    }
    public Double getPowerDemand() {
        return powerDemand;
    }
    public void setPowerDemand(Double powerDemanded) {
        this.powerDemand = powerDemanded;
//        this.powerMaximum = this.powerCurrent + this.powerDemand;
    }
    public Double getPowerCurrent() {
        return powerCurrent;
    }
    public void setPowerCurrent(Double powerCurrent) {
        this.powerCurrent = powerCurrent;
//        this.powerMaximum = this.powerCurrent + this.powerDemand;
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
    public void setPowerMaximum(Double powerMaximum) {
        this.powerMaximum = powerMaximum;
    }

    public Voltage getVoltageIn() {
        return voltageIn;
    }

    public void setVoltageIn(Voltage voltageIn) {
        this.voltageIn = voltageIn;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public int compareTo(Object o) {
        return this.number - ((Point)o).getNumber();
    }
}