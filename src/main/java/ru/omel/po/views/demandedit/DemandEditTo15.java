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
import ru.omel.po.views.support.PrivilegeLayout;

@Route(value = "demandto15/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandto15")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Физические лица до 15 кВт (ком.-быт. нужды)")
public class DemandEditTo15 extends GeneralForm {
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
                          PrivilegeService privilegeService,
                          Component... components) {
        super(reasonService, demandService,demandTypeService,statusService,garantService,
                 pointService,generalService,voltageService,
                 safetyService,planService,priceService,sendService,userService,
                historyService, fileStoredService, DType.TO15,noteService, privilegeService, components);
        // сервисы
        this.maxPower = 15.0;
        demander.setHelperText(demander.getHelperText() + " или физического лица");
        demandTypeService.findById(DemandType.TO15).ifPresent(r -> demandType.setValue(r));
        expirationsLayout = new ExpirationsLayout(expirationService
                ,safetyService, historyService, this, client);
        safetyService.findById(3L).ifPresent(r -> safety.setValue(r));
        safety.setReadOnly(true);

        voltage.addValueChangeListener(e -> setOptional());

        Component[] fields = {passportSerries,passportNumber,passportIssued,inn,
                addressRegistration,addressActual,addressEquals,
                labelPrivilege,privilegeNot,accordionPrivilege,
                powerDemand, powerCurrent,
                powerMaximum, voltage, safety, accordionExpiration};
        for(Component field : fields){
            field.setVisible(true);
        }

        accordionExpiration.add("Этапы выполнения работ (открыть/закрыть по клику мышкой)"
                ,this.expirationsLayout);
        powerMaximum.addValueChangeListener(e ->
            expirationsLayout.setPowerMax(powerMaximum.getValue())
        );
        voltageService.findById(1L).ifPresent(r -> voltage.setValue(r));
        voltage.setReadOnly(true);
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
            voltage.setReadOnly(true);
            if(demand.getStatus().getState()!= Status.EState.EDIT){
                expirationsLayout.setReadOnly();
            }
        }
        safety.setReadOnly(true);
        pointBinder.readBean(this.point);
        privilegeLayout.setDemand(demand);
        setOptional();
    }

    @Override
    @Transactional
    public boolean save() {
        super.save();
        point.setDemand(demand);
        expirationsLayout.setDemand(demand);
        if(privilegeLayout.getPrivilege(demand) != PrivilegeLayout.PrivilegeState.NOTCHANGE) {
            String strHistory;
            switch(privilegeLayout.getPrivilege(demand)){
                case SET -> {
                    strHistory = "Заявитель заполнил анкету льгот";
                    demand.setPrivilege(true);
                }
                case NOTSET -> {
                    strHistory = "Заявитель очистил анкету льгот";
                    demand.setPrivilege(false);
                }
                case CHANGE ->
                    strHistory = "Заявитель изменил анкету льгот";
                default ->
                    strHistory = "";
            }
            privilegeLayout.savePrivilege(demand);
            History history = new History(demand,strHistory);
            historyService.save(history);
            historyExists = true;
        }

        historyExists |= historyService.saveHistory(client,demand,point,Point.class);
        pointService.update(this.point);
        historyExists |= expirationsLayout.saveExpirations();
        demand.setChange(demand.isChange() || historyExists);
        if(!demand.isPrivilegeNot() && privilegeLayout.getPrivilegeStatus()) demand.setPrivilege(true);
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        demandService.update(demand);
        return true;
    }

    @Override
    protected Boolean verifyField() {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.BOTTOM_START);
        notification.setDuration(5000);

        if(!Boolean.TRUE.equals(super.verifyField())) return false;
//        ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        if((privilegeNot.getValue() && privilegeLayout.getPrivilegeStatus())
                || (!privilegeNot.getValue() && !privilegeLayout.getPrivilegeStatus())) {
            privilegeNot.focus();
            privilegeNot.setReadOnly(false);
            setReadOnly(false);
            notification.setText("Обязательно подтвердить ИЛИ НАЛИЧИЕ, ИЛИ ОТСУТСТВИЕ льгот!");
            notification.open();
            return false;
        }
        if(!powerMaximum.isEmpty() && powerMaximum.getValue() > 15.0) {
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

    private void setOptional(){
        voltageIn.setVisible(voltage.getValue() != null && voltage.getValue().getId() == 1L);
    }
}
