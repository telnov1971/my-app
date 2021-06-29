package com.example.application.views.demandedit;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.demandlist.DemandList;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

@Route(value = "demandto15/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandto15")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор заявки до 15 кВт")
public class DemandEditTo15 extends Div implements BeforeEnterObserver {

    @Value("${upload.path.windows}")
    private String uploadPathWindows;
    @Value("${upload.path.linux}")
    private String uploadPathLinux;


    private final String DEMAND_ID = "demandID";
    private final VoltageService voltageService;
    private final SafetyService safetyService;

    private FormLayout formDemand = new FormLayout();
    private BeanValidationBinder<Demand> binderDemand = new BeanValidationBinder<>(Demand.class);
    private Demand demand = new Demand();

    private HorizontalLayout buttonBar = new HorizontalLayout();

    private DatePicker createdate;
    private Select<DemandType> demandType;
    private TextArea demander;
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

    private Select<Plan> plan;
    private Select<Price> price;
    private Select<Garant> garant;

    private Select<Status> status;

    private Binder<Point> binderPoints = new Binder<>(Point.class);

    private Button save = new Button("Сохранить");
    private Button reset = new Button("Отменить");

    MultiFileBuffer buffer = new MultiFileBuffer();
    Upload multiUpload = new Upload(buffer);
    private String originalFileName;
    private String mimeType;

    private final DemandService demandService;
    private final DemandTypeService demandTypeService;
    private final StatusService statusService;
    private final GarantService garantService;
    private final PointService pointService;
    private final PlanService planService;
    private final PriceService priceService;

    public DemandEditTo15(DemandService demandService,
                          DemandTypeService demandTypeService,
                          StatusService statusService,
                          GarantService garantService,
                          PointService pointService,
                          VoltageService voltageService,
                          SafetyService safetyService,
                          PlanService planService, PriceService priceService, Component... components) {
        super(components);
        this.demandService = demandService;
        this.demandTypeService = demandTypeService;
        this.statusService = statusService;
        this.garantService = garantService;
        this.pointService = pointService;
        this.voltageService = voltageService;
        this.safetyService = safetyService;
        this.planService = planService;
        this.priceService = priceService;

        createdate = new DatePicker("Дата создания");
        createdate.setValue(LocalDate.now());
        createdate.setReadOnly(true);

        demandType = new Select<>();
        demandType.setLabel("Тип заявки");
        List<DemandType> demandTypeList = demandTypeService.findAll();
        demandType.setItemLabelGenerator(DemandType::getName);
        demandType.setItems(demandTypeList);
        demandType.setValue(demandTypeService.findById(demandTypeService.TO15).get());
        demandType.setReadOnly(true);

        demander = new TextArea("Заявитель");
        contact = new TextField("Контактный телефон");
        passportSerries = new TextField("Паспорт серия");
        passportNumber = new TextField("Паспорт номер");
        pasportIssued = new TextArea("Паспорт выдан");
        addressRegistration = new TextField("Адрес регистрации");
        addressActual = new TextField("Адрес фактический");
        reason = new TextArea("Причина подключения");
        object = new TextArea("Объект");
        address = new TextArea("Адрес объекта");

        powerDemand = new NumberField("Мощность заявленная");
        powerCurrent = new NumberField("Мощность текущая");
        powerMaximum = new NumberField("Мощность максимальная");
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

        price = new Select<>();
        price.setLabel("Ценовая категория");
        List<Price> pricetList = priceService.findAll();
        price.setItemLabelGenerator(Price::getName);
        price.setItems(pricetList);

        status = new Select<>();
        status.setLabel("Статус");
        List<Status> statusList = statusService.findAll();
        status.setItemLabelGenerator(Status::getName);
        status.setItems(statusList);
        status.setValue(statusService.findById(1L).get());
        status.setReadOnly(true);

        binderDemand.bindInstanceFields(this);
        binderPoints.bindInstanceFields(this);

        save.addClickListener(event -> {
            binderDemand.writeBeanIfValid(demand);
            demandService.update(this.demand);

            binderPoints.writeBeanIfValid(point);
            point.setDemand(demand);
            pointService.update(this.point);

            UI.getCurrent().navigate(DemandList.class);
        });

        reset.addClickListener(event -> {
            // clear fields by setting null
            binderDemand.readBean(null);
            binderPoints.readBean(null);
            UI.getCurrent().navigate(DemandList.class);
        });

        formDemand.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1em", 1),
                new FormLayout.ResponsiveStep("40em", 2),
                new FormLayout.ResponsiveStep("50em", 3),
                new FormLayout.ResponsiveStep("68em", 4)
        );

