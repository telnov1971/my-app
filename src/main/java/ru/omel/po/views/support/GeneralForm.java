package ru.omel.po.views.support;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.NotificationVariant;
import ru.omel.po.data.entity.*;
import ru.omel.po.data.service.*;
import ru.omel.po.views.demandlist.DemandList;
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
import com.vaadin.flow.internal.Pair;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public abstract class GeneralForm extends Div implements BeforeEnterObserver {
    protected Pair<Focusable, Boolean> alertHere = new Pair<>(null, true);

    protected int client = 1;
    protected boolean historyExists = false;
    protected static final String DEMAND_ID = "demandID";
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
    protected Double maxPower;

    protected TextField demandId = new TextField("Номер заявки");
    protected DateTimePicker createdate;
    protected Select<DemandType> demandType;
    protected Select<Status> status;

    protected Accordion accordionDemander = new Accordion();
    protected FormLayout formDemander = new FormLayout();
    protected ComboBox<String> demander = new ComboBox<>();
    protected List<String> demanderList = new ArrayList<>();
    protected Select<String> typeDemander;
    protected TextField delegate;
    protected ComboBox<String> inn = new ComboBox<>();
    protected List<String> innList = new ArrayList<>();
    protected DatePicker innDate;
    protected ComboBox<String> ogrn = new ComboBox<>();
    protected List<String> ogrnList = new ArrayList<>();

    protected ComboBox<String> contact = new ComboBox<>();
    protected List<String> contactList = new ArrayList<>();
    protected ComboBox<String> meEmail = new ComboBox<>();
    protected List<String> meEmailList = new ArrayList<>();
    protected ComboBox<String> passportSerries = new ComboBox<>();
    protected List<String> passportSerriesList = new ArrayList<>();
    protected ComboBox<String> passportNumber = new ComboBox<>();
    protected List<String> passportNumberList = new ArrayList<>();
    protected ComboBox<String> passportIssued = new ComboBox<>();
    protected List<String> passportIssuedList = new ArrayList<>();

    protected ComboBox<String> addressRegistration = new ComboBox<>();
    protected List<String> addressRegistrationList = new ArrayList<>();

    protected TextField addressActual;
    protected Checkbox addressEquals;

    protected Label labelPrivilege;
    protected Checkbox privilegeNot = new Checkbox();
    protected Accordion accordionPrivilege = new Accordion();
    protected PrivilegeLayout privilegeLayout;
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

    protected final TextArea space;

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
    protected final PrivilegeService privilegeService;

    protected GeneralForm(ReasonService reasonService,
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
                       PrivilegeService privilegeService,
                       Component... components) {
        super(components);
        // сервисы
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
        this.privilegeService = privilegeService;

        this.decimalFormat = new DecimalFormat("###.##",
                new DecimalFormatSymbols());

        Label label = new Label("                                                ");
        label.setHeight("1px");

        filesLayout = new FilesLayout(this.fileStoredService, this.historyService, client);
        notesLayout = new NotesLayout(noteService, this.historyService, client);

        privilegeLayout = new PrivilegeLayout(this.privilegeService, this.historyService, this);
        accordionPrivilege.add("Выбор основания дающего право на льготы по оплате"
                +" (открыть/закрыть по клику мышкой)",privilegeLayout);
        labelPrivilege = new Label("Если Заявитель имеет право на получение льгот по оплате,"
                +" необходимо выбрать основание, которое Заявитель должен подтвердить"
                +" соответствующими документами или подтвердить что \"Я ");
        labelPrivilege.getElement().getStyle().set("color", "red");
        labelPrivilege.getElement().getStyle().set("font-size", "1.3em");
        privilegeNot.setLabel("не являюсь лицом, которому может быть предоставлена льготная ставка\"");
        privilegeNot.getElement().getStyle().set("color", "red");
        privilegeNot.addValueChangeListener(e -> {
            if(Boolean.TRUE.equals(privilegeNot.getValue()))
                privilegeLayout.setValueFalse();
        });

        historyLayout = new HistoryLayout(this.historyService);
        historyLayout.setWidthFull();
        accordionHistory.add("История событий (открыть/закрыть по клику мышкой)",historyLayout);
        accordionHistory.setWidthFull();

        // описание полей
        demandId.setReadOnly(true);

        createdate = new DateTimePicker("Дата и время создания");
        createdate.setValue(LocalDateTime.now());
        createdate.setReadOnly(true);

        demandType = ViewHelper.createSelect(DemandType::getName, demandTypeService.findAll(),
                "Тип заявки", DemandType.class);
        demandType.setReadOnly(true);

        demander.setLabel("Заявитель (обязательное поле)");
        demander.setAllowCustomValue(true);
        demander.setItems(demanderList);

        demander.setHelperText("полное наименование заявителя – юридического лица;" +
                " фамилия, имя, отчество заявителя – индивидуального предпринимателя");
        delegate = new TextField("ФИО представителя","Представитель юр.лица");
        inn.setLabel("ИНН или СНИЛС (обязательное поле)");
        inn.setPlaceholder("ИНН для юр.лиц и ИП, СНИЛс для физ.лиц");
        inn.setAllowCustomValue(true);
        inn.setItems(innList);
        inn.setHelperText(
                "идентификационный номер налогоплательщика для юр.лиц и ИП"+
                        " / номер СНИЛС для физических лиц");
        innDate = new DatePicker("Дата регистрации в реестре");
        innDate.addValueChangeListener(e -> {
            LocalDate date = innDate.getValue();
            if(date.getYear() > 50 && date.getYear() < 100)
                innDate.setValue(LocalDate.of(1900 + date.getYear()
                        ,date.getMonthValue()
                        ,date.getDayOfMonth()));
            if(date.getYear() > 0 && date.getYear() < 51)
                innDate.setValue(LocalDate.of(2000 + date.getYear()
                        ,date.getMonthValue()
                        ,date.getDayOfMonth()));
        });
        innDate.setInvalid(true);
        ogrn.setLabel("ОГРН (обязательное поле)");
        ogrn.setPlaceholder("ОГРН для юр.лиц и ИП");
        ogrn.setAllowCustomValue(true);
        ogrn.setItems(ogrnList);
        ogrn.setHelperText("(номер записи в Едином государственном реестре юридических лиц");
        contact.setLabel("Контактный телефон (обязательное поле)");
        contact.setAllowCustomValue(true);
        contact.setItems(contactList);
        meEmail.setLabel("Электронная почта (обязательное поле)");
        meEmail.setAllowCustomValue(true);
        meEmail.setItems(demanderList);
        passportSerries.setLabel("Паспорт серия (обязательное поле)");
        passportSerries.setPlaceholder("Четыре цифры");
        passportSerries.setAllowCustomValue(true);
        passportSerries.setItems(passportSerriesList);
        passportNumber.setLabel("Паспорт номер (обязательное поле)");
        passportNumber.setPlaceholder("Шесть цифр");
        passportNumber.setAllowCustomValue(true);
        passportNumber.setItems(passportNumberList);
        passportIssued.setLabel("Паспорт выдан");
        passportIssued.setAllowCustomValue(true);
        passportIssued.setItems(passportIssuedList);
        passportIssued.setHelperText("(кем, когда)");
        addressRegistration.setLabel("Адрес регистрации (обязательное поле)");
        addressRegistration.setAllowCustomValue(true);
        addressRegistration.setItems(addressRegistrationList);
        addressRegistration.setHelperText("(место регистрации заявителя - индекс, адрес)");
        addressEquals = new Checkbox("Адрес фактический совпадает с адресом регистрации", false);
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
        powerDemand.setHelperText("(цифры, точка или запятая)");
        powerDemand.setStep(0.01);
        powerDemand.setAutocorrect(true);
        powerCurrent = new NumberField("Мощность ранее присоединённая, кВт", "0,00 кВт");
        powerCurrent.setHelperText("(цифры, точка или запятая)");
        powerCurrent.setAutocorrect(true);
        powerMaximum = new NumberField("Мощность максимальная, кВт", "0,00 кВт");
        powerMaximum.setAutocorrect(true);
        powerMaximum.setValue(0.0);
        powerMaximum.setReadOnly(true);

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
        garantText = new TextField("Наименование гарантирующего поставщика (обязательное) *");
        garantText.addClassName("v-captiontext");
        garantText.addClassName("v-required-field-indicator");

        // создание селекторов
        typeDemander = new Select<>();
        typeDemander.setLabel("Тип заявителя");
        typeDemander.setItems("Физическое лицо", "Юридическое лицо", "Индивидуальный предприниматель");

        List<Reason> reasonList = reasonService.findAll().stream().
                filter(r -> r.getDtype().contains(dType)).toList();
        reason = ViewHelper.createSelect(Reason::getName, reasonList,
                "Причина обращения (обязательное поле)", Reason.class);

        voltage = ViewHelper.createSelect(Voltage::getName, voltageService.findAllByOptional(false),
                "Класс напряжения", Voltage.class);

        voltageIn = ViewHelper.createSelect(Voltage::getName, voltageService.findAllByOptional(true),
                "Уровень напряжения на вводе", Voltage.class);

        safety = ViewHelper.createSelect(Safety::getName, safetyService.findAll(),
                "Категория надежности", Safety.class);
        safetyService.findById(3L).ifPresent(r -> safety.setValue(r));
        safety.setReadOnly(true);

        garant = ViewHelper.createSelect(Garant::getName, garantService.findAllByActive(true),
                "Гарантирующий поставщик", Garant.class);

        plan = ViewHelper.createSelect(Plan::getName, planService.findAll(),
                "Рассрочка платежа", Plan.class);

        status = ViewHelper.createSelect(Status::getName, statusService.findAll(),
                "Статус", Status.class);
        statusService.findById(1L).ifPresent(r -> status.setValue(r));
        status.setReadOnly(true);

        binderDemand.bindInstanceFields(this);
        pointBinder.bindInstanceFields(this);
        generalBinder.bindInstanceFields(this);

        // кол-во колонок формы от ширины окна
        setColumnCount(formDemand);
        setColumnCount(formDemander);

        formDemander.add(typeDemander,inn,innDate,ogrn,label,
                passportSerries,passportNumber, passportIssued,
                addressRegistration,addressActual,addressEquals);
        setWidthFormDemander();
        accordionDemander.add("Данные заявителя (открыть/закрыть по клику мышкой)", formDemander);

        formDemand.add(demandId,createdate, demandType, status, label);
        formDemand.add(demander,delegate,contact,meEmail);
        formDemand.add(accordionDemander);
        formDemand.add(labelPrivilege,privilegeNot,accordionPrivilege);
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

        Component[] fields = {delegate, typeDemander, ogrn, //inn, innDate,
                passportSerries,passportNumber, passportIssued,
                labelPrivilege,privilegeNot,accordionPrivilege,
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
        space = new TextArea();
        space.setWidthFull();
        space.getElement().getStyle().set("color","red");
        space.setReadOnly(true);
    }

    protected void settingTemporalReasons(){}

    protected void settingTemporalDemander() {
        switch (typeDemander.getValue()) {
            case "Физическое лицо" -> {
                passportSerries.setVisible(true);
                passportNumber.setVisible(true);
                passportIssued.setVisible(true);
                ogrn.setVisible(false);
            }
            case "Юридическое лицо" -> {
                passportSerries.setVisible(false);
                passportNumber.setVisible(false);
                passportIssued.setVisible(false);
                ogrn.setVisible(true);
            }
            default -> {
                passportSerries.setVisible(true);
                passportNumber.setVisible(true);
                passportIssued.setVisible(true);
                ogrn.setVisible(true);
            }
        }
    }

    protected void setListeners() {
        save.addClickListener(event -> {
            if(Boolean.TRUE.equals(!verifyField())) return;
            if(!save()) return;
            UI.getCurrent().navigate(DemandList.class);
        });
        reset.addClickListener(event -> {
            try {
                filesLayout.deleteFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
            UI.getCurrent().navigate(DemandList.class);
        });
        demander.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            if(!demanderList.contains(customValue))
                demanderList.add(customValue);
            demander.setItems(demanderList);
            demander.setValue(customValue);
        });
        demander.addValueChangeListener(e -> ViewHelper.deselect(demander));
        contact.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            if(!contactList.contains(customValue))
                contactList.add(customValue);
            contact.setItems(demanderList);
            contact.setValue(customValue);
        });
        contact.addValueChangeListener(e -> ViewHelper.deselect(contact));
        meEmail.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            if(!meEmailList.contains(customValue))
                meEmailList.add(customValue);
            meEmail.setItems(meEmailList);
            meEmail.setValue(customValue);
        });
        meEmail.addValueChangeListener(e -> ViewHelper.deselect(contact));
        typeDemander.addValueChangeListener(e -> {
            ViewHelper.deselect(typeDemander);
            settingTemporalDemander();
        });
        inn.addValueChangeListener(e -> {
            int length;
            if(inn.isVisible() && inn.getValue()!=null) {
                length = inn.getValue().length();
            } else {
                return;
            }
            if((length < 10) || (length > 50)) {
                alertHere = ViewHelper.attention(inn,
                        "Поле ИНН дожно содержать от 10 символов"
                        ,alertHere.getFirst(),space);
                if(inn != null) inn.focus();
            } else {
                ViewHelper.deselect(inn);
            }
        });
        inn.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            int length = customValue.length();
            if((length < 10) || (length > 50)) {
                alertHere = ViewHelper.attention(inn,
                        "Поле ИНН дожно содержать от 10 символов"
                        ,alertHere.getFirst(),space);
                if(inn != null) inn.focus();
                return;
            } else {
                ViewHelper.deselect(inn);
            }
            if(!innList.contains(customValue))
                innList.add(customValue);
            inn.setItems(innList);
            inn.setValue(customValue);
        });
        ogrn.addValueChangeListener(e -> {
            int length;
            if(ogrn.isVisible() && ogrn.getValue()!=null) {
                length = ogrn.getValue().length();
            } else {
                return;
            }
            if((length < 13) || (length > 15)) {
                alertHere = ViewHelper.attention(ogrn,
                        "Поле ОГРН дожно содержать от 13 до 15 символов"
                        ,alertHere.getFirst(),space);
                if(ogrn != null) ogrn.focus();
            } else {
                ViewHelper.deselect(ogrn);
            }
        });
        ogrn.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            int length = customValue.length();
            if((length < 13) || (length > 15)) {
                alertHere = ViewHelper.attention(ogrn,
                        "Поле ОГРН дожно содержать от 13 до 15 символов"
                        ,alertHere.getFirst(),space);
                if(ogrn != null) ogrn.focus();
                return;
            } else {
                ViewHelper.deselect(ogrn);
            }
            if(!ogrnList.contains(customValue))
                ogrnList.add(customValue);
            ogrn.setItems(ogrnList);
            ogrn.setValue(customValue);
        });
        if(passportSerries.isVisible()) {
            passportSerries.addValueChangeListener(e -> {
                if(passportSerries.getValue() == null) return;
                if (passportSerries.getValue().length() != 4) {
                    alertHere = ViewHelper.attention(passportSerries
                            , "Поле Паспорт серия должно содержать 4 цифры"
                            , alertHere.getFirst(), space);
                    passportSerries.focus();
                } else {
                    ViewHelper.deselect(passportSerries);
                }
            });
        }
        passportSerries.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            if(!passportSerriesList.contains(customValue))
                passportSerriesList.add(customValue);
            passportSerries.setItems(passportSerriesList);
            passportSerries.setValue(customValue);
        });
        if(passportNumber.isVisible()) {
            passportNumber.addValueChangeListener(e -> {
                if(passportNumber.getValue()==null) return;
                if(passportNumber.getValue().length() != 6) {
                    alertHere = ViewHelper.attention(passportNumber
                            , "Поле Паспорт номер  должно содержать 6 цифр"
                            , alertHere.getFirst(), space);
                    passportNumber.focus();
                } else {
                    ViewHelper.deselect(passportNumber);
                }
            });
        }
        passportNumber.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            if(!passportNumberList.contains(customValue))
                passportNumberList.add(customValue);
            passportNumber.setItems(passportNumberList);
            passportNumber.setValue(customValue);
        });
        passportIssued.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            if(!passportIssuedList.contains(customValue))
                passportIssuedList.add(customValue);
            passportIssued.setItems(passportIssuedList);
            passportIssued.setValue(customValue);
        });
        addressRegistration.addValueChangeListener(e -> ViewHelper.deselect(addressRegistration));
        addressRegistration.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            if(!addressRegistrationList.contains(customValue))
                addressRegistrationList.add(customValue);
            addressRegistration.setItems(addressRegistrationList);
            addressRegistration.setValue(customValue);
        });
        addressEquals.addValueChangeListener(event -> {
            if(Boolean.TRUE.equals(addressEquals.getValue())) {
                addressActual.setValue(addressRegistration.getValue());
                addressActual.setEnabled(false);
            } else {
                addressActual.setEnabled(true);
            }
        });
        addressActual.addValueChangeListener(e -> ViewHelper.deselect(addressActual));
        reason.addValueChangeListener(e -> {
            powerCurrent.setReadOnly(reason.getValue().getId() == 1L);
            powerDemand.setReadOnly(reason.getValue().getId() == 3L
                    || reason.getValue().getId() == 4L);
            settingTemporalReasons();
            ViewHelper.deselect(reason);
        });
        object.addValueChangeListener(e -> ViewHelper.deselect(object));
        address.addValueChangeListener(e -> ViewHelper.deselect(address));
        specification.addValueChangeListener(e -> ViewHelper.deselect(specification));
        garant.addValueChangeListener(e->{
            if(garant.getValue().getId() != 1) {
                garantText.setVisible(true);
                garantText.setReadOnly(false);
            } else {
                garantText.setValue("");
                garantText.setVisible(false);
                garantText.setReadOnly(true);
                ViewHelper.deselect(garant);
            }
        });
        garantText.addValueChangeListener(e -> ViewHelper.deselect(garantText));
        powerDemand.addBlurListener(e->testPower(powerDemand));
        powerDemand.addValueChangeListener(e -> changePower(powerDemand));
        powerCurrent.addBlurListener(e->testPower(powerCurrent));
        powerCurrent.addValueChangeListener(e -> changePower(powerCurrent));
    }

    private void setWidthFormDemand() {
        Component[] oneColumn = {demandId,createdate,demandType,status
                ,contact,meEmail,countPoints,powerDemand
                ,powerCurrent,powerMaximum,voltage,safety,garant,plan};
        for (Component component : oneColumn) {
            formDemand.setColspan(component,1);
        }
        Component[] fourColumn = {demander,delegate,accordionDemander
                ,labelPrivilege,privilegeNot,accordionPrivilege
                ,reason,object,address,specification
                ,accordionPoints,countTransformations,countGenerations,techminGeneration,reservation
                ,period,contract,accordionExpiration,accordionHistory,garantText};
        for (Component component : fourColumn) {
            formDemand.setColspan(component,4);
        }
    }

    private void setWidthFormDemander() {
        formDemander.setColspan(typeDemander, 1);
        formDemander.setColspan(inn, 2);
        formDemander.setColspan(innDate, 1);
        formDemander.setColspan(ogrn, 2);
        formDemander.setColspan(passportNumber, 1);
        formDemander.setColspan(passportSerries, 1);
        formDemander.setColspan(passportIssued, 4);
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

    private void changePower(NumberField field) {
        double currentP;
        double demandP;
        powerCurrent.getElement().getStyle().set("border-width","0px");
        powerDemand.getElement().getStyle().set("border-width","0px");
        currentP = powerCurrent.getValue() != null ? powerCurrent.getValue(): 0.0;
        demandP = powerDemand.getValue() != null ? powerDemand.getValue() : 0.0;
        powerMaximum.setValue(currentP + demandP);
        // При максимальной мощности свыше 5 кВт напряжение на входе должно быть только 380 В
        if((currentP + demandP) > 5.0) {
            voltageService.findById(4L).ifPresent(r -> voltageIn.setValue(r));
            voltageIn.setReadOnly(true);
        } else {
            voltageIn.setReadOnly(false);
        }
        if (powerMaximum.getValue() > this.maxPower) {
            Notification notification = new Notification(
                    "Для такого типа заявки превышена макисальная мощность", 5000,
                    Notification.Position.MIDDLE);
            notification.open();
            field.focus();
        }
    }
    private void testPower(NumberField field) {
        if(field.isReadOnly()) return;
        if(field.getValue() == null){
            Notification notification = new Notification(
                    "Ошибка ввода числа", 5000,
                    Notification.Position.MIDDLE);
            notification.open();
            field.focus();
        }
    }

    public boolean save() {
        if(typeDemander.isVisible() && (typeDemander.getValue() != null)) {
                demand.setTypeDemander(typeDemander.getValue());

        }
        demand.setChangeDate(LocalDateTime.now());
        if (demand.getUser() == null) {
            demand.setUser(userService.findByUsername(
                    SecurityContextHolder.getContext().getAuthentication().getName()));
            demand.setCreateDate(LocalDateTime.now());
            demand.setLoad1c(false);
            demand.setExecuted(false);
        }
        historyExists = historyService.saveHistory(client, demand, demand, Demand.class);
        if(historyExists)
            demand.setChange(true);
        demand = demandService.update(demand);
        filesLayout.setDemand(demand);
        notesLayout.setDemand(demand);

        historyExists |= filesLayout.saveFiles();
        historyExists |= notesLayout.saveNotes(client);
        return true;
    }

    protected Boolean verifyField() {
        alertHere = new Pair<>(null, true);
        space.setValue("");
        space.setLabel("");
        if(demander.isEmpty()) {
            alertHere = ViewHelper.attention(demander
                    ,"Не заполнено поле Заявитель"
                    ,alertHere.getFirst(),space);
        }
        if(contact.isEmpty() && contact.isVisible()) {
            alertHere = ViewHelper.attention(contact
                    ,"Не заполнено поле Контактный телефон"
                    ,alertHere.getFirst(),space);
        }
        if(meEmail.isEmpty()) {
            alertHere = ViewHelper.attention(meEmail
                    ,"Не заполнено поле Электронная почта"
                    ,alertHere.getFirst(),space);
        }
        if(typeDemander.isVisible() && typeDemander.getValue() == null) {
            alertHere = ViewHelper.attention(typeDemander
                    ,"Не заполнено поле Тип заявителя"
                    ,alertHere.getFirst(),space);
        }
        if(!typeDemander.isVisible() ||
                (typeDemander.getValue() != null &&
                !typeDemander.getValue().equals("Индивидуальный предприниматель"))) {
            if (passportSerries.isEmpty() &&
                    passportSerries.isVisible()
                    ) {
                alertHere = ViewHelper.attention(passportSerries
                        , "Не заполнено поле Паспорт серия"
                        ,alertHere.getFirst(),space);
            }
            if (!passportSerries.isEmpty() &&
                    passportSerries.isVisible() &&
                    (passportSerries.getValue().length() != 4))
                    {alertHere = ViewHelper.attention(passportSerries
                            ,"Поле Паспорт серия должно содержать 4 цифры"
                            ,alertHere.getFirst(),space);
            }
            if (passportNumber.isEmpty() &&
                    passportNumber.isVisible()) {
                alertHere = ViewHelper.attention(passportNumber
                        , "Не заполнено поле Паспорт номер"
                        ,alertHere.getFirst(),space);
            }
            if (!passportNumber.isEmpty() &&
                    passportNumber.isVisible() &&
                    (passportNumber.getValue().length() != 6))
                    {alertHere = ViewHelper.attention(passportNumber
                            ,"Поле Паспорт номер  должно содержать 6 цифр"
                            ,alertHere.getFirst(),space);
            }
        }
        if(inn.isEmpty() && inn.isVisible()) {
            alertHere = ViewHelper.attention(inn
                    ,"Не заполнено поле ИНН или СНИЛС"
                    ,alertHere.getFirst(),space);
        }
        if(!inn.isEmpty() && inn.isVisible()) {
            int length = inn.getValue().length();
            if((length < 10) || (length > 50))
                alertHere = ViewHelper.attention(inn
                        ,"Поле ИНН (или СНИЛС) дожно содержать от 10 символов"
                        ,alertHere.getFirst(),space);
        }
        if(ogrn.isEmpty() && ogrn.isVisible()) {
            alertHere = ViewHelper.attention(ogrn
                    ,"Не заполнено поле ОГРН"
                    ,alertHere.getFirst(),space);
        }
        if(!ogrn.isEmpty() && ogrn.isVisible()) {
            int length = ogrn.getValue().length();
            if((length < 13) || (length > 15))
                alertHere = ViewHelper.attention(ogrn
                        ,"Поле ОГРН дожно содержать от 13 до 15 символов"
                        ,alertHere.getFirst(),space);
        }
        if(addressRegistration.isEmpty() && addressRegistration.isVisible()) {
            alertHere = ViewHelper.attention(addressRegistration
                    ,"Не заполнено поле Адрес регистрации"
                    ,alertHere.getFirst(),space);
        }
        if(addressActual.isEmpty() && addressActual.isVisible()) {
            alertHere = ViewHelper.attention(addressActual
                    ,"Не заполнено поле Адрес фактический"
                    ,alertHere.getFirst(),space);
        }
        if(reason.getValue() == null) {
            alertHere = ViewHelper.attention(reason
                    ,"Необходимо выбрать причину обращения"
                    ,alertHere.getFirst(),space);
        }
        if(object.isEmpty()) {
            alertHere = ViewHelper.attention(object
                    ,"Не заполнено поле Объект"
                    ,alertHere.getFirst(),space);
        }
        if(address.isEmpty()) {
            alertHere = ViewHelper.attention(address
                    ,"Не заполнено поле Адрес объекта"
                    ,alertHere.getFirst(),space);
        }
        if(specification.isEmpty() && (demandType.getValue().getId().equals(DemandType.TO150))) {
            alertHere = ViewHelper.attention(specification
                    ,"Не заполнено поле Характер нагрузки}"
                    ,alertHere.getFirst(),space);
        }
        // если выбрана причина подключения
        // если текущая мощность видима и не указана или равна 0 и причина - увеличение,
        // то поле надо зополнить
        if(reason.getValue() != null &&
                ((powerCurrent.isEmpty() || powerCurrent.getValue() == 0.0) &&
                    powerCurrent.isVisible() && (reason.getValue().getId() == 2L))) {
                alertHere = ViewHelper.attention(powerCurrent
                        ,"При увеличении мощности нужно указать ранее присоединённую..."
                        ,alertHere.getFirst(),space);

        }
        // если запрас мощности виден и не заполнен или равен 0,
        // то поле надо заполнить
        if ((powerDemand.isEmpty() || powerDemand.getValue() == 0.0)
                && powerDemand.isVisible() && !powerDemand.isReadOnly()) {
            alertHere = ViewHelper.attention(powerDemand
                    , "Не заполнено поле Мощность..."
                    ,alertHere.getFirst(),space);
        }
        // если максимальная мощность не 0,
        // то проверяем чтобы не превышала максимум для типа заявки
        if ((!powerMaximum.isEmpty() || powerMaximum.getValue() != 0.0)
                && powerMaximum.getValue() > this.maxPower) {
            alertHere = ViewHelper.attention(powerDemand
                    ,"Максимальная мощность превышает допустимую..."
                    ,alertHere.getFirst(),space);
        }
        if(voltage.isVisible() && !voltage.isReadOnly() &&
                voltage.getValue()==null){
            alertHere = ViewHelper.attention(voltage
                    ,"Необходимо выбрать класс напряжения"
                    ,alertHere.getFirst(),space);
        }
        if(voltageIn.isVisible() && !voltageIn.isReadOnly() &&
                voltageIn.getValue()==null){
            alertHere = ViewHelper.attention(voltageIn
                    ,"Необходимо выбрать уровень напряжения на вводе"
                    ,alertHere.getFirst(),space);
        }
        if(garant.getValue() == null) {
            alertHere = ViewHelper.attention(garant
                    ,"Необходимо выбрать гарантирующего поставщика"
                    ,alertHere.getFirst(),space);
        }
        if(garantText.isVisible() && garantText.isEmpty()) {
            alertHere = ViewHelper.attention(garantText
                    ,"Необходимо указать гарантирующего поставщика"
                    ,alertHere.getFirst(),space);
        }
        // если есть ошибки в полях
        if(alertHere.getFirst() != null) {
            alertHere.getFirst().focus();
            return false;
        }
        if(editPnt > 0) {
            pointsLayout.setDemand(demand);
            if(!Boolean.TRUE.equals(pointsLayout.saveEdited())) return false;
        }
        if(editExp > 0) {
            expirationsLayout.setDemand(demand);
            if (!Boolean.TRUE.equals(expirationsLayout.saveEdited())) return false;
        }
        // связываем значения полей страницы и объекта заявки
        if (!binderDemand.validate().getValidationErrors().isEmpty()) {
            List<ValidationResult> validationResults = binderDemand.validate().getValidationErrors();
            for (ValidationResult validationResult : validationResults) {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText(String.format("Ошибка %s", validationResult.getErrorMessage()));
                notification.setPosition(Notification.Position.BOTTOM_START);
                notification.setDuration(3000);
                notification.open();
            }
            return false;
        } else {
            binderDemand.writeBeanIfValid(demand);
            return true;
        }
    }

    protected void setReadOnly(Boolean readOnly) {
         AbstractField[] fields = {
                demander, typeDemander, delegate
                 , inn, innDate, ogrn, contact, meEmail
                 , passportSerries, passportNumber, passportIssued
                 , addressRegistration, addressActual, addressEquals, privilegeNot
                 , reason, object, address, specification
                 , countPoints, powerDemand, powerCurrent, voltage, safety, period
                 , contract, countTransformations, countGenerations
                 , techminGeneration, reservation
                 , plan, garant, garantText
        };
        for(AbstractField field : fields) {
            field.setReadOnly(readOnly);
        }
        privilegeLayout.setReadOnly(readOnly);
        if(demand!=null) {
            if(demand.getMeEmail()==null || demand.getMeEmail().isEmpty()) meEmail.setReadOnly(false);
            if(demand.getInn()==null || demand.getInn().isEmpty()) inn.setReadOnly(false);
            if(demand.getOgrn()==null || demand.getOgrn().isEmpty()) ogrn.setReadOnly(false);
            if(!demand.isPrivilegeNot() && !demand.isPrivilege()) {
                privilegeNot.setReadOnly(false);
                privilegeLayout.setReadOnly(false);
            }
        }
    }

    protected void populateForm(Demand value) {
        this.demand = value;
        binderDemand.readBean(this.demand);
        generalBinder.readBean(null);
        demandType.setReadOnly(true);
        createdate.setReadOnly(true);
        if(value != null) {
            if(typeDemander.isVisible() && (value.getTypeDemander() != null)) {
                    typeDemander.setValue(value.getTypeDemander());
            }
            demandId.setValue(demand.getId().toString());
            filesLayout.findAllByDemand(demand);
            notesLayout.findAllByDemand(demand);
            historyLayout.findAllByDemand(demand);
            switch (demand.getStatus().getState()) {
                case ADD -> {
                    setReadOnly(true);
                    if(pointsLayout != null)
                        pointsLayout.setReadOnly();
                }
                case NOTE -> {
                    setReadOnly(true);
                    if(filesLayout != null)
                        filesLayout.setReadOnly();
                    if(pointsLayout != null)
                        pointsLayout.setReadOnly();
                }
                case FREEZE -> {
                    setReadOnly(true);
                    if(filesLayout != null)
                        filesLayout.setReadOnly();
                    if(pointsLayout != null)
                        pointsLayout.setReadOnly();
                    if(notesLayout != null)
                        notesLayout.setReadOnly();
                }
                default -> setReadOnly(false);
            }
            if(addressActual.getValue().equals(addressRegistration.getValue())
                && !addressRegistration.isEmpty()) {
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
        if(editPnt <= 0 && editExp <= 0) {
            save.setEnabled(true);
            attentionLabel.setText("");
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User currentUser;
        Role role = Role.ANONYMOUS;
        Optional<Long> id = event.getRouteParameters().getLong(DEMAND_ID);
        currentUser = userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
        if(currentUser != null) {
            if(currentUser.getRoles().contains(Role.USER))
                role = Role.USER;
            if(currentUser.getRoles().contains(Role.GARANT))
                role = Role.GARANT;
            if(currentUser.getRoles().contains(Role.SALES))
                role = Role.SALES;
            if(currentUser.getRoles().contains(Role.ADMIN))
                role = Role.ADMIN;
        }
        switch (role) {
            case USER -> client = 1;
            case SALES -> client = 2;
            case GARANT -> client = 2;
            case ADMIN -> client = 0;
            default -> client = 1;
        }
        filesLayout.setClient(client);
        notesLayout.setClient(client);
        if(expirationsLayout != null)
            expirationsLayout.setClient(client);
        typeDemander.setReadOnly(false);
        if (id.isPresent()) {
            Notification notification = new Notification();
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.BOTTOM_START);
            notification.setDuration(3000);

            Optional<Demand> demandFromBackend = demandService.get(id.get());
            if (demandFromBackend.isPresent()) {
                if(role == Role.ADMIN) {
                    setReadOnly(false);
                    filesLayout.setDeleteVisible(true);
                    populateForm(demandFromBackend.get());
                } else if (demandFromBackend.get().getUser().equals(currentUser)
                        || ((role == Role.GARANT || role == Role.SALES)  &&
                                demandFromBackend.get().getGarant().getId()>0L)) {
                    populateForm(demandFromBackend.get());
                    if((role == Role.GARANT || role == Role.SALES)) {
                        setReadOnly(true);
                        expirationsLayout.setReadOnly();
                        typeDemander.setReadOnly(true);
                        filesLayout.setDeleteVisible(false);
                    }
                } else {
                    notification.setText(String.format("Заявка с ID = %d не Ваша", id.get()));
                    notification.open();
                    filesLayout.setDeleteVisible(false);
                    clearForm();
                }
            } else {
                notification.setText(String.format("Заявка с ID = %d не найдена", id.get()));
                notification.open();
                clearForm();
            }
        }

        demanderList = createList(currentUser, ViewHelper.FieldName.DEMANDER);
        demander.setItems(demanderList);
        contactList = createList(currentUser, ViewHelper.FieldName.CONTACT);
        contact.setItems(contactList);
        meEmailList = createList(currentUser, ViewHelper.FieldName.MEEMAIL);
        meEmail.setItems(meEmailList);
        innList = createList(currentUser, ViewHelper.FieldName.INN);
        inn.setItems(innList);
        ogrnList = createList(currentUser, ViewHelper.FieldName.OGRN);
        ogrn.setItems(ogrnList);
        passportSerriesList = createList(currentUser, ViewHelper.FieldName.PASPORTSERIES);
        passportSerries.setItems(passportSerriesList);
        passportNumberList = createList(currentUser, ViewHelper.FieldName.PASPORTNUMBER);
        passportNumber.setItems(passportNumberList);
        passportIssuedList = createList(currentUser, ViewHelper.FieldName.PASPORTISSUED);
        passportIssued.setItems(passportNumberList);
        addressRegistrationList = createList(currentUser, ViewHelper.FieldName.ADDRESSREGISTRATION);
        addressRegistration.setItems(addressRegistrationList);

        if(this.demand!=null){
            demander.setValue(demand.getDemander());
            contact.setValue(demand.getContact());
            meEmail.setValue(demand.getMeEmail());
            inn.setValue(demand.getInn());
            ogrn.setValue(demand.getOgrn());
            passportSerries.setValue(demand.getPassportSerries());
            passportNumber.setValue(demand.getPassportNumber());
            passportIssued.setValue(demand.getPassportIssued());
            addressRegistration.setValue(demand.getAddressRegistration());
            filesLayout.setDemand(demand);
        }
    }
    public void setPowerMaximum(Double powerMax) {
        powerMaximum.setValue(powerMax);
    }

    // создает список использованых ранее значений для поля
    private List<String> createList(User user, ViewHelper.FieldName fieldName){
        List<String> list = new ArrayList<>();
        List<Demand> demandList = this.demandService.findAllByUser(user);
        for(Demand dem : demandList){
            switch (fieldName){
                case DEMANDER:
                    if(dem.getDemander()!=null && (!list.contains(dem.getDemander())))
                            list.add(dem.getDemander());
                    break;
                case DELEGATE:
                    if(dem.getDelegate()!=null && (!list.contains(dem.getDelegate())))
                            list.add(dem.getDelegate());
                    break;
                case INN:
                    if(dem.getInn()!=null && (!list.contains(dem.getInn())))
                            list.add(dem.getInn());
                    break;
                case OGRN:
                    if(dem.getOgrn()!=null && (!list.contains(dem.getOgrn())))
                            list.add(dem.getOgrn());
                    break;
                case CONTACT:
                    if(dem.getContact()!=null && (!list.contains(dem.getContact())))
                            list.add(dem.getContact());
                    if(user.getContact()!=null && (!list.contains(user.getContact())))
                            list.add(user.getContact());
                    break;
                case MEEMAIL:
                    if(dem.getMeEmail()!=null && (!list.contains(dem.getMeEmail())))
                            list.add(dem.getMeEmail());
                    if(user.getEmail()!=null && (!list.contains(user.getEmail())))
                            list.add(user.getEmail());
                    break;
                case PASPORTSERIES:
                    if(dem.getPassportSerries()!=null &&
                            (!list.contains(dem.getPassportSerries())))
                            list.add(dem.getPassportSerries());
                    break;
                case PASPORTNUMBER:
                    if(dem.getPassportNumber()!=null &&
                            (!list.contains(dem.getPassportNumber())))
                            list.add(dem.getPassportNumber());
                    break;
                case PASPORTISSUED:
                    if(dem.getPassportIssued()!=null &&
                            (!list.contains(dem.getPassportIssued())))
                            list.add(dem.getPassportIssued());
                    break;
                case ADDRESSREGISTRATION:
                    if(dem.getAddressRegistration()!=null &&
                            (!list.contains(dem.getAddressRegistration())))
                            list.add(dem.getAddressRegistration());
                    break;
            }
        }
        switch(fieldName){
            case DEMANDER:
                if(user.getFio()!=null && (!list.contains(user.getFio())))
                        list.add(user.getFio());
                break;
            case CONTACT:
                if(user.getContact()!=null && (!list.contains(user.getContact())))
                        list.add(user.getContact());
                break;
            case MEEMAIL:
                if(user.getEmail()!=null && (!list.contains(user.getEmail())))
                        list.add(user.getEmail());
                break;

        }
        return list;
    }

    public void setPrivilegeNot(){
        privilegeNot.setValue(false);
    }
}

