package com.example.application.views.demandedit;

import com.example.application.data.entity.DType;
import com.example.application.data.entity.Demand;
import com.example.application.data.entity.DemandType;
import com.example.application.data.entity.Point;
import com.example.application.data.service.*;
import com.example.application.views.main.MainView;
import com.example.application.views.support.ExpirationsLayout;
import com.example.application.views.support.GeneralForm;
import com.example.application.views.support.NotesLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "demandto150/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandto150")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор заявки до 150 кВт")
public class DemandEditTo150 extends GeneralForm {
    private final UserService userService;
    private ExpirationsLayout expirationsLayout;
    private NotesLayout notesLayout;

    public DemandEditTo150(ReasonService reasonService,
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
                historyService, fileStoredService,false, DType.TO150, components);
        this.userService = userService;
        this.MaxPower = 150.0;
        demandType.setValue(demandTypeService.findById(DemandType.TO150).get());

        expirationsLayout = new ExpirationsLayout(expirationService,safetyService, historyService);
        notesLayout = new NotesLayout(noteService,historyService);

        Component fields[] = {delegate, inn, innDate, powerDemand, powerCurrent,
                powerMaximum, voltage, safety, specification, plan, accordionExpiration};
        for(Component field : fields){
            field.setVisible(true);
        }

        accordionExpiration.add("Этапы выполнения работ",this.expirationsLayout);

        add(formDemand,filesLayout,notesLayout,buttonBar,accordionHistory);
    }

    @Override
    public void populateForm(Demand value) {
        this.demand = value;
        binderDemand.readBean(this.demand);
        generalBinder.readBean(null);
        demandType.setReadOnly(true);
        createdate.setReadOnly(true);
        if(value != null) {
            if(pointService.findAllByDemand(demand).isEmpty()) {
                point = new Point();
            } else {
                point = pointService.findAllByDemand(demand).get(0);
            }
            filesLayout.findAllByDemand(demand);
            expirationsLayout.findAllByDemand(demand);
            notesLayout.findAllByDemand(demand);
            historyLayout.findAllByDemand(demand);
            switch(demand.getStatus().getState()){
                case ADD: {
                    setReadOnly();
                } break;
                case NOTE: {
                    setReadOnly();
                    filesLayout.setReadOnly();
                    expirationsLayout.setReadOnly();
                } break;
                case FREEZE: {
                    setReadOnly();
                    filesLayout.setReadOnly();
                    notesLayout.setReadOnly();
                    expirationsLayout.setReadOnly();
                } break;
            }
        }
        pointBinder.readBean(this.point);
    }
    public boolean save() {
        passportSerries.setValue("0000");
        passportNumber.setValue("000000");
        if(!super.save() || (pointBinder.validate().getValidationErrors().size() > 0)) return false;
        pointBinder.writeBeanIfValid(point);
        point.setDemand(demand);
        historyService.saveHistory(demand,point,Point.class);
        pointService.update(this.point);
        filesLayout.setDemand(demand);
        filesLayout.saveFiles();
        expirationsLayout.setDemand(demand);
        expirationsLayout.saveExpirations();
        notesLayout.setDemand(demand);
        notesLayout.saveNotes();
        return true;
    }
}
