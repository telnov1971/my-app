package com.example.application.config;

import com.example.application.views.safe.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

// Позволяет добавить слушатель навигации глобально ко всем экземплярам
// пользовательского интерфейса с помощью инициализации службы прослушивания.
@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {
    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter); // Добавляет перехват всех входов
        });
    }

    /**
     * Перенаправляет пользователя, если он не имеет права доступа к странице.
     *
     * @param event
     * перед навигацией событие с подробной информацией о событии
     */
    private void beforeEnter(BeforeEnterEvent event) {
        if(!LoginView.class.equals(event.getNavigationTarget()) // Пропускает к самой странице входа в систему
                &&!SecurityUtils.isUserLoggedIn()) { // Перенаправляет только если пользователь не вошел в систему
            event.rerouteTo(LoginView.class); // Фактическое перенаправление на страницу входа при необходимости
        }
    }
}
