package com.example.application.views.support;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.FileStored;
import com.example.application.data.entity.Point;
import com.example.application.data.service.FileStoredService;
import com.example.application.data.service.PointService;
import com.example.application.data.service.SafetyService;
import com.example.application.data.service.VoltageService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

public class FilesLayout extends VerticalLayout {

    private Demand demand;
    private HorizontalLayout pointsButtonLayout = new HorizontalLayout();
    private List<FileStored> fileStoreds;
    private Grid<FileStored> fileStoredGrid = new Grid<>(FileStored.class,false);
    private ListDataProvider<FileStored> fileStoredListDataProvider;
    private Binder<FileStored> binderFileStored = new Binder<>(FileStored.class);
    private Editor<FileStored> editorFileStored;

    private List<FileStored> files;

    private final FileStoredService fileStoredService;
    private final VoltageService voltageService;
    private final SafetyService safetyService;

    public FilesLayout(FileStoredService fileStoredService
            , VoltageService voltageService
            , SafetyService safetyService) {
        this.fileStoredService = fileStoredService;
        this.voltageService = voltageService;
        this.safetyService = safetyService;

        fileStoredGrid.setHeightByRows(true);
        files = new ArrayList<>();

        Grid.Column<FileStored> columnName =
                fileStoredGrid.addColumn(FileStored::getName)
                        .setHeader("Имя файла")
                        .setAutoWidth(true);
        Grid.Column<FileStored> columnLink =
                fileStoredGrid.addColumn(FileStored::getLink)
                        .setHeader("Имя файла")
                        .setAutoWidth(true);
        files.add(new FileStored());
        fileStoredGrid.setItems(files);
        fileStoredListDataProvider = (ListDataProvider<FileStored>) fileStoredGrid.getDataProvider();
        files.remove(files.size() - 1);

        fileStoredListDataProvider.refreshAll();
        //pointGrid.getDataProvider().refreshAll();


        Collection<Anchor> anchors = Collections.newSetFromMap(new WeakHashMap<>());
        Grid.Column<FileStored> anchorColumn = fileStoredGrid.addComponentColumn(file -> {
            StreamResource resource = null;

            String filename = file.getLink();
            //response.setContentType("application/octet-stream");
            File outFile = new File("\\\\omel1s.omel.corp\\LK$\\" + filename);
            try {
                InputStream inputStream = new FileInputStream(outFile);
                resource = new StreamResource(
                        file.getName(),
                        () -> {
                            try {
                                return new ByteArrayInputStream(inputStream.readAllBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Anchor anchor = new Anchor(resource,file.getName());
            anchor.addClassName("anchor");

//            edit.addClickListener(e -> {
//                editorFileStored.editItem(points);
//                fieldPowerDemand.focus();
//            });
//            edit.setEnabled(!editorFileStored.isOpen());
            anchors.add(anchor);
            return anchor;
        }).setAutoWidth(true).setHeader("Сохранённые файлы");

//        editorFileStored.addOpenListener(e -> editButtons.stream()
//                .forEach(button -> button.setEnabled(!editorFileStored.isOpen())));
//        editorFileStored.addCloseListener(e -> editButtons.stream()
//                .forEach(button -> button.setEnabled(!editorFileStored.isOpen())));
//        Button save = new Button("Сохранить", e -> editorFileStored.save());
//        save.addClassName("save");
//        Button cancel = new Button("Отменить", e -> editorFileStored.cancel());
//        cancel.addClassName("cancel");
//        Div divSave = new Div(save);
//        Div divCancel = new Div(cancel);
//        Div buttons = new Div(divSave, divCancel);
//        anchorColumn.setEditorComponent(buttons);

        columnName.setVisible(false);
        columnLink.setVisible(false);

        add(fileStoredGrid);
    }

    public void pointsClean() {
        files = new ArrayList<>();
    }

    public void pointAdd(FileStored fileStored) {
        files.add(fileStored);
        fileStoredGrid.setItems(files);
        fileStoredListDataProvider = (ListDataProvider<FileStored>) fileStoredGrid.getDataProvider();
    }

    public void findAllByDemand(Demand demand) {
        files = fileStoredService.findAllByDemand(demand);
        if(files.isEmpty()) {
            pointAdd(new FileStored("/","_",demand));
        }
        fileStoredGrid.setItems(files);
        fileStoredListDataProvider = (ListDataProvider<FileStored>) fileStoredGrid.getDataProvider();
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public void savePoints() {
        for(FileStored file : files) {
            file.setDemand(demand);
            fileStoredService.update(file);
        }
    }
}
