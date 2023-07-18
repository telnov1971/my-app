package ru.omel.po.views.users;

import com.vaadin.flow.component.button.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.omel.po.security.CustomRequestCache;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@Tag("sa-login-view")
@Route(value = LoginView.ROUTE) //, layout = MainView.class)
@PageTitle("Вход в личный кабинет")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE = "login";
    private final LoginForm login = new LoginForm();

    private static final Logger logger = LogManager.getLogger(LoginView.class);

    public LoginView(AuthenticationManager authenticationManager, // запрашивает подтверждение входа
                     CustomRequestCache requestCache) {
        addClassName("login-view");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setHorizontalComponentAlignment(Alignment.CENTER);
        login.setI18n(createRussianLoginI18n());
        login.setAction(LoginView.ROUTE);
        login.addForgotPasswordListener(event -> UI.getCurrent().navigate(Forgot.class));

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(Alignment.CENTER);
        verticalLayout.setAlignSelf(Alignment.CENTER);
        Button registration = new Button("Зарегистрироваться");
        registration.addClickListener(event -> UI.getCurrent().navigate(Profile.class));
        verticalLayout.add(login,registration);

        add(verticalLayout);

        login.addLoginListener(e -> {
            try{
                // try to authenticate with given credentials, should always return not null or throw an {@link AuthenticationException}
                final Authentication authentication = authenticationManager
                // Запускает процесс аутентификации с создания объекта запроса аутентификации и
                // позволяет менеджеру сделать все остальное. В случае успеха мы получаем полностью
                // настроенный объект аутентификации.
                        .authenticate(new UsernamePasswordAuthenticationToken(e.getUsername(),
                                e.getPassword()));
                // if authentication was successful we will update the security context and redirect to the page requested first
                SecurityContextHolder.getContext().setAuthentication(authentication);
                UI.getCurrent().navigate(requestCache.resolveRedirectUrl());
                logger.info(logger.getLevel());
                logger.info(String.format("User login successful: %s", e.getUsername()));
            } catch(AuthenticationException ex) {
                // show default error message
                // Note: You should not expose any detailed information here like "username is known but password is wrong"
                // as it weakens security.
                login.setError(true);
                logger.info(String.format("User not login: %s", e.getUsername()));
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }

    private LoginI18n createRussianLoginI18n() {
        final LoginI18n i18n = LoginI18n.createDefault();

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Личный кабинет");
        i18n.getHeader().setDescription("Для подачи заявок на технологическое присоединение");
        i18n.getForm().setUsername("Логин");
        i18n.getForm().setTitle("");
        i18n.getForm().setSubmit("Войти");
        i18n.getForm().setPassword("Пароль");
        i18n.getForm().setForgotPassword("Забыли пароль?");
        i18n.getErrorMessage().setTitle("Неверное имя/пароль");
        i18n.getErrorMessage()
                .setMessage("Проверьте имя/пароль и попробуйте ещё раз.");
        i18n.setAdditionalInformation("");
        return i18n;
    }
}
