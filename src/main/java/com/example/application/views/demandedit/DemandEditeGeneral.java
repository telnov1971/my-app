package com.example.application.views.demandedit;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.demandlist.DemandList;
import com.example.application.views.main.MainView;
import com.example.application.views.support.ExpirationsLayout;
import com.example.application.views.support.FilesLayout;
import com.example.application.views.support.GeneralForm;
import com.example.application.views.support.PointsLayout;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;

import java.io.IOException;

@Route(value = "demandreciver/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandreciver")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор заявки на энергопринимающие устройства")
public class DemandEditeGeneral extends GeneralForm {
    private HorizontalLayout buttonBar = new HorizontalLayout();
    private Button save = new Button("Сохранить");
    private Button reset = new Button("Отменить");

    private final FileStoredService fileStoredService;
    private final UserService userService;
    private FilesLayout filesLayout;

    private PointsLayout pointsLayout;
    private ExpirationsLayout expirationsLayout;

    public DemandEditeGeneral(DemandService demandService,
                              DemandTypeService demandTypeService,
                              StatusService statusService,
                              GarantService garantService,
                              PointService pointService,
                              GeneralService generalService,
                              ExpirationService expirationService,
                              UserService userService,
                              VoltageService voltageService,
                              SafetyService safetyService,
                              PlanService planService,
                              PriceService priceService,
                              SendService sendService,
                              FileStoredService fileStoredService,
                              Component... components) {
        super(demandService,demandTypeService,statusService,garantService,
                pointService,generalService,voltageService,
                safetyService,planService,priceService,sendService,userService,
                components);
        this.userService = userService;
        this.fileStoredService = fileStoredService;
        this.MaxPower = 1000000000.0;
        demandType.setValue(demandTypeService.findById(DemandType.GENERAL).get());

        filesLayout = new FilesLayout(this.fileStoredService
                , voltageService
                , safetyService);

        pointsLayout = new PointsLayout(pointService
                ,voltageService
                ,safetyService);

        expirationsLayout = new ExpirationsLayout(expirationService,safetyService);

        save.addClickListener(event -> {
            if(save()) UI.getCurrent().navigate(DemandList.class);
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

        Component fields[] = {inn, innDate, countPoints, accordionPoints, specification, countTransformations,
                countGenerations, techminGeneration, reservation, accordionExpiration};
        for(Component field : fields){
            field.setVisible(true);
        }

        buttonBar.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonBar.setSpacing(true);
        reset.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonBar.add(save,reset);

        accordionPoints.add("Точки подключения", this.pointsLayout);
        accordionExpiration.add("Этапы выполнения работ",this.expirationsLayout);
        add(formDemand, filesLayout, buttonBar);
    }

    @Override
    public void populateForm(Demand value) {
        this.demand = value;
        binderDemand.readBean(this.demand);
        generalBinder.readBean(null);
        demandType.setReadOnly(true);
        createdate.setReadOnly(true);
        if(value != null) {
            if(generalService.findAllByDemand(demand).isEmpty()) {
                general = new General();
            } else {
                general = generalService.findAllByDemand(demand).get(0);
            }
            pointsLayout.findAllByDemand(demand);
            filesLayout.findAllByDemand(demand);
            expirationsLayout.findAllByDemand(demand);
        }
        generalBinder.readBean(general);
    }
    public boolean save() {
        if(!super.save() || (binderDemand.validate().getValidationErrors().size() > 0)) return false;
        generalBinder.writeBeanIfValid(general);
        general.setDemand(demand);
        generalService.update(this.general);
        pointsLayout.setDemand(demand);
        pointsLayout.savePoints();
        filesLayout.setDemand(demand);
        filesLayout.saveFiles();
        expirationsLayout.setDemand(demand);
        expirationsLayout.saveExpirations();
        return true;
    }
    @Override
    public void clearForm() {
        binderDemand.readBean(null);
        pointBinder.readBean(null);
        generalBinder.readBean(null);
        populateForm(null);
    }
}
