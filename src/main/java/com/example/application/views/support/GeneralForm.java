package com.example.application.views.support;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.demandlist.DemandList;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
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
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.internal.Pair;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public abstract class GeneralForm extends Div implements BeforeEnterObserver {
    protected Pair<Focusable, Boolean> alertHere = new Pair<Focusable, Boolean>(null,true);

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
    protected Label attentionLabel = new Label();
    private int editPnt = 0;
    private int editExp = 0;

    // максимальная мощность по типу заявки
    protected Double MaxPower;

    protected TextField demandId = new TextField("Номер заявки");
    protected DateTimePicker createdate;
    protected Select<DemandType> demandType;
    protected Select<Status> status;

    protected Accordion accordionDemander = new Accordion();
    protected FormLayout formDemander = new FormLayout();
    protected TextArea demander;
    protected Select<String> typeDemander;
    protected TextField delegate;
    protected TextField inn;
    protected DatePicker innDate;
    protected TextField contact;
    protected TextField passportSerries;
    protected TextField passportNumber;
    protected TextArea pasportIssued;
    protected TextField addressRegistration;
    protected TextField addressActual;
    protected Checkbox addressEquals;

    protected Select<Reason> reason;
    protected TextArea object;
    protected TextArea address;
    protected TextArea specification;

    protected Accordion accordionPoints = new Accordion();
    protected PointsLayout pointsLayout;
    protected Accordion accordionExpiration = new Accordion();
    protected ExpirationsLayout expirationsLayout;
    protected Point point = new Point();
    protected Binder<Point> pointBinder = new Binder<>(Point.class);
    protected IntegerField countPoints;
    protected NumberField powerDemand;
    protected NumberField powerCurrent;
    protected NumberField powerMaximum;
    protected Select<Voltage> voltage;
    protected Select<Voltage> voltageIn;
    protected Select<Safety> safety;
    protected List<Point> points;

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
    protected final FileStoredService fileStoredService;

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

        filesLayout = new FilesLayout(this.fileStoredService, historyService);
        notesLayout = new NotesLayout(noteService);

        historyLayout = new HistoryLayout(this.historyService);
        historyLayout.setWidthFull();
        accordionHistory.add("История событий",historyLayout);
        accordionHistory.setWidthFull();

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
            demander.setHelperText("полное наименование заявителя – юридического лица;" +
                    " фамилия, имя, отчество заявителя – индивидуального предпринимателя");
            delegate = new TextField("ФИО представителя","Представитель юр.лица");
            inn = new TextField("Реквизиты заявителя (обязательное поле)");
//                    "ОГРН для юр.лиц, ИНН для ИП");
            inn.setHelperText("(номер записи в Едином государственном реестре юридических лиц"+
                    " / номер записи в Едином государственном реестре индивидуальных предпринимателей)");
            innDate = new DatePicker("Дата регистрации в реестре");
            contact = new TextField("Контактный телефон (обязательное поле)");
            passportSerries = new TextField("Паспорт серия (обязательное поле)", "Четыре цифры");
            passportNumber = new TextField("Паспорт номер (обязательное поле)", "Шесть цифр");
            pasportIssued = new TextArea("Паспорт выдан");
            pasportIssued.setHelperText("(кем, когда)");
            addressRegistration = new TextField("Адрес регистрации (обязательное поле)");
            addressRegistration.setHelperText("(место регистрации заявителя - индекс, адрес)");
            addressEquals = new Checkbox("Адрес регистрации совпадает с фактическим", false);
            addressActual = new TextField("Адрес фактический (обязательное поле)");
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
            powerMaximum.setValue(0.0);

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

            typeDemander = new Select<>();
            typeDemander.setLabel("Тип заявителя");
            typeDemander.setItems("Физическое лицо", "Юридическое лицо", "Индивидуальный предприниматель");

            List<Reason> reasonList = reasonService.findAll().stream().
                filter(r -> r.getDtype().contains(dType)).collect(Collectors.toList());
            reason = createSelect(Reason::getName, reasonList,
                    "Причина обращения (обязательное поле)", Reason.class);

            voltage = createSelect(Voltage::getName, voltageService.findAllByOptional(false),
                    "Класс напряжения", Voltage.class);

            voltageIn = createSelect(Voltage::getName, voltageService.findAllByOptional(true),
                    "Уровень напряжения на вводе", Voltage.class);

            safety = createSelect(Safety::getName, safetyService.findAll(),
                    "Категория надежности", Safety.class);
            if(safetyService.findById(3L).isPresent())
                safety.setValue(safetyService.findById(3L).get());
            safety.setReadOnly(true);

            garant = createSelect(Garant::getName, garantService.findAllByActive(true),
                    "Гарантирующий поставщик", Garant.class);

            plan = createSelect(Plan::getName, planService.findAll(),
                    "Рассрочка платежа", Plan.class);

            status = createSelect(Status::getName, statusService.findAll(),
                    "Статус", Status.class);
            if(statusService.findById(1L).isPresent())
                status.setValue(statusService.findById(1L).get());
            status.setReadOnly(true);
        }

        // настройка проверки значений полей
        {
            binderDemand.forField(inn)
//                    .withValidator(
//                            new StringLengthValidator(
//                                    "ИНН должен содержать от 10 до 13 знаков",
//                                    10, 13))
                    .bind(Demand::getInn, Demand::setInn);

            pointBinder.forField(powerDemand)
//                    .withValidator(
//                            new DoubleRangeValidator(
//                                    "Мощность не может быть отрицательной",
//                                    0.0, null))
                    .bind(Point::getPowerDemand, Point::setPowerDemand);

            pointBinder.forField(powerCurrent)
//                    .withValidator(
//                            new DoubleRangeValidator(
//                                    "Мощность не может быть отрицательной",
//                                    0.0, null))
                    .bind(Point::getPowerCurrent, Point::setPowerCurrent);

            pointBinder.forField(powerMaximum)
//                    .withValidator(
//                            new DoubleRangeValidator(
//                                    "Мощность не может быть отрицательной",
//                                    0.0, null))
                    .bind(Point::getPowerMaximum, null);
        }

        binderDemand.bindInstanceFields(this);
        pointBinder.bindInstanceFields(this);
        generalBinder.bindInstanceFields(this);

        // кол-во колонок формы от ширины окна
        setColumnCount(formDemand);
        setColumnCount(formDemander);

        formDemander.add(typeDemander,inn,innDate,label,
                passportSerries,passportNumber,pasportIssued,
                addressRegistration,addressActual,addressEquals);
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
        buttonBar.add(save,reset,attentionLabel);

        Component[] fields = {delegate, typeDemander, inn, innDate,
                passportSerries,passportNumber,pasportIssued,
                addressRegistration,addressActual, addressEquals,
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

    protected void settingTemporalReasons(){}

    protected void settingTemporalDemander() {
        switch (typeDemander.getValue()) {
            case "Физическое лицо":
                inn.setVisible(false);
                innDate.setVisible(false);
                passportSerries.setVisible(true);
                passportNumber.setVisible(true);
                pasportIssued.setVisible(true);
                break;
            case "Юридическое лицо":
                inn.setVisible(true);
                innDate.setVisible(true);
                passportSerries.setVisible(false);
                passportNumber.setVisible(false);
                pasportIssued.setVisible(false);
                break;
            case "Индивидуальный предприниматель":
                inn.setVisible(true);
                innDate.setVisible(true);
                passportSerries.setVisible(true);
                passportNumber.setVisible(true);
                pasportIssued.setVisible(true);
                break;
        }
    }

    protected void deselect(AbstractField field){
        if(!field.isEmpty()) {
            field.getElement().getStyle().set("border-width","0px");
        }
    }

    protected void setListeners() {
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
        demander.addValueChangeListener(e -> deselect(demander));
        contact.addValueChangeListener(e -> deselect(contact));
        typeDemander.addValueChangeListener(e -> {
            deselect(typeDemander);
            settingTemporalDemander();
        });
        inn.addValueChangeListener(e -> {
            int length = inn.getValue().length();
            if((length < 10) || (length > 13)) {
                alertHere = ViewHelper.attention(inn,
                        "Поле Реквизиты заявителя дожно содержать от 10 до 13 цифр"
                        ,alertHere.getFirst());
                if(inn != null) inn.focus();
            } else {
                inn.getElement().getStyle().set("border-width", "0px");
            }
            deselect(inn);
        });
        passportSerries.addValueChangeListener(e->{
            if(passportSerries.getValue().length() != 4) {
                alertHere = ViewHelper.attention(passportSerries
                        , "Поле Паспорт серия должно содержать 4 цифры"
                        ,alertHere.getFirst());
                if(passportSerries != null) passportSerries.focus();
            } else {
                passportSerries.getElement().getStyle().set("border-width", "0px");
            }
            deselect(passportSerries);
        });
        passportNumber.addValueChangeListener(e->{
            if(passportNumber.getValue().length() != 6) {
                alertHere = ViewHelper.attention(passportNumber
                        ,"Поле Паспорт номер  должно содержать 6 цифр"
                        ,alertHere.getFirst());
                if(passportNumber != null) passportNumber.focus();
            } else {
                passportNumber.getElement().getStyle().set("border-width", "0px");
            }
            deselect(passportNumber);
        });
        addressRegistration.addValueChangeListener(e -> deselect(addressRegistration));
        addressEquals.addValueChangeListener(event -> {
            if(addressEquals.getValue()){
                addressActual.setValue(addressRegistration.getValue());
                addressActual.setEnabled(false);
            } else {
                addressActual.setEnabled(true);
            }
        });
        addressActual.addValueChangeListener(e -> deselect(addressActual));
        reason.addValueChangeListener(e -> {
            if(reason.getValue().getId() == 1) {
                powerCurrent.setValue(0.0);
                powerCurrent.setReadOnly(true);
            } else {
                powerCurrent.setReadOnly(false);
            }
            settingTemporalReasons();
            deselect(reason);
        });
        object.addValueChangeListener(e -> deselect(object));
        address.addValueChangeListener(e -> deselect(address));
        specification.addValueChangeListener(e -> deselect(specification));
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
        powerDemand.addBlurListener(e->testPower(powerDemand));
        powerDemand.addValueChangeListener(e -> changePower(powerDemand));
        powerCurrent.addBlurListener(e->testPower(powerCurrent));
        powerCurrent.addValueChangeListener(e -> changePower(powerCurrent));
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
        formDemander.setColspan(typeDemander, 1);
        formDemander.setColspan(inn, 1);
        formDemander.setColspan(innDate, 1);
        formDemander.setColspan(passportNumber, 1);
        formDemander.setColspan(passportSerries, 1);
        formDemander.setColspan(pasportIssued, 4);
        formDemander.setColspan(addressRegistration, 4);
        formDemander.setColspan(addressActual, 4);
        formDemander.setColspan(addressEquals, 4);
    }

    private void setColumnCount(@NotNull FormLayout form) {
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
        double currentP;
        double demandP;
        powerCurrent.getElement().getStyle().set("border-width","0px");
        powerDemand.getElement().getStyle().set("border-width","0px");
        currentP = powerCurrent.getValue() != null ? powerCurrent.getValue(): 0.0;
        demandP = powerDemand.getValue() != null ? powerDemand.getValue() : 0.0;
        powerMaximum.setValue(currentP + demandP);
        if((currentP + demandP) > 5.0) {
            if(voltageService.findById(4L).isPresent())
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
        if(typeDemander.isVisible()){
            if(typeDemander.getValue() != null) {
                demand.setTypeDemander(typeDemander.getValue());
            }
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
        alertHere = new Pair<Focusable, Boolean>(null,true);
        if(demander.isEmpty()) {
            alertHere = ViewHelper.attention(demander,"Не заполнено поле Заявитель",alertHere.getFirst());
        }
        if(contact.isEmpty() && contact.isVisible()) {
            alertHere = ViewHelper.attention(contact,"Не заполнено поле Контактный телефон",alertHere.getFirst());
        }
        if(typeDemander.isVisible() && typeDemander.getValue() == null) {
            alertHere = ViewHelper.attention(typeDemander,"Не заполнено поле Тип заявителя",alertHere.getFirst());
        }
        if(!typeDemander.isVisible() ||
                (typeDemander.getValue() != null &&
                !typeDemander.getValue().equals("Индивидуальный предприниматель"))) {
            if (passportSerries.isEmpty() &&
                    passportSerries.isVisible()
                    ) {
                alertHere = ViewHelper.attention(passportSerries, "Не заполнено поле Паспорт серия",alertHere.getFirst());
            }
            if (!passportSerries.isEmpty() &&
                    passportSerries.isVisible()) {
                if (passportSerries.getValue().length() != 4)
                    alertHere = ViewHelper.attention(passportSerries
                            ,"Поле Паспорт серия должно содержать 4 цифры",alertHere.getFirst());
            }
            if (passportNumber.isEmpty() &&
                    passportNumber.isVisible()) {
                alertHere = ViewHelper.attention(passportNumber, "Не заполнено поле Паспорт номер",alertHere.getFirst());
            }
            if (!passportNumber.isEmpty() &&
                    passportNumber.isVisible()) {
                if (passportNumber.getValue().length() != 6)
                    alertHere = ViewHelper.attention(passportNumber
                            ,"Поле Паспорт номер  должно содержать 6 цифр",alertHere.getFirst());
            }
        }
        if(inn.isEmpty() && inn.isVisible()) {
            alertHere = ViewHelper.attention(inn,"Не заполнено поле Реквизиты заявителя",alertHere.getFirst());
        }
        if(!inn.isEmpty() && inn.isVisible()) {
            int length = inn.getValue().length();
            if((length < 10) || (length > 13))
                alertHere = ViewHelper.attention(inn
                        ,"Поле Реквизиты заявителя дожно содержать от 10 до 13 цифр",alertHere.getFirst());
        }
        if(addressRegistration.isEmpty() && addressRegistration.isVisible()) {
            alertHere = ViewHelper.attention(addressRegistration
                    ,"Не заполнено поле Адрес регистрации",alertHere.getFirst());
        }
        if(addressActual.isEmpty() && addressActual.isVisible()) {
            alertHere = ViewHelper.attention(addressActual,"Не заполнено поле Адрес фактический",alertHere.getFirst());
        }
        if(reason.getValue() == null) {
            alertHere = ViewHelper.attention(reason,"Необходимо выбрать причину обращения",alertHere.getFirst());
        }
        if(object.isEmpty()) {
            alertHere = ViewHelper.attention(object,"Не заполнено поле Объект",alertHere.getFirst());
        }
        if(address.isEmpty()) {
            alertHere = ViewHelper.attention(address,"Не заполнено поле Адрес объекта",alertHere.getFirst());
        }
        if(specification.isEmpty() && (demandType.getValue().getId().equals(DemandType.TO150))) {
            alertHere = ViewHelper.attention(specification,"Не заполнено поле Характер нагрузки}",alertHere.getFirst());
        }
        if(reason.getValue() != null) {
            if ((powerCurrent.isEmpty() || powerCurrent.getValue() == 0.0) &&
                    powerMaximum.isVisible() && (reason.getValue().getId() == 2L)) {
                alertHere = ViewHelper.attention(powerCurrent
                        ,"При увеличении мощности нужно указать ранее присоединённую..."
                        ,alertHere.getFirst());
            }
        }
        if ((powerDemand.isEmpty() || powerDemand.getValue() == 0.0) && powerMaximum.isVisible()) {
            alertHere = ViewHelper.attention(powerDemand, "Не заполнено поле Мощность...",alertHere.getFirst());
        }
        if ((!powerMaximum.isEmpty() || powerMaximum.getValue() != 0.0)
                && powerMaximum.getValue() > this.MaxPower) {
            alertHere = ViewHelper.attention(powerDemand
                    ,"Максимальная мощность превышает допустимую...",alertHere.getFirst());
        }
        if(alertHere.getFirst() != null) alertHere.getFirst().focus();
        return alertHere.getSecond();
    }

    protected void setReadOnly(Boolean readOnly) {
         AbstractField[] fields = {
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
            if(typeDemander.isVisible()){
                if(value.getTypeDemander() != null) {
                    typeDemander.setValue(value.getTypeDemander());
                }
            }
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
            if(addressActual.getValue().equals(addressRegistration.getValue())) {
                addressActual.setEnabled(false);
                addressEquals.setValue(true);
            }
        }
    }

    protected void clearForm() {
        binderDemand.readBean(null);
        pointBinder.readBean(null);
        generalBinder.readBean(null);
        populateForm(null);
    }

    protected void saveMode(int edEx, int edPt) {
        editPnt += edPt;
        editExp += edEx;
        if(editPnt > 0 && editExp <= 0) {
            save.setEnabled(false);
            attentionLabel.setText("Вы не сохранили точки подключения. " +
                    "Нажмите СОХРАНИТЬ в таблице точек подлючения");
        }
        if(editExp > 0 && editPnt <=0 ) {
            save.setEnabled(false);
            attentionLabel.setText("Вы не сохранили этапы работ. " +
                    "Нажмите СОХРАНИТЬ в таблице этапов");
        }
        if(editPnt > 0 && editExp > 0) {
            save.setEnabled(false);
            attentionLabel.setText("Вы не сохранили этапы работ и точки подлючения. " +
                    "Нажмите СОХРАНИТЬ в таблице этапов и таблице точек подлючения");
        }
        if(editPnt <= 0 && editExp <= 0) {
            save.setEnabled(true);
            attentionLabel.setText("");
        }
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
}

