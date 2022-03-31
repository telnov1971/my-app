package com.example.application.views.support;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.Expiration;
import com.example.application.data.entity.Role;
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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
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
    private final TextArea helpers;
    private final TextField fieldStep;
    private final TextField fieldPlanProject;
    private final TextField fieldPlanUsage;
    private final Grid.Column<Expiration> editorColumn;
    private final Button addButton;
    private Button removeButton;
    private GeneralForm formParent;

    private final ExpirationService expirationService;
    private final HistoryService historyService;
    private final int client;

    private double powerMax;
    private int count = 0;

    public ExpirationsLayout(ExpirationService expirationService
            , SafetyService safetyService
            , HistoryService historyService
            , GeneralForm paramFormParent
            , int client) {
        this.expirationService = expirationService;
        this.historyService = historyService;
        formParent = paramFormParent;
        this.client = client;
        formParent.expirationsLayout = this;
        expirationGrid.setHeightByRows(true);
        expirations = new ArrayList<>();
        helpers = new TextArea();
        helpers.setValue("Сроки проектирования и поэтапного введения в эксплуатацию объекта"+
                " (в том числе по этапам и очередям), планируемое поэтапное распределение максимальной мощности " +
                "(обязательны к заполнению) (ВНИМАНИЕ: после сохранения этапы не удаляются, " +
                "можно только редактировать)");
        //helpers.setHeight("1em");
        helpers.setReadOnly(true);
        helpers.setWidthFull();
        helpers.getElement().getStyle().set("font-size","1em");
        editorExpiration = expirationGrid.getEditor();
        fieldStep = new TextField();
        fieldPlanProject = new TextField();
        fieldPlanUsage = new TextField();
        NumberField fieldPowerMax= new NumberField();
        Select<Safety> selectSafety = ViewHelper.createSelect(Safety::getName, safetyService.findAll(),
                "Категория надежности", Safety.class);
        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        addButton = new Button("Добавить этап");

        editorColumn = expirationGrid.addComponentColumn(expiration -> {
            Button edit = new Button(new Icon(VaadinIcon.EDIT));
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editorExpiration.editItem(expiration);
                fieldStep.focus();
                addButton.setEnabled(false);
            });
            edit.setEnabled(!editorExpiration.isOpen());
//            edit.setText("ОТКРЫТЬ");
            edit.getElement().setAttribute("title","открыть");
            editButtons.add(edit);
            return edit;
        }).setAutoWidth(true).setResizable(true);

        Grid.Column<Expiration> columnStep =
                expirationGrid.addColumn(Expiration::getStep)
                        .setHeader("Этап/Очередь")
                        .setAutoWidth(true)
                        .setResizable(true);
        Grid.Column<Expiration> columnPlanProject =
                expirationGrid.addColumn(Expiration::getPlanProject)
                        .setAutoWidth(true)
                        .setHeader("Срок проектирования")
                        .setResizable(true);
        Grid.Column<Expiration> columnPlanUsage =
                expirationGrid.addColumn(Expiration::getPlanUsage)
                        .setAutoWidth(true)
                        .setHeader("Срок ввода")
                        .setResizable(true);
        Grid.Column<Expiration> columnPowerMax =
                expirationGrid.addColumn(Expiration::getPowerMax)
                        .setAutoWidth(true)
                        .setResizable(true)
                        .setHeader("Макс.мощность");
        expirationGrid.addColumn(expiration -> expiration.getSafety().getName())
                .setAutoWidth(true)
                .setResizable(true)
                .setHeader("Кат. надёж.");
        expirationGrid.setItems(expirations);
        expirationsDataProvider = (ListDataProvider<Expiration>) expirationGrid.getDataProvider();

        Binder<Expiration> binderExpiration = new Binder<>(Expiration.class);
        editorExpiration.setBinder(binderExpiration);
        editorExpiration.setBuffered(true);

        binderExpiration.forField(fieldStep).bind("step");
        columnStep.setEditorComponent(fieldStep);
        fieldStep.addValueChangeListener(e-> ViewHelper.deselect(fieldStep));

        binderExpiration.forField(fieldPlanProject).bind("planProject");
        columnPlanProject.setEditorComponent(fieldPlanProject);
        fieldPlanProject.addValueChangeListener(e-> ViewHelper.deselect(fieldPlanProject));

        binderExpiration.forField(fieldPlanUsage).bind("planUsage");
        columnPlanUsage.setEditorComponent(fieldPlanUsage);
        fieldPlanUsage.addValueChangeListener(e-> ViewHelper.deselect(fieldPlanUsage));

        fieldPowerMax.setValue(1d);
        fieldPowerMax.setMin(0);
        binderExpiration.forField(fieldPowerMax).bind("powerMax");
        columnPowerMax.setEditorComponent(fieldPowerMax);

        binderExpiration.forField(selectSafety).bind("safety");

        addButton.addClickListener(event -> {
            ViewHelper.noAlert(expirationGrid.getElement());
            Expiration expiration = new Expiration("",
                    "","",powerMax,
                    safetyService.findById(3L).get());
            if(formParent.safety.isVisible()){
                expiration.setSafety(formParent.safety.getValue());
            }
            if(formParent.accordionPoints.isVisible()){
                if(formParent.points != null && formParent.points.size() > 0){
                    expiration.setSafety(formParent.points.get(0).getSafety());
                } else {
                    Notification.show("Нужно заполнить точки подключения", 3000,
                            Notification.Position.BOTTOM_START);
                    if(formParent.pointsLayout != null)
                        formParent.pointsLayout.setFocus();
                    return;
                }
            }
            expirationsDataProvider.getItems().add(expiration);
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
                formParent.saveMode(-1,0);
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
            saveEdited();
        });
//        save.setText("СОХРАНИТЬ");
        save.addClassName("save");
        save.getElement().setAttribute("title","сохранить");
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
//        cancel.setText("ОТМЕНИТЬ");
        cancel.addClassName("cancel");
        cancel.getElement().setAttribute("title","отменить");
        Div divSave = new Div(save);
        Div divCancel = new Div(cancel);
        Div buttons = new Div(divSave, divCancel);
        editorColumn.setEditorComponent(buttons);

        HorizontalLayout expirationsButtonLayout = new HorizontalLayout();
        expirationsButtonLayout.add(addButton,removeButton);
        add(helpers,expirationGrid, expirationsButtonLayout);
    }

    public Boolean saveEdited() {
        if(fieldStep.isEmpty()){
            ViewHelper.alert(fieldStep.getElement());
            fieldStep.focus();
            return false;
        }
        if(fieldPlanProject.isEmpty()){
            ViewHelper.alert(fieldPlanProject.getElement());
            fieldPlanProject.focus();
            return false;
        }
        if(fieldPlanUsage.isEmpty()){
            ViewHelper.alert(fieldPlanUsage.getElement());
            fieldPlanUsage.focus();
            return false;
        }
        formParent.saveMode(-1,0);
        editorExpiration.save();
        addButton.setEnabled(true);
        return true;
    }

    public void pointAdd() {
        expirationGrid.setItems(expirations);
        expirationsDataProvider = (ListDataProvider<Expiration>) expirationGrid.getDataProvider();
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
            historyService.saveHistory(client, demand, expiration, Expiration.class);
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
        ViewHelper.alert(expirationGrid.getElement());
        addButton.focus();
    }

    public void setNewSafety(Safety safety) {
        for(int i=0; i < expirations.size(); i++){
            Expiration expiration = expirations.get(i);
            expiration.setSafety(safety);
            expirations.set(i, expiration);
        }
        expirationsDataProvider.refreshAll();
    }
}
