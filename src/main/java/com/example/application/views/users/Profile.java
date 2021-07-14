package com.example.application.views.users;

import com.example.application.data.entity.User;
import com.example.application.data.service.MailSenderService;
import com.example.application.data.service.UserService;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Route(value = "profile/:userID?", layout = MainView.class)
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор профиля пользователя")
public class Profile extends Div implements BeforeEnterObserver {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;
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
    private Button recover = new Button("Восстановить");
    private Button reset = new Button("Отменить");
    private Label note = new Label("Для регистрации введите логин, е-майл и пароль. Для " +
            "восстановления пароля введите логин или е-майл. Для активации аккаунта необходимо " +
            "перейти по ссылке в присланом письме.");

    public Profile(UserService userService, PasswordEncoder passwordEncoder, MailSenderService mailSenderService, Component... components) {
        super(components);
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSenderService;

        notyfy = new Label("");
        notyfy.setVisible(false);
        notyfy.addClassName("warning");
        username = new TextField("Логин");
        password = new PasswordField("Пароль");
        passwordVerify = new PasswordField("Проверка пароля");
        email = new EmailField("E-mail");
        userBinder.bindInstanceFields(this);
        save.addClickListener(event -> {
            if(username.getValue().equals("")) {
                notyfy.setText("Имя пользователя не может быть пустым");
                notyfy.setVisible(true);
                return;
            }
            if(password.getValue().equals("")) {
                notyfy.setText("Пароль не может быть пустым");
                notyfy.setVisible(true);
                return;
            }
            if(email.getValue().equals("")) {
                notyfy.setText("Почта не может быть пустой");
                notyfy.setVisible(true);
                return;
            }
            if(password.getValue().equals(passwordVerify.getValue())) {
                if(userService.findByUsername(user.getUsername())==null){
                    //user.setPassword(passwordEncoder.encode(password.getValue()));
                    userBinder.writeBeanIfValid(user);
                    user.setPassword(this.passwordEncoder.encode(user.getPassword()));
                    user.setActive(false);
                    user.setActivationCode(UUID.randomUUID().toString());
                    sendMessage(user);

                    userService.update(this.user);
                    UI.getCurrent().navigate("/");
                } else {
                    notyfy.setText("Такой пользователь уже существует");
                    notyfy.setVisible(true);
                }
            } else {
                notyfy.setText("Пароли не совпадают");
                notyfy.setVisible(true);
            }
        });
        recover.addClickListener(event -> {
            if((userService.findByUsername(user.getUsername())!=null) ||
                    (userService.findByEmail(user.getEmail())!=null)) {
                userBinder.writeBeanIfValid(user);
                user.setPassword(this.passwordEncoder.encode(user.getPassword()));
                user.setActive(false);

                userService.update(this.user);
                UI.getCurrent().navigate("/");
            } else {
                notyfy.setText("Такой пользователь уже существует");
                notyfy.setVisible(true);
            }
        });
        reset.addClickListener(event -> {
            // clear fields by setting null
            userBinder.readBean(null);
            notyfy.setVisible(false);
            UI.getCurrent().navigate("/");
        });

        buttonBar.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonBar.setSpacing(true);
        reset.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonBar.add(save,recover, reset);

        userForm.add(username, email, password, passwordVerify);
        add(notyfy, userForm, buttonBar, note);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> userId = event.getRouteParameters().getLong(USER_ID);
        if (userId.isPresent()) {
            Optional<User> userFromBackend = userService.findById(userId.get());
            if (userFromBackend.isPresent()) {
                if (userFromBackend.get().getUsername().
                        equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
                    populateForm(userFromBackend.get());
                } else {
                    Notification.show("Это не Вы :(", 3000,
                            Notification.Position.BOTTOM_START);
                    clearForm();
                }
            } else {
                //Notification.show(String.format("The requested demand was not found, ID = %d", demandId.get()), 3000,
                //Notification.Position.BOTTOM_START);
                clearForm();
            }
        } else {
//            Stream<Component> components = getParent().get().getChildren();
//            components.filter(c ->
//                    c.getClass().equals(MenuBar.class))
//                    .forEach(c -> c.setVisible(false));
        }
    }

    private void clearForm() {
        populateForm(null);
        notyfy.setVisible(false);
    }

    private void populateForm(User value) {
        userFromDB = value;
        if(userFromDB != null) {
            user.setId(userFromDB.getId());
            user.setUsername(userFromDB.getUsername());
            user.setEmail(userFromDB.getEmail());
        }
        userBinder.readBean(user);
    }

    private void sendMessage(User user){
        String host = "http://" + VaadinRequest.getCurrent().getHeader("host");
                //VaadinService.getCurrentRequest().getPathInfo();
        if (!user.getEmail().isEmpty()) {
            String message = String.format(
                    "Здравствуйте, %s! \n" +
                            "Добро пожаловать в Личный кабинет АО Омскэлектро.\n" +
                            "Пожалуйста перейдите по ссылке: %s/activate/%s\n" +
                            "для активации вашей регистрации.",
                    user.getUsername(),
                    host,
                    user.getActivationCode()
            );
            mailSenderService.send(user.getEmail(), "Activation code", message);
        }

    }
}
