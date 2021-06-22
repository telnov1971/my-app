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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.internal.MessageDigestUtil;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.text.html.HTML;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Route(value = "demandto15/:demandID?", layout = MainView.class)
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор заявки")
public class DemandEditTo15 extends Div implements BeforeEnterObserver {
    private final String DEMAND_ID = "demandID";
    private final VoltageService voltageService;
    private final SafetyService safetyService;


    private FormLayout formDemand = new FormLayout();
    private BeanValidationBinder<Demand> binder = new BeanValidationBinder<>(Demand.class);
    private Demand demand = new Demand();
    private HorizontalLayout buttonBar = new HorizontalLayout();
    private VerticalLayout pointsLayout = new VerticalLayout();
    private HorizontalLayout pointsButtonLayout = new HorizontalLayout();

    private DatePicker createdate;
    private Select<DemandType> demandType;
    private TextField object;
    private TextField address;
    private Select<Garant> garant;
    private Select<Status> status;
    private List<Point> points = new ArrayList<>();
    private Grid<Point> pointGrid = new Grid<>(Point.class, false);
    private ListDataProvider<Point> pointDataProvider;
    private Binder<Point> binderPoints = new Binder<>(Point.class);
    private Editor<Point> editorPoints;

    private Button save = new Button("Сохранить");
    private Button reset = new Button("Отменить");

    MultiFileBuffer buffer = new MultiFileBuffer();
    Upload upload = new Upload(buffer);

    private final DemandService demandService;
    private final DemandTypeService demandTypeService;
    private final StatusService statusService;
    private final GarantService garantService;
    private final PointService pointService;

    public DemandEditTo15(DemandService demandService,
                          DemandTypeService demandTypeService,
                          StatusService statusService,
                          GarantService garantService,
                          PointService pointService,
                          VoltageService voltageService,
                          SafetyService safetyService,
                          Component... components) {
        super(components);
        this.demandService = demandService;
        this.demandTypeService = demandTypeService;
        this.statusService = statusService;
        this.garantService = garantService;
        this.pointService = pointService;
        this.voltageService = voltageService;
        this.safetyService = safetyService;

        createdate = new DatePicker("Дата создания");
        createdate.setValue(LocalDate.now());
        createdate.setReadOnly(true);

        demandType = new Select<>();
        demandType.setLabel("Тип заявки");
        List<DemandType> demandTypeList = demandTypeService.findAll();
        demandType.setItemLabelGenerator(DemandType::getName);
        demandType.setItems(demandTypeList);

        object = new TextField("Объект");

        address = new TextField("Адрес");

        garant = new Select<>();
        garant.setLabel("Гарантирующий поставщик");
        List<Garant> garantList = garantService.findAll();
        garant.setItemLabelGenerator(Garant::getName);
        garant.setItems(garantList);

        status = new Select<>();
        status.setLabel("Статус");
        List<Status> statusList = statusService.findAll();
        status.setItemLabelGenerator(Status::getName);
        status.setItems(statusList);


        binder.bindInstanceFields(this);
        /*
        binder.forField(createdate).bind(Demand::getCreatedate,Demand::setCreatedate);
        binder.forField(demandType).bind(Demand::getDemandType,Demand::setDemandType);
        binder.forField(object).bind(Demand::getObject,Demand::setObject);
        binder.forField(address).bind(Demand::getAddress,Demand::setAddress);
        binder.forField(garant).bind(Demand::getGarant,Demand::setGarant);
        binder.forField(status).bind(Demand::getStatus,Demand::setStatus);
        */
        save.addClickListener(event -> {
            binder.writeBeanIfValid(demand);
            demandService.update(this.demand);
            UI.getCurrent().navigate(DemandList.class);
        });

        reset.addClickListener(event -> {
            // clear fields by setting null
            binder.readBean(null);
            UI.getCurrent().navigate(DemandList.class);
        });

        Component[] fields = new Component[]{createdate,demandType,object,address,garant,status};
        formDemand.add(fields);
        buttonBar.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonBar.setSpacing(true);
        reset.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonBar.add(save,reset);

        createPointsLayout();
        createUploadLayout();

        add(formDemand, buttonBar, pointsLayout, upload);
    }

