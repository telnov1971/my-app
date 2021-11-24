package com.example.application.views.support;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.demandlist.DemandList;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
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
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
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
    protected NotesLayout notesLayout;

    protected HorizontalLayout buttonBar = new HorizontalLayout();
    protected Button save = new Button("Сохранить");
    protected Button reset = new Button("Отменить");

    // максимальная мощность по типу заявки
    protected Double MaxPower;

    protected TextField demandId = new TextField("Номер заявки");
    protected DateTimePicker createdate;
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
    protected TextField garantText;

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
                       DType dType,
                       NoteService noteService,
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
                , safetyService, historyService);
        notesLayout = new NotesLayout(noteService);

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
            demandId.setReadOnly(true);

            createdate = new DateTimePicker("Дата и время создания");
            createdate.setValue(LocalDateTime.now());
            createdate.setReadOnly(true);

            demandType = createSelect(DemandType::getName, demandTypeService.findAll(),
                    "Тип заявки", DemandType.class);
            demandType.setReadOnly(true);

            demander = new TextArea("Заявитель (обязательное поле)",
                    "Наименование организации, ФИО заявителя");
            demander.setHelperText("(полное наименование заявителя – юридического лица;" +
                    " фамилия, имя, отчество заявителя – индивидуального предпринимателя или физического лица)");
            delegate = new TextField("ФИО представителя","Представитель юр.лица");
            inn = new TextField("Реквизиты заявителя (обязательное поле)",
                    "ОГРН для юр.лиц, ИНН для ИП");
            inn.setHelperText("(номер записи в Едином государственном реестре юридических лиц"+
                    " / номер записи в Едином государственном реестре индивидуальных предпринимателей)");
            innDate = new DatePicker("Дата регистрации в реестре");
            contact = new TextField("Контактный телефон (обязательное поле)");
            passportSerries = new TextField("Паспорт серия (обязательное поле)", "Четыре цифры");
            passportNumber = new TextField("Паспорт номер (обязательное поле)", "Шесть цифр");
            pasportIssued = new TextArea("Паспорт выдан");
            pasportIssued.setHelperText("(кем, когда)");
            addressRegistration = new TextField("Адрес регистрации");
            addressRegistration.setHelperText("(место регистрации заявителя - индекс, адрес)");
            addressActual = new TextField("Адрес фактический");
            addressActual.setHelperText("(фактический адрес - индекс, адрес)");
            object = new TextArea("Объект (обязательное поле)");
            object.setHelperText("(наименование энергопринимающих устройств для присоединения)");
            address = new TextArea("Адрес объекта (обязательное поле)");
            address.setHelperText("(место нахождения энергопринимающих устройств)");
            specification = new TextArea("Характер нагрузки");
            specification.setHelperText("(характер нагрузки (вид экономической деятельности заявителя))");

            countPoints = new IntegerField("Кол-во точек подключения");
            powerDemand = new NumberField("Мощность присоединяемая, кВт (обязательное поле)", "0,00 кВт");
            //powerDemand.setHelperText("(максимальная мощность присоединяемых энергопринимающих устройств)");
            powerDemand.setHelperText("(цифры, точка или запятая)");
            powerDemand.setStep(0.01);
            powerDemand.setAutocorrect(true);
            powerCurrent = new NumberField("Мощность ранее присоединённая, кВт", "0,00 кВт");
            powerCurrent.setHelperText("(цифры, точка или запятая)");
            powerCurrent.setAutocorrect(true);
            powerMaximum = new NumberField("Мощность максимальная, кВт", "0,00 кВт");
            powerMaximum.setAutocorrect(true);

            countTransformations = new TextArea("Кол-во и мощ-ть присоединяемых трансформаторов");
            countGenerations = new TextArea("Кол-во и мощ-ть генераторов");
            techminGeneration = new TextArea("Технологический минимум для генераторов");
            techminGeneration.setPlaceholder("Величина и обоснование технологического минимума");
            techminGeneration.setHelperText("(величина и обоснование технологического минимума)");
            reservation = new TextArea("Технологическая и аварийная бронь");
            reservation.setPlaceholder("Величина и обоснование технологической и аварийной брони");
            reservation.setHelperText("(величина и обоснование технологической и аварийной брони)");
            period = new TextArea("Срок подключения по временной схеме");
            contract = new TextField("Реквизиты договора");
            contract.setHelperText("(реквизиты договора на технологическое присоединение)");
//            contract.getElement().setAttribute("title","реквизиты договора на технологическое присоединение");
            garantText = new TextField("Наименование гарантирующего поставщика (обязательное) *");
            garantText.addClassName("v-captiontext");
            garantText.addClassName("v-required-field-indicator");
