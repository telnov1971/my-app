package com.example.application.views.support;

import com.example.application.config.AppEnv;
import com.example.application.data.entity.Demand;
import com.example.application.data.entity.FileStored;
import com.example.application.data.service.FileStoredService;
import com.example.application.data.service.HistoryService;
import com.example.application.data.service.SafetyService;
import com.example.application.data.service.VoltageService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.server.StreamResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FilesLayout extends VerticalLayout {
    private String uploadPath;

    private Demand demand;
    private HorizontalLayout pointsButtonLayout = new HorizontalLayout();
    private List<FileStored> fileStoreds;
    private Grid<FileStored> fileStoredGrid = new Grid<>(FileStored.class,false);
    private ListDataProvider<FileStored> fileStoredListDataProvider;
    private Binder<FileStored> binderFileStored = new Binder<>(FileStored.class);
    private Editor<FileStored> editorFileStored;
    private Map<String,String> filesToSave = new HashMap<>();

    MultiFileBuffer buffer = new MultiFileBuffer();
    Upload multiUpload = new Upload(buffer);
    private String originalFileName;

    private List<FileStored> files;

    private final FileStoredService fileStoredService;
    private final VoltageService voltageService;
    private final SafetyService safetyService;
    private final HistoryService historyService;

    public FilesLayout(FileStoredService fileStoredService
            , VoltageService voltageService
            , SafetyService safetyService
            , HistoryService historyService) {
        this.historyService = historyService;
        this.uploadPath = AppEnv.getUploadPath();
        this.fileStoredService = fileStoredService;
        this.voltageService = voltageService;
        this.safetyService = safetyService;

        fileStoredGrid.setHeightByRows(true);
        files = new ArrayList<>();

        Label fileTableName = new Label("Прикреплённые документы (можно только добавить, удалить нельзя)");
        Grid.Column<FileStored> columnDate =
                fileStoredGrid.addColumn(FileStored::getCreatedate)
                        .setHeader("Загружено")
                        .setAutoWidth(true);
        Grid.Column<FileStored> columnName =
                fileStoredGrid.addColumn(FileStored::getName)
                        .setHeader("Имя файла")
                        .setAutoWidth(true);
        Grid.Column<FileStored> columnLink =
                fileStoredGrid.addColumn(FileStored::getLink)
                        .setHeader("Ссылка на файл")
                        .setAutoWidth(true);
        Grid.Column<FileStored> columnClient =
                fileStoredGrid.addComponentColumn(file ->
                                new Label(file.getClient()?"Клиент":"Омскэлектро"))
                        .setHeader("Кем загружено")
                        .setAutoWidth(true);
        files.add(new FileStored());
        fileStoredGrid.setItems(files);
        fileStoredListDataProvider = (ListDataProvider<FileStored>) fileStoredGrid.getDataProvider();
        files.remove(files.size() - 1);

        fileStoredListDataProvider.refreshAll();
        //pointGrid.getDataProvider().refreshAll();


        Collection<Anchor> anchors = Collections.newSetFromMap(new WeakHashMap<>());
        //Grid.Column<FileStored> anchorColumn =
        fileStoredGrid.addComponentColumn(file -> {
            StreamResource resource = null;
            Anchor anchor = new Anchor();

            String filename = file.getLink();
            File outFile = new File(uploadPath + filename);
            try {
                if(outFile.exists()) {
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
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if(resource!=null) {
                anchor = new Anchor(resource, file.getName());
                anchor.addClassName("anchor");

                anchors.add(anchor);
            }
            return anchor;
        }).setAutoWidth(true).setHeader("Сохранённые файлы");

        columnName.setVisible(false);
        columnLink.setVisible(false);

        createUploadLayout();
        add(fileTableName,fileStoredGrid, multiUpload);
    }

    private void createUploadLayout() {
        multiUpload.addSucceededListener(event -> {
            this.originalFileName = event.getFileName();
            String fileExt = ".txt";

            String uuidFile = UUID.randomUUID().toString();
            if(this.originalFileName.lastIndexOf(".") != -1 &&
                    this.originalFileName.lastIndexOf(".") != 0)
                // то вырезаем расширение файла, то есть ХХХХХ.txt -> txt
                fileExt = this.originalFileName.substring(this.originalFileName.lastIndexOf(".")+1);
            String newFile = uuidFile + "." + fileExt;
            String resultFilename = uploadPath + newFile;

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(resultFilename);
                InputStream inputStream = buffer.getInputStream(event.getFileName());
                fileOutputStream.write(inputStream.readAllBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            filesToSave.put(newFile,this.originalFileName);
        });
        multiUpload.addFailedListener(event -> {

        });
        multiUpload.addFileRejectedListener(event -> {

        });

        //upload.setAutoUpload(false);
        multiUpload.setUploadButton(new Button("Загрузить файл"));
        add(multiUpload);
    }

    public void findAllByDemand(Demand demand) {
        files = fileStoredService.findAllByDemand(demand);
        fileStoredGrid.setItems(files);
        fileStoredListDataProvider = (ListDataProvider<FileStored>) fileStoredGrid.getDataProvider();
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public void saveFiles() {
        for(Map.Entry<String, String> entry: filesToSave.entrySet()) {
            FileStored file = new FileStored(entry.getValue(),entry.getKey(), true, demand);
            file.setDemand(demand);
            historyService.saveHistory(demand, file, FileStored.class);
            fileStoredService.update(file);
        }
    }

    public void deleteFiles() throws IOException {
        for(Map.Entry<String, String> entry: filesToSave.entrySet()) {

            Path fileToDeletePath = Paths.get(uploadPath + entry.getKey());
            Files.delete(fileToDeletePath);        }
    }

    public void setReadOnly(){
        multiUpload.setVisible(false);
    }
}
