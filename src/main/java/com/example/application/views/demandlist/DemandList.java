package com.example.application.views.demandlist;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.DemandType;
import com.example.application.data.service.DemandService;
import com.example.application.data.service.DemandTypeService;
import com.example.application.views.demandedit.DemandEditTo15;
import com.example.application.views.main.MainView;
import com.example.application.views.masterdetail.MasterDetailView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@Route(value = "demandlist", layout = MainView.class)
public class DemandList extends Div {
    private Grid<Demand> grid = new Grid<>(Demand.class, false);

    private BeanValidationBinder<Demand> binder;
    private DemandService demandService;
    private DemandTypeService demandTypeService;

    @Autowired
    public DemandList(DemandService demandService,
                            DemandTypeService demandTypeService) {
        this.demandService = demandService;
        this.demandTypeService = demandTypeService;
        addClassNames("master-detail-view", "flex", "flex-col", "h-full");

        // Configure Grid
        grid.addColumn("createdate").setAutoWidth(true).setHeader("Дата создания");
        grid.addColumn("demandType.name").setAutoWidth(true).setHeader("Тип");
        grid.addColumn("object").setAutoWidth(true).setHeader("Объект");
        grid.addColumn("address").setAutoWidth(true).setHeader("Адрес объекта");
        grid.addColumn("garant.name").setAutoWidth(true).setHeader("Поставщик");
        grid.addColumn("status.name").setAutoWidth(true).setHeader("Статус");
        TemplateRenderer<Demand> doneRenderer = TemplateRenderer.<Demand>of(
                "<iron-icon hidden='[[!item.done]]' icon='vaadin:check' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: var(--lumo-primary-text-color);'></iron-icon><iron-icon hidden='[[item.done]]' icon='vaadin:minus' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: var(--lumo-disabled-text-color);'></iron-icon>")
                .withProperty("done", Demand::isDone);
        grid.addColumn(doneRenderer).setHeader("Завершено").setAutoWidth(true);

        grid.setItems(query -> demandService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        Div messageDiv = new Div();

        grid.addItemDoubleClickListener(event ->{
            UI.getCurrent().navigate(DemandEditTo15.class, new RouteParameters("demandID",
                    String.valueOf(event.getItem().getId())));
        });

        /*
        grid.asSingleSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                    event.getOldValue().getId(), event.getValue().getId());
            messageDiv.setText(message);
            UI.getCurrent().navigate(DemandEditTo15.class, new RouteParameters("demandID",
                    String.valueOf(event.getValue().getId())));
        });

         */

        add(grid, messageDiv);
    }
}