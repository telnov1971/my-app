package com.example.application.views.demandedit;

import com.example.application.config.AppEnv;
import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.example.application.views.demandlist.DemandList;
import com.example.application.views.main.MainView;
import com.example.application.views.support.FilesLayout;
import com.example.application.views.support.GeneralForm;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Route(value = "demandtemporary/:demandID?", layout = MainView.class)
@RouteAlias(value ="demandtemporary")
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор заявки на временное подключение")
public class DemandEditTemporary extends GeneralForm implements BeforeEnterObserver {
    private final String DEMAND_ID = "demandID";
    private HorizontalLayout buttonBar = new HorizontalLayout();
    private Button save = new Button("Сохранить");
    private Button reset = new Button("Отменить");

    private final FileStoredService fileStoredService;
    private final UserService userService;
    private FilesLayout filesLayout;

    public DemandEditTemporary(DemandService demandService,
                               DemandTypeService demandTypeService,
                               StatusService statusService,
                               GarantService garantService,
                               PointService pointService,
                               GeneralService generalService,
                               ExpirationService expirationService,
                               UserService userService, VoltageService voltageService,
                               SafetyService safetyService,
                               PlanService planService,
                               PriceService priceService,
                               SendService sendService,
                               FileStoredService fileStoredService,
                               Component... components) {
        super(demandService,demandTypeService,statusService,garantService,
                pointService,generalService,expirationService,voltageService,
                safetyService,planService,priceService,sendService,
                components);
        this.userService = userService;
        this.fileStoredService = fileStoredService;
        this.MaxPower = 1000000000.0;
        demandType.setValue(demandTypeService.findById(DemandType.TEMPORARY).get());

        filesLayout = new FilesLayout(this.fileStoredService
                , voltageService
                , safetyService);

        save.addClickListener(event -> {
            save();

            filesLayout.setDemand(demand);
            filesLayout.saveFiles();

            UI.getCurrent().navigate(DemandList.class);
        });
        reset.addClickListener(event -> {
            try {
                filesLayout.deleteFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
            UI.getCurrent().navigate(DemandList.class);
        });

        Component fields[] = {inn, innDate, powerDemand, powerCurrent,
                powerMaximum, voltage, safety, specification, period, contract};
        for(Component field : fields){
            field.setVisible(true);
        }


        buttonBar.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonBar.setSpacing(true);
        reset.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonBar.add(save,reset);

        add(formDemand,filesLayout,buttonBar);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> demandId = event.getRouteParameters().getLong(DEMAND_ID);
        if (demandId.isPresent()) {
            Optional<Demand> demandFromBackend = demandService.get(demandId.get());
            if (demandFromBackend.isPresent()) {
                if (demandFromBackend.get().getUser().equals(userService.findByUsername(
                        SecurityContextHolder.getContext().getAuthentication().getName()
                ))) {
                    populateForm(demandFromBackend.get());
                } else {
                    Notification.show(String.format("Заявка с ID = %d не Ваша", demandId.get()), 3000,
                            Notification.Position.BOTTOM_START);
                    clearForm();
                }
            } else {
                Notification.show(String.format("Заявка с ID = %d не найдена", demandId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                clearForm();
            }
        }
    }

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
        }
        pointBinder.readBean(this.point);
    }
    public void save() {
        binderDemand.writeBeanIfValid(demand);
        if(demand.getUser()==null){
            demand.setUser(userService.findByUsername(
                    SecurityContextHolder.getContext().getAuthentication().getName()));
            demand.setCreateDate(LocalDate.now());
            demand.setLoad1c(false);
            demand.setChange(false);
            demand.setDone(false);
        }
        demandService.update(this.demand);

        pointBinder.writeBeanIfValid(point);
        point.setDemand(demand);
        pointService.update(this.point);

        UI.getCurrent().navigate(DemandList.class);
    }

    public void clearForm() {
        binderDemand.readBean(null);
        pointBinder.readBean(null);
        generalBinder.readBean(null);
        populateForm(null);
    }

}