    private void createPointsLayout() {
        pointGrid.setHeightByRows(true);

        Grid.Column<Point> columnPowerDemand =
                pointGrid.addColumn(Point::getPowerDemand)
                        .setHeader("Мощ. заяв.")
                        .setAutoWidth(true);
        Grid.Column<Point> columnPowerCurrent =
                pointGrid.addColumn(Point::getPowerCurrent).
                        setAutoWidth(true).
                        setHeader("Мощ. тек. ");
        pointGrid.addColumn(Point::getPowerMaximum).
                setAutoWidth(true).
                setHeader("Мощ. мак. ");
        Grid.Column<Point> columnSafety =
                pointGrid.addColumn(point -> point.getSafety().getName())
                        .setAutoWidth(true)
                        .setHeader("Кат. надёж.");
        Grid.Column<Point> columnVoltage =
                pointGrid.addColumn(point -> point.getVoltage().getName())
                        .setAutoWidth(true)
                        .setHeader("Ур. напр. ");
        points.add(new Point());
        pointGrid.setItems(points);
        pointDataProvider = (ListDataProvider<Point>) pointGrid.getDataProvider();
        points.remove(points.size() - 1);

        Button addButton = new Button("Добавить точку", event -> {
            pointDataProvider.getItems().add(new Point(0.0,
                    0.0,
                    voltageService.findById(1L).get(),
                    safetyService.findById(1L).get()
            ));
            pointDataProvider.refreshAll();
            //pointGrid.getDataProvider().refreshAll();
        });

        Button removeButton = new Button("Удалить последнюю", event -> {
            this.points.remove(points.size() - 1);
            pointDataProvider.refreshAll();
            //pointGrid.getDataProvider().refreshAll();
        });

        editorPoints = pointGrid.getEditor();
        editorPoints.setBinder(binderPoints);
        editorPoints.setBuffered(true);

        NumberField fieldPowerDemand = new NumberField();
        fieldPowerDemand.setValue(1d);
        fieldPowerDemand.setHasControls(true);
        fieldPowerDemand.setMin(0);
        binderPoints.forField(fieldPowerDemand).bind("powerDemand");
        columnPowerDemand.setEditorComponent(fieldPowerDemand);

        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        Grid.Column<Point> editorColumn = pointGrid.addComponentColumn(points -> {
            Button edit = new Button("Редактировать");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editorPoints.editItem(points);
                fieldPowerDemand.focus();
            });
            edit.setEnabled(!editorPoints.isOpen());
            editButtons.add(edit);
            return edit;
        }).setAutoWidth(true);

        editorPoints.addOpenListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editorPoints.isOpen())));
        editorPoints.addCloseListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editorPoints.isOpen())));
        Button save = new Button("Сохранить", e -> editorPoints.save());
        save.addClassName("save");
        Button cancel = new Button("Отменить", e -> editorPoints.cancel());
        cancel.addClassName("cancel");
        Div divSave = new Div(save);
        Div divCancel = new Div(cancel);
        Div buttons = new Div(divSave, divCancel);
        editorColumn.setEditorComponent(buttons);


        pointsButtonLayout.add(addButton,removeButton);
        pointsLayout.add(pointGrid,pointsButtonLayout);
    }

    private void createUploadLayout() {
        Div output = new Div();
        /*
        upload.addSucceededListener(event -> {
            showOutput(event.getFileName(), output);
        });
        upload.addFileRejectedListener(event -> {
            showOutput(event.getErrorMessage(), output);
        });
        */
        add(upload, output);
    }

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
        points.add(new Point(0.0,
                0.0,
                voltageService.findById(1L).get(),
                safetyService.findById(1L).get()));
        populateForm(null);

    }

    private void populateForm(Demand value) {
        demand = value;
        binder.readBean(this.demand);
        if(value != null) {
            demandType.setReadOnly(true);
            createdate.setReadOnly(true);

            points = pointService.findAllByDemand(demand);
        }
/*
        pointGrid.addColumn(Point::getPowerDemanded).setHeader("Мощность заявленная");
        pointGrid.addColumn(Point::getPowerCurrent).setAutoWidth(true).setHeader("Мощность текущая");
        pointGrid.addColumn(Point::getPowerMaximum).setAutoWidth(true).setHeader("Мощность максимальная");
        pointGrid.addColumn(point -> point.getSafety().getName()).setAutoWidth(true).setHeader("Категория надёжности");
        pointGrid.addColumn(point -> point.getVoltage().getName()).setAutoWidth(true).setHeader("Уровень напряжения");
*/
        pointGrid.setItems(points);
        pointDataProvider = (ListDataProvider<Point>) pointGrid.getDataProvider();
    }

    private void showOutput(String text,
                            HasComponents outputContainer) {
        HtmlComponent p = new HtmlComponent(Tag.P);
        p.getElement().setText(text);
        outputContainer.add(p);
    }
}