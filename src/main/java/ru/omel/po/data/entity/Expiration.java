package ru.omel.po.data.entity;

import org.hibernate.annotations.DynamicUpdate;
import ru.omel.po.data.AbstractEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Table(name = "EXPIRATION")
@Entity
@DynamicUpdate
public class Expiration extends AbstractEntity {
    @ManyToOne //(fetch = FetchType.EAGER)
//    @JoinColumn(name = "demand_id")
    private Demand demand;

    private String step;
    @Column(name = "plan_project")
    private String planProject;
    @Column(name = "plan_usage")
    private String planUsage;
    @Column(name = "power_max")
    private Double powerMax;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "safety_id")
    private Safety safety;
    @ColumnDefault("false")
    @Column(name = "it_load1c")
    private boolean load1c = false;

    public Expiration() {
    }

    public Expiration(String step, String planProject, String planUsage, Double powerMax, Safety safety) {
        this.step = step;
        this.planProject = planProject;
        this.planUsage = planUsage;
        this.powerMax = powerMax;
        this.safety = safety;
    }
    public String getStep() {
        return step;
    }
    public void setStep(String step) {
        this.step = step;
    }
    public String getPlanProject() {
        return planProject;
    }
    public void setPlanProject(String planProject) {
        this.planProject = planProject;
    }
    public String getPlanUsage() {
        return planUsage;
    }
    public void setPlanUsage(String planUsage) {
        this.planUsage = planUsage;
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
    public Demand getDemand() {
        return demand;
    }
    public void setDemand(Demand demand) {
        this.demand = demand;
    }
    public boolean isLoad1c() {
        return load1c;
    }
    public void setLoad1c(boolean load1c) {
        this.load1c = load1c;
    }
}