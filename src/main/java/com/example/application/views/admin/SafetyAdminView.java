package com.example.application.views.admin;

import com.example.application.data.entity.Safety;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.crudui.crud.impl.GridCrud;

@Route(value = "admin/safety")
public class SafetyAdminView extends VerticalLayout {
    public SafetyAdminView() {
        GridCrud<Safety> safetyGridCrud = new GridCrud<>(Safety.class);
        add(safetyGridCrud);
    }
}
