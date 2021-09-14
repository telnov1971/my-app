package com.example.application.views.demandedit;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.main.MainView;
import com.example.application.views.support.ExpirationsLayout;
import com.example.application.views.support.GeneralForm;
import com.vaadin.flow.component.*;
import com.vaadin.flow.router.*;

@Route(value = "demandto15/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandto15")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор заявки до 15 кВт")
public class DemandEditTo15 extends GeneralForm {
    private final UserService userService;
    private ExpirationsLayout expirationsLayout;

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
                          Component... components) {
        super(reasonService, demandService,demandTypeService,statusService,garantService,
                 pointService,generalService,voltageService,
                 safetyService,planService,priceService,sendService,userService,
                historyService, fileStoredService,false, DType.TO15, components);
        this.userService = userService;
        // сервисы
        this.MaxPower = 15.0;
        demandType.setValue(demandTypeService.findById(DemandType.TO15).get());
        expirationsLayout = new ExpirationsLayout(expirationService,safetyService);
        safety.setValue(safetyService.findById(3L).get());
        safety.setReadOnly(true);

        voltage.addValueChangeListener(e -> {
            setOptional();
        });

        Component fields[] = {passportSerries,passportNumber,pasportIssued,
                addressRegistration,addressActual,
                powerDemand, powerCurrent,
                powerMaximum, voltage, safety, accordionExpiration};
        for(Component field : fields){
            field.setVisible(true);
        }

        accordionExpiration.add("Этапы выполнения работ",this.expirationsLayout);
        add(formDemand,filesLayout,buttonBar,accordionHistory);
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
            historyLayout.findAllByDemand(demand);
        }
        pointBinder.readBean(this.point);
        setOptional();
    }

    public boolean save() {
        inn.setValue("0000000000");
        if(!super.save() || (pointBinder.validate().getValidationErrors().size() > 0)) return false;
        pointBinder.writeBeanIfValid(point);
        point.setDemand(demand);
        History historyPoint = new History();
        try {
            String his = historyService.writeHistory(point);
            historyPoint.setHistory(his);
        } catch (Exception e) {System.out.println(e.getMessage());}
        try {
            historyPoint.setDemand(demand);
            if(!historyPoint.getHistory().equals("")) {
                historyService.save(historyPoint);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        pointService.update(this.point);
        filesLayout.setDemand(demand);
        filesLayout.saveFiles();
        expirationsLayout.setDemand(demand);
        expirationsLayout.saveExpirations();
        return true;
    }

    private void setOptional(){
        if(voltage.getValue()!=null && voltage.getValue().getId()==1L) {
            voltageIn.setVisible(true);
        } else {
            voltageIn.setVisible(false);
        }
    }
}
