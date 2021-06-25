package com.example.application.security;

import com.example.application.views.demandedit.DemandEditTo15;
import com.example.application.views.demandlist.DemandList;
import com.example.application.views.users.LoginView;
import com.example.application.views.users.Profile;
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
        // Пропускает к самой странице входа в систему
        // Перенаправляет только если пользователь не вошел в систему
        if(!SecurityUtils.isUserLoggedIn()){
            if(LoginView.class.equals(event.getNavigationTarget())
            || Profile.class.equals(event.getNavigationTarget())) {
                return;
            }
            event.rerouteTo(LoginView.class);
        }
//        if(!LoginView.class.equals(event.getNavigationTarget())
//                && !SecurityUtils.isUserLoggedIn()) {
//            // Фактическое перенаправление на страницу входа при необходимости
//            event.rerouteTo(LoginView.class);
//        }
    }
}
