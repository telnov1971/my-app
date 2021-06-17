package com.example.application.views.demandedit;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.main.MainView;
import com.example.application.views.masterdetail.MasterDetailView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
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
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Optional;

@Route(value = "demandto15/:demandID?", layout = MainView.class)
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
public class DemandEditTo15 extends Div implements BeforeEnterObserver {
    private final String DEMAND_ID = "demandID";

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

    private Button save = new Button("Сохранить");
    private Button reset = new Button("Отменить");

    private final DemandService demandService;
    private final DemandTypeService demandTypeService;
    private final StatusService statusService;
    private final GarantService garantService;

    public DemandEditTo15(DemandService demandService, DemandTypeService demandTypeService, StatusService statusService, GarantService garantService, Component... components) {
        super(components);
        this.demandService = demandService;
        this.demandTypeService = demandTypeService;
        this.statusService = statusService;
        this.garantService = garantService;

        /*
        * createdate
        * demandType.name
        * object
        * address
        * garant.name
        * status.name
        * */

        createdate = new DatePicker("Дата создания");

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
            try {
                binder.writeBean(demand);
                demandService.update(this.demand);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
            //binder.writeBeanIfValid(demand);
        });

        reset.addClickListener(event -> {
            // clear fields by setting null
            binder.readBean(null);
        });

        formDemand.add(createdate,demandType,object,address,garant,status);
        buttonBar.add(save,reset);
        add(formDemand, buttonBar);
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
                // when a row is selected but the data is no longer available,
                // refresh grid
                clearForm();
                //event.forwardTo(DemandEditTo15.class);
            }
        }
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Demand value) {
        this.demand = value;
        binder.readBean(this.demand);
    }
}
