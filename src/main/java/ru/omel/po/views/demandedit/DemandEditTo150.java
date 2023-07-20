package ru.omel.po.views.demandedit;

import com.vaadin.flow.component.notification.NotificationVariant;
import org.springframework.transaction.annotation.Transactional;
import ru.omel.po.data.entity.*;
import ru.omel.po.data.service.*;
import ru.omel.po.views.main.MainView;
import ru.omel.po.views.support.ExpirationsLayout;
import ru.omel.po.views.support.GeneralForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "demandto150/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandto150")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Юридические и физические лица, ИП до 150кВт (один источник электропитания)")
public class DemandEditTo150 extends GeneralForm {
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
                           PrivilegeService privilegeService,
                           Component... components) {
        super(reasonService, demandService,demandTypeService,statusService,garantService,
                pointService,generalService,voltageService,
                safetyService,planService,priceService,sendService,userService,
                historyService, fileStoredService, DType.TO150,noteService, privilegeService, components);
        this.maxPower = 150.0;
        demandTypeService.findById(DemandType.TO150).ifPresent(r ->
                demandType.setValue(r));

        expirationsLayout = new ExpirationsLayout(expirationService,safetyService, historyService, this, client);

        Component[] fields = {delegate, typeDemander, inn, innDate, ogrn,
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
            expirationsLayout.findAllByDemand(demand);
            if(demand.getStatus().getState() != Status.EState.EDIT ) {
                if(expirationsLayout != null)
                    expirationsLayout.setReadOnly();
            }
        }
        safety.setReadOnly(true);
        specification.setLabel("Характер нагрузки (обязательное поле)");
        pointBinder.readBean(this.point);
    }
    @Transactional
    @Override
    public boolean save() {
        super.save();
        point.setDemand(demand);
        historyExists |= historyService.saveHistory(client, demand, point, Point.class);
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        pointService.update(this.point);
        expirationsLayout.setDemand(demand);
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        historyExists |= expirationsLayout.saveExpirations();
        demand.setChange(demand.isChange() || historyExists);
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        demandService.update(demand);
        return true;
    }

    @Override
    protected Boolean verifyField() {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.BOTTOM_START);
        notification.setDuration(3000);

        if(Boolean.TRUE.equals(!super.verifyField())) return false;
        if(!powerMaximum.isEmpty() && powerMaximum.getValue() > 150.0) {
            powerCurrent.focus();
            notification.setText("Максимальна мощность больше допустимой");
            notification.open();
            return false;
        }
        if(expirationsLayout.getExpirationsSize()==0){
            powerCurrent.focus();
            expirationsLayout.setFocus();
            notification.setText("Не заполнены этапы работ");
            notification.open();
            return false;
        }
        if(!pointBinder.validate().getValidationErrors().isEmpty()) return false;
        pointBinder.writeBeanIfValid(point);
        return true;
    }

    @Override
    protected void settingTemporalDemander(){
        // "Физическое лицо", "Юридическое лицо", "Индивидуальный предприниматель"
        switch(typeDemander.getValue()){
            case "Физическое лицо" -> {
                passportSerries.setVisible(true);
                passportNumber.setVisible(true);
                passportIssued.setVisible(true);
                ogrn.setVisible(false);
            }
            case "Юридическое лицо" -> {
                passportSerries.setVisible(false);
                passportNumber.setVisible(false);
                passportIssued.setVisible(false);
                ogrn.setVisible(true);
            }
            default -> {
                passportSerries.setVisible(true);
                passportNumber.setVisible(true);
                passportIssued.setVisible(true);
                ogrn.setVisible(true);
            }
        }
    }

    private void setOptional(){
        voltageIn.setVisible(voltage.getValue() != null && voltage.getValue().getId() == 1L);
    }
}
