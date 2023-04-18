package ru.omel.po.views.demandedit;

import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.*;
import org.springframework.transaction.annotation.Transactional;
import ru.omel.po.data.entity.DType;
import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.DemandType;
import ru.omel.po.data.entity.General;
import ru.omel.po.data.service.*;
import ru.omel.po.views.main.MainView;
import ru.omel.po.views.support.ExpirationsLayout;
import ru.omel.po.views.support.GeneralForm;
import ru.omel.po.views.support.PointsLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.notification.Notification;

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
                              PrivilegeService privilegeService,
                              Component... components) {
        super(reasonService, demandService,demandTypeService,statusService,garantService,
                pointService,generalService,voltageService,
                safetyService,planService,priceService,sendService,userService,
                historyService, fileStoredService, DType.GENERAL,noteService, privilegeService, components);
        this.MaxPower = 1000000000.0;
        if(demandTypeService.findById(DemandType.GENERAL).isPresent())
            demandType.setValue(demandTypeService.findById(DemandType.GENERAL).get());

        pointsLayout = new PointsLayout(pointService
                , voltageService
                , safetyService
                , historyService
                , this
                , client);

        expirationsLayout = new ExpirationsLayout(expirationService
                , safetyService
                , historyService
                , this
                , client);

        Component[] fields = {typeDemander,
                addressRegistration,addressActual,addressEquals,
                accordionPoints, specification, countTransformations,
                countGenerations, techminGeneration, reservation, accordionExpiration};
        for(Component field : fields){
            field.setVisible(true);
        }

        accordionPoints.add("Точки подключения (открыть/закрыть по клику мышкой)"
                ,this.pointsLayout);
        accordionExpiration.add("Этапы выполнения работ (открыть/закрыть по клику мышкой)"
                ,this.expirationsLayout);
        powerMaximum.addValueChangeListener(e ->
                expirationsLayout.setPowerMax(powerMaximum.getValue())
        );
        add(formDemand,filesLayout,notesLayout,buttonBar,accordionHistory,space);
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
                case EDIT:
                    break;
                case ADD:
                case NOTE:
                case FREEZE: {
                    expirationsLayout.setReadOnly();
                } break;
            }
        }
        generalBinder.readBean(general);
    }
    @Transactional
    public boolean save() {
        if((binderDemand.validate().getValidationErrors().size() > 0) || !super.save()) return false;
        generalBinder.writeBeanIfValid(general);
        general.setDemand(demand);
        historyExists |= historyService.saveHistory(client, demand, general, General.class);
        generalService.update(this.general);

        pointsLayout.setDemand(demand);
        historyExists |= pointsLayout.savePoints();

        expirationsLayout.setDemand(demand);
        historyExists |= expirationsLayout.saveExpirations();

        demand.setChange(demand.isChange() || historyExists);
        demandService.update(demand);

        return true;
    }

    @Override
    protected Boolean verifyField() {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.BOTTOM_START);
        notification.setDuration(3000);

        if(!super.verifyField()) return false;
        if(pointsLayout.getPointSize()==0){
            specification.focus();
            pointsLayout.setFocus();
            notification.setText("Не заполнены точки подключения");
            notification.open();
            return false;
        }
        if(expirationsLayout.getExpirationsSize()==0){
            safety.focus();
            expirationsLayout.setFocus();
            notification.setText("Не заполнены этапы работ");
            notification.open();
            return false;
        }
        return true;
    }

}
