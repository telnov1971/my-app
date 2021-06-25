package com.example.application.views.users;

import com.example.application.data.entity.User;
import com.example.application.data.service.UserService;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.*;

import java.util.Optional;

@Route(value = "profile/:userID?", layout = MainView.class)
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор заявки")
public class Profile extends Div implements BeforeEnterObserver {
    private final UserService userService;
    private final String USER_ID = "userID";
    private User user = new User();
    private BeanValidationBinder<User> userBinder = new BeanValidationBinder<>(User.class);
    private User userFromDB;
    private FormLayout userForm = new FormLayout();
    private HorizontalLayout buttonBar = new HorizontalLayout();
    private TextField username;
    private PasswordField password;
    private PasswordField passwordVerify;
    private EmailField email;
    private Label notyfy;
    private Button save = new Button("Зарегистрировать");
    private Button reset = new Button("Отменить");

    public Profile(UserService userService, Component... components) {
        super(components);
        this.userService = userService;

        username = new TextField("Логин");
        password = new PasswordField("Пароль");
        passwordVerify = new PasswordField("Проверка пароля");
        email = new EmailField("E-mail");
        userBinder.bindInstanceFields(this);
        save.addClickListener(event -> {
            if(userService.findByUsername(user.getUsername())!=null){
                userBinder.writeBeanIfValid(user);

                userService.update(this.user);
                UI.getCurrent().navigate("/");
            } else {
                notyfy.setText("Такой пользователь уже есть");
            }
        });
        reset.addClickListener(event -> {
            // clear fields by setting null
            userBinder.readBean(null);
            UI.getCurrent().navigate("/");
        });

        buttonBar.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonBar.setSpacing(true);
        reset.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonBar.add(save,reset);

        userForm.add(username, email, password, passwordVerify);
        add(userForm, buttonBar);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> userId = event.getRouteParameters().getLong(USER_ID);
        if (userId.isPresent()) {
            Optional<User> userFromBackend = userService.findById(userId.get());
            if (userFromBackend.isPresent()) {
                populateForm(userFromBackend.get());
            } else {
                //Notification.show(String.format("The requested demand was not found, ID = %d", demandId.get()), 3000,
                //Notification.Position.BOTTOM_START);
                clearForm();
            }
        }
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(User value) {
        userFromDB = value;
        user.setId(userFromDB.getId());
        user.setUsername(userFromDB.getUsername());
        user.setEmail(userFromDB.getEmail());
        userBinder.readBean(user);
    }

}