        Component[] fields = new Component[]{
                createdate,demandType,status
                ,demander,contact
                ,passportSerries,passportNumber,pasportIssued
                ,addressRegistration,addressActual,
                reason,object,address
                ,powerDemand,powerCurrent,powerMaximum,voltage,safety
                ,garant};
        formDemand.add(fields);
        formDemand.setColspan(demander, 2);
        formDemand.setColspan(reason, 2);
        formDemand.setColspan(object, 2);
        formDemand.setColspan(address, 2);

        buttonBar.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonBar.setSpacing(true);
        reset.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonBar.add(save,reset);

        //createPointsLayout();
        createUploadLayout();

        this.getElement().getStyle().set("margin","15px");
        add(formDemand,multiUpload,buttonBar);
    }

    private void createUploadLayout() {
        Div output = new Div();
        //Upload upload = new Upload(this::receiveUpload);

        multiUpload.addSucceededListener(event -> {

            this.originalFileName = event.getFileName();
            String file2 = "";
            String uploadPath = new String();
            String osName = System.getProperty("os.name");
            if(osName.contains("Windows")) uploadPath = uploadPathWindows;
            if(osName.contains("Linux")) uploadPath = uploadPathLinux;
            //File uploadDir = new File(uploadPath);

            String uuidFile = UUID.randomUUID().toString();
            if(this.originalFileName.lastIndexOf(".") != -1 &&
                    this.originalFileName.lastIndexOf(".") != 0)
                // то вырезаем все знаки после последней точки в названии файла, то есть ХХХХХ.txt -> txt
                file2 = this.originalFileName.substring(this.originalFileName.lastIndexOf(".")+1);
            String resultFilename = uploadPath + uuidFile + "." + file2;

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(resultFilename);
                InputStream inputStream = buffer.getInputStream(event.getFileName());
                fileOutputStream.write(inputStream.readAllBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //file.deleteOnExit();
            //buffer.receiveUpload(this.originalFileName, event.getMIMEType());
            output.removeAll();
            output.add(new Text("Uploaded: "+originalFileName+" to "+ resultFilename+ " | Type: "+mimeType));
            //output.add(new Image(new StreamResource(this.originalFileName,this::loadFile),"Uploaded image"));

            //showOutput(event.getFileName(), output);
        });
        multiUpload.addFailedListener(event -> {
            output.removeAll();
            output.add(new Text("Upload failed: " + event.getReason()));
        });
        multiUpload.addFileRejectedListener(event -> {
            showOutput(event.getErrorMessage(), output);
        });

        //upload.setAutoUpload(false);
        multiUpload.setUploadButton(new Button("Загрузить файл"));
        add(multiUpload, output);
    }

    /* Load a file from local filesystem.
     *
    public InputStream loadFile() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Failed to create InputStream for: '" + this.file.getAbsolutePath(), e);
        }
        return null;
    }

    //Receive a uploaded file to a file.

    @Override
    public OutputStream receiveUpload(String originalFileName, String MIMEType) {
        this.originalFileName = originalFileName;
        this.mimeType = MIMEType;
        try {
            String file2 = "";
            String uploadPath = new String();
            String osName = System.getProperty("os.name");
            if(osName.contains("Windows")) uploadPath = uploadPathWindows;
            if(osName.contains("Linux")) uploadPath = uploadPathLinux;
            File uploadDir = new File(uploadPath);

            String uuidFile = UUID.randomUUID().toString();
            if(this.originalFileName.lastIndexOf(".") != -1 &&
                    this.originalFileName.lastIndexOf(".") != 0)
                // то вырезаем все знаки после последней точки в названии файла, то есть ХХХХХ.txt -> txt
                file2 = this.originalFileName.substring(this.originalFileName.lastIndexOf(".")+1);
            String resultFilename = uuidFile + "." + file2;

            this.file = new File(uploadPath + "/" + resultFilename);

            file.deleteOnExit();
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Failed to create InputStream for: '" + this.file.getAbsolutePath(), e);
        }
        return null;
    }*/


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> demandId = event.getRouteParameters().getLong(DEMAND_ID);
        if (demandId.isPresent()) {
            Optional<Demand> demandFromBackend = demandService.get(demandId.get());
            if (demandFromBackend.isPresent()) {
                populateForm(demandFromBackend.get());
            } else {
                //Notification.show(String.format("The requested demand was not found, ID = %d", demandId.get()), 3000,
                        //Notification.Position.BOTTOM_START);
                clearForm();
            }
        }
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Demand value) {
        demand = value;
        binderDemand.readBean(this.demand);
        if(value != null) {
            demandType.setReadOnly(true);
            createdate.setReadOnly(true);
            point = pointService.findAllByDemand(demand).get(0);
        }
        binderPoints.readBean(this.point);
    }

    private void showOutput(String text,
                            HasComponents outputContainer) {
        HtmlComponent p = new HtmlComponent(Tag.P);
        p.getElement().setText(text);
        outputContainer.add(p);
    }
}
