package com.example.application.views.support;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.demandlist.DemandList;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.List;

public class GeneralForm extends Div {
    @Value("${upload.path.windows}")
    private String uploadPathWindows;
    @Value("${upload.path.linux}")
    private String uploadPathLinux;

    private FormLayout formDemand = new FormLayout();
    private BeanValidationBinder<Demand> binderDemand = new BeanValidationBinder<>(Demand.class);
    private Demand demand = new Demand();
    private Long demandTypeID;
    private Double MaxPower;

    private DatePicker createdate;
    private Select<DemandType> demandType;
    private TextArea demander;
    private TextField inn;
    private TextField contact;
    private TextField passportSerries;
    private TextField passportNumber;
    private TextArea pasportIssued;
    private TextField addressRegistration;
    private TextField addressActual;
    private TextArea reason;
    private TextArea object;
    private TextArea address;

    private Point point = new Point();
    private NumberField powerDemand;
    private NumberField powerCurrent;
    private NumberField powerMaximum;
    private Select<Voltage> voltage;
    private Select<Safety> safety;
    private PointsLayout pointsLayout;

    private Select<Plan> plan;
    private Select<Price> price;
    private Select<Send> send;
    private Select<Garant> garant;

    private Select<Status> status;

    private Binder<Point> pointBinder = new Binder<>(Point.class);

    private final DemandService demandService;
    private final DemandTypeService demandTypeService;
    private final StatusService statusService;
    private final GarantService garantService;
    private final PointService pointService;
    private final PlanService planService;
    private final PriceService priceService;
    private final VoltageService voltageService;
    private final SafetyService safetyService;
    private final SendService sendService;

