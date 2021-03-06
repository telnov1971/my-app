package ru.omel.po.views.support;

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
import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.Point;
import ru.omel.po.data.entity.Safety;
import ru.omel.po.data.entity.Voltage;
import ru.omel.po.data.service.HistoryService;
import ru.omel.po.data.service.PointService;
import ru.omel.po.data.service.SafetyService;
import ru.omel.po.data.service.VoltageService;

import java.util.*;

public class PointsLayout extends VerticalLayout {
    private Demand demand;
    private List<Point> points;
    private final Grid<Point> pointGrid = new Grid<>(Point.class, false);
    private ListDataProvider<Point> pointDataProvider;
    private final Editor<Point> editorPoints;
    private final GeneralForm formParent;
    private NumberField fieldPowerDemand  = new NumberField();
    private NumberField fieldPowerCurrent = new NumberField();
    private NumberField fieldPowerMaximum  = new NumberField();
    private Select<Safety> selectSafety;
    private Select<Voltage> selectVoltage;
//    private Select<Voltage> selectVoltageIn;

    private final Button addButton;
    private final Button removeButton;
    int count = 0;

    private final PointService pointService;
    private final VoltageService voltageService;
    private final SafetyService safetyService;
    private final HistoryService historyService;
    private final int client;

    public PointsLayout(PointService pointService
            , VoltageService voltageService
            , SafetyService safetyService
            , HistoryService historyService
            , GeneralForm formParent
            , int client) {
        this.pointService = pointService;
        this.voltageService = voltageService;
        this.safetyService = safetyService;
        this.historyService = historyService;
        this.formParent = formParent;
        this.client = client;
        pointGrid.setHeightByRows(true);
        points = new ArrayList<>();
        formParent.points = points;
        formParent.pointsLayout = this;
        Label helpers = new Label("?????????????????????????? ???? ???????????? ??????????????????????????" +
                " (????????????????: ?????????? ???????????????????? ?????????? ???? ??????????????????, " +
                "?????????? ???????????? ??????????????????????????)");
        Select<Safety> selectSafety = ViewHelper.createSelect(Safety::getName, safetyService.findAll(),
                "", Safety.class);
        Select<Voltage> selectVoltage = new Select<>();
//        Select<Voltage> selectVoltageIn = new Select<>();
        editorPoints = pointGrid.getEditor();

        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        Grid.Column<Point> editorColumn = pointGrid.addComponentColumn(point -> {
            Button edit = new Button(new Icon(VaadinIcon.EDIT));
            edit.addClassName("edit");
            edit.getElement().setAttribute("title","??????????????");
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
                formParent.saveMode(0,1);
            });
            edit.setEnabled(!editorPoints.isOpen());
            editButtons.add(edit);
            return edit;
        }).setAutoWidth(true).setResizable(true);
        Grid.Column<Point> columnNumber =
                pointGrid.addColumn(Point::getNumber)
                        .setHeader("???")
                        .setResizable(true)
                        .setAutoWidth(true);
        Grid.Column<Point> columnPowerCurrent =
                pointGrid.addColumn(Point::getPowerCurrent)
                        .setAutoWidth(true)
                        .setHeader("??????. ?????????? ????., ?????? ")
                        .setResizable(true);
        Grid.Column<Point> columnPowerDemand =
                pointGrid.addColumn(Point::getPowerDemand)
                        .setHeader("??????. ????????., ??????")
                        .setAutoWidth(true)
                        .setResizable(true);
        Grid.Column<Point> columnPowerMaximum =
                pointGrid.addColumn(Point::getPowerMaximum)
                        .setAutoWidth(true)
                        .setHeader("??????. ??????., ?????? ")
                        .setResizable(true);
        Grid.Column<Point> columnSafety =
                pointGrid.addColumn(point -> point.getSafety().getName())
                        .setAutoWidth(true)
                        .setHeader("??????. ??????????.")
                        .setResizable(true);
        Grid.Column<Point> columnVoltage =
                pointGrid.addColumn(point -> point.getVoltage().getName())
                        .setAutoWidth(true)
                        .setHeader("?????????? ????????. ")
                        .setResizable(true);
//        Grid.Column<Point> columnVoltageIn =
//                pointGrid.addColumn(point -> point.getVoltageIn()!=null ? point.getVoltage().getName() : "")
//                        .setAutoWidth(true)
//                        .setHeader("????.????????. ???? ?????????? ")
//                        .setResizable(true);
        columnNumber.setSortable(true);
//        points.add(new Point());
        pointGrid.setItems(points);
        pointDataProvider = (ListDataProvider<Point>) pointGrid.getDataProvider();
