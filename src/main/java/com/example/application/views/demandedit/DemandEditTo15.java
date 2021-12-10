package com.example.application.views.demandedit;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.main.MainView;
import com.example.application.views.support.ExpirationsLayout;
import com.example.application.views.support.GeneralForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "demandto15/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandto15")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Физические лица до 15 кВт (ком.-быт. нужды)")
public class DemandEditTo15 extends GeneralForm {
    private final ExpirationsLayout expirationsLayout;

    public DemandEditTo15(ReasonService reasonService,
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
                historyService, fileStoredService, DType.TO15,noteService,components);
        // сервисы
        this.MaxPower = 15.0;
        demander.setHelperText(demander.getHelperText() + " или физического лица");
        if(demandTypeService.findById(DemandType.TO15).isPresent())
            demandType.setValue(demandTypeService.findById(DemandType.TO15).get());
        expirationsLayout = new ExpirationsLayout(expirationService
                ,safetyService, historyService, this);
        if(safetyService.findById(3L).isPresent())
            safety.setValue(safetyService.findById(3L).get());
        safety.setReadOnly(true);

        voltage.addValueChangeListener(e -> setOptional());

        Component[] fields = {passportSerries,passportNumber,pasportIssued,
                addressRegistration,addressActual,addressEquals,
                powerDemand, powerCurrent,
                powerMaximum, voltage, safety, accordionExpiration};
        for(Component field : fields){
            field.setVisible(true);
        }

        accordionExpiration.add("Этапы выполнения работ",this.expirationsLayout);
        powerMaximum.addValueChangeListener(e ->
            expirationsLayout.setPowerMax(powerMaximum.getValue())
        );
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
            expirationsLayout.findAllByDemand(demand);
            switch(demand.getStatus().getState()){
                case NOTE:
                case FREEZE: {
                    expirationsLayout.setReadOnly();
                } break;
            }
        }
        pointBinder.readBean(this.point);
        setOptional();
    }

    public boolean save() {
        inn.setValue("0000000000");
        if(!super.save() || (pointBinder.validate().getValidationErrors().size() > 0)) return false;
        pointBinder.writeBeanIfValid(point);
        point.setDemand(demand);
        historyService.saveHistory(demand,point,Point.class);
        pointService.update(this.point);
        expirationsLayout.setDemand(demand);
        expirationsLayout.saveExpirations();

        return true;
    }

    @Override
    protected Boolean verifyField() {
        if(!super.verifyField()) return false;
        if(!powerMaximum.isEmpty() && powerMaximum.getValue() > 15.0) {
            powerCurrent.focus();
            Notification.show("Максимальна мощность больше допустимой", 3000,
                    Notification.Position.BOTTOM_START);
            return false;
        }
        if(expirationsLayout.getExpirationsSize()==0){
            powerCurrent.focus();
            expirationsLayout.setFocus();
            Notification.show("Не заполнены этапы работ", 3000,
                    Notification.Position.BOTTOM_START);
            return false;
        }
        return true;
    }

    private void setOptional(){
        voltageIn.setVisible(voltage.getValue() != null && voltage.getValue().getId() == 1L);
    }
}
