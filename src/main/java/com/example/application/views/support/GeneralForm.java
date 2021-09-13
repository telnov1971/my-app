package com.example.application.views.support;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.demandlist.DemandList;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class GeneralForm extends Div implements BeforeEnterObserver {
    protected final String DEMAND_ID = "demandID";
    protected DecimalFormat decimalFormat;
    protected FormLayout formDemand = new FormLayout();
    protected BeanValidationBinder<Demand> binderDemand = new BeanValidationBinder<>(Demand.class);
    protected Demand demand = new Demand();
    protected FilesLayout filesLayout;

    protected HorizontalLayout buttonBar = new HorizontalLayout();
    protected Button save = new Button("Сохранить");
    protected Button reset = new Button("Отменить");

    // максимальная мощность по типу заявки
    protected Double MaxPower;

    protected DatePicker createdate;
    protected Select<DemandType> demandType;
    protected Select<Status> status;

    protected Accordion accordionDemander = new Accordion();
    protected FormLayout formDemander = new FormLayout();
    protected TextArea demander;
    protected TextField delegate;
    protected TextField inn;
    protected DatePicker innDate;
    protected TextField contact;
    protected TextField passportSerries;
    protected TextField passportNumber;
    protected TextArea pasportIssued;
    protected TextField addressRegistration;
    protected TextField addressActual;

    protected Select<Reason> reason;
    protected TextArea object;
    protected TextArea address;
    protected TextArea specification;

    protected Accordion accordionPoints = new Accordion();
    protected Accordion accordionExpiration = new Accordion();
    protected Point point = new Point();
    protected Binder<Point> pointBinder = new Binder<>(Point.class);
    protected IntegerField countPoints;
    protected NumberField powerDemand;
    protected NumberField powerCurrent;
    protected NumberField powerMaximum;
    protected Select<Voltage> voltage;
    protected Select<Voltage> voltageIn;
    protected Select<Safety> safety;

    protected General general = new General();
    protected Binder<General> generalBinder = new Binder<>(General.class);
    protected TextArea period;
    protected TextField contract;
    protected TextArea countTransformations;
    protected TextArea countGenerations;
    protected TextArea techminGeneration;
    protected TextArea reservation;

    protected Select<Plan> plan;
    protected Select<Garant> garant;

    protected Accordion accordionHistory = new Accordion();
    protected HistoryLayout historyLayout;

    protected final ReasonService reasonService;
    protected final DemandService demandService;
    protected final DemandTypeService demandTypeService;
    protected final StatusService statusService;
    protected final GarantService garantService;
    protected final PointService pointService;
    protected final GeneralService generalService;
    protected final PlanService planService;
    protected final PriceService priceService;
    protected final VoltageService voltageService;
    protected final SafetyService safetyService;
    protected final SendService sendService;
    protected final UserService userService;
    protected final HistoryService historyService;
    private final FileStoredService fileStoredService;

    public GeneralForm(ReasonService reasonService,
                       DemandService demandService,
                       DemandTypeService demandTypeService,
                       StatusService statusService,
                       GarantService garantService,
                       PointService pointService,
                       GeneralService generalService,
                       VoltageService voltageService,
                       SafetyService safetyService,
                       PlanService planService,
                       PriceService priceService,
                       SendService sendService,
                       UserService userService,
                       HistoryService historyService,
                       FileStoredService fileStoredService,
                       Boolean temporal,
                       DType dType,
                       Component... components) {
        super(components);
        // сервисы
        {
            this.reasonService = reasonService;
            this.generalService = generalService;
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
            this.userService = userService;
            this.fileStoredService = fileStoredService;
            this.historyService = historyService;
        }

        this.decimalFormat = new DecimalFormat("###.##",
                new DecimalFormatSymbols());

        Label label = new Label("                                                ");
        label.setHeight("1px");

        filesLayout = new FilesLayout(this.fileStoredService
                , voltageService
                , safetyService);

        historyLayout = new HistoryLayout(this.historyService);
        historyLayout.setWidthFull();
        accordionHistory.add("История событий",historyLayout);
        accordionHistory.setWidthFull();

        save.addClickListener(event -> {
            if(save()) UI.getCurrent().navigate(DemandList.class);
        });
        reset.addClickListener(event -> {
            try {
                filesLayout.deleteFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
            UI.getCurrent().navigate(DemandList.class);
        });

        // описание полей
        {
            createdate = new DatePicker("Дата создания");
            createdate.setValue(LocalDate.now());
            createdate.setReadOnly(true);

            demandType = createSelect(DemandType::getName, demandTypeService.findAll(),
                    "Тип заявки", DemandType.class);
            demandType.setReadOnly(true);

            demander = new TextArea("Заявитель", "Наименование организации, ФИО заявителя");
            delegate = new TextField("ФИО представителя","Представитель юр.лица");
            inn = new TextField("Реквизиты заявителя", "ОГРН для юр.лиц, ИНН для ИП");
            innDate = new DatePicker("Дата выдачи");
            contact = new TextField("Контактный телефон");
            passportSerries = new TextField("Паспорт серия", "Четыре цифры");
            passportNumber = new TextField("Паспорт номер", "Шесть цифр");
            pasportIssued = new TextArea("Паспорт выдан");
            addressRegistration = new TextField("Адрес регистрации");
            addressActual = new TextField("Адрес фактический");
            object = new TextArea("Объект");
            address = new TextArea("Адрес объекта");
            specification = new TextArea("Характер нагрузки");

            countPoints = new IntegerField("Кол-во точек подключения");
            powerDemand = new NumberField("Мощность присоединяемая, кВт", "0,00 кВт");
            powerDemand.setStep(0.01);
            powerDemand.setAutocorrect(true);
            powerCurrent = new NumberField("Мощность ранее присоединённая, кВт", "0,00 кВт");
            powerCurrent.setAutocorrect(true);
            powerMaximum = new NumberField("Мощность максимальная, кВт", "0,00 кВт");
            powerMaximum.setAutocorrect(true);

            countTransformations = new TextArea("Кол-во и мощ-ть присоединяемых трансформаторов");
            countGenerations = new TextArea("Кол-во и мощ-ть генераторов");
            techminGeneration = new TextArea("Технологический минимум для генераторов");
            techminGeneration.setPlaceholder("Величина и обоснование технологического минимума");
            reservation = new TextArea("Технологическая и аварийная бронь");
            reservation.setPlaceholder("Величина и обоснование технологической и аварийной брони");
            period = new TextArea("Срок подключения по временной схеме");
            contract = new TextField("Реквизиты договора");
        }

        // создание селекторов
        {
            List<Reason> reasonList = reasonService.findAll().stream().
                filter(r -> r.getDtype().contains(dType)).collect(Collectors.toList());
            reason = createSelect(Reason::getName, reasonList,
                    "Причина обращения", Reason.class);

            voltage = createSelect(Voltage::getName, voltageService.findAllByOptional(false),
                    "Класс напряжения", Voltage.class);

            voltageIn = createSelect(Voltage::getName, voltageService.findAllByOptional(true),
                    "Уровень напряжения на вводе", Voltage.class);

            safety = createSelect(Safety::getName, safetyService.findAll(),
                    "Категория надежности", Safety.class);

            garant = createSelect(Garant::getName, garantService.findAllByActive(true),
                    "Гарантирующий поставщик", Garant.class);

            plan = createSelect(Plan::getName, planService.findAll(),
                    "Рассрочка платежа", Plan.class);

            status = createSelect(Status::getName, statusService.findAll(),
                    "Статус", Status.class);
            status.setValue(statusService.findById(1L).get());
            status.setReadOnly(true);
        }

        // настройка проверки значений полей
        {
            binderDemand.forField(inn)
                    .withValidator(
                            new StringLengthValidator(
                                    "ИНН должен содержать от 10 до 13 знаков",
                                    10, 13))
                    .bind(Demand::getInn, Demand::setInn);

            pointBinder.forField(powerDemand)
                    .withValidator(
                            new DoubleRangeValidator(
                                    "Мощность не может быть отрицательной",
                                    0.0, null))
                    .bind(Point::getPowerDemand, Point::setPowerDemand);

            pointBinder.forField(powerCurrent)
                    .withValidator(
                            new DoubleRangeValidator(
                                    "Мощность не может быть отрицательной",
                                    0.0, null))
                    .bind(Point::getPowerCurrent, Point::setPowerCurrent);

            pointBinder.forField(powerMaximum)
                    .withValidator(
                            new DoubleRangeValidator(
                                    "Мощность не может быть отрицательной",
                                    0.0, null))
                    .bind(Point::getPowerMaximum, null);
        }

        binderDemand.bindInstanceFields(this);
        pointBinder.bindInstanceFields(this);
        generalBinder.bindInstanceFields(this);

        // события формы
        powerDemand.addBlurListener(e->{testPower(powerDemand);});
        powerDemand.addValueChangeListener(e -> {
            changePower(powerDemand);
        });

        powerCurrent.addBlurListener(e->{testPower(powerCurrent);});
        powerCurrent.addValueChangeListener(e -> {
            changePower(powerCurrent);
        });

        // кол-во колонок формы от ширины окна
        setColumnCount(formDemand);
        setColumnCount(formDemander);

        formDemander.add(inn,innDate,label,
                passportSerries,passportNumber,pasportIssued,
                addressRegistration,addressActual);
        setWidthFormDemander();
        accordionDemander.add("Данные заявителя", formDemander);

        formDemand.add(createdate, demandType, status, label);
        formDemand.add(demander,delegate,contact);
        formDemand.add(accordionDemander);
        formDemand.add(reason, object, address, specification,label);
        formDemand.add(countPoints, accordionPoints, powerDemand, powerCurrent
                , powerMaximum, voltage, voltageIn, safety, label);
        formDemand.add(countTransformations,countGenerations,techminGeneration,reservation);
        formDemand.add(period,contract);
        formDemand.add(accordionExpiration);
        formDemand.add(garant, plan);
        setWidthFormDemand();

        buttonBar.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonBar.setSpacing(true);
        reset.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonBar.add(save,reset);

        Component fields[] = {delegate, inn, innDate,
                passportSerries,passportNumber,pasportIssued,
                addressRegistration,addressActual,
                countPoints, accordionPoints, powerDemand, powerCurrent,
                powerMaximum, voltage, voltageIn, safety, specification,
                countTransformations,accordionExpiration,
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
        formDemand.setColspan(delegate, 4);
        formDemand.setColspan(contact, 1);
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
        formDemand.setColspan(accordionExpiration, 4);
        formDemand.setColspan(garant, 1);
        formDemand.setColspan(plan, 1);
        formDemand.setColspan(accordionHistory,4);
    }

    private void setWidthFormDemander() {
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

    protected <C> Select<C> createSelect(ItemLabelGenerator<C> gen, List<C> list,
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

    public boolean save() {
        if (binderDemand.validate().getValidationErrors().size() > 0) return false;
        binderDemand.writeBeanIfValid(demand);
        if(demand.getUser()==null){
            demand.setUser(userService.findByUsername(
                    SecurityContextHolder.getContext().getAuthentication().getName()));
            demand.setCreateDate(LocalDate.now());
            demand.setLoad1c(false);
            demand.setChange(false);
            demand.setExecuted(false);
        }
//        if(demand.getInn().equals("0000000000")) demand.setInn(null);
//        if(demand.getPassportSerries().equals("0000")) demand.setPassportSerries(null);
//        if(demand.getPassportNumber().equals("000000")) demand.setPassportNumber(null);
        History history = new History();
        try {
            String his = historyService.writeHistory(demand);
            history.setHistory(his);
        } catch (Exception e) {System.out.println(e.getMessage());}
        try {
            if(demandService.update(this.demand)!=null) {
                history.setDemand(demand);
                if(history.getHistory()!="") {
                    historyService.save(history);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    protected void setReadOnly() {
        AbstractField fields[] = {
                demander,delegate,inn,innDate,contact,passportSerries,passportNumber,pasportIssued,
                addressRegistration,addressActual,reason,object,address,specification,
                countPoints,powerDemand,powerCurrent,powerMaximum,voltage,safety,period,
                contract,countTransformations,countGenerations,techminGeneration,reservation,
                plan,garant
        };
        for(AbstractField field : fields) {
            field.setReadOnly(true);
        }
    }

    protected void populateForm(Demand value) { }
    protected void clearForm() {
        binderDemand.readBean(null);
        pointBinder.readBean(null);
        generalBinder.readBean(null);
        populateForm(null);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> demandId = event.getRouteParameters().getLong(DEMAND_ID);
        if (demandId.isPresent()) {
            Optional<Demand> demandFromBackend = demandService.get(demandId.get());
            if (demandFromBackend.isPresent()) {
                User currentUser = userService.findByUsername(
                        SecurityContextHolder.getContext().getAuthentication().getName());
                if (demandFromBackend.get().getUser().equals(currentUser) ||
                        (currentUser.isGarant() &&
                                demandFromBackend.get().getGarant().equals(currentUser.getGarant()))) {
                    populateForm(demandFromBackend.get());
                    if(currentUser.isGarant()) setReadOnly();
                } else {
                    Notification.show(String.format("Заявка с ID = %d не Ваша", demandId.get()), 3000,
                            Notification.Position.BOTTOM_START);
                    clearForm();
                }
            } else {
                Notification.show(String.format("Заявка с ID = %d не найдена", demandId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                clearForm();
            }
        }
    }
}

