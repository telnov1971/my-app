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
import com.vaadin.flow.component.html.Label;
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
    private List<Expiration> expirations;
    private final Grid<Expiration> expirationGrid = new Grid<>(Expiration.class, false);
    private ListDataProvider<Expiration> expirationsDataProvider;
    private final Editor<Expiration> editorExpiration;
    private final TextField fieldStep;
    private final TextField fieldPlanProject;
    private final TextField fieldPlanUsage;
    private final Grid.Column<Expiration> editorColumn;
    private final Button addButton;
    private Button removeButton;

    private final ExpirationService expirationService;
    private final HistoryService historyService;

    private double powerMax;
    private int count = 0;

    public ExpirationsLayout(ExpirationService expirationService
            , SafetyService safetyService
            , HistoryService historyService, GeneralForm formParent) {
        this.expirationService = expirationService;
        this.historyService = historyService;
        expirationGrid.setHeightByRows(true);
        expirations = new ArrayList<>();
        Label helpers = new Label("Сроки проектирования и поэтапного введения в эксплуатацию объекта"+
                " (в том числе по этапам и очередям), планируемое поэтапное распределение максимальной мощности " +
                "(обязательны к заполнению) (ВНИМАНИЕ: после сохранения этапы не удаляются, " +
                "можно только редактировать)");

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
        expirationGrid.setItems(expirations);
        expirationsDataProvider = (ListDataProvider<Expiration>) expirationGrid.getDataProvider();

        editorExpiration = expirationGrid.getEditor();
        Binder<Expiration> binderExpiration = new Binder<>(Expiration.class);
        editorExpiration.setBinder(binderExpiration);
        editorExpiration.setBuffered(true);

        fieldStep = new TextField();
        binderExpiration.forField(fieldStep).bind("step");
        columnStep.setEditorComponent(fieldStep);
        fieldStep.addValueChangeListener(e-> formParent.deselect(fieldStep));

        fieldPlanProject = new TextField();
        binderExpiration.forField(fieldPlanProject).bind("planProject");
        columnPlanProject.setEditorComponent(fieldPlanProject);
        fieldPlanProject.addValueChangeListener(e-> formParent.deselect(fieldPlanProject));

        fieldPlanUsage = new TextField();
        binderExpiration.forField(fieldPlanUsage).bind("planUsage");
        columnPlanUsage.setEditorComponent(fieldPlanUsage);
        fieldPlanUsage.addValueChangeListener(e-> formParent.deselect(fieldPlanUsage));

        NumberField fieldPowerMax= new NumberField();
        fieldPowerMax.setValue(1d);
        fieldPowerMax.setMin(0);
        binderExpiration.forField(fieldPowerMax).bind("powerMax");
        columnPowerMax.setEditorComponent(fieldPowerMax);

        Select<Safety> selectSafety = new Select<>();
        selectSafety.setItems(safetyService.findAll());
        selectSafety.setItemLabelGenerator(Safety::getName);
        binderExpiration.forField(selectSafety).bind("safety");
        columnSafety.setEditorComponent(selectSafety);

        addButton = new Button("Добавить этап");

        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        editorColumn = expirationGrid.addComponentColumn(expiration -> {
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
            expirationGrid.getElement().getStyle().set("border-width","0px");
            expirationsDataProvider.getItems().add(new Expiration("",
                    "","",powerMax,
                    safetyService.findById(3L).get()));
            expirationsDataProvider.refreshAll();
            expirationGrid.select(expirations.get(expirations.size() - 1));
            editorExpiration.editItem(expirations.get(expirations.size() - 1));
            fieldStep.focus();
            addButton.setEnabled(false);
            removeButton.setEnabled(true);
            formParent.saveMode(1,0);
        });

        removeButton = new Button("Удалить последнюю", event -> {
            if(expirations.toArray().length > count) {
                this.expirations.remove(expirations.size() - 1);
                expirationsDataProvider.refreshAll();
                addButton.setEnabled(true);
            } else {
                removeButton.setEnabled(false);
            }
        });
        removeButton.setEnabled(false);

        editorExpiration.addOpenListener(e -> editButtons
                .forEach(button -> button.setEnabled(!editorExpiration.isOpen())));
        editorExpiration.addCloseListener(e -> editButtons
                .forEach(button -> button.setEnabled(!editorExpiration.isOpen())));
        Button save = new Button(new Icon(VaadinIcon.CHECK_CIRCLE_O), e -> {
            if(fieldStep.isEmpty()){
                attention(fieldStep);
                return;
            }
            if(fieldPlanProject.isEmpty()){
                attention(fieldPlanProject);
                return;
            }
            if(fieldPlanUsage.isEmpty()){
                attention(fieldPlanUsage);
                return;
            }
            formParent.saveMode(-1,0);
            editorExpiration.save();
            addButton.setEnabled(true);
        });
        save.setText("СОХРАНИТЬ");
        save.addClassName("save");
        Button cancel = new Button(new Icon(VaadinIcon.CLOSE_CIRCLE_O), e -> {
            editorExpiration.cancel();
            addButton.setEnabled(true);
            if(fieldStep.getValue().equals("")
                    && fieldPlanProject.getValue().equals("")
                    && fieldPlanUsage.getValue().equals("")
            ) expirations.remove(expirations.size() - 1);
            expirationsDataProvider.refreshAll();
            formParent.saveMode(-1,0);
        });
        cancel.setText("ОТМЕНИТЬ");
        cancel.addClassName("cancel");
        Div divSave = new Div(save);
        Div divCancel = new Div(cancel);
        Div buttons = new Div(divSave, divCancel);
        editorColumn.setEditorComponent(buttons);

        HorizontalLayout expirationsButtonLayout = new HorizontalLayout();
        expirationsButtonLayout.add(addButton,removeButton);
        add(helpers,expirationGrid, expirationsButtonLayout);
    }

    public void pointAdd() {
        expirationGrid.setItems(expirations);
        expirationsDataProvider = (ListDataProvider<Expiration>) expirationGrid.getDataProvider();
    }

    private void attention(TextField field){
        field.focus();
        field.getElement().getStyle().set("border-width","1px");
        field.getElement().getStyle().set("border-style","dashed");
        field.getElement().getStyle().set("border-color","red");
    }

    public void findAllByDemand(Demand demand) {
        expirations = expirationService.findAllByDemand(demand);
        count = expirations.toArray().length;
        if(expirations.isEmpty()) {
            pointAdd();}
        expirationGrid.setItems(expirations);
        expirationsDataProvider = (ListDataProvider<Expiration>) expirationGrid.getDataProvider();
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public void saveExpirations() {
        for(Expiration expiration : expirations) {
            if(expiration.getStep().isEmpty()||
                expiration.getPlanProject().isEmpty()||
                expiration.getPlanUsage().isEmpty()) continue;
            expiration.setDemand(demand);
            historyService.saveHistory(demand, expiration, Expiration.class);
            expirationService.update(expiration);
        }
    }

    public void setReadOnly() {
        editorColumn.setVisible(false);
        addButton.setVisible(false);
        removeButton.setVisible(false);
    }

    public void setPowerMax(double powerMax) {
        this.powerMax = powerMax;
    }

    public int getExpirationsSize() {
        return expirations.size();
    }

    public void setFocus() {
        expirationGrid.getElement().getStyle().set("border-width","3px");
        expirationGrid.getElement().getStyle().set("border-style","dotted");
        expirationGrid.getElement().getStyle().set("border-color","red");
        addButton.focus();
    }
}
