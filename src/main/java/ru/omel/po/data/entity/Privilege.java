package ru.omel.po.data.entity;

import org.hibernate.annotations.DynamicUpdate;
import ru.omel.po.data.AbstractEntity;

import javax.persistence.*;

@Entity
@Table(name = "privilege")
@DynamicUpdate
public class Privilege extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "demand_id")
    private Demand demand;
    private Boolean needy = false;
    private Boolean veteran = false;
    private Boolean invalid = false;
    private Boolean chernobyl = false;
    private Boolean semipalatinsk = false;
    private Boolean lawmaker = false;
    private Boolean lighthouse = false;
    @Column(name = "chernobyl_risk")
    private Boolean chernobylRisk = false;
    @Column(name = "many_children")
    private Boolean manyChildren = false;

    public Privilege() {
    }

    public Demand getDemand() {
        return demand;
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public Boolean getNeedy() {
        return needy;
    }

    public void setNeedy(Boolean needy) {
        this.needy = needy;
    }

    public Boolean getVeteran() {
        return veteran;
    }

    public void setVeteran(Boolean veteran) {
        this.veteran = veteran;
    }

    public Boolean getInvalid() {
        return invalid;
    }

    public void setInvalid(Boolean invalid) {
        this.invalid = invalid;
    }

    public Boolean getChernobyl() {
        return chernobyl;
    }

    public void setChernobyl(Boolean chernobyl) {
        this.chernobyl = chernobyl;
    }

    public Boolean getSemipalatinsk() {
        return semipalatinsk;
    }

    public void setSemipalatinsk(Boolean semipalatinsk) {
        this.semipalatinsk = semipalatinsk;
    }

    public Boolean getLawmaker() {
        return lawmaker;
    }

    public void setLawmaker(Boolean lawmaker) {
        this.lawmaker = lawmaker;
    }

    public Boolean getLighthouse() {
        return lighthouse;
    }

    public void setLighthouse(Boolean lighthouse) {
        this.lighthouse = lighthouse;
    }

    public Boolean getChernobylRisk() {
        return chernobylRisk;
    }

    public void setChernobylRisk(Boolean chernobylRisk) {
        this.chernobylRisk = chernobylRisk;
    }

    public Boolean getManyChildren() {
        return manyChildren;
    }

    public void setManyChildren(Boolean manyChildren) {
        this.manyChildren = manyChildren;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Privilege)) {
            return false; // null or other class
        }
        Privilege other = (Privilege) obj;

        if (this.getId() != null) {
            return needy.equals(other.getNeedy())
                    && invalid.equals(other.getInvalid())
                    && chernobyl.equals(other.getChernobyl())
                    && semipalatinsk.equals(other.getSemipalatinsk())
                    && lawmaker.equals(other.getLawmaker())
                    && lighthouse.equals(other.getLighthouse())
                    && chernobylRisk.equals(other.getChernobylRisk())
                    && manyChildren.equals(other.getManyChildren())
                    && veteran.equals(other.getVeteran())
                    ;
        }
        return super.equals(other);
    }
}