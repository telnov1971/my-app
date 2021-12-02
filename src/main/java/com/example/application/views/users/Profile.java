package com.example.application.views.users;

import com.example.application.data.entity.Role;
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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

@Route(value = "profile/:userID?", layout = MainView.class)
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор профиля пользователя")
public class Profile extends Div implements BeforeEnterObserver {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;
    private final User user = new User();
    private final BeanValidationBinder<User> userBinder = new BeanValidationBinder<>(User.class);
    private final TextField username;
    private final PasswordField password;
    private final PasswordField passwordVerify;
    private final EmailField email;
    private final TextField fio;
    private final TextField contact;
    private final Label notyfy;
    private final Button save = new Button("Зарегистрировать");

    public Profile(UserService userService,
                   PasswordEncoder passwordEncoder,
                   MailSenderService mailSenderService,
                   Component... components) {
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
        fio = new TextField("ФИО пользователя");
        contact = new TextField("Контактный телефон");
        userBinder.bindInstanceFields(this);

        username.addValueChangeListener(e -> saveButtonActive());
        email.addValueChangeListener(e -> saveButtonActive());
        password.addValueChangeListener(e -> saveButtonActive());
        passwordVerify.addValueChangeListener(e -> saveButtonActive());
        fio.addValueChangeListener(e -> saveButtonActive());
        contact.addValueChangeListener(e -> saveButtonActive());

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
            if(fio.getValue().equals("")) {
                notyfy.setText("ФИО не может быть пустой");
                notyfy.setVisible(true);
                return;
            }
            if(contact.getValue().equals("")) {
                notyfy.setText("Контактный телефон не может быть пустым");
                notyfy.setVisible(true);
                return;
            }
            if(password.getValue().equals(passwordVerify.getValue())) {
                if(userService.findByUsername(user.getUsername())==null){
                    userBinder.writeBeanIfValid(user);
                    user.setPassword(this.passwordEncoder.encode(user.getPassword()));
                    user.setRoles(Set.of(Role.USER));
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
        Button recover = new Button("Восстановить");
        recover.addClickListener(event -> {
            if(password.getValue().equals(passwordVerify.getValue())) {
                User existUser = null;
                if(!username.getValue().equals(""))
                    existUser = userService.findByUsername(username.getValue());
                if(existUser==null && !email.getValue().equals(""))
                    existUser = userService.findByEmail(email.getValue());
                if(existUser!=null) {
                    existUser.setPassword(this.passwordEncoder.encode(password.getValue()));
                    existUser.setActive(false);
                    existUser.setActivationCode(UUID.randomUUID().toString());
                    userService.update(existUser);
                    sendMessage(existUser);
                    UI.getCurrent().navigate("/");
                } else {
                    notyfy.setText("Такой пользователь не существует");
                    notyfy.setVisible(true);
                }
            } else {
                notyfy.setText("Пароли не совпадают");
                notyfy.setVisible(true);
            }
        });
        Button reset = new Button("Отменить");
        reset.addClickListener(event -> {
            // clear fields by setting null
            userBinder.readBean(null);
            notyfy.setVisible(false);
            UI.getCurrent().navigate("/");
        });

        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonBar.setSpacing(true);
        reset.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonBar.add(save, recover, reset);

        FormLayout userForm = new FormLayout();
        userForm.add(username, email, password, passwordVerify, fio, contact);
        Label note = new Label("Для регистрации введите логин, е-майл и пароль. Для " +
                "восстановления пароля введите логин или е-майл. Для активации аккаунта необходимо " +
                "перейти по ссылке в присланом письме.");
        add(notyfy, userForm, buttonBar, note);
        this.getElement().getStyle().set("margin", "15px");
        saveButtonActive();
    }

    private void saveButtonActive() {
        save.setEnabled(!username.getValue().equals("") &&
                !email.getValue().equals("") &&
                !password.getValue().equals("") &&
                !passwordVerify.getValue().equals("") &&
                !fio.getValue().equals("") &&
                !contact.getValue().equals(""));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
//        Optional<Long> userId = event.getRouteParameters().getLong(USER_ID);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userService.findByUsername(username);
//        UI.getCurrent().navigate(Profile.class, new RouteParameters("userID",
//                String.valueOf(user.getId())));

//        if (userId.isPresent()) {
        if (username != null) {
//            Optional<User> userFromBackend = userService.findById(userId.get());
            User userFromBackend = userService.findByUsername(username);
//            if (userFromBackend.isPresent()) {
            if (userFromBackend != null) {
//                if (userFromBackend.get().getUsername().
                if (userFromBackend.getUsername().
                        equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
//                    populateForm(userFromBackend.get());
                    populateForm(userFromBackend);
                } else {
                    Notification.show("Это не Вы :(", 3000,
                            Notification.Position.BOTTOM_START);
                    clearForm();
                }
            } else {
                clearForm();
            }
        }
    }

    private void clearForm() {
        populateForm(null);
        notyfy.setVisible(false);
    }

    private void populateForm(User value) {
        if(value != null) {
            user.setId(value.getId());
            user.setUsername(value.getUsername());
            user.setEmail(value.getEmail());
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