//            .v-caption {}
//  .v-captiontext {}
//  .v-required-field-indicator {}
        }

        // создание селекторов
        {
            List<Reason> reasonList = reasonService.findAll().stream().
                filter(r -> r.getDtype().contains(dType)).collect(Collectors.toList());
            reason = createSelect(Reason::getName, reasonList,
                    "Причина обращения", Reason.class);
            reason.addValueChangeListener(e->{
                if(reason.getValue().getId() == 1){
                    powerCurrent.setValue(0.0);
                    powerCurrent.setEnabled(false);
                } else {
                    powerCurrent.setEnabled(true);
                }
            });

            voltage = createSelect(Voltage::getName, voltageService.findAllByOptional(false),
                    "Класс напряжения", Voltage.class);

            voltageIn = createSelect(Voltage::getName, voltageService.findAllByOptional(true),
                    "Уровень напряжения на вводе", Voltage.class);

            safety = createSelect(Safety::getName, safetyService.findAll(),
                    "Категория надежности", Safety.class);
            safety.setValue(safetyService.findById(3L).get());
            safety.setReadOnly(true);

            garant = createSelect(Garant::getName, garantService.findAllByActive(true),
                    "Гарантирующий поставщик", Garant.class);
            garant.addValueChangeListener(e->{
                if(garant.getValue().getId() != 1) {
                    garantText.setVisible(true);
                    garantText.setReadOnly(false);
                } else {
                    garantText.setValue("");
                    garantText.setVisible(false);
                    garantText.setReadOnly(true);
                }
            });

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

        formDemand.add(demandId,createdate, demandType, status, label);
        formDemand.add(demander,delegate,contact);
        formDemand.add(accordionDemander);
        formDemand.add(reason, object, address, specification,label);
        formDemand.add(countPoints, accordionPoints, powerCurrent, powerDemand
                , powerMaximum, voltage, voltageIn, safety, label);
        formDemand.add(countTransformations,countGenerations,techminGeneration,reservation);
        formDemand.add(period,contract);
        formDemand.add(accordionExpiration);
        formDemand.add(garant, garantText, plan);
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
                countGenerations, techminGeneration, reservation, plan, period, contract, garantText};
        for(Component field : fields){
            field.setVisible(false);
        }
        this.getElement().getStyle().set("margin", "15px");
        setListeners();
        accordionHistory.close();
    }

    protected void setListeners() {
        demander.addValueChangeListener(e->{
            if(!demander.isEmpty()) {
                demander.getElement().getStyle().set("border-width","0px");
            }
        });
        contact.addValueChangeListener(e->{
            if(!contact.isEmpty()) {
                contact.getElement().getStyle().set("border-width","0px");
            }
        });
        passportSerries.addValueChangeListener(e->{
            if(!passportSerries.isEmpty()) {
                passportSerries.getElement().getStyle().set("border-width","0px");
            }
        });
        passportNumber.addValueChangeListener(e->{
            if(!passportNumber.isEmpty()) {
                passportNumber.getElement().getStyle().set("border-width","0px");
            }
        });
        inn.addValueChangeListener(e->{
            if(!inn.isEmpty()) {
                inn.getElement().getStyle().set("border-width","0px");
            }
        });
        object.addValueChangeListener(e->{
            if(!object.isEmpty()) {
                object.getElement().getStyle().set("border-width","0px");
            }
        });
        address.addValueChangeListener(e->{
            if(!address.isEmpty()) {
                address.getElement().getStyle().set("border-width","0px");
            }
        });
    }

    private void setWidthFormDemand() {
        formDemand.setColspan(demandId,1);
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
        formDemand.setColspan(garantText,4);
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
        double currentP = 0.0;
        double demandP = 0.0;
        powerCurrent.getElement().getStyle().set("border-width","0px");
        powerDemand.getElement().getStyle().set("border-width","0px");
        currentP = powerCurrent.getValue() != null ? powerCurrent.getValue(): 0.0;
        demandP = powerDemand.getValue() != null ? powerDemand.getValue() : 0.0;
        powerMaximum.setValue(currentP + demandP);
        if((currentP + demandP) > 5.0) {
            voltageIn.setValue(voltageService.findById(4L).get());
            voltageIn.setReadOnly(true);
        } else {
            voltageIn.setReadOnly(false);
        }
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
            field.setValue(0.0);
            field.focus();
        }
    }

    public boolean save() {
        if(!verifyField()) return false;
        if (binderDemand.validate().getValidationErrors().size() > 0) {
            List<ValidationResult> validationResults = binderDemand.validate().getValidationErrors();
            for (ValidationResult validationResult : validationResults) {
                Notification.show(String.format("Ошибка %s", validationResult.getErrorMessage()));
            }
            return false;
        }
        demand.setChange(true);
        demand.setChangeDate(LocalDateTime.now());
        if(binderDemand.writeBeanIfValid(demand)) {
            if (demand.getUser() == null) {
                demand.setUser(userService.findByUsername(
                        SecurityContextHolder.getContext().getAuthentication().getName()));
                demand.setCreateDate(LocalDateTime.now());
                demand.setLoad1c(false);
                demand.setExecuted(false);
            }
            historyService.saveHistory(demand, demand, Demand.class);
            demandService.update(demand);

            filesLayout.setDemand(demand);
            filesLayout.saveFiles();
            notesLayout.setDemand(demand);
            notesLayout.saveNotes();

            return true;
        } else {
            return false;
        }
    }

    protected Boolean verifyField() {
        final Boolean[] result = {true};
        final Focusable[] fieldGoto = {null};
        class Attention {
            public void attention(AbstractField field, String message) {
                Notification.show(String.format(message), 3000,
                        Notification.Position.BOTTOM_START);
                fieldGoto[0] = fieldGoto[0] == null ? (Focusable) field : fieldGoto[0];
                field.getElement().getStyle().set("border-width","1px");
                field.getElement().getStyle().set("border-style","dashed");
                field.getElement().getStyle().set("border-color","red");
                result[0] = false;
            }
        }
        Attention attention = new Attention();
        if(demander.isEmpty()) {
            attention.attention(demander, "Не заполнено поле Заявитель");
        }
        if(contact.isEmpty() && contact.isVisible()) {
            attention.attention(contact,"Не заполнено поле Контактный телефон");
        }
        if(passportSerries.isEmpty() && passportSerries.isVisible()) {
            attention.attention(passportSerries,"Не заполнено поле Паспорт серия");
        }
        if(passportNumber.isEmpty() && passportNumber.isVisible()) {
            attention.attention(passportNumber,"Не заполнено поле Паспорт номер");
        }
        if(inn.isEmpty() && inn.isVisible()) {
            attention.attention(inn,"Не заполнено поле Реквизиты заявителя");
        }
        if(object.isEmpty()) {
            attention.attention(object,"Не заполнено поле Объект");
        }
        if(address.isEmpty()) {
            attention.attention(address,"Не заполнено поле Адрес объекта");
        }
        if(powerMaximum.isEmpty() && powerMaximum.isVisible()) {
            attention.attention(powerDemand,"Не заполнено поле Мощность...");
        }
        if(fieldGoto[0] != null) fieldGoto[0].focus();
        return result[0];
    }

    protected void setReadOnly(Boolean readOnly) {
        AbstractField fields[] = {
                demander,delegate,inn,innDate,contact,passportSerries,passportNumber,pasportIssued,
                addressRegistration,addressActual,reason,object,address,specification,
                countPoints,powerDemand,powerCurrent,powerMaximum,voltage,safety,period,
                contract,countTransformations,countGenerations,techminGeneration,reservation,
                plan,garant,garantText
        };
        for(AbstractField field : fields) {
            field.setReadOnly(readOnly);
        }
    }

    protected void populateForm(Demand value) {
        this.demand = value;
        binderDemand.readBean(this.demand);
        generalBinder.readBean(null);
        demandType.setReadOnly(true);
        createdate.setReadOnly(true);
        if(value != null) {
            demandId.setValue(demand.getId().toString());
            filesLayout.findAllByDemand(demand);
            notesLayout.findAllByDemand(demand);
            historyLayout.findAllByDemand(demand);
            switch(demand.getStatus().getState()){
                case ADD: {
                    setReadOnly(true);
                } break;
                case NOTE: {
                    setReadOnly(true);
                    filesLayout.setReadOnly();
                } break;
                case FREEZE: {
                    setReadOnly(true);
                    filesLayout.setReadOnly();
                    notesLayout.setReadOnly();
                } break;
            }
        }
    }

    protected void clearForm() {
        binderDemand.readBean(null);
        pointBinder.readBean(null);
        generalBinder.readBean(null);
        populateForm(null);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Role role = Role.ANONYMOUS;
        User currentUser;
        Optional<Long> demandId = event.getRouteParameters().getLong(DEMAND_ID);
        if (demandId.isPresent()) {
            currentUser = userService.findByUsername(
                    SecurityContextHolder.getContext().getAuthentication().getName());
            if(currentUser != null) {
                role = currentUser.getRoles().contains(Role.USER) ?
                        Role.USER :
                        currentUser.getRoles().contains(Role.GARANT) ?
                                Role.GARANT :
                                currentUser.getRoles().contains(Role.ADMIN) ?
                                        Role.ADMIN :
                                        Role.ANONYMOUS;
            }

            Optional<Demand> demandFromBackend = demandService.get(demandId.get());
            if (demandFromBackend.isPresent()) {
                if(role == Role.ADMIN) {
                    setReadOnly(false);
                    populateForm(demandFromBackend.get());
                } else if (demandFromBackend.get().getUser().equals(currentUser)
                        || (role == Role.GARANT &&
                                demandFromBackend.get().getGarant().getId()>1L)) {
                    populateForm(demandFromBackend.get());
                    if(role == Role.GARANT)
                        setReadOnly(true);
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

    public void saveEnable(Boolean enable) {
        save.setEnabled(enable);
    }
}

