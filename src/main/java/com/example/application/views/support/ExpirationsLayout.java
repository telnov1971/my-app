package com.example.application.views.support;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.Expiration;
import com.example.application.data.entity.Safety;
import com.example.application.data.service.ExpirationService;
import com.example.application.data.service.HistoryService;
import com.example.application.data.service.SafetyService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.*;

public class ExpirationsLayout extends VerticalLayout {
    private Demand demand;
    private HorizontalLayout expirationsButtonLayout = new HorizontalLayout();
    private List<Expiration> expirations;
    private Grid<Expiration> expirationGrid = new Grid<>(Expiration.class, false);
    private ListDataProvider<Expiration> expirationsDataProvider;
    private Binder<Expiration> binderExpiration = new Binder<>(Expiration.class);
    private Editor<Expiration> editorExpiration;

    private final ExpirationService expirationService;
    private final SafetyService safetyService;
    private final HistoryService historyService;

    public ExpirationsLayout(ExpirationService expirationService
            , SafetyService safetyService, HistoryService historyService) {
        this.expirationService = expirationService;
        this.safetyService = safetyService;
        this.historyService = historyService;
        expirationGrid.setHeightByRows(true);
        expirations = new ArrayList<>();

        Grid.Column<Expiration> columnStep =
                expirationGrid.addColumn(Expiration::getStep)
                        .setHeader("Этап/Очередь")
                        .setAutoWidth(true);
        Grid.Column<Expiration> columnPlanProject =
                expirationGrid.addColumn(Expiration::getPlanProject).
                        setAutoWidth(true).
                        setHeader("Срок проектирования");
        Grid.Column<Expiration> columnPlanUsage =
                expirationGrid.addColumn(Expiration::getPlanUsage).
                        setAutoWidth(true).
                        setHeader("Срок ввода");
        Grid.Column<Expiration> columnPowerMax =
                expirationGrid.addColumn(Expiration::getPowerMax).
                        setAutoWidth(true).
                        setHeader("Макс.мощность");
        Grid.Column<Expiration> columnSafety =
                expirationGrid.addColumn(expiration -> expiration.getSafety().getName())
                        .setAutoWidth(true)
                        .setHeader("Кат. надёж.");
        Expiration temp = new Expiration();
        temp.setSafety(safetyService.findById(3L).get());
        expirations.add(temp);
        expirationGrid.setItems(expirations);
        expirationsDataProvider = (ListDataProvider<Expiration>) expirationGrid.getDataProvider();
        expirations.remove(expirations.size() - 1);

        editorExpiration = expirationGrid.getEditor();
        editorExpiration.setBinder(binderExpiration);
        editorExpiration.setBuffered(true);

        TextField fieldStep = new TextField();
        binderExpiration.forField(fieldStep).bind("step");
        columnStep.setEditorComponent(fieldStep);

        TextField fieldPlanProject = new TextField();
        binderExpiration.forField(fieldPlanProject).bind("planProject");
        columnPlanProject.setEditorComponent(fieldPlanProject);

        TextField fieldPlanUsage = new TextField();
        binderExpiration.forField(fieldPlanUsage).bind("planUsage");
        columnPlanUsage.setEditorComponent(fieldPlanUsage);

        NumberField fieldPowerMax= new NumberField();
        fieldPowerMax.setValue(1d);
        fieldPowerMax.setMin(0);
        binderExpiration.forField(fieldPowerMax).bind("powerMax");
        columnPowerMax.setEditorComponent(fieldPowerMax);

        Select<Safety> selectSafety = new Select();
        selectSafety.setItems(this.safetyService.findAll());
        selectSafety.setItemLabelGenerator(Safety::getName);
        binderExpiration.forField(selectSafety).bind("safety");
        columnSafety.setEditorComponent(selectSafety);

        Button addButton = new Button("Добавить этап");

        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        Grid.Column<Expiration> editorColumn = expirationGrid.addComponentColumn(expiration -> {
            Button edit = new Button(new Icon(VaadinIcon.EDIT));
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editorExpiration.editItem(expiration);
                fieldStep.focus();
                addButton.setEnabled(false);
            });
            edit.setEnabled(!editorExpiration.isOpen());
            editButtons.add(edit);
            return edit;
        }).setAutoWidth(true);

        addButton.addClickListener(event -> {
            expirationsDataProvider.getItems().add(new Expiration("",
                    "","",0.0,
                    safetyService.findById(3L).get()));
            expirationsDataProvider.refreshAll();
            expirationGrid.select(expirations.get(expirations.size() - 1));
            editorExpiration.editItem(expirations.get(expirations.size() - 1));
            fieldStep.focus();
            addButton.setEnabled(false);
        });

        Button removeButton = new Button("Удалить последнюю", event -> {
            this.expirations.remove(expirations.size() - 1);
            expirationsDataProvider.refreshAll();
            addButton.setEnabled(true);
        });

        editorExpiration.addOpenListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editorExpiration.isOpen())));
        editorExpiration.addCloseListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editorExpiration.isOpen())));
        Button save = new Button(new Icon(VaadinIcon.CHECK_CIRCLE_O), e -> {
            editorExpiration.save();
            addButton.setEnabled(true);
        });
        save.addClassName("save");
        Button cancel = new Button(new Icon(VaadinIcon.CLOSE_CIRCLE_O), e -> {
            editorExpiration.cancel();
            addButton.setEnabled(true);
            if(fieldStep.getValue().equals("")
                    && fieldPlanProject.getValue().equals("")
                    && fieldPlanUsage.getValue().equals("")
            ) expirations.remove(expirations.size() - 1);
            expirationsDataProvider.refreshAll();
        });
        cancel.addClassName("cancel");
        Div divSave = new Div(save);
        Div divCancel = new Div(cancel);
        Div buttons = new Div(divSave, divCancel);
        editorColumn.setEditorComponent(buttons);

        expirationsButtonLayout.add(addButton,removeButton);
        add(expirationGrid, expirationsButtonLayout);
    }

    public void expirationsClean() {
        expirations = new ArrayList<>();
    }

    public void pointAdd(Expiration expiration) {
        expiration.setSafety(safetyService.findById(3L).get());
        expirations.add(expiration);
        expirationGrid.setItems(expirations);
        expirationsDataProvider = (ListDataProvider<Expiration>) expirationGrid.getDataProvider();
        expirations.remove(expirations.size() - 1);
    }

    public void findAllByDemand(Demand demand) {
        expirations = expirationService.findAllByDemand(demand);
        if(expirations.isEmpty()) {
            pointAdd(new Expiration());}
        expirationGrid.setItems(expirations);
        expirationsDataProvider = (ListDataProvider<Expiration>) expirationGrid.getDataProvider();
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public void saveExpirations() {
        for(Expiration expiration : expirations) {
            expiration.setDemand(demand);
            historyService.saveHistory(demand, expiration, Expiration.class);
            expirationService.update(expiration);
        }
    }
}
