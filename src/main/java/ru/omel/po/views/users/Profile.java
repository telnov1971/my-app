package ru.omel.po.views.users;

import com.vaadin.flow.component.notification.NotificationVariant;
import ru.omel.po.data.entity.Role;
import ru.omel.po.data.entity.User;
import ru.omel.po.data.service.MailSenderService;
import ru.omel.po.data.service.UserService;
import ru.omel.po.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Route(value = "profile/:userID?")//, layout = MainView.class)
//@Route(value = "demandto15/:demandID?/:action?(edit)", layout = MainView.class)
@PageTitle("Редактор профиля пользователя")
public class Profile extends Div implements BeforeEnterObserver {
    protected final String USER_ID = "userID";
    private boolean editMode = false;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;
    private final User editUser = new User();
    private final BeanValidationBinder<User> userBinder = new BeanValidationBinder<>(User.class);
    private final TextField username;
    private final PasswordField password;
    private final PasswordField passwordVerify;
    private final EmailField email;
    private final TextField fio;
    private final TextField contact;
    private final Label notyfy;
    private final Label note;
    private final Label subnote;
    private final Button saveButton = new Button("Зарегистрировать");

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
        notyfy.getElement().setAttribute("style","color:red");
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

        saveButton.addClickListener(event -> registration());

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
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonBar.add(saveButton, reset);

        FormLayout userForm = new FormLayout();
        userForm.add(username, email, password, passwordVerify, fio, contact);
        note = new Label("Для регистрации заполните все поля. " +
                "Для активации аккаунта необходимо " +
                "перейти по ссылке в присланом письме.");
        note.getElement().getStyle().set("color", "red");
        note.getElement().getStyle().set("font-size", "1.5em");
        note.setHeight("1em");
        subnote = new Label("В качестве логина и пароля рекомендуется " +
                "использовать только латинские буквы, цифры и знаки: @ # _ & . ");
        subnote.getElement().getStyle().set("color", "black");
        subnote.getElement().getStyle().set("font-size", "0.8em");
        subnote.setHeight("0.9em");
        VerticalLayout notesLayout = new VerticalLayout();
        notesLayout.add(note, subnote);
        add(notyfy, notesLayout, userForm, buttonBar);
        this.getElement().getStyle().set("margin", "15px");
        saveButtonActive();
    }

    private void registration() {
        if(verifyFill()) {
            if (password.getValue().equals(passwordVerify.getValue())) {
                User existUser = userService.findByUsername(username.getValue());
                if((existUser != null) && !editMode) {
                    notyfy.setText("Такое имя пользователя уже существует");
                    notyfy.setVisible(true);
                    return;
                }
                userBinder.writeBeanIfValid(editUser);
                if(!editMode){
                    editUser.setCreateDate(LocalDateTime.now());
                    editUser.setPassword(this.passwordEncoder.encode(password.getValue()));
                    editUser.setRoles(Set.of(Role.USER));
                    if(editUser.getEmail().contains("support@omskelectro.ru")) {
                        editUser.setActive(true);
                    } else {
                        editUser.setActive(false);
                        editUser.setActivationCode(UUID.randomUUID().toString());
                        sendMessage(editUser);
                    }
                } else {
                    if(password.getValue().equals(""))
                        editUser.setPassword(existUser.getPassword());
                    else
                        editUser.setPassword(this.passwordEncoder.encode(password.getValue()));
                }
                userService.update(this.editUser);
//                if (!editMode) {
                    UI.getCurrent().navigate("/");
//                }
            } else {
                notyfy.setText("Пароли не совпадают");
                notyfy.setVisible(true);
            }
        }
    }

    private boolean verifyFill() {
        if(username.getValue().equals("")) {
            notyfy.setText("Имя пользователя не может быть пустым");
            notyfy.setVisible(true);
            return false;
        }
        if(!editMode && password.getValue().equals("")) {
            notyfy.setText("Пароль не может быть пустым");
            notyfy.setVisible(true);
            return false;
        }
        if(email.getValue().equals("")) {
            notyfy.setText("Почта не может быть пустой");
            notyfy.setVisible(true);
            return false;
        }
        if(fio.getValue().equals("")) {
            notyfy.setText("ФИО не может быть пустой");
            notyfy.setVisible(true);
            return false;
        }
        if(contact.getValue().equals("")) {
            notyfy.setText("Контактный телефон не может быть пустым");
            notyfy.setVisible(true);
            return false;
        }
        return true;
    }

    private void saveButtonActive() {
        saveButton.setEnabled(!username.getValue().equals("") &&
                !email.getValue().equals("") &&
                (editMode || !password.getValue().equals("")) &&
                (editMode || !passwordVerify.getValue().equals("")) &&
                !fio.getValue().equals("") &&
                !contact.getValue().equals(""));
        if (!password.getValue().equals(passwordVerify.getValue())){
            notyfy.setText("Пароли не совпадают");
            notyfy.setVisible(true);
        } else {
            notyfy.setText("");
            notyfy.setVisible(false);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> userdId = event.getRouteParameters().getLong(USER_ID);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username != null) {
            User userFromBackend = userService.findByUsername(username);
            if (userFromBackend != null) {
                if(userFromBackend.getRoles().contains(Role.ADMIN)) {
                    if(userdId.isPresent()) {
                        if(userService.findById(userdId.get()).isPresent()) {
                            populateForm((userService.findById(userdId.get())).get());
                            return;
                        }
                    }
                }
                if (userFromBackend.getUsername().
                        equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
                    populateForm(userFromBackend);
                } else {
                    Notification notification = new Notification();
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setText("Это не Вы :(");
                    notification.setPosition(Notification.Position.BOTTOM_START);
                    notification.setDuration(3000);
                    notification.open();

                    clearForm();
                }
            } else {
                clearForm();
            }
        }
    }

    private void clearForm() {
        editMode = false;
        populateForm(null);
        notyfy.setText("");
        notyfy.setVisible(false);
    }

    private void populateForm(User value) {
        if(value != null) {
            editMode = true;
            editUser.setId(value.getId());
            editUser.setUsername(value.getUsername());
            editUser.setEmail(value.getEmail());
            editUser.setPassword(value.getPassword());
            editUser.setActive(value.getActive());
            editUser.setRoles(value.getRoles());
            editUser.setFio(value.getFio());
            editUser.setContact(value.getContact());
            saveButton.setText("Сохранить");
            note.setVisible(false);
        }
        userBinder.readBean(editUser);
        password.setValue("");
        passwordVerify.setValue("");
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
