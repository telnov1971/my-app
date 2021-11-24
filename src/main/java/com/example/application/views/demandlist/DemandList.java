package com.example.application.views.demandlist;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.DemandType;
import com.example.application.data.entity.Role;
import com.example.application.data.entity.User;
import com.example.application.data.service.DemandService;
import com.example.application.data.service.UserService;
import com.example.application.views.demandedit.DemandEditTemporal;
import com.example.application.views.demandedit.DemandEditTo15;
import com.example.application.views.demandedit.DemandEditTo150;
import com.example.application.views.demandedit.DemandEditeGeneral;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.WeakHashMap;

@Route(value = "demandlist", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Список заявок")
public class DemandList extends Div {
    private TextField filterId = new TextField();
    private TextField filterText = new TextField();
    private Grid.Column<Demand> demanderColumn;
    private Button clearFilter = new Button(new Icon(VaadinIcon.ERASER));
    private final Grid<Demand> grid = new Grid<>(Demand.class, false);

    private BeanValidationBinder<Demand> binder;
    private final DemandService demandService;
    private final UserService userService;

    @Autowired
    public DemandList(DemandService demandService, UserService userService) {
        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.getElement().getStyle().set("margin", "10px");
        VerticalLayout verticalLayout = new VerticalLayout();
        this.demandService = demandService;
        this.userService = userService;
        addClassNames("master-detail-view", "flex", "flex-col", "h-full");

        // Configure Grid
        grid.addColumn("id").setAutoWidth(false).setWidth("3em").setHeader("ID");
        demanderColumn = grid.addColumn("demander").setHeader("Заявитель")
                .setResizable(true).setWidth("200px");
        grid.addColumn("status.name").setAutoWidth(true).setHeader("Статус");
        //grid.addColumn("createDate").setAutoWidth(true).setHeader("Дата создания");
        Grid.Column<Demand> createDate = grid.addComponentColumn(demand -> new Label(demand.getCreateDate()
                        .format(DateTimeFormatter.ofPattern("uuuu-MM-dd | HH:mm:ss"))))
                .setHeader("Дата и время").setAutoWidth(true);
        grid.addColumn("object").setHeader("Объект")
                .setResizable(true).setWidth("200px");
        grid.addColumn("address").setHeader("Адрес объекта")
                .setResizable(true).setWidth("200px");
        grid.addColumn("demandType.name").setAutoWidth(true).setHeader("Тип");
        /*
        TemplateRenderer<Demand> doneRenderer = TemplateRenderer.<Demand>of(
                "<iron-icon hidden='[[!item.done]]' icon='vaadin:check' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: var(--lumo-primary-text-color);'></iron-icon><iron-icon hidden='[[item.done]]' icon='vaadin:minus' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: var(--lumo-disabled-text-color);'></iron-icon>")
                .withProperty("done", Demand::isDone);
        grid.addColumn(doneRenderer).setHeader("Завершено").setAutoWidth(true);
         */
        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        Grid.Column<Demand> editorColumn = grid.addComponentColumn(demand -> {
            Button edit = new Button(new Icon(VaadinIcon.EDIT));
            edit.addClassName("edit");
            edit.addClickListener(event -> {
                if (demand.getDemandType().getId() == DemandType.TO15) {
                    UI.getCurrent().navigate(DemandEditTo15.class, new RouteParameters("demandID",
                            String.valueOf(demand.getId())));
                }
                if (demand.getDemandType().getId() == DemandType.TO150) {
                    UI.getCurrent().navigate(DemandEditTo150.class, new RouteParameters("demandID",
                            String.valueOf(demand.getId())));
                }
                if (demand.getDemandType().getId() == DemandType.TEMPORAL) {
                    UI.getCurrent().navigate(DemandEditTemporal.class, new RouteParameters("demandID",
                            String.valueOf(demand.getId())));
                }
                if (demand.getDemandType().getId() == DemandType.GENERAL) {
                    UI.getCurrent().navigate(DemandEditeGeneral.class, new RouteParameters("demandID",
                            String.valueOf(demand.getId())));
                }
            });
            edit.setEnabled(true);
            editButtons.add(edit);
            return edit;
        }).setWidth("3em");

        gridSetting(null,null);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        grid.addItemDoubleClickListener(event ->{
            if (event.getItem().getDemandType().getId() == DemandType.TO15) {
                UI.getCurrent().navigate(DemandEditTo15.class, new RouteParameters("demandID",
                        String.valueOf(event.getItem().getId())));
            }
            if (event.getItem().getDemandType().getId() == DemandType.TO150) {
                UI.getCurrent().navigate(DemandEditTo150.class, new RouteParameters("demandID",
                        String.valueOf(event.getItem().getId())));
            }
            if (event.getItem().getDemandType().getId() == DemandType.TEMPORAL) {
                UI.getCurrent().navigate(DemandEditTemporal.class, new RouteParameters("demandID",
                        String.valueOf(event.getItem().getId())));
            }
            if (event.getItem().getDemandType().getId() == DemandType.GENERAL) {
                UI.getCurrent().navigate(DemandEditeGeneral.class, new RouteParameters("demandID",
                        String.valueOf(event.getItem().getId())));
            }
        });

        filterId.setLabel("Поиск по номеру задачи");
        filterId.setHelperText("После ввода номера нажмите Enter");
        filterId.setPlaceholder("Номер заявки");
        filterId.addKeyDownListener(Key.ENTER,event -> {
            filterText.setValue("");
            if(filterId.getValue()!=null){
                try {
                    gridSetting(Long.valueOf(filterId.getValue()),null);
                } catch (Exception e) {
                    Notification notification = new Notification(
                            "Задачи с таким номером не найдено", 5000,
                            Notification.Position.MIDDLE);
                    notification.open();
                    gridSetting(null,null);
                }
            } else {
                gridSetting(null,null);
            }
        });
        filterText.setLabel("Поиск по содержимому полей Заявитель, Объект и Адрес");
        filterText.setHelperText("После ввода текста нажмите Enter");
        filterText.setPlaceholder("Любой текст");
        filterText.setWidthFull();
        filterText.addKeyDownListener(Key.ENTER,event -> {
            filterId.setValue("");
            if(filterText.getValue()!=null){
                gridSetting(null,filterText.getValue());
            } else {
                gridSetting(null,null);
            }
        });
        clearFilter.setText("Очистить фильтр");
        clearFilter.addClickListener(event -> {
            filterId.setValue("");
            filterText.setValue("");
            gridSetting(null,null);
        });

        filterLayout.add(filterId,filterText,clearFilter);
        filterLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        //verticalLayout.add(filterLayout,grid);
        grid.getElement().setAttribute("title","кликните дважды для открытия заявки");
        add(filterLayout,grid);
    }

    private void gridSetting(Long id, String text) {
        Role role = Role.ANONYMOUS;
        // Определим текущего пользователя
        User currentUser =  this.userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        grid.setPageSize(20);
        grid.setSortableColumns("id","object","address");
        // Определим роль и кол-во заявок
        if(currentUser.getRoles().contains(Role.ADMIN)) {
            filterVisible(true);
            role = Role.ADMIN;
        } else if(currentUser.getRoles().contains(Role.GARANT)) {
            filterVisible(true);
            role = Role.GARANT;
        } else if(currentUser.getRoles().contains(Role.USER)) {
            filterVisible(false);
            role = Role.USER;
        }
        // Поиск по номеру заявки
        if(id != null && text == null) {
            if(demandService.findById(id).isEmpty()) {
                Notification notification = new Notification(
                        "Задача с таким номером не найдена", 5000,
                        Notification.Position.MIDDLE);
                notification.open();
                return;
            }
            switch(role){
                case ADMIN:
                    grid.setItems(demandService.findById(id).get());
                    break;
                case GARANT:
                    grid.setItems(demandService.findByIdAndGarant(id,currentUser.getGarant()).get());
                    break;
            }
            return;
        }
        // поиск по тексту в Заявителе, Объекте или Адресе
        if (text != null && id == null) {
            switch(role){
                case ADMIN:
                    grid.setItems(demandService.findText(text));
                    break;
                case GARANT:
                    grid.setItems(demandService.findText(text,
                                    currentUser.getGarant().getId()));
                    break;
            }
            return;
        }
        // вывод всех заявок доступных пользователю
        switch(role){
            case ADMIN:
                grid.setItems(query ->
                        demandService.findAll(
                                PageRequest.of(query.getPage(), query.getPageSize(),
                                        VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
                break;
            case GARANT:
                grid.setItems(query ->
                        demandService.findAllByGarant(currentUser.getGarant(),
                                PageRequest.of(query.getPage(), query.getPageSize(),
                                        VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
                break;
            case USER:
                grid.setItems(query ->
                        demandService.findAllByUser(currentUser,
                                PageRequest.of(query.getPage(), query.getPageSize(),
                                        VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
                break;
        }
    }

    private void filterVisible(Boolean visible) {
        filterText.setVisible(visible);
        filterId.setVisible(visible);
        clearFilter.setVisible(visible);
        demanderColumn.setVisible(visible);
    }
}