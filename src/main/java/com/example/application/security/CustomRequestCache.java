package com.example.application.security;

import com.example.application.views.users.LoginView;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomRequestCache extends HttpSessionRequestCache {
    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        if (!SecurityUtils.isFrameworkInternalRequest(request)) {
            super.saveRequest(request, response);
        }
    }

    public String resolveRedirectUrl() {
        SavedRequest savedRequest =
                getRequest(VaadinServletRequest.getCurrent().getHttpServletRequest(),
                        VaadinServletResponse.getCurrent().getHttpServletResponse());
        if(savedRequest instanceof DefaultSavedRequest) {
            // получить сохраненный URL-адрес перенаправления
            final String requestURI = ((DefaultSavedRequest) savedRequest).getRequestURI();
            // проверить правильность URI и запретить перенаправление на вход в систему
            if(requestURI != null && !requestURI.isEmpty() && !requestURI.contains(LoginView.ROUTE)) {
                // вернуть адрес без начальной '/'
                return requestURI.startsWith("/") ? requestURI.substring(1) : requestURI;
            }
        }
        // если ничего не получилось, перенаправьте на основную страницу
        return "";
    }
}
