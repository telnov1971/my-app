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
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Route(value = "demandtemporary/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandtemporary")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор заявки на временное подключение")
public class DemandEditTemporary extends GeneralForm implements BeforeEnterObserver {
    @Value("${upload.path.windows}")
    private String uploadPathWindows;
    @Value("${upload.path.linux}")
    private String uploadPathLinux;
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

    public DemandEditTemporary(DemandService demandService,
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
        this.fileStoredService = fileStoredService;
        this.MaxPower = 1000000000.0;
        demandType.setValue(demandTypeService.findById(DemandType.TEMPORARY).get());

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

        Component fields[] = {inn, innDate, powerDemand, powerCurrent,
                powerMaximum, voltage, safety, specification, period, contract};
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
                populateForm(demandFromBackend.get());
            } else {
                //Notification.show(String.format("The requested demand was not found, ID = %d", demandId.get()), 3000,
                        //Notification.Position.BOTTOM_START);
                clearForm();
            }
        }
    }

    public void populateForm(Demand value) {
        this.demand = value;
        binderDemand.readBean(this.demand);
        generalBinder.readBean(null);
        if(value != null) {
            demandType.setReadOnly(true);
            createdate.setReadOnly(true);
            if(pointService.findAllByDemand(demand).isEmpty()) {
                point = new Point();
            } else {
                point = pointService.findAllByDemand(demand).get(0);
            }
            filesLayout.findAllByDemand(demand);
        }
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
        generalBinder.readBean(null);
        populateForm(null);
    }

}
