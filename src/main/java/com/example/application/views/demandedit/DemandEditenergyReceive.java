package com.example.application.views.demandedit;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.demandlist.DemandList;
import com.example.application.views.main.MainView;
import com.example.application.views.support.FilesLayout;
import com.example.application.views.support.GeneralForm;
import com.example.application.views.support.PointsLayout;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
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
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.spi.AfterLoadAction;
import org.hibernate.persister.entity.Loadable;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Route(value = "demandreciver/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandreciver")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор заявки на энергопринимающие устройства")
public class DemandEditenergyReceive extends Div implements BeforeEnterObserver {
    @Value("${upload.path.windows}")
    private String uploadPathWindows;
    @Value("${upload.path.linux}")
    private String uploadPathLinux;
    public static String uploadPath = "";

    private Demand demand = new Demand();
    private final DemandService demandService;
    private final DemandTypeService demandTypeService;
    private final StatusService statusService;
    private final GarantService garantService;
    private final PointService pointService;
    private final PlanService planService;
    private final PriceService priceService;
    private final VoltageService voltageService;
    private final SafetyService safetyService;
    private final SendService sendService;

    private final String DEMAND_ID = "demandID";
    private HorizontalLayout buttonBar = new HorizontalLayout();
    private Button save = new Button("Сохранить");
    private Button reset = new Button("Отменить");
    GeneralForm generalForm;

    MultiFileBuffer buffer = new MultiFileBuffer();
    Upload multiUpload = new Upload(buffer);
    private String originalFileName;
    private final FileStoredService fileStoredService;
    private FilesLayout filesLayout;

    private PointsLayout pointsLayout;

    public DemandEditenergyReceive(DemandService demandService
            , DemandTypeService demandTypeService
            , StatusService statusService
            , GarantService garantService
            , PointService pointService
            , VoltageService voltageService
            , SafetyService safetyService
            , PlanService planService
            , PriceService priceService
            , SendService sendService
            , FileStoredService fileStoredService
            , Component... components) {
        super(components);
        this.demandService = demandService;
        this.demandTypeService = demandTypeService;
        this.statusService = statusService;
        this.garantService = garantService;
        this.pointService = pointService;
        this.planService = planService;
        this.priceService = priceService;
        this.voltageService = voltageService;
        this.safetyService = safetyService;
        this.sendService = sendService;
        this.fileStoredService = fileStoredService;

        generalForm = new GeneralForm(demandService,demandTypeService,statusService
                ,garantService,pointService,voltageService,safetyService,planService
                ,priceService,sendService,DemandType.RECIVER);

        pointsLayout = new PointsLayout(pointService
                ,voltageService
                ,safetyService);

        filesLayout = new FilesLayout(this.fileStoredService
                , voltageService
                , safetyService
                , uploadPathWindows
                , uploadPathLinux);

        save.addClickListener(event -> {
            generalForm.save();

            pointsLayout.setDemand(demand);
            pointsLayout.savePoints();

            filesLayout.setDemand(demand);
            filesLayout.saveFiles();

            UI.getCurrent().navigate(DemandList.class);
        });

        reset.addClickListener(event -> {
            // clear fields by setting null
            pointsLayout.pointsClean();
            try {
                filesLayout.deleteFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
            UI.getCurrent().navigate(DemandList.class);
        });

        buttonBar.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonBar.setSpacing(true);
        reset.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonBar.add(save,reset);

        this.getElement().getStyle().set("margin","15px");
        add(generalForm
                ,pointsLayout
                ,filesLayout
                , buttonBar
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> demandId = event.getRouteParameters().getLong(DEMAND_ID);
        if (demandId.isPresent()) {
            Optional<Demand> demandFromBackend = demandService.get(demandId.get());
            if (demandFromBackend.isPresent()) {
                demand = demandFromBackend.get();
                generalForm.populateForm(demand);
                filesLayout.findAllByDemand(demand);
            } else {
                Notification.show(String.format("Заявка с ID = %d не найдена", demandId.get()), 3000,
                Notification.Position.BOTTOM_START);
                generalForm.clearForm();
            }
        }
        uploadPath = "";
        String osName = System.getProperty("os.name");
        if(osName.contains("Windows")) uploadPath = uploadPathWindows;
        if(osName.contains("Linux")) uploadPath = uploadPathLinux;
    }
}
