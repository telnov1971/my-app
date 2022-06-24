package ru.omel.po.views.support;

import ru.omel.po.config.AppEnv;
import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.FileStored;
import ru.omel.po.data.service.FileStoredService;
import ru.omel.po.data.service.HistoryService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.server.StreamResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FilesLayout extends VerticalLayout {
    private final String uploadPath;
    private final String dbName;

    private Demand demand;
    private final Grid<FileStored> fileStoredGrid = new Grid<>(FileStored.class,false);
    private ListDataProvider<FileStored> fileStoredListDataProvider;
    private final Map<String,String> filesToSave = new HashMap<>();

    MultiFileBuffer buffer = new MultiFileBuffer();
    Upload multiUpload = new Upload(buffer);
    private String originalFileName;

    private List<FileStored> files;

    private final FileStoredService fileStoredService;
    private final HistoryService historyService;
    private int client;

    public FilesLayout(FileStoredService fileStoredService
            , HistoryService historyService, int client) {
        this.historyService = historyService;
        this.uploadPath = AppEnv.getUploadPath();
        String temp = AppEnv.getDbName();
        String temp2 = temp.substring(temp.lastIndexOf("/")+1);
        this.dbName = temp2.equals("po2") ? temp2 : "";
        this.fileStoredService = fileStoredService;
        this.client = client;

        fileStoredGrid.setHeightByRows(true);
        files = new ArrayList<>();

        Label fileTableName = new Label("Прикреплённые документы (можно только добавить, удалить нельзя)");
        fileStoredGrid.addComponentColumn(file -> new Label(file.getCreatedate().format(
                                            DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm"))))
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
        fileStoredGrid.addComponentColumn(file -> {
                    String str = "";
                    switch (file.getClient()){
                        case 0:
                            str = "Омскэлектро";
                        break;
                        case 1:
                            str ="Клиент";
                        break;
                        case 2:
                            str = "ГП";
                        break;
                    }
                    return new Label(str);})
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
            String dirName = file.getDirectory() != null ?
                    uploadPath + file.getDirectory() + "\\" : uploadPath;
            File outFile = new File(dirName + filename);
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
//                    inputStream.close();
                }
            } catch (IOException e) {
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
                inputStream.close();
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

    public boolean saveFiles() {
        boolean result = false;
        for(Map.Entry<String, String> entry: filesToSave.entrySet()) {
            if((fileStoredService.findByLink(entry.getKey())).isEmpty()){
                String dir = moveFile(entry.getKey());
                FileStored file = new FileStored(entry.getValue(), dir, entry.getKey(), demand);
                file.setDemand(demand);
                file.setClient(client);
                try{
                    fileStoredService.update(file);
                } catch (Exception e) {
                    return result;
                }
                if(historyService.saveHistory(client, demand, file, FileStored.class)) {
                    result = true;
                }
            }
        }
        return result;
    }

    private String moveFile(String filename) {
        String dirName = dbName + "\\" + demand.getId();
        String resultFilename;
        File dir = new File(uploadPath + dirName);
        if(!dir.exists()) dir.mkdir();
        if(dir.exists()){
            resultFilename = uploadPath + dirName + "\\" + filename;
            try {
                Files.copy(Paths.get(uploadPath + filename), Paths.get(resultFilename));
//                Files.move(Paths.get(uploadPath + filename), Paths.get(resultFilename));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dirName;
    }
    public void deleteFiles() throws IOException {
        for(Map.Entry<String, String> entry: filesToSave.entrySet()) {

            Path fileToDeletePath = Paths.get(uploadPath + entry.getKey());
            Files.delete(fileToDeletePath);        }
    }

    public void setReadOnly(){
        multiUpload.setVisible(false);
    }

    public void setClient(int client){
        this.client = client;
    }
}
