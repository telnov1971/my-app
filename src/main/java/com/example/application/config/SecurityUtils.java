package com.example.application.config;

import com.vaadin.flow.shared.ApplicationConstants;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import com.vaadin.flow.server.HandlerHelper.RequestType;
import java.util.stream.Stream;

public final class SecurityUtils {
    private SecurityUtils() {
    }

    /**
     * Проверяет, аутентифицирован ли пользователь. Поскольку Spring Security всегда будет создавать
     * {@link AnonymousAuthenticationToken} мы должны явно игнорировать эти маркеры.
     */
    static boolean isUserLoggedIn() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();     // Получает токен аутентификации из контекста безопасности
        boolean result = authentication != null                             // Ошибка, если аутентификация недоступна
            && !(authentication instanceof AnonymousAuthenticationToken)    // Сбой для токенов анонимной аутентификации. Spring Security добавит этот тип
                                                                            // токена, если все остальные механизмы аутентификации по умолчанию не сработали.
            && authentication.isAuthenticated();                            // Ошибка, если токен аутентификации доступен, но не аутентифицирован.
        return result;
    }

    /**
     * Проверяет если запрос внутренний для каркаса. Содержит
     * проверку наличия параметров запроса и их соответствия известным типам
     *
     * @param request {@link HttpServletRequest}
     * @return true если запрос внутренний. False иначе.
     */
    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
                && Stream.of(RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }


}