//        points.remove(points.size() - 1);

        Binder<Point> binderPoints = new Binder<>(Point.class);
        editorPoints.setBinder(binderPoints);
        editorPoints.setBuffered(true);

        fieldPowerMaximum.setReadOnly(true);
        columnPowerMaximum.setEditorComponent(fieldPowerMaximum);
        binderPoints.forField(fieldPowerMaximum).bind("powerMaximum");

        fieldPowerDemand.setValue(1d);
        //fieldPowerDemand.setHasControls(true);
        fieldPowerDemand.setMin(0);
        fieldPowerDemand.addValueChangeListener(e -> {
            if(!fieldPowerDemand.isEmpty()) {
                ViewHelper.deselect(fieldPowerDemand);
                Double pc = fieldPowerCurrent.isEmpty() ? 0.0 : fieldPowerCurrent.getValue();
                pc += fieldPowerDemand.getValue();
                fieldPowerMaximum.setValue(pc);
                formParent.setPowerMaximum(pc);
            }
        });
        binderPoints.forField(fieldPowerDemand).bind("powerDemand");
        columnPowerDemand.setEditorComponent(fieldPowerDemand);

        fieldPowerCurrent.setValue(1d);
        fieldPowerCurrent.setMin(0);
        binderPoints.forField(fieldPowerCurrent).bind("powerCurrent");
        columnPowerCurrent.setEditorComponent(fieldPowerCurrent);
        fieldPowerCurrent.addValueChangeListener(e -> {
            if(!fieldPowerCurrent.isEmpty()) {
                ViewHelper.deselect(fieldPowerCurrent);
                Double pd = fieldPowerDemand.isEmpty() ? 0.0 : fieldPowerDemand.getValue();
                pd += fieldPowerCurrent.getValue();
                fieldPowerMaximum.setValue(pd);
                formParent.setPowerMaximum(pd);
            }
        });

        binderPoints.forField(selectSafety).bind("safety");
        columnSafety.setEditorComponent(selectSafety);

        selectVoltage.setItems(voltageService.findAllByOptional(false));
        selectVoltage.setItemLabelGenerator(Voltage::getName);
        binderPoints.forField(selectVoltage).bind("voltage");
        columnVoltage.setEditorComponent(selectVoltage);

//        selectVoltageIn.setItems(voltageService.findAllByOptional(true));
//        selectVoltageIn.setItemLabelGenerator(Voltage::getName);
//        binderPoints.forField(selectVoltage).bind("voltageIn");
//        columnVoltageIn.setEditorComponent(selectVoltageIn);

        addButton = new Button("???????????????? ??????????");
        removeButton = new Button("?????????????? ??????????????????");
        removeButton.setEnabled(false);

        addButton.addClickListener(event -> {
            if(formParent.reason.getValue() == null) {
                formParent.alertHere =
                        ViewHelper.attention(formParent.reason
                                ,"???????????????????? ?????????????? ?????????????? ??????????????????"
                                ,formParent.alertHere.getFirst()
                                ,formParent.space);
                formParent.reason.focus();
                return;
            }
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
            saveEdited();
        });
//        save.setText("??????????????????");
        save.addClassName("save");
        save.getElement().setAttribute("title","??????????????????");
        Button cancel = new Button(new Icon(VaadinIcon.CLOSE_CIRCLE_O), e -> {
            if(fieldPowerDemand.getValue() == 0.0) {
                points.remove(points.size() - 1);
                pointDataProvider.refreshAll();
            }
            editorPoints.cancel();
            addButton.setEnabled(true);
            formParent.saveMode(0,-1);
        });
//        cancel.setText("????????????????");
        cancel.addClassName("cancel");
        cancel.getElement().setAttribute("title","????????????????");
        Div divSave = new Div(save);
        Div divCancel = new Div(cancel);
        Div buttons = new Div(divSave, divCancel);
        editorColumn.setEditorComponent(buttons);

        HorizontalLayout pointsButtonLayout = new HorizontalLayout();
        pointsButtonLayout.add(addButton,removeButton);
        Label helpersRow = new Label("?????????????????? ???????????????????? ?? ?????????????? ???????????????????? ?????????? " +
                "?????????????? ???????????? ?????? ???????????? ?????????? ??????????????????????");
        helpersRow.getElement().getStyle().set("font-size","0.8em");
        helpersRow.getElement().getStyle().set("font-style","italic");
        add(helpers,pointGrid,helpersRow,pointsButtonLayout);
    }

    public Boolean saveEdited() {
        if(fieldPowerCurrent.getValue() == 0.0 &&
                (formParent.reason.getValue().getId() == 2L ||
                        formParent.reason.getValue().getId() == 7L ||
                        formParent.reason.getValue().getId() == 8L)) {
            attention(fieldPowerCurrent);
            return false;
        }
        if(fieldPowerDemand.getValue() == 0.0) {
            attention(fieldPowerDemand);
            return false;
        }
        editorPoints.save();
        for(int i = 1; i < points.size(); i++) {
            Point p = points.get(i);
            p.setVoltage(points.get(0).getVoltage());
            p.setSafety(points.get(0).getSafety());
            points.set(i,p);
        }
        addButton.setEnabled(true);
        ViewHelper.noAlert(pointGrid.getElement());
        formParent.expirationsLayout.setNewSafety(points.get(0).getSafety());
        formParent.saveMode(0,-1);
        pointDataProvider.refreshAll();
        return true;
    }

    public void findAllByDemand(Demand demand) {
        points = pointService.findAllByDemand(demand);
        formParent.points = points;
        count = points.size();
        if(count > 1) Collections.sort(points);
        pointGrid.setItems(points);
        pointDataProvider = (ListDataProvider<Point>) pointGrid.getDataProvider();
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public boolean savePoints() {
        boolean result = false;
        for(Point point : points) {
            if((point.getPowerDemand() == 0.0)
                    && (point.getPowerCurrent() == 0.0)) continue;
            point.setDemand(demand);
            result |= historyService.saveHistory(client, demand, point, Point.class);
            try {
                pointService.update(point);
            } catch (Exception e) {
                return false;
            }
        }
        return result;
    }

    private void attention(NumberField field){
        field.focus();
        field.getElement().getStyle().set("margin","0.1em");
        field.getElement().getStyle().set("padding","0.1em");
        field.getElement().getStyle().set("border-radius","0.5em");
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
        pointGrid.focus();
        addButton.focus();
    }
}
