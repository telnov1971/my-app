package com.example.application.data.entity;

import javax.persistence.*;

import com.example.application.data.AbstractEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Demand extends AbstractEntity {

    @Column(name = "create_date")
    private LocalDate createDate;

    // заявитель
    private String demander;
    // паспорт серия
    @Column(name = "pas_ser")
    private String passportSerries;
    // пасорт номер
    @Column(name = "pas_num")
    private String passportNumber;
    // пасорт выдан
    @Column(name = "pas_iss")
    private String pasportIssued;
    // госрегистрация
    private String inn;
    // госрегистрация дата
    @Column(name = "inn_date")
    private LocalDate inndate;
    // адрес регистрации
    @Column(name = "add_reg")
    private String addressRegistration;
    // адрес фактический
    @Column(name = "add_act")
    private String addressActual;
    // номер договора
    private String contact;

    // причина подключения
    private String reason;
    // объект подключения
    private String object;
    // адрес подключения
    private String address;
    // характер нагрузки
    private String specification;

    // точки подключения
    @OneToMany(mappedBy = "demand", cascade = CascadeType.ALL)
    private List<Point> points = new ArrayList<>();

    // сроки этапов
    @OneToMany(mappedBy = "demand", cascade = CascadeType.ALL)
    private List<Expiration> expirations = new ArrayList<>();

    // гарантирующий поставщик
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "garant_id")
    private Garant garant;

    // план рассчётов
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id")
    private Plan plan;
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "price_id")
//    private Price price;

    // временный срок
    @Column(name = "period_connection")
    private String period;

    // реквизиты договора
    private String contract;

    // способ передачи
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "send_id")
    private Send send;
    // тип заявки
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dtype_id")
    private DemandType demandType;
    // статус
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private Status status;
    // выполнена
    @Column(name = "it_done")
    private boolean done;
    // пользователь
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "it_load1c")
    private boolean load1c;
    @Column(name = "it_change")
    private boolean change;

    public Demand() {
    }

    public LocalDate getCreateDate() {
        return createDate;
    }
    public void setCreateDate(LocalDate createdate) {
        this.createDate = createdate;
    }
    public String getObject() {
        return object;
    }
    public void setObject(String object) {
        this.object = object;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public boolean isDone() {
        return done;
    }
    public void setDone(boolean done) {
        this.done = done;
    }
    public String getDemander() {
        return demander;
    }
    public void setDemander(String demander) {
        this.demander = demander;
    }
    public String getPassportSerries() {
        return passportSerries;
    }
    public void setPassportSerries(String passportSerries) {
        this.passportSerries = passportSerries;
    }
    public String getPassportNumber() {
        return passportNumber;
    }
    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }
    public String getPasportIssued() {
        return pasportIssued;
    }
    public void setPasportIssued(String pasportIssued) {
        this.pasportIssued = pasportIssued;
    }
    public String getInn() {
        return inn;
    }
    public void setInn(String inn) {
        this.inn = inn;
    }
    public String getAddressRegistration() {
        return addressRegistration;
    }
    public void setAddressRegistration(String addressRegistration) {
        this.addressRegistration = addressRegistration;
    }
    public String getAddressActual() {
        return addressActual;
    }
    public void setAddressActual(String addressActual) {
        this.addressActual = addressActual;
    }
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public List<Point> getPoints() {
        return points;
    }
    public void setPoints(List<Point> points) {
        this.points = points;
    }
    public List<Expiration> getExpirations() {
        return expirations;
    }
    public void setExpirations(List<Expiration> expirations) {
        this.expirations = expirations;
    }
    public Garant getGarant() {
        return garant;
    }
    public void setGarant(Garant garant) {
        this.garant = garant;
    }
    public DemandType getDemandType() {
        return demandType;
    }
    public void setDemandType(DemandType demandType) {
        this.demandType = demandType;
    }
//    public Price getPrice() {
//        return price;
//    }
//    public void setPrice(Price price) {
//        this.price = price;
//    }
    public Send getSend() {
        return send;
    }
    public void setSend(Send send) {
        this.send = send;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public LocalDate getInndate() {
        return inndate;
    }
    public void setInndate(LocalDate inndate) {
        this.inndate = inndate;
    }
    public String getSpecification() {
        return specification;
    }
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    public Plan getPlan() {
        return plan;
    }
    public void setPlan(Plan plan) {
        this.plan = plan;
    }
    public String getPeriod() {
        return period;
    }
    public void setPeriod(String period) {
        this.period = period;
    }
    public String getContract() {
        return contract;
    }
    public void setContract(String contract) {
        this.contract = contract;
    }
    public boolean isLoad1c() {
        return load1c;
    }
    public void setLoad1c(boolean load1c) {
        this.load1c = load1c;
    }
    public boolean isChange() {
        return change;
    }
    public void setChange(boolean update) {
        this.change = update;
    }
}
