package ru.omel.po.views.users;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.omel.po.data.entity.User;
import ru.omel.po.data.service.MailSenderService;
import ru.omel.po.data.service.UserService;

import javax.validation.constraints.NotNull;

@Route(value = "forgot" ) //, layout = MainView.class)
@PageTitle("Востановление доступа в личный кабинет")
public class Forgot extends Div {
    private enum Type {EMAIL,LOGIN}
    private final TextField email;
    private final TextField login;
    private final Label label;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;
    private User editUser = new User();

    public Forgot(UserService userService, PasswordEncoder passwordEncoder, MailSenderService mailSenderService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSenderService;

        VerticalLayout verticalLayout = new VerticalLayout();
        Label title = new Label("Введите данные используемые при регистрации в личном кабинете");
        email = new TextField("E-mail");
        email.setMaxWidth("90%");
        email.setWidth("30em");
        login = new TextField("Логин (имя пользователя)");
        login.setMaxWidth("90%");
        login.setWidth("30em");
        Label labelOr = new Label("ИЛИ");
        Button button = new Button("Выслать новый сгенерированный пароль");
        label = new Label("");
        label.getElement().setAttribute("style","color:red");
        verticalLayout.add(title,email,labelOr,login,button,label);
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.setAlignSelf(FlexComponent.Alignment.CENTER);
        button.addClickListener(event -> {
            boolean successful = false;
            if(email.getValue() != null) {
                if (!email.getValue().isEmpty()) {
                    successful = passwordGenerated(email.getValue(), Type.EMAIL);
                }
            }
            if(login.getValue() != null) {
                if (!login.getValue().isEmpty()) {
                    successful = passwordGenerated(login.getValue(), Type.LOGIN);
                }
            }
            if(!successful){
                label.setText("Вам нужно указать e-mail или логин для Вашей идентификации");
            } else {
                UI.getCurrent().navigate("/");
            }
        });
        add(verticalLayout);
    }
    private boolean passwordGenerated(String text, Forgot.Type type) {
        String error = "";
        switch (type) {
            case EMAIL -> {
                editUser = userService.findByEmail(text);
                error = "Такого E-mail не зарегистрировано";
            }
            case LOGIN -> {
                editUser = userService.findByUsername(login.getValue());
                error = "Такого логина не зарегистрировано";
            }
        }
        if(editUser != null) {
            String passwordExplicit = (passwordEncoder.encode(editUser.getEmail())).substring(0,7);
            String password = passwordEncoder.encode(passwordExplicit);
            editUser.setPassword(password);
            userService.update(editUser);
            sendMessage(editUser, passwordExplicit);
            return true;
        } else {
            label.setText(error);
            return false;
        }
    }
    private void sendMessage(@NotNull User user, String passwordExplicit){
        if (!user.getEmail().isEmpty()) {
            String message = String.format(
                    "Здравствуйте, %s! \n" +
                            "Для входа в Ваш личный кабинет на сайте АО Омскэлектро Ваш:.\n" +
                            "Логин (имя пользователя): %s\n" +
                            "Пароль: %s.\n" +
                            "Рекомендуем сменить пароль после входа",
                    user.getFio(),
                    user.getUsername(),
                    passwordExplicit
            );
            mailSenderService.send(user.getEmail(), "Востановление входа в личный кабинет Омскэлектро", message);
        }
    }
}
