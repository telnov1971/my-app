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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.WeakHashMap;

@Route(value = "demandlist", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Список заявок")
public class DemandList extends Div {
    private final TextField filterId = new TextField();
    private final TextField filterText = new TextField();
    private final Grid.Column<Demand> demanderColumn;
    private final Button clearFilter = new Button(new Icon(VaadinIcon.ERASER));
    private final Grid<Demand> grid = new Grid<>(Demand.class, false);

    private final DemandService demandService;
    private final UserService userService;

    //@Autowired
    public DemandList(DemandService demandService, UserService userService) {
        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.getElement().getStyle().set("margin", "10px");
        this.demandService = demandService;
        this.userService = userService;
        addClassNames("master-detail-view", "flex", "flex-col", "h-full");

        // Configure Grid
        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        grid.addComponentColumn(demand -> {
            Button edit = new Button(new Icon(VaadinIcon.EDIT));
//            edit.setText("ОТКРЫТЬ");
            edit.addClassName("edit");
            edit.getElement().setAttribute("title","открыть");
            edit.addClickListener(event -> {
                if (Objects.equals(demand.getDemandType().getId(), DemandType.TO15)) {
                    UI.getCurrent().navigate(DemandEditTo15.class, new RouteParameters("demandID",
                            String.valueOf(demand.getId())));
                }
                if (Objects.equals(demand.getDemandType().getId(), DemandType.TO150)) {
                    UI.getCurrent().navigate(DemandEditTo150.class, new RouteParameters("demandID",
                            String.valueOf(demand.getId())));
                }
                if (Objects.equals(demand.getDemandType().getId(), DemandType.TEMPORAL)) {
                    UI.getCurrent().navigate(DemandEditTemporal.class, new RouteParameters("demandID",
                            String.valueOf(demand.getId())));
                }
                if (Objects.equals(demand.getDemandType().getId(), DemandType.GENERAL)) {
                    UI.getCurrent().navigate(DemandEditeGeneral.class, new RouteParameters("demandID",
                            String.valueOf(demand.getId())));
                }
            });
            edit.setEnabled(true);
            editButtons.add(edit);
            return edit;
        }).setResizable(true).setWidth("20px");
        grid.addColumn("id").setResizable(true).setWidth("5ex").setHeader("ID");
        demanderColumn = grid.addColumn("demander").setHeader("Заявитель")
                .setResizable(true).setWidth("20ex");
        grid.addColumn("status.name").setResizable(true).setWidth("10ex").setHeader("Статус");
        grid.addColumn("object").setHeader("Объект")
                .setResizable(true).setWidth("20ex");
        grid.addColumn("address").setHeader("Адрес объекта")
                .setResizable(true).setWidth("20ex");
        grid.addColumn("demandType.name").setResizable(true).setWidth("7ex").setHeader("Тип");
        grid.addComponentColumn(demand -> new Label(demand.getCreateDate()
                        .format(DateTimeFormatter.ofPattern("uuuu-MM-dd _ HH:mm:ss"))))
                .setHeader("Дата и время").setResizable(true).setWidth("10ex");

        gridSetting(null,null);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        grid.addItemDoubleClickListener(event ->{
            if (Objects.equals(event.getItem().getDemandType().getId(), DemandType.TO15)) {
                UI.getCurrent().navigate(DemandEditTo15.class, new RouteParameters("demandID",
                        String.valueOf(event.getItem().getId())));
            }
            if (Objects.equals(event.getItem().getDemandType().getId(), DemandType.TO150)) {
                UI.getCurrent().navigate(DemandEditTo150.class, new RouteParameters("demandID",
                        String.valueOf(event.getItem().getId())));
            }
            if (Objects.equals(event.getItem().getDemandType().getId(), DemandType.TEMPORAL)) {
                UI.getCurrent().navigate(DemandEditTemporal.class, new RouteParameters("demandID",
                        String.valueOf(event.getItem().getId())));
            }
            if (Objects.equals(event.getItem().getDemandType().getId(), DemandType.GENERAL)) {
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
//        grid.getElement().setAttribute("title","кликните дважды для открытия заявки");
        TextField space = new TextField();
        space.setWidthFull();
        space.setReadOnly(true);

        add(filterLayout,grid,space);
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
                    if(demandService.findById(id).isPresent())
                        grid.setItems(demandService.findById(id).get());
                    break;
                case GARANT:
                    if(demandService.findByIdAndGarant(id,currentUser.getGarant()).isPresent())
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