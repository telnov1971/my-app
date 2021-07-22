package com.example.application.views.support;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.checkerframework.checker.units.qual.C;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.List;

public class GeneralForm extends Div {
    protected DecimalFormat decimalFormat;
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

    protected Accordion accordionPoints = new Accordion();
    protected Point point = new Point();
    protected Binder<Point> pointBinder = new Binder<>(Point.class);
    protected IntegerField countPoints;
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

    protected String history = "";

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

        this.decimalFormat = new DecimalFormat("###.##",
                new DecimalFormatSymbols());

        Label label = new Label("                                                ");
        label.setHeight("1px");

        createdate = new DatePicker("Дата создания");
        createdate.setValue(LocalDate.now());
        createdate.setReadOnly(true);

        demandType = createSelect(DemandType::getName, demandTypeService.findAll(),
                "Тип заявки", DemandType.class);
        demandType.setReadOnly(true);

        demander = new TextArea("Заявитель","ФИО подающего заявку");
        inn = new TextField("ИНН","От 10 до 12 цифр");
        innDate = new DatePicker("Дата выдачи");
        contact = new TextField("Контактный телефон");
        passportSerries = new TextField("Паспорт серия","Четыре цифры");
        passportNumber = new TextField("Паспорт номер","Шесть цифр");
        pasportIssued = new TextArea("Паспорт выдан");
        addressRegistration = new TextField("Адрес регистрации");
        addressActual = new TextField("Адрес фактический");
        reason = new TextArea("Причина подключения");
        object = new TextArea("Объект");
        address = new TextArea("Адрес объекта");
        specification = new TextArea("Характер нагрузки");

        countPoints = new IntegerField("Кол-во точек подключения");
        powerDemand = new NumberField("Мощность заявленная","0,00 кВт");
        powerDemand.setStep(0.01);
        powerDemand.setAutocorrect(true);
        powerCurrent = new NumberField("Мощность текущая","0,00 кВт");
        powerCurrent.setAutocorrect(true);
        powerMaximum = new NumberField("Мощность максимальная", "0,00 кВт");
        powerMaximum.setAutocorrect(true);

        countTransformations = new TextArea("Кол-во и мощ-ть трансформаторов");
        countGenerations = new TextArea("Кол-во и мощ-ть генераторов");
        techminGeneration = new TextArea("Тех.мин. для генераторов");
        reservation = new TextArea("Бронирование");
        period = new TextArea("Срок подключения по временной схеме");
        contract = new TextField("Реквизиты договора");

        voltage = createSelect(Voltage::getName, voltageService.findAll(),
                "Уровень напряжения", Voltage.class);

        safety = createSelect(Safety::getName, safetyService.findAll(),
                "Категория надежности", Safety.class);

        garant = createSelect(Garant::getName, garantService.findAll(),
                "Гарантирующий поставщик", Garant.class);

        plan = createSelect(Plan::getName, planService.findAll(),
                "Рассрочка платежа", Plan.class);

        send = createSelect(Send::getName, sendService.findAll(),
                "Способ получения договора", Send.class);

        status = createSelect(Status::getName, statusService.findAll(),
                "Статус", Status.class);
        status.setValue(statusService.findById(1L).get());
        status.setReadOnly(true);

        binderDemand.forField(inn)
                .withValidator(
                        new StringLengthValidator(
                                "ИНН должен содержать от 10 до 12 знаков",
                        10,12))
                .bind(Demand::getInn, Demand::setInn);

        pointBinder.forField(powerDemand)
                .withValidator(
                        new DoubleRangeValidator(
                                "Мощность не может быть отрицательной",
                                0.0,null))
                .bind(Point::getPowerDemand, Point::setPowerDemand);

        pointBinder.forField(powerCurrent)
                .withValidator(
                        new DoubleRangeValidator(
                                "Мощность не может быть отрицательной",
                                0.0,null))
                .bind(Point::getPowerCurrent, Point::setPowerCurrent);

        pointBinder.forField(powerMaximum)
                .withValidator(
                        new DoubleRangeValidator(
                                "Мощность не может быть отрицательной",
                                0.0,null))
                .bind(Point::getPowerMaximum,null);

        binderDemand.bindInstanceFields(this);
        pointBinder.bindInstanceFields(this);
        generalBinder.bindInstanceFields(this);

