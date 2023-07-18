package ru.omel.po.views.demandedit;

import org.springframework.transaction.annotation.Transactional;
import ru.omel.po.data.service.*;
import ru.omel.po.data.entity.DType;
import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.DemandType;
import ru.omel.po.data.entity.Point;
import ru.omel.po.views.main.MainView;
import ru.omel.po.views.support.GeneralForm;
import com.vaadin.flow.component.Component;
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
                              PrivilegeService privilegeService,
                              Component... components) {
        super(reasonService, demandService,demandTypeService,statusService,garantService,
                pointService,generalService,voltageService,
                safetyService,planService,priceService,sendService,userService,
                historyService, fileStoredService, DType.TEMPORAL,noteService,
                privilegeService, components);
        this.maxPower = 1000000.0;
        demandTypeService.findById(DemandType.TEMPORAL).ifPresent(r -> demandType.setValue(r));

        // inn, innDate, passportSerries,passportNumber, pasportIssued
        Component[] fields = {typeDemander,
                addressRegistration,addressActual,addressEquals,
                powerDemand, powerCurrent, powerMaximum, voltage, safety,
                specification};
        for(Component field : fields){
            field.setVisible(true);
        }
        voltage.addValueChangeListener(e -> setOptional());
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
        }
        pointBinder.readBean(this.point);
        safety.setReadOnly(true);
    }

    @Transactional
    @Override
    public boolean save() {
        super.save();
        point.setDemand(demand);
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        historyExists |= historyService.saveHistory(client, demand,point,Point.class);
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        pointService.update(this.point);
        demand.setChange(demand.isChange() || historyExists);
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        demandService.update(demand);
        return true;
    }

    @Override
    protected Boolean verifyField() {
        if(!Boolean.TRUE.equals(super.verifyField())) return false;
        if(!pointBinder.validate().getValidationErrors().isEmpty()) return false;
        pointBinder.writeBeanIfValid(point);
        return true;
    }

    @Override
    protected void settingTemporalReasons() {
        powerCurrent.setEnabled(true);
        if(reason.getValue().getId() == 1){
            powerCurrent.setValue(0.0);
            powerCurrent.setEnabled(false);
        }
        if(reason.getValue().getId() == 5){
            contract.setVisible(true);
            period.setVisible(false);
            this.maxPower = 1000000.0;
            powerMaximum.setHelperText("");
            period.setHelperText("");
        }
        if(reason.getValue().getId() == 6){
            contract.setVisible(false);
            period.setVisible(true);
            this.maxPower = 150.0;
            powerMaximum.setHelperText("Для передвижных объектов максимальная мощность не более 150 кВт");
            period.setHelperText("Для передвижных объектов срок подключения не должен превышать 12 месяцев");
        }
    }
    private void setOptional(){
        voltageIn.setVisible(voltage.getValue() != null && voltage.getValue().getId() == 1L);
    }
}
