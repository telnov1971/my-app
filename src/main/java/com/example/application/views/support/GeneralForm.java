package com.example.application.views.support;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.demandlist.DemandList;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.accordion.Accordion;
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
    protected String uploadPathWindows;
    @Value("${upload.path.linux}")
    protected String uploadPathLinux;

    protected FormLayout formDemand = new FormLayout();
    protected BeanValidationBinder<Demand> binderDemand = new BeanValidationBinder<>(Demand.class);
    protected Demand demand = new Demand();

    // максимальная мощность по типу заявки
    protected Double MaxPower;

    protected DatePicker createdate;
    protected Select<DemandType> demandType;
    protected Select<Status> status;

    protected Accordion accordionDemander = new Accordion();
    protected FormLayout formDemander = new FormLayout();
    protected TextArea demander;
    protected TextField inn;
    protected DatePicker innDate;
    protected TextField contact;
    protected TextField passportSerries;
    protected TextField passportNumber;
    protected TextArea pasportIssued;
    protected TextField addressRegistration;
    protected TextField addressActual;

    protected TextArea reason;
    protected TextArea object;
    protected TextArea address;
    protected TextArea specification;

    protected Point point = new Point();
    protected Binder<Point> pointBinder = new Binder<>(Point.class);
    protected NumberField countPoints;
    protected NumberField powerDemand;
    protected NumberField powerCurrent;
    protected NumberField powerMaximum;
    protected Select<Voltage> voltage;
    protected Select<Safety> safety;
    protected PointsLayout pointsLayout;

    protected General general = new General();
    protected Binder<General> generalBinder = new Binder<>(General.class);
    protected TextArea period;
    protected TextField contract;
    protected TextArea countTransformations;
    protected TextArea countGenerations;
    protected TextArea techminGeneration;
    protected TextArea reservation;

    protected ExpirationLayout expirationLayout;

    protected Select<Plan> plan;
    protected Select<Send> send;
    protected Select<Garant> garant;

    protected final DemandService demandService;
    protected final DemandTypeService demandTypeService;
    protected final StatusService statusService;
    protected final GarantService garantService;
    protected final PointService pointService;
    protected final GeneralService generalService;
    protected final ExpirationService expirationService;
    protected final PlanService planService;
    protected final PriceService priceService;
    protected final VoltageService voltageService;
    protected final SafetyService safetyService;
    protected final SendService sendService;

    public GeneralForm(DemandService demandService,
                       DemandTypeService demandTypeService,
                       StatusService statusService,
                       GarantService garantService,
                       PointService pointService,
                       GeneralService generalService,
                       ExpirationService expirationService,
                       VoltageService voltageService,
                       SafetyService safetyService,
                       PlanService planService,
                       PriceService priceService,
                       SendService sendService,
                       Component... components) {
        super(components);
//        if (demandTypeID == DemandType.TO15)
//            this.MaxPower = 15.0;
//        else if (demandTypeID == DemandType.TO150)
//            this.MaxPower = 150.0;
//        else
//            this.MaxPower = 1000000000.0;

        // сервисы
        {
            this.generalService = generalService;
            this.expirationService = expirationService;
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

        Label label = new Label("                                                ");
        label.setHeight("1px");

        createdate = new DatePicker("Дата создания");
        createdate.setValue(LocalDate.now());
        createdate.setReadOnly(true);

        demandType = new Select<>();
        demandType.setLabel("Тип заявки");
        List<DemandType> demandTypeList = demandTypeService.findAll();
        demandType.setItemLabelGenerator(DemandType::getName);
        demandType.setItems(demandTypeList);
        demandType.setReadOnly(true);

        demander = new TextArea("Заявитель");
        inn = new TextField("ИНН");
        innDate = new DatePicker("Дата выдачи");
        contact = new TextField("Контактный телефон");
        passportSerries = new TextField("Паспорт серия");
        passportNumber = new TextField("Паспорт номер");
        pasportIssued = new TextArea("Паспорт выдан");
        addressRegistration = new TextField("Адрес регистрации");
        addressActual = new TextField("Адрес фактический");
        reason = new TextArea("Причина подключения");
        object = new TextArea("Объект");
        address = new TextArea("Адрес объекта");
        specification = new TextArea("Характер нагрузки");

        countPoints = new NumberField("Кол-во точек подключения");
        powerDemand = new NumberField("Мощность заявленная");
        powerCurrent = new NumberField("Мощность текущая");
        powerMaximum = new NumberField("Мощность максимальная");

        countTransformations = new TextArea("Кол-во и мощ-ть трансформаторов");
        countGenerations = new TextArea("Кол-во и мощ-ть генераторов");
        techminGeneration = new TextArea("Тех.мин. для генераторов");
        reservation = new TextArea("Бронирование");
        period = new TextArea("Срок подключения по временной схеме");
        contract = new TextField("Реквизиты договора");

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

//                price = new Select<>();
//                price.setLabel("Ценовая категория");
//                List<Price> pricetList = priceService.findAll();
//                price.setItemLabelGenerator(Price::getName);
//                price.setItems(pricetList);

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
        formDemander.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1em", 1),
                new FormLayout.ResponsiveStep("40em", 2),
                new FormLayout.ResponsiveStep("50em", 3),
                new FormLayout.ResponsiveStep("68em", 4)
        );

        formDemander.add(contact,label,
                inn,innDate,label,
                passportSerries,passportNumber,pasportIssued,
                addressRegistration,addressActual);
        formDemander.setColspan(label, 4);
        formDemander.setColspan(contact, 1);
        formDemander.setColspan(inn, 1);
        formDemander.setColspan(innDate, 1);
        formDemander.setColspan(passportNumber, 1);
        formDemander.setColspan(passportSerries, 1);
        formDemander.setColspan(pasportIssued, 4);
        formDemander.setColspan(addressRegistration, 4);
        formDemander.setColspan(addressActual, 4);
        accordionDemander.add("Данные заявителя", formDemander);

        formDemand.add(createdate, demandType, status, label);
        formDemand.add(demander);
        formDemand.add(accordionDemander);
        formDemand.add(reason, object, address, specification,label);
        formDemand.add(countPoints, powerDemand, powerCurrent, powerMaximum, voltage, safety, label);
        formDemand.add(countTransformations,countGenerations,techminGeneration,reservation);
        formDemand.add(period,contract);
        formDemand.add(garant, plan, send);

            //установка ширины полей
        formDemand.setColspan(label, 4);
        formDemand.setColspan(createdate, 1);
        formDemand.setColspan(demandType, 1);
        formDemand.setColspan(status, 1);
        formDemand.setColspan(demander, 4);
        formDemand.setColspan(accordionDemander, 4);
        formDemand.setColspan(reason, 4);
        formDemand.setColspan(object, 4);
        formDemand.setColspan(address, 4);
        formDemand.setColspan(specification, 4);

        formDemand.setColspan(countPoints, 1);
        formDemand.setColspan(powerDemand, 1);
        formDemand.setColspan(powerCurrent, 1);
        formDemand.setColspan(powerMaximum, 1);
        formDemand.setColspan(voltage, 1);
        formDemand.setColspan(safety, 1);

        formDemand.setColspan(countTransformations, 4);
        formDemand.setColspan(countGenerations, 4);
        formDemand.setColspan(techminGeneration, 4);
        formDemand.setColspan(reservation, 4);
        formDemand.setColspan(period, 4);
        formDemand.setColspan(contract, 4);
        formDemand.setColspan(garant, 1);
//            formDemand.setColspan(price, 1);
        formDemand.setColspan(plan, 1);
        formDemand.setColspan(send, 1);

        Component fields[] = {inn, innDate, countPoints, powerDemand, powerCurrent,
                powerMaximum, voltage, safety, specification, countTransformations,
                countGenerations, techminGeneration, reservation, plan, period, contract};
        for(Component field : fields){
            field.setVisible(false);
        }
        this.getElement().getStyle().set("margin", "15px");
    }

    public void save() {
        binderDemand.writeBeanIfValid(demand);
        demandService.update(this.demand);

            pointBinder.writeBeanIfValid(point);
            point.setDemand(demand);
            pointService.update(this.point);
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
            point = new Point();
            pointBinder.readBean(this.point);
    }
}

