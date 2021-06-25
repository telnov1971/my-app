package com.example.application.views.safe;

import com.example.application.security.CustomRequestCache;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@Tag("sa-login-view")
@Route(value = LoginView.ROUTE) //, layout = MainView.class)
@PageTitle("Вход в личный кабинет")
//public class LoginView extends Div  implements BeforeEnterObserver {
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE = "login";
    private LoginOverlay login = new LoginOverlay();

    public LoginView(AuthenticationManager authenticationManager, // запрашивает подтверждение входа
                     CustomRequestCache requestCache) {
//        addClassName("login-view");
//        setSizeFull();
//        setAlignItems(Alignment.CENTER);
//        setJustifyContentMode(JustifyContentMode.CENTER);
        login.setOpened(true);
        login.setTitle("Вход в личный кабинет");
        login.setI18n(createRussianLoginI18n());
        login.setAction("login");
        add(login);

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
                login.close();
                UI.getCurrent().navigate(requestCache.resolveRedirectUrl());
            } catch(AuthenticationException ex) {
                // show default error message
                // Note: You should not expose any detailed information here like "username is known but password is wrong"
                // as it weakens security.
                login.setError(true);
            }
            /*boolean isAuthenticated = authenticate(e);
            if (isAuthenticated) {
                UI.getCurrent().navigate(DemandList.class);
            } else {
                component.setError(true);
            }*/
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
        i18n.getForm().setUsername("Пользователь");
        i18n.getForm().setTitle("");
        i18n.getForm().setSubmit("Войти");
        i18n.getForm().setPassword("Пароль");
        i18n.getForm().setForgotPassword("Востановить пароль");
        i18n.getErrorMessage().setTitle("Неверное имя/пароль");
        i18n.getErrorMessage()
                .setMessage("Проверьте имя/пароль и попробуйте ещё раз.");
        i18n.setAdditionalInformation("");
        return i18n;
    }
}
