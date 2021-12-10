package com.example.application.views.support;

import com.example.application.data.entity.*;
import com.example.application.data.service.HistoryService;
import com.example.application.data.service.PointService;
import com.example.application.data.service.SafetyService;
import com.example.application.data.service.VoltageService;
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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.*;

public class PointsLayout extends VerticalLayout {
    private Demand demand;
    private List<Point> points;
    private final Grid<Point> pointGrid = new Grid<>(Point.class, false);
    private ListDataProvider<Point> pointDataProvider;
    private final Editor<Point> editorPoints;

    private final Button addButton;
    private final Button removeButton;
    int count = 0;

    private final PointService pointService;
    private final VoltageService voltageService;
    private final SafetyService safetyService;
    private final HistoryService historyService;

    public PointsLayout(PointService pointService
            , VoltageService voltageService
            , SafetyService safetyService
            , HistoryService historyService
            , GeneralForm formParent) {
        this.pointService = pointService;
        this.voltageService = voltageService;
        this.safetyService = safetyService;
        this.historyService = historyService;
        pointGrid.setHeightByRows(true);
        points = new ArrayList<>();
        Label helpers = new Label("распределение по точкам присоединения (ВНИМАНИЕ: после сохранения точки не удаляются, " +
                "можно только редактировать)");

        Grid.Column<Point> columnNumber =
                pointGrid.addColumn(Point::getNumber)
                        .setHeader("№")
                        .setAutoWidth(true);
        Grid.Column<Point> columnPowerCurrent =
                pointGrid.addColumn(Point::getPowerCurrent).
                        setAutoWidth(true).
                        setHeader("Мощ. ранее пр., кВт ");
        Grid.Column<Point> columnPowerDemand =
                pointGrid.addColumn(Point::getPowerDemand)
                        .setHeader("Мощ. прис., кВт")
                        .setAutoWidth(true);
        pointGrid.addColumn(Point::getPowerMaximum).
                setAutoWidth(true).
                setHeader("Мощ. мак., кВт ");
        Grid.Column<Point> columnSafety =
                pointGrid.addColumn(point -> point.getSafety().getName())
                        .setAutoWidth(true)
                        .setHeader("Кат. надёж.");
        Grid.Column<Point> columnVoltage =
                pointGrid.addColumn(point -> point.getVoltage().getName())
                        .setAutoWidth(true)
                        .setHeader("Ур. напр. ");
        columnNumber.setSortable(true);
//        points.add(new Point());
        pointGrid.setItems(points);
        pointDataProvider = (ListDataProvider<Point>) pointGrid.getDataProvider();
//        points.remove(points.size() - 1);

        editorPoints = pointGrid.getEditor();
        Binder<Point> binderPoints = new Binder<>(Point.class);
        editorPoints.setBinder(binderPoints);
        editorPoints.setBuffered(true);

        NumberField fieldPowerDemand = new NumberField();
        fieldPowerDemand.setValue(1d);
        //fieldPowerDemand.setHasControls(true);
        fieldPowerDemand.setMin(0);
        fieldPowerDemand.addValueChangeListener(e -> {
            if(!fieldPowerDemand.isEmpty())
                formParent.deselect(fieldPowerDemand);
        });
        binderPoints.forField(fieldPowerDemand).bind("powerDemand");
        columnPowerDemand.setEditorComponent(fieldPowerDemand);

        NumberField fieldPowerCurrent = new NumberField();
        fieldPowerCurrent.setValue(1d);
        fieldPowerCurrent.setMin(0);
        binderPoints.forField(fieldPowerCurrent).bind("powerCurrent");
        columnPowerCurrent.setEditorComponent(fieldPowerCurrent);
        fieldPowerCurrent.addValueChangeListener(e -> {
            if(!fieldPowerCurrent.isEmpty())
                formParent.deselect(fieldPowerCurrent);
        });

        Select<Safety> selectSafety = new Select<>();
        selectSafety.setItems(safetyService.findAll());
        selectSafety.setItemLabelGenerator(Safety::getName);
        binderPoints.forField(selectSafety).bind("safety");
        columnSafety.setEditorComponent(selectSafety);

        Select<Voltage> selectVoltage = new Select<>();
        selectVoltage.setItems(voltageService.findAllByOptional(false));
        selectVoltage.setItemLabelGenerator(Voltage::getName);
        binderPoints.forField(selectVoltage).bind("voltage");
        columnVoltage.setEditorComponent(selectVoltage);

        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        Grid.Column<Point> editorColumn = pointGrid.addComponentColumn(point -> {
            Button edit = new Button(new Icon(VaadinIcon.EDIT));
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                if(formParent.reason.getValue().getId() == 1) {
                    fieldPowerCurrent.setValue(0.0);
                    fieldPowerCurrent.setReadOnly(true);
                } else {
                    fieldPowerCurrent.setReadOnly(false);
                }
                if(point.getNumber() == 1) {
                    selectSafety.setReadOnly(false);
                    selectVoltage.setReadOnly(false);
                } else {
                    selectSafety.setReadOnly(true);
                    selectVoltage.setReadOnly(true);
                }
                editorPoints.editItem(point);
                fieldPowerDemand.focus();
            });
            edit.setEnabled(!editorPoints.isOpen());
            editButtons.add(edit);
            return edit;
        }).setAutoWidth(true);

        addButton = new Button("Добавить точку");
        removeButton = new Button("Удалить последнюю");
        removeButton.setEnabled(false);

        addButton.addClickListener(event -> {
            if(formParent.reason.getValue().getId() == 1) {
                fieldPowerCurrent.setValue(0.0);
                fieldPowerCurrent.setReadOnly(true);
            } else {
                fieldPowerCurrent.setReadOnly(false);
            }
            Integer maxNumber = 0;
            for (Point p : points) {
                maxNumber = p.getNumber() > maxNumber ? p.getNumber() : maxNumber;
            }
            if(points.size() >= 1) {
                pointDataProvider.getItems().add(new Point(++maxNumber, 0.0,
                        0.0,
                        points.get(0).getVoltage(),
                        null,
                        points.get(0).getSafety()
                ));
                selectSafety.setReadOnly(true);
                selectVoltage.setReadOnly(true);
            } else {
                pointDataProvider.getItems().add(new Point(++maxNumber, 0.0,
                        0.0,
                        this.voltageService.findById(1L).get(),
                        null,
                        this.safetyService.findById(3L).get()
                ));
            }
            pointDataProvider.refreshAll();
            pointGrid.select(points.get(points.size()-1));
            editorPoints.editItem(points.get(points.size()-1));
            addButton.setEnabled(false);
            removeButton.setEnabled(true);
            fieldPowerDemand.focus();
            formParent.saveMode(0,1);
            //pointGrid.getDataProvider().refreshAll();
        });

        removeButton.addClickListener(event -> {
            if(points.size() > count) {
                this.points.remove(points.size() - 1);
                pointDataProvider.refreshAll();
                removeButton.setEnabled(points.size() != count);
            } else {
                removeButton.setEnabled(false);
            }
            addButton.setEnabled(true);
        });

        editorPoints.addOpenListener(e -> editButtons
                .forEach(button -> button.setEnabled(!editorPoints.isOpen())));
        editorPoints.addCloseListener(e -> editButtons
                .forEach(button -> button.setEnabled(!editorPoints.isOpen())));
        Button save = new Button(new Icon(VaadinIcon.CHECK_CIRCLE_O), e -> {
            if(fieldPowerCurrent.getValue() == 0.0 &&
                    (formParent.reason.getValue().getId() == 2L ||
                            formParent.reason.getValue().getId() == 7L ||
                            formParent.reason.getValue().getId() == 8L)) {
                attention(fieldPowerCurrent);
                return;
            }
            if(fieldPowerDemand.getValue() == 0.0) {
                attention(fieldPowerDemand);
                return;
            }
            editorPoints.save();
            for(int i = 1; i < points.size(); i++) {
                Point p = points.get(i);
                p.setVoltage(points.get(0).getVoltage());
                p.setSafety(points.get(0).getSafety());
                points.set(i,p);
            }
            addButton.setEnabled(true);
            pointGrid.getElement().getStyle().set("border-width","0px");
            formParent.saveMode(0,-1);
            pointDataProvider.refreshAll();
        });
        save.setText("СОХРАНИТЬ");
        save.addClassName("save");
        Button cancel = new Button(new Icon(VaadinIcon.CLOSE_CIRCLE_O), e -> {
            if(fieldPowerDemand.getValue() == 0.0) {
                points.remove(points.size() - 1);
                pointDataProvider.refreshAll();
            }
            editorPoints.cancel();
            addButton.setEnabled(true);
            formParent.saveMode(0,-1);
        });
        cancel.setText("ОТМЕНИТЬ");
        cancel.addClassName("cancel");
        Div divSave = new Div(save);
        Div divCancel = new Div(cancel);
        Div buttons = new Div(divSave, divCancel);
        editorColumn.setEditorComponent(buttons);

        HorizontalLayout pointsButtonLayout = new HorizontalLayout();
        pointsButtonLayout.add(addButton,removeButton);
        Label helpersRow = new Label("Категорию надёжности и уровень напряжения нужно " +
                "указать только для первой точки подключения");
        helpersRow.getElement().getStyle().set("font-size","0.8em");
        helpersRow.getElement().getStyle().set("font-style","italic");
        add(helpers,pointGrid,helpersRow,pointsButtonLayout);
    }

    public void pointAdd(Point point) {
        points.add(point);
        pointGrid.setItems(points);
        pointDataProvider = (ListDataProvider<Point>) pointGrid.getDataProvider();
    }

    public void findAllByDemand(Demand demand) {
        points = pointService.findAllByDemand(demand);
        count = points.size();
        if(count > 1) Collections.sort(points);
        pointGrid.setItems(points);
        pointDataProvider = (ListDataProvider<Point>) pointGrid.getDataProvider();
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public void savePoints() {
        for(Point point : points) {
            if((point.getPowerDemand() == 0.0)
                    && (point.getPowerCurrent() == 0.0)) continue;
            point.setDemand(demand);
            historyService.saveHistory(demand, point, Point.class);
            pointService.update(point);
        }
    }

    private void attention(NumberField field){
        field.focus();
        field.getElement().getStyle().set("border-width","1px");
        field.getElement().getStyle().set("border-style","dashed");
        field.getElement().getStyle().set("border-color","red");
    }

    public int getPointSize() {
        return points.size();
    }
    public void setFocus() {
        pointGrid.getElement().getStyle().set("border-width","3px");
        pointGrid.getElement().getStyle().set("border-style","dotted");
        pointGrid.getElement().getStyle().set("border-color","red");
        addButton.focus();
    }
}
