package com.example.application.views.demandedit;

import com.example.application.data.entity.DType;
import com.example.application.data.entity.Demand;
import com.example.application.data.entity.DemandType;
import com.example.application.data.entity.Point;
import com.example.application.data.service.*;
import com.example.application.views.main.MainView;
import com.example.application.views.support.GeneralForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "demandtemporary/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandtemporary")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Временное присоединение")
public class DemandEditTemporal extends GeneralForm {

    public DemandEditTemporal(ReasonService reasonService,
                              DemandService demandService,
                              DemandTypeService demandTypeService,
                              StatusService statusService,
                              GarantService garantService,
                              PointService pointService,
                              GeneralService generalService,
                              //ExpirationService expirationService,
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
                historyService, fileStoredService, DType.TEMPORAL,noteService,components);
        this.MaxPower = 1000000000.0;
        demandType.setValue(demandTypeService.findById(DemandType.TEMPORAL).get());

        Component fields[] = {inn, innDate,
                passportSerries,passportNumber,pasportIssued,
                addressRegistration,addressActual,
                powerDemand, powerCurrent, powerMaximum, voltage, safety,
                specification, period, contract};
        for(Component field : fields){
            field.setVisible(true);
        }

        //accordionExpiration.add("Этапы выполнения работ",this.expirationsLayout);
        add(formDemand,filesLayout,notesLayout,buttonBar,accordionHistory);
    }

    @Override
    public void populateForm(Demand value) {
        super.populateForm(value);
        if(value != null) {
            if(pointService.findAllByDemand(demand).isEmpty()) {
                point = new Point();
            } else {
                point = pointService.findAllByDemand(demand).get(0);
            }
        }
        pointBinder.readBean(this.point);
    }

    public boolean save() {
        if(!super.save() || (pointBinder.validate().getValidationErrors().size() > 0)) return false;
        pointBinder.writeBeanIfValid(point);
        point.setDemand(demand);
        historyService.saveHistory(demand,point,Point.class);
        pointService.update(this.point);
//        filesLayout.setDemand(demand);
//        filesLayout.saveFiles();
//        notesLayout.setDemand(demand);
//        notesLayout.saveNotes();
        return true;
    }

    @Override
    protected Boolean verifyField() {
        return true;
    }
}
