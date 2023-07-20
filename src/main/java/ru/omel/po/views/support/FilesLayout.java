package ru.omel.po.views.support;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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

    private final Grid.Column<FileStored> columnDelete;
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
            , HistoryService historyService
            , int client) {
        this.historyService = historyService;
        this.uploadPath = AppEnv.getUploadPath();
        String temp = AppEnv.getDbName();
        this.dbName = temp.substring(temp.lastIndexOf("/")+1);
        this.fileStoredService = fileStoredService;
        this.client = client;

        fileStoredGrid.setAllRowsVisible(true);
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
        Collection<Button> deleteButtons = Collections.newSetFromMap(new WeakHashMap<>());
        columnDelete = fileStoredGrid.addComponentColumn(file -> {
            Button delete = new Button(new Icon(VaadinIcon.TRASH));
            delete.addClassName("delete");
            delete.getElement().setAttribute("title","удалить");
            delete.addClickListener(event -> {
                String filename = file.getLink();
                String dirName = file.getDirectory() != null ?
                        uploadPath + file.getDirectory() + "\\" : uploadPath;
                File outFile = new File(dirName + filename);
                try {
                    if (outFile.exists()) {
                        try {
                            Files.delete(outFile.toPath());
                        } catch (IOException ioException){
                            Notification alert = new Notification("Не смог удалить файл на диске");
                            alert.setDuration(5000);
                            alert.addThemeVariants(NotificationVariant.LUMO_ERROR);
                            alert.open();
                        }
                        fileStoredService.delete(file.getId());
                        files.remove(file);
                        fileStoredGrid.setItems(files);
                        historyService.historyOfDelete(demand.getId(), file.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            delete.setEnabled(true);
            deleteButtons.add(delete);
            return delete;
        }).setResizable(false).setAutoWidth(false).setHeader("УДАЛИТЬ");
        setDeleteVisible(false);
        fileStoredGrid.addComponentColumn(file -> {
                    String str = switch (file.getClient()) {
                        case 0 -> "Омскэлектро";
                        case 1 -> "Клиент";
                        case 2 -> "ГП";
                        default -> "";
                    };
                    return new Label(str);})
                .setHeader("Кем загружено")
                .setAutoWidth(true);
        files.add(new FileStored());
        fileStoredGrid.setItems(files);
        fileStoredListDataProvider = (ListDataProvider<FileStored>) fileStoredGrid.getDataProvider();
        files.remove(files.size() - 1);

        fileStoredListDataProvider.refreshAll();

        Collection<Anchor> anchors = Collections.newSetFromMap(new WeakHashMap<>());
        fileStoredGrid.addComponentColumn(file -> {
            StreamResource resource = null;
            Anchor anchor = new Anchor();

            String filename = file.getLink();
            String dirName = file.getDirectory() != null ?
                    uploadPath + file.getDirectory() + "\\" : uploadPath;
            File outFile = new File(dirName + filename);
            try {
                if(outFile.exists()) {
                    if (outFile.exists()) {
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
        Paragraph hint = new Paragraph("Размер файла не должен превышать 50 МБ");
        add(fileTableName,fileStoredGrid, hint, multiUpload);
    }

    private void createUploadLayout() {
        UploadFilesI18N i18N = new UploadFilesI18N();
        multiUpload.setI18n(i18N);

        multiUpload.setDropAllowed(true);
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
        multiUpload.setMaxFileSize(50*1024*1024);
        multiUpload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(
                    errorMessage,
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

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
                Files.move(Paths.get(uploadPath + filename), Paths.get(resultFilename));
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
    public void setDeleteVisible(boolean deleteVisible){
        columnDelete.setVisible(deleteVisible);
    }
}
