package com.example.application.views.demandedit;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.demandlist.DemandList;
import com.example.application.views.main.MainView;
import com.example.application.views.support.FilesLayout;
import com.example.application.views.support.GeneralForm;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;

import java.io.*;

@Route(value = "demandto15/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandto15")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор заявки до 15 кВт")
public class DemandEditTo15 extends GeneralForm {
    private final UserService userService;

    public DemandEditTo15(DemandService demandService,
                          DemandTypeService demandTypeService,
                          StatusService statusService,
                          GarantService garantService,
                          PointService pointService,
                          GeneralService generalService,
                          UserService userService,
                          VoltageService voltageService,
                          SafetyService safetyService,
                          PlanService planService,
                          PriceService priceService,
                          SendService sendService,
                          FileStoredService fileStoredService,
                          HistoryService historyService,
                          Component... components) {
        super(demandService,demandTypeService,statusService,garantService,
                 pointService,generalService,voltageService,
                 safetyService,planService,priceService,sendService,userService,
                historyService, fileStoredService, components);
        this.userService = userService;
        // сервисы
        this.MaxPower = 15.0;
        demandType.setValue(demandTypeService.findById(DemandType.TO15).get());

        Component fields[] = {powerDemand, powerCurrent,
                powerMaximum, voltage, safety};
        for(Component field : fields){
            field.setVisible(true);
        }

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
            historyLayout.findAllByDemand(demand);
        }
        pointBinder.readBean(this.point);
    }

    public boolean save() {
        inn.setValue("0000000000");
        if(!super.save() || (pointBinder.validate().getValidationErrors().size() > 0)) return false;
        pointBinder.writeBeanIfValid(point);
        point.setDemand(demand);
        pointService.update(this.point);
        filesLayout.setDemand(demand);
        filesLayout.saveFiles();
        return true;
    }
}
