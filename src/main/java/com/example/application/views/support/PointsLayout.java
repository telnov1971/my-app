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
    private HorizontalLayout pointsButtonLayout = new HorizontalLayout();
    private List<Point> points;
    private Grid<Point> pointGrid = new Grid<>(Point.class, false);
    private ListDataProvider<Point> pointDataProvider;
    private Binder<Point> binderPoints = new Binder<>(Point.class);
    private Editor<Point> editorPoints;

    private final PointService pointService;
    private final VoltageService voltageService;
    private final SafetyService safetyService;
    private final HistoryService historyService;

    public PointsLayout(PointService pointService
            , VoltageService voltageService
            , SafetyService safetyService
            , HistoryService historyService) {
        this.pointService = pointService;
        this.voltageService = voltageService;
        this.safetyService = safetyService;
        this.historyService = historyService;
        pointGrid.setHeightByRows(true);
        points = new ArrayList<>();
        Label helpers = new Label("распределение по точкам присоединения");

        Grid.Column<Point> columnPowerDemand =
                pointGrid.addColumn(Point::getPowerDemand)
                        .setHeader("Мощ. прис., кВт")
                        .setAutoWidth(true);
        Grid.Column<Point> columnPowerCurrent =
                pointGrid.addColumn(Point::getPowerCurrent).
                        setAutoWidth(true).
                        setHeader("Мощ. ранее пр., кВт ");
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
        points.add(new Point());
        pointGrid.setItems(points);
        pointDataProvider = (ListDataProvider<Point>) pointGrid.getDataProvider();
        points.remove(points.size() - 1);

        Button addButton = new Button("Добавить точку", event -> {
            pointDataProvider.getItems().add(new Point(0.0,
                    0.0,
                    this.voltageService.findById(1L).get(),
                    null,
                    this.safetyService.findById(3L).get()
            ));
            pointDataProvider.refreshAll();
            //pointGrid.getDataProvider().refreshAll();
        });

        Button removeButton = new Button("Удалить последнюю", event -> {
            this.points.remove(points.size() - 1);
            pointDataProvider.refreshAll();
            //pointGrid.getDataProvider().refreshAll();
        });

        editorPoints = pointGrid.getEditor();
        editorPoints.setBinder(binderPoints);
        editorPoints.setBuffered(true);

        NumberField fieldPowerDemand = new NumberField();
        fieldPowerDemand.setValue(1d);
        //fieldPowerDemand.setHasControls(true);
        fieldPowerDemand.setMin(0);
        binderPoints.forField(fieldPowerDemand).bind("powerDemand");
        columnPowerDemand.setEditorComponent(fieldPowerDemand);

        NumberField fieldPowerCurrent = new NumberField();
        fieldPowerCurrent.setValue(1d);
        fieldPowerCurrent.setMin(0);
        binderPoints.forField(fieldPowerCurrent).bind("powerCurrent");
        columnPowerCurrent.setEditorComponent(fieldPowerCurrent);

        Select<Safety> selectSafety = new Select();
        selectSafety.setItems(safetyService.findAll());
        selectSafety.setItemLabelGenerator(Safety::getName);
        binderPoints.forField(selectSafety).bind("safety");
        columnSafety.setEditorComponent(selectSafety);

        Select<Voltage> selectVoltage = new Select();
        selectVoltage.setItems(voltageService.findAllByOptional(false));
        selectVoltage.setItemLabelGenerator(Voltage::getName);
        binderPoints.forField(selectVoltage).bind("voltage");
        columnVoltage.setEditorComponent(selectVoltage);

        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        Grid.Column<Point> editorColumn = pointGrid.addComponentColumn(points -> {
            Button edit = new Button(new Icon(VaadinIcon.EDIT));
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editorPoints.editItem(points);
                fieldPowerDemand.focus();
            });
            edit.setEnabled(!editorPoints.isOpen());
            editButtons.add(edit);
            return edit;
        }).setAutoWidth(true);

        editorPoints.addOpenListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editorPoints.isOpen())));
        editorPoints.addCloseListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editorPoints.isOpen())));
        Button save = new Button(new Icon(VaadinIcon.CHECK_CIRCLE_O), e -> editorPoints.save());
        save.addClassName("save");
        Button cancel = new Button(new Icon(VaadinIcon.CLOSE_CIRCLE_O), e -> editorPoints.cancel());
        cancel.addClassName("cancel");
        Div divSave = new Div(save);
        Div divCancel = new Div(cancel);
        Div buttons = new Div(divSave, divCancel);
        editorColumn.setEditorComponent(buttons);


        pointsButtonLayout.add(addButton,removeButton);
        add(helpers,pointGrid,pointsButtonLayout);
    }

    public void pointsClean() {
        points = new ArrayList<>();
    }

    public void pointAdd(Point point) {
        points.add(point);
        pointGrid.setItems(points);
        pointDataProvider = (ListDataProvider<Point>) pointGrid.getDataProvider();
    }

    public void findAllByDemand(Demand demand) {
        points = pointService.findAllByDemand(demand);
        if(points.isEmpty()) {
            pointAdd(new Point(0.0,
                    0.0,
                    voltageService.findById(1L).get(),
                    null,
                    safetyService.findById(3L).get()));
        }
        pointGrid.setItems(points);
        pointDataProvider = (ListDataProvider<Point>) pointGrid.getDataProvider();
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public void savePoints() {
        for(Point point : points) {
            point.setDemand(demand);
            historyService.saveHistory(demand, point, Point.class);
//            History historyPoint = new History();
//            try {
//                String his = historyService.writeHistory(point);
//                historyPoint.setHistory(his);
//                historyPoint.setDemand(demand);
//            } catch (Exception e) {System.out.println(e.getMessage());}
//            try {
//                if(!historyPoint.getHistory().equals("")) {
//                    historyService.save(historyPoint);
//                }
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//                e.printStackTrace();
//            }
            pointService.update(point);
        }
    }
}
