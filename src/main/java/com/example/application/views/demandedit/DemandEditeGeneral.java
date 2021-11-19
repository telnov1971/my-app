package com.example.application.views.demandedit;

import com.example.application.data.entity.DType;
import com.example.application.data.entity.Demand;
import com.example.application.data.entity.DemandType;
import com.example.application.data.entity.General;
import com.example.application.data.service.*;
import com.example.application.views.main.MainView;
import com.example.application.views.support.ExpirationsLayout;
import com.example.application.views.support.GeneralForm;
import com.example.application.views.support.PointsLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "demandreciver/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandreciver")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Иные категории потребителей")
public class DemandEditeGeneral extends GeneralForm {

    private final PointsLayout pointsLayout;
    private final ExpirationsLayout expirationsLayout;

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
                              NoteService noteService,
                              Component... components) {
        super(reasonService, demandService,demandTypeService,statusService,garantService,
                pointService,generalService,voltageService,
                safetyService,planService,priceService,sendService,userService,
                historyService, fileStoredService, DType.GENERAL,noteService,components);
        this.MaxPower = 1000000000.0;
        demandType.setValue(demandTypeService.findById(DemandType.GENERAL).get());

        pointsLayout = new PointsLayout(pointService
                ,voltageService
                ,safetyService
                ,historyService);

        expirationsLayout = new ExpirationsLayout(expirationService,safetyService, historyService);

        Component[] fields = {inn, innDate,
                passportSerries,passportNumber,pasportIssued,
                addressRegistration,addressActual,
                accordionPoints, specification, countTransformations,
                countGenerations, techminGeneration, reservation, accordionExpiration};
        for(Component field : fields){
            field.setVisible(true);
        }

        accordionPoints.add("Точки подключения", this.pointsLayout);
        accordionExpiration.add("Этапы выполнения работ",this.expirationsLayout);
        powerMaximum.addValueChangeListener(e -> {
            expirationsLayout.setPowerMax(powerMaximum.getValue());
        });
        add(formDemand,filesLayout,notesLayout,buttonBar,accordionHistory);
    }

    @Override
    public void populateForm(Demand value) {
        super.populateForm(value);
        if(value != null) {
            if(generalService.findAllByDemand(demand).isEmpty()) {
                general = new General();
            } else {
                general = generalService.findAllByDemand(demand).get(0);
            }
            pointsLayout.findAllByDemand(demand);
            expirationsLayout.findAllByDemand(demand);
            switch(demand.getStatus().getState()){
                case NOTE:
                case FREEZE: {
                    expirationsLayout.setReadOnly();
                } break;
            }
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
        expirationsLayout.setDemand(demand);
        expirationsLayout.saveExpirations();

//        filesLayout.setDemand(demand);
//        filesLayout.saveFiles();
//        notesLayout.setDemand(demand);
//        notesLayout.saveNotes();
        return true;
    }
}
