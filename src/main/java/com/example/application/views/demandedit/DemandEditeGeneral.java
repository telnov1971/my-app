package com.example.application.views.demandedit;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.main.MainView;
import com.example.application.views.support.ExpirationsLayout;
import com.example.application.views.support.GeneralForm;
import com.example.application.views.support.PointsLayout;
import com.vaadin.flow.component.*;
import com.vaadin.flow.router.*;

@Route(value = "demandreciver/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandreciver")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор заявки на энергопринимающие устройства")
public class DemandEditeGeneral extends GeneralForm {
    private final UserService userService;

    private PointsLayout pointsLayout;
    private ExpirationsLayout expirationsLayout;

    public DemandEditeGeneral(ReasonService reasonService,
                              DemandService demandService,
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
                              HistoryService historyService,
                              Component... components) {
        super(reasonService, demandService,demandTypeService,statusService,garantService,
                pointService,generalService,voltageService,
                safetyService,planService,priceService,sendService,userService,
                historyService, fileStoredService,false, components);
        this.userService = userService;
        this.MaxPower = 1000000000.0;
        demandType.setValue(demandTypeService.findById(DemandType.GENERAL).get());

        pointsLayout = new PointsLayout(pointService
                ,voltageService
                ,safetyService);

        expirationsLayout = new ExpirationsLayout(expirationService,safetyService);

        Component fields[] = {inn, innDate,
                passportSerries,passportNumber,pasportIssued,
                addressRegistration,addressActual,
                accordionPoints, specification, countTransformations,
                countGenerations, techminGeneration, reservation, accordionExpiration};
        for(Component field : fields){
            field.setVisible(true);
        }

        accordionPoints.add("Точки подключения", this.pointsLayout);
        accordionExpiration.add("Этапы выполнения работ",this.expirationsLayout);
        add(formDemand, filesLayout, buttonBar, accordionHistory);
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
            historyLayout.findAllByDemand(demand);
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
}
