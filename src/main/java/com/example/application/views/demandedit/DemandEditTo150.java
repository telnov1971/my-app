package com.example.application.views.demandedit;

import com.example.application.data.entity.DType;
import com.example.application.data.entity.Demand;
import com.example.application.data.entity.DemandType;
import com.example.application.data.entity.Point;
import com.example.application.data.service.*;
import com.example.application.views.main.MainView;
import com.example.application.views.support.ExpirationsLayout;
import com.example.application.views.support.GeneralForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "demandto150/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandto150")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Юридические лица и ИП до 150кВт (один источник электропитания)")
public class DemandEditTo150 extends GeneralForm {
    private final ExpirationsLayout expirationsLayout;

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
                historyService, fileStoredService, DType.TO150,noteService,components);
        this.MaxPower = 150.0;
        if(demandTypeService.findById(DemandType.TO150).isPresent())
            demandType.setValue(demandTypeService.findById(DemandType.TO150).get());

        expirationsLayout = new ExpirationsLayout(expirationService,safetyService, historyService, this, client);

        typeDemander.setItems("Юридическое лицо", "Индивидуальный предприниматель");
        Component[] fields = {delegate, typeDemander, inn, innDate,
                addressRegistration,addressActual,addressEquals,
                powerDemand, powerCurrent,
                powerMaximum, voltage, safety, specification, plan, accordionExpiration};
        for(Component field : fields){
            field.setVisible(true);
        }

        accordionExpiration.add("Этапы выполнения работ (открыть/закрыть по клику мышкой)",this.expirationsLayout);
        powerMaximum.addValueChangeListener(e -> {
            expirationsLayout.setPowerMax(powerMaximum.getValue());
            if(powerMaximum.getValue() < 15.0) {
                plan.setValue(planService.findById(1L));
                plan.setReadOnly(true);
            } else {
                plan.setReadOnly(false);
            }
        });
        add(formDemand,filesLayout,notesLayout,buttonBar,accordionHistory,space);
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
                case EDIT:
                    break;
                case ADD:
                case NOTE:
                case FREEZE: {
                    expirationsLayout.setReadOnly();
                } break;
            }
        }
        safety.setReadOnly(true);
        specification.setLabel("Характер нагрузки (обязательное поле)");
        pointBinder.readBean(this.point);
    }
    public boolean save() {
        if(!super.save() || (pointBinder.validate().getValidationErrors().size() > 0)) return false;
        pointBinder.writeBeanIfValid(point);
        point.setDemand(demand);
        historyExists |= historyService.saveHistory(client, demand,point,Point.class);
        pointService.update(this.point);
        expirationsLayout.setDemand(demand);
        historyExists |= expirationsLayout.saveExpirations();
        demand.setChange(demand.isChange() || historyExists);
        demandService.update(demand);

        return true;
    }

    @Override
    protected Boolean verifyField() {
        if(!super.verifyField()) return false;
        if(!powerMaximum.isEmpty() && powerMaximum.getValue() > 150.0) {
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

    @Override
    protected void settingTemporalDemander(){
        // "Физическое лицо", "Юридическое лицо", "Индивидуальный предприниматель"
        switch(typeDemander.getValue()){
            case "Юридическое лицо":
                passportSerries.setVisible(false);
                passportNumber.setVisible(false);
                pasportIssued.setVisible(false);
                break;
            case "Индивидуальный предприниматель":
                passportSerries.setVisible(true);
                passportNumber.setVisible(true);
                pasportIssued.setVisible(true);
                break;
        }
    }
}
