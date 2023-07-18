package ru.omel.po.views.admin;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.omel.po.data.entity.User;
import ru.omel.po.data.service.UserService;
import ru.omel.po.views.main.MainView;
import ru.omel.po.views.users.Profile;

import java.time.LocalDateTime;
import java.util.*;

@PageTitle("Список пользователей")
@Route(value = "users", layout = MainView.class)
//implements BeforeEnterObserver
public class ListUsers  extends Div  {
    private final Grid<User> grid = new Grid<>(User.class, false);
    private final TextField filterText = new TextField();

    private final UserService userService;

    public ListUsers(UserService userService) {
        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.getElement().getStyle().set("margin", "10px");
        this.userService = userService;
        grid.setAllRowsVisible(true);
        grid.setHeightFull();
        grid.setPageSize(20);
        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        grid.addColumn(User::getActive)
                .setHeader("Актив.");
        grid.addColumn(User::getUsername)
                .setHeader("Логин")
                .setResizable(true)
                .setSortable(true)
                .setAutoWidth(true);
        grid.addColumn(User::getFio)
                .setHeader("ФИО")
                .setResizable(true)
                .setSortable(true)
                .setAutoWidth(true);
        grid.addColumn(User::getEmail)
                .setHeader("E-mail")
                .setResizable(true)
                .setSortable(true)
                .setAutoWidth(true);
        grid.addComponentColumn(user -> {
            Button edit = new Button(new Icon(VaadinIcon.EDIT));
            edit.addClassName("edit");
            edit.getElement().setAttribute("title","открыть");
            edit.addClickListener(e ->
                    UI.getCurrent().navigate(Profile.class,
                            new RouteParameters("userID",
                                    String.valueOf(user.getId()))));
            editButtons.add(edit);
            return edit;
        }).setAutoWidth(true).setResizable(true);
        grid.setPageSize(20);
        gridSetting("");

        filterText.setLabel("Поиск по содержимому полей Логин, ФИО и E-mail");
        filterText.setHelperText("После ввода текста нажмите Enter");
        filterText.setPlaceholder("Любой текст");
        filterText.setWidthFull();
        filterText.addKeyDownListener(Key.ENTER,event -> {
            if(filterText.getValue()!=null){
                gridSetting(filterText.getValue());
            } else {
                gridSetting("");
            }
        });
        Button clearFilter = new Button(new Icon(VaadinIcon.ERASER));
        clearFilter.setText("Очистить фильтр");
        clearFilter.addClickListener(event -> {
            filterText.setValue("");
            gridSetting("");
        });

        filterLayout.add(filterText, clearFilter);
        filterLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        TextField space = new TextField();
        space.setWidthFull();
        space.setReadOnly(true);

        add(filterLayout, grid, space);
    }
    private void gridSetting(String text) {
        // Определим текущего пользователя
        User currentUser =  this.userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        currentUser.setVisitDate(LocalDateTime.now());
        userService.update(currentUser);
        grid.setPageSize(20);

        // поиск по тексту в Логин, ФИО и E-mail
        if (!text.equals("")) {
            List<User> listUser = userService.findText(text);
            if(listUser != null) {
                grid.setItems(listUser);
            }
            else {
                Notification notification = new Notification(
                        "Такой текст не найден", 5000,
                        Notification.Position.MIDDLE);
                notification.open();
            }
        }
    }

}