        // события формы
        powerDemand.addBlurListener(e->{testPower(powerDemand);});
        powerDemand.addValueChangeListener(e -> {
            changePower(powerDemand);
            writeHistory(e, "Заявленная мощность");
        });
        powerCurrent.addBlurListener(e->{testPower(powerCurrent);});
        powerCurrent.addValueChangeListener(e -> {
            changePower(powerCurrent);
            writeHistory(e,"Текущая мощность");
        });

        // кол-во колонок формы от ширины окна
        setColumnCount(formDemand);
        setColumnCount(formDemander);

        formDemander.add(contact,label,
                inn,innDate,label,
                passportSerries,passportNumber,pasportIssued,
                addressRegistration,addressActual);
        setWidthFormDemander();
        accordionDemander.add("Данные заявителя", formDemander);

        formDemand.add(createdate, demandType, status, label);
        formDemand.add(demander);
        formDemand.add(accordionDemander);
        formDemand.add(reason, object, address, specification,label);
        formDemand.add(countPoints, accordionPoints, powerDemand, powerCurrent
                , powerMaximum, voltage, safety, label);
        formDemand.add(countTransformations,countGenerations,techminGeneration,reservation);
        formDemand.add(period,contract);
        formDemand.add(garant, plan, send);
        setWidthFormDemand();

        Component fields[] = {inn, innDate, countPoints, accordionPoints, powerDemand, powerCurrent,
                powerMaximum, voltage, safety, specification, countTransformations,
                countGenerations, techminGeneration, reservation, plan, period, contract};
        for(Component field : fields){
            field.setVisible(false);
        }
        this.getElement().getStyle().set("margin", "15px");
    }

    private void setWidthFormDemand() {
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
        formDemand.setColspan(accordionPoints, 4);
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
        formDemand.setColspan(plan, 1);
        formDemand.setColspan(send, 1);
    }

    private void setWidthFormDemander() {
        formDemander.setColspan(contact, 1);
        formDemander.setColspan(inn, 1);
        formDemander.setColspan(innDate, 1);
        formDemander.setColspan(passportNumber, 1);
        formDemander.setColspan(passportSerries, 1);
        formDemander.setColspan(pasportIssued, 4);
        formDemander.setColspan(addressRegistration, 4);
        formDemander.setColspan(addressActual, 4);
    }

    private void setColumnCount(FormLayout form) {
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1em", 1),
                new FormLayout.ResponsiveStep("40em", 2),
                new FormLayout.ResponsiveStep("50em", 3),
                new FormLayout.ResponsiveStep("68em", 4)
        );
    }

    private <C> Select<C> createSelect(ItemLabelGenerator<C> gen, List<C> list,
                                        String label, Class<C> clazz){
        Select<C> select = new Select<>();
        select.setLabel(label);
        select.setItemLabelGenerator(gen);
        select.setItems(list);
        return select;
    }

    private void changePower(NumberField field) {
        Double currentP = 0.0;
        Double demandP = 0.0;
        currentP = powerCurrent.getValue() != null ? powerCurrent.getValue(): 0.0;
        demandP = powerDemand.getValue() != null ? powerDemand.getValue() : 0.0;
        powerMaximum.setValue(currentP + demandP);
        if (powerMaximum.getValue() > this.MaxPower) {
            Notification notification = new Notification(
                    "Для такого типа заявки превышена макисальная мощность", 5000,
                    Notification.Position.MIDDLE);
            notification.open();
            field.focus();
        }
    }

    private void testPower(NumberField field) {
        if(field.getValue() == null){
            Notification notification = new Notification(
                    "Ошибка ввода числа", 5000,
                    Notification.Position.MIDDLE);
            notification.open();
            field.focus();
        }
    }

    private void writeHistory(AbstractField.ComponentValueChangeEvent e, String field) {
        if(e.getOldValue()!=null && e.getValue()!=null) {
            if(!e.getOldValue().equals(e.getValue())) {
                history = history + "Значение " + field + " " +
                        e.getOldValue() + " сменилось на " + e.getValue() + "\n";
            }
        } else {
            if(e.getValue()!=null)
                history = history + "Значение " + field +
                    " сменилось на " + e.getValue() + "\n";
        }
    }
}

