package com.example.application.views.demandedit;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.demandlist.DemandList;
import com.example.application.views.main.MainView;
import com.example.application.views.support.FilesLayout;
import com.example.application.views.support.GeneralForm;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.*;

@Route(value = "demandto15/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandto15")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор заявки до 15 кВт")
public class DemandEditTo15 extends GeneralForm implements BeforeEnterObserver {
    @Value("${upload.path.windows}")
    protected String uploadPathWindows;
    @Value("${upload.path.linux}")
    protected String uploadPathLinux;
    public static String uploadPath = "";

    private final String DEMAND_ID = "demandID";
    private HorizontalLayout buttonBar = new HorizontalLayout();
    private Button save = new Button("Сохранить");
    private Button reset = new Button("Отменить");

    MultiFileBuffer buffer = new MultiFileBuffer();
    Upload multiUpload = new Upload(buffer);
    private String originalFileName;
    private final FileStoredService fileStoredService;
    private FilesLayout filesLayout;

    public DemandEditTo15(DemandService demandService,
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
                          FileStoredService fileStoredService,
                          Component... components) {
        super(demandService,demandTypeService,statusService,garantService,
                 pointService,generalService,expirationService,voltageService,
                 safetyService,planService,priceService,sendService,
                 components);
        // сервисы
        this.fileStoredService = fileStoredService;
        this.MaxPower = 15.0;
        demandType.setValue(demandTypeService.findById(DemandType.TO15).get());

        filesLayout = new FilesLayout(this.fileStoredService
                , voltageService
                , safetyService
                , uploadPathWindows
                , uploadPathLinux);

        save.addClickListener(event -> {
            save();

            filesLayout.setDemand(demand);
            filesLayout.saveFiles();

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

        Component fields[] = {powerDemand, powerCurrent,
                powerMaximum, voltage, safety};
        for(Component field : fields){
            field.setVisible(true);
        }

        buttonBar.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonBar.setSpacing(true);
        reset.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonBar.add(save,reset);

        add(formDemand,filesLayout,buttonBar);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> demandId = event.getRouteParameters().getLong(DEMAND_ID);
        if (demandId.isPresent()) {
            Optional<Demand> demandFromBackend = demandService.get(demandId.get());
            if (demandFromBackend.isPresent()) {
                demand = demandFromBackend.get();
                populateForm(demand);
                filesLayout.findAllByDemand(demand);
            } else {
                Notification.show(String.format("Заявка с ID = %d не найдена", demandId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                clearForm();
            }
        }
        uploadPath = "";
        String osName = System.getProperty("os.name");
        if(osName.contains("Windows")) uploadPath = uploadPathWindows;
        if(osName.contains("Linux")) uploadPath = uploadPathLinux;
    }
    public void populateForm(Demand value) {
        this.demand = value;
        binderDemand.readBean(this.demand);
        if(value != null) {
            demandType.setReadOnly(true);
            createdate.setReadOnly(true);
            if(pointService.findAllByDemand(demand).isEmpty()) {
                point = new Point();
            } else {
                point = pointService.findAllByDemand(demand).get(0);
            }
        }
        pointBinder.readBean(this.point);
        point = new Point();
        pointBinder.readBean(this.point);
    }
    public void save() {
        binderDemand.writeBeanIfValid(demand);
        demandService.update(this.demand);

        pointBinder.writeBeanIfValid(point);
        point.setDemand(demand);
        pointService.update(this.point);
        UI.getCurrent().navigate(DemandList.class);
    }

    public void clearForm() {
        binderDemand.readBean(null);
        pointBinder.readBean(null);
        //populateForm(null);
    }
}