    public GeneralForm(DemandService demandService,
                       DemandTypeService demandTypeService,
                       StatusService statusService,
                       GarantService garantService,
                       PointService pointService,
                       VoltageService voltageService,
                       SafetyService safetyService,
                       PlanService planService,
                       PriceService priceService,
                       SendService sendService,
                       Long demandTypeID,
                       Component... components) {
        super(components);
        this.demandTypeID = demandTypeID;
        if (demandTypeID == DemandType.TO15)
            this.MaxPower = 15.0;
        else if (demandTypeID == DemandType.TO150)
            this.MaxPower = 150.0;
        else
            this.MaxPower = 1000000000.0;

        // сервисы
        {
            this.demandService = demandService;
            this.demandTypeService = demandTypeService;
            this.statusService = statusService;
            this.garantService = garantService;
            this.pointService = pointService;
            this.voltageService = voltageService;
            this.safetyService = safetyService;
            this.planService = planService;
            this.priceService = priceService;
            this.sendService = sendService;
        }

        Label label = new Label(" ");
        label.setHeight("1px");
        createdate = new DatePicker("Дата создания");
        createdate.setValue(LocalDate.now());
        createdate.setReadOnly(true);

        demandType = new Select<>();
        demandType.setLabel("Тип заявки");
        List<DemandType> demandTypeList = demandTypeService.findAll();
        demandType.setItemLabelGenerator(DemandType::getName);
        demandType.setItems(demandTypeList);
        demandType.setValue(demandTypeService.findById(demandTypeID).get());
        demandType.setReadOnly(true);

        demander = new TextArea("Заявитель");
        if (demandTypeID != DemandType.TO15)
            inn = new TextField("ИНН");
        contact = new TextField("Контактный телефон");
        passportSerries = new TextField("Паспорт серия");
        passportNumber = new TextField("Паспорт номер");
        pasportIssued = new TextArea("Паспорт выдан");
        addressRegistration = new TextField("Адрес регистрации");
        addressActual = new TextField("Адрес фактический");
        reason = new TextArea("Причина подключения");
        object = new TextArea("Объект");
        address = new TextArea("Адрес объекта");

        if (demandTypeID != DemandType.RECIVER) {
            powerDemand = new NumberField("Мощность заявленная");
            powerCurrent = new NumberField("Мощность текущая");
            powerMaximum = new NumberField("Мощность максимальная");
            // настройка всех полей выбора
            {
                if (demandTypeID != DemandType.RECIVER) {
                    voltage = new Select<>();
                    voltage.setLabel("Уровень напряжения");
                    List<Voltage> voltageList = voltageService.findAll();
                    voltage.setItemLabelGenerator(Voltage::getName);
                    voltage.setItems(voltageList);

                    safety = new Select<>();
                    safety.setLabel("Категория надежности");
                    List<Safety> safetyList = safetyService.findAll();
                    safety.setItemLabelGenerator(Safety::getName);
                    safety.setItems(safetyList);
                }
                garant = new Select<>();
                garant.setLabel("Гарантирующий поставщик");
                List<Garant> garantList = garantService.findAll();
                garant.setItemLabelGenerator(Garant::getName);
                garant.setItems(garantList);

                plan = new Select<>();
                plan.setLabel("Рассрочка платежа");
                List<Plan> plantList = planService.findAll();
                plan.setItemLabelGenerator(Plan::getName);
                plan.setItems(plantList);

                price = new Select<>();
                price.setLabel("Ценовая категория");
                List<Price> pricetList = priceService.findAll();
                price.setItemLabelGenerator(Price::getName);
                price.setItems(pricetList);

                send = new Select<>();
                send.setLabel("Способ получения договора");
                List<Send> sendList = sendService.findAll();
                send.setItemLabelGenerator(Send::getName);
                send.setItems(sendList);

                status = new Select<>();
                status.setLabel("Статус");
                List<Status> statusList = statusService.findAll();
                status.setItemLabelGenerator(Status::getName);
                status.setItems(statusList);
                status.setValue(statusService.findById(1L).get());
                status.setReadOnly(true);
            }

            binderDemand.bindInstanceFields(this);
            pointBinder.bindInstanceFields(this);

            // события формы
            powerDemand.addValueChangeListener(e -> {
                if ((powerCurrent.getValue() != null) &&
                        (powerCurrent.getValue() > 0.0)) {
                    powerMaximum.setValue(powerCurrent.getValue() +
                            powerDemand.getValue());
                } else {
                    powerMaximum.setValue(powerDemand.getValue());
                }
                if (powerMaximum.getValue() > this.MaxPower) {
                    Notification notification = new Notification(
                            "Для такого типа заявки превышена макисальная мощность", 3000,
                            Notification.Position.TOP_START);
                    notification.open();
                    powerDemand.setValue(this.MaxPower - powerCurrent.getValue());
                }
            });
            powerCurrent.addValueChangeListener(e -> {
                if ((powerDemand.getValue() != null) &&
                        (powerDemand.getValue() > 0.0)) {
                    powerMaximum.setValue(powerCurrent.getValue() +
                            powerDemand.getValue());
                } else {
                    powerMaximum.setValue(powerCurrent.getValue());
                }
                if (powerMaximum.getValue() > this.MaxPower) {
                    Notification notification = new Notification(
                            "Максимальная мощность не может быть 15 кВт", 3000,
                            Notification.Position.TOP_START);
                    notification.open();
                    powerCurrent.setValue(this.MaxPower - powerDemand.getValue());
                }
            });

            // кол-во колонок формы от ширины окна
            formDemand.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("1em", 1),
                    new FormLayout.ResponsiveStep("40em", 2),
                    new FormLayout.ResponsiveStep("50em", 3),
                    new FormLayout.ResponsiveStep("68em", 4)
            );

            formDemand.add(createdate, demandType, status, label);
            formDemand.add(demander);
            if (demandTypeID != DemandType.TO15)
                formDemand.add(inn);
            formDemand.add(contact, label, passportSerries, passportNumber, pasportIssued, label);
            formDemand.add(addressRegistration, addressActual, label);
            formDemand.add(reason, object, address, label);
            if (demandTypeID != DemandType.RECIVER) {
                formDemand.add(powerDemand, powerCurrent, powerMaximum, voltage, safety, label);
            }
            formDemand.add(garant, price, plan, send);

            //установка ширины полей
            {
                formDemand.setColspan(label, 4);
                formDemand.setColspan(createdate, 1);
                formDemand.setColspan(demandType, 1);
                formDemand.setColspan(status, 1);
                formDemand.setColspan(contact, 1);
                if (demandTypeID != DemandType.TO15) {
                    formDemand.setColspan(inn, 2);
                }
                formDemand.setColspan(passportNumber, 1);
                formDemand.setColspan(passportSerries, 1);
                formDemand.setColspan(pasportIssued, 2);
                formDemand.setColspan(addressRegistration, 2);
                formDemand.setColspan(addressActual, 2);
                if (demandTypeID != DemandType.RECIVER) {
                    formDemand.setColspan(powerDemand, 1);
                    formDemand.setColspan(powerCurrent, 1);
                    formDemand.setColspan(powerMaximum, 1);
                    formDemand.setColspan(voltage, 1);
                    formDemand.setColspan(safety, 1);
                }
                formDemand.setColspan(garant, 1);
                formDemand.setColspan(price, 1);
                formDemand.setColspan(plan, 1);
                formDemand.setColspan(send, 1);
                formDemand.setColspan(demander, 4);
                formDemand.setColspan(reason, 4);
                formDemand.setColspan(object, 4);
                formDemand.setColspan(address, 4);
            }

            this.getElement().getStyle().set("margin", "15px");

            add(formDemand);
        }
    }
    public void save() {
        binderDemand.writeBeanIfValid(demand);
        demandService.update(this.demand);

        if(demandTypeID != DemandType.RECIVER) {
            pointBinder.writeBeanIfValid(point);
            point.setDemand(demand);
            pointService.update(this.point);
        }
        UI.getCurrent().navigate(DemandList.class);
    }

    public void clearForm() {
        binderDemand.readBean(null);
        pointBinder.readBean(null);
        populateForm(null);
    }

    public void populateForm(Demand value) {
        this.demand = value;
        binderDemand.readBean(this.demand);
        if (demandTypeID != DemandType.RECIVER) {
            if(value != null) {
                demandType.setReadOnly(true);
                createdate.setReadOnly(true);
                if(pointService.findAllByDemand(demand).isEmpty()) {
                    point = new Point();
                } else {
                    point = pointService.findAllByDemand(demand).get(0);
                }
            }
            pointBinder.readBean(this.point);
        } else {
            point = new Point();
            pointBinder.readBean(this.point);
        }
    }
}

