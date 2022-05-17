package ru.omel.po.data.entity;

import ru.omel.po.data.AbstractEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Demand extends AbstractEntity {

    @NotNull
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @NotNull
    @Column(name = "change_date")
    private LocalDateTime changeDate;

    // заявитель
    @NotNull
    @NotEmpty
    private String demander;
    // представитель
    private String delegate;
    //
    @Column(name = "type_demander")
    private String typeDemander;
    // паспорт серия
    @Column(name = "pas_ser")
//    @Size(min=4,max=4,message="Серия паспорта состоит из 4 цифр")
    private String passportSerries;
    // пасорт номер
    @Column(name = "pas_num")
//    @Size(min=6,max=6,message="Номер паспорта состоит из 6 цифр")
    private String passportNumber;
    // пасорт выдан
    @Column(name = "pas_iss")
    private String pasportIssued;
    // госрегистрация
    // @Size(min=10,max=12,message="ИНН содержит от 10 до 12 цифр")
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
    // номер телефона
    private String contact;

    // причина обращения
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reason_id")
    private Reason reason;
    // объект подключения
    @NotNull
    @NotEmpty
    private String object;
    // адрес подключения
    @NotNull
    @NotEmpty
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

    @JoinColumn(name = "garant_text")
    private String garantText;

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

    // тип заявки
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dtype_id")
    private DemandType demandType;
    // статус
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private Status status;
    // выполнена
    @ColumnDefault("false")
    @Column(name = "executed")
    private boolean executed = false;
    // пользователь
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @ColumnDefault("false")
    @Column(name = "it_load1c")
    private boolean load1c = false;
    @ColumnDefault("false")
    @Column(name = "it_change")
    private boolean change = false;

    public Demand() {
        this.createDate = LocalDateTime.now();
        this.points = new ArrayList<>();
        this.expirations = new ArrayList<>();
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }
    public void setCreateDate(LocalDateTime createdate) {
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
    public boolean isExecuted() {
        return executed;
    }
    public void setExecuted(boolean done) {
        this.executed = done;
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
    public Reason getReason() {
        return reason;
    }
    public void setReason(Reason reason) {
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
    public String getDelegate() {
        return delegate;
    }
    public void setDelegate(String delegate) {
        this.delegate = delegate;
    }
    public LocalDateTime getChangeDate() {
        return changeDate;
    }
    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }
    public String getGarantText() {
        return garantText;
    }
    public void setGarantText(String garantText) {
        this.garantText = garantText;
    }
    public String getTypeDemander() {/**/
        return typeDemander;
    }
    public void setTypeDemander(String typeDemander) {
        this.typeDemander = typeDemander;
    }
}
