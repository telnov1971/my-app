package com.example.application.views.support;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.Point;
import com.example.application.data.service.PointService;
import com.example.application.data.service.SafetyService;
import com.example.application.data.service.VoltageService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.*;

public class PointsLayout extends VerticalLayout {
    private HorizontalLayout pointsButtonLayout = new HorizontalLayout();
    private List<Point> points;
    private Grid<Point> pointGrid = new Grid<>(Point.class, false);
    private ListDataProvider<Point> pointDataProvider;
    private Binder<Point> binderPoints = new Binder<>(Point.class);
    private Editor<Point> editorPoints;

    private final PointService pointService;
    private final VoltageService voltageService;
    private final SafetyService safetyService;

    public PointsLayout(PointService pointService
            , VoltageService voltageService
            , SafetyService safetyService) {
        this.pointService = pointService;
        this.voltageService = voltageService;
        this.safetyService = safetyService;
        pointGrid.setHeightByRows(true);
        points = new ArrayList<>();

        Grid.Column<Point> columnPowerDemand =
                pointGrid.addColumn(Point::getPowerDemand)
                        .setHeader("Мощ. заяв.")
                        .setAutoWidth(true);
        Grid.Column<Point> columnPowerCurrent =
                pointGrid.addColumn(Point::getPowerCurrent).
                        setAutoWidth(true).
                        setHeader("Мощ. тек. ");
        pointGrid.addColumn(Point::getPowerMaximum).
                setAutoWidth(true).
                setHeader("Мощ. мак. ");
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
                    this.safetyService.findById(1L).get()
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
        fieldPowerDemand.setHasControls(true);
        fieldPowerDemand.setMin(0);
        binderPoints.forField(fieldPowerDemand).bind("powerDemand");
        columnPowerDemand.setEditorComponent(fieldPowerDemand);

        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        Grid.Column<Point> editorColumn = pointGrid.addComponentColumn(points -> {
            Button edit = new Button("Редактировать");
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
        Button save = new Button("Сохранить", e -> editorPoints.save());
        save.addClassName("save");
        Button cancel = new Button("Отменить", e -> editorPoints.cancel());
        cancel.addClassName("cancel");
        Div divSave = new Div(save);
        Div divCancel = new Div(cancel);
        Div buttons = new Div(divSave, divCancel);
        editorColumn.setEditorComponent(buttons);


        pointsButtonLayout.add(addButton,removeButton);
        add(pointGrid,pointsButtonLayout);
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
                    safetyService.findById(1L).get()));
        }
        pointGrid.setItems(points);
        pointDataProvider = (ListDataProvider<Point>) pointGrid.getDataProvider();
    }
}
