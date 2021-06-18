package com.example.application.views.demandedit;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.demandlist.DemandList;
import com.example.application.views.main.MainView;
import com.example.application.views.masterdetail.MasterDetailView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.hibernate.service.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    private DatePicker createdate;
    private Select<DemandType> demandType;
    private TextField object;
    private TextField address;
    private Select<Garant> garant;
    private Select<Status> status;
    private List<Point> points = new ArrayList<>();
    private Grid<Point> pointGrid = new Grid<>(Point.class);

    private Button save = new Button("Сохранить");
    private Button reset = new Button("Отменить");

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

        pointGrid.setHeightByRows(true);
        points.add(new Point(0.0,
                0.0,
                voltageService.findById(1L).get(),
                safetyService.findById(1L).get()));
        this.pointGrid.addColumn(Point::getPowerDemanded).setHeader("Заявленная");
        this.pointGrid.addColumn("safety.name").setHeader("Надёжность");
        //pointGrid.setItems(points);
        //pointGrid.addColumn(Point::getPowerDemanded).setAutoWidth(true).setHeader("Мощность заявленная");
        ////Grid.Column<Point> firstNameColumn = pointGrid.addColumn(Point::getPowerDemanded).setHeader("Мощность заявленная");
        //pointGrid.addColumn(Point::getPowerCurrent).setAutoWidth(true).setHeader("Мощность текущая");
        //pointGrid.addColumn(Point::getPowerMaximum).setAutoWidth(true).setHeader("Мощность максимальная");
        //pointGrid.addColumn("safety.name").setAutoWidth(true).setHeader("Категория надёжности");
        //pointGrid.addColumn("voltage.name").setAutoWidth(true).setHeader("Уровень напряжения");

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

        add(formDemand, buttonBar, pointGrid);
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
        populateForm(null);
    }

    private void populateForm(Demand value) {
        this.demand = value;
        binder.readBean(this.demand);
        if(value != null) {
            this.demandType.setReadOnly(true);
            this.createdate.setReadOnly(true);
            this.points = this.pointService.findAllByDemand(demand);
            this.pointGrid.setItems(points);
            this.pointGrid.addColumn(Point::getPowerDemanded).setHeader("Заявленная");
            this.pointGrid.addColumn("safety.name").setHeader("Надёжность");
        }
    }
}
