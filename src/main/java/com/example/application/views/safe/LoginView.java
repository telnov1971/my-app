package com.example.application.views.safe;

import com.example.application.config.CustomRequestCache;
import com.example.application.views.demandlist.DemandList;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.router.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@Tag("sa-login-view")
@Route(value = "login", layout = MainView.class)
@PageTitle("Вход в личный кабинет")
public class LoginView extends Div  implements BeforeEnterObserver {
    public static final CharSequence ROUTE = "login";
    private LoginForm component = new LoginForm();

    public LoginView(AuthenticationManager authenticationManager, // запрашивает подтверждение входа
                     CustomRequestCache requestCache) {
        addClassName("login-view");
        setSizeFull();
        component.addLoginListener(e -> {
            try{
                // try to authenticate with given credentials, should always return not null or throw an {@link AuthenticationException}
                final Authentication authentication = authenticationManager
                // Запускает процесс аутентификации с создания объекта запроса аутентификации и
                // позволяет менеджеру сделать все остальное. В случае успеха мы получаем полностью
                // настроенный объект аутентификации.
                        .authenticate(new UsernamePasswordAuthenticationToken(e.getUsername(),
                                e.getPassword()));
                // if authentication was successful we will update the security context and redirect to the page requested first
                SecurityContextHolder.getContext().setAuthentication(authentication); // (5)
                // component.close(); // (6)
                UI.getCurrent().navigate(requestCache.resolveRedirectUrl()); // (7)
            } catch(AuthenticationException ex) {
                // show default error message
                // Note: You should not expose any detailed information here like "username is known but password is wrong"
                // as it weakens security.
                component.setError(true);
            }
            /*boolean isAuthenticated = authenticate(e);
            if (isAuthenticated) {
                UI.getCurrent().navigate(DemandList.class);
            } else {
                component.setError(true);
            }*/
        });

        component.setI18n(createRussianLoginI18n());
        component.setAction("login");
        add(component);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            component.setError(true);
        }
    }


    private boolean authenticate(AbstractLogin.LoginEvent e) {
        return true;
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
