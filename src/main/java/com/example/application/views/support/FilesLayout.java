package com.example.application.views.support;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.FileStored;
import com.example.application.data.service.FileStoredService;
import com.example.application.data.service.SafetyService;
import com.example.application.data.service.VoltageService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.example.application.views.demandedit.DemandEditenergyReceive.uploadPath;

public class FilesLayout extends VerticalLayout {
    @Value("${upload.path.windows}")
    private String uploadPathWindows;
    @Value("${upload.path.linux}")
    private String uploadPathLinux;

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

    public FilesLayout(FileStoredService fileStoredService
            , VoltageService voltageService
            , SafetyService safetyService
            , String uploadPathWindows
            , String uploadPathLinux) {
        this.fileStoredService = fileStoredService;
        this.voltageService = voltageService;
        this.safetyService = safetyService;
        this.uploadPathWindows = uploadPathWindows;
        this.uploadPathLinux = uploadPathLinux;

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
        //Grid.Column<FileStored> anchorColumn =
        fileStoredGrid.addComponentColumn(file -> {
            StreamResource resource = null;

            String filename = file.getLink();
            File outFile = new File(uploadPath + filename);
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

            anchors.add(anchor);
            return anchor;
        }).setAutoWidth(true).setHeader("Сохранённые файлы");

        columnName.setVisible(false);
        columnLink.setVisible(false);

        createUploadLayout();
        add(fileStoredGrid, multiUpload);
    }

    private void createUploadLayout() {
        Div output = new Div();
        //Upload upload = new Upload(this::receiveUpload);

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
            output.removeAll();
            output.add(new Text("Uploaded: "+originalFileName+" to "+ resultFilename));
            filesToSave.put(newFile,this.originalFileName);
        });
        multiUpload.addFailedListener(event -> {
            output.removeAll();
            output.add(new Text("Upload failed: " + event.getReason()));
        });
        multiUpload.addFileRejectedListener(event -> {
            output.removeAll();
            output.add(new Text(event.getErrorMessage()));
        });

        //upload.setAutoUpload(false);
        multiUpload.setUploadButton(new Button("Загрузить файл"));
        add(multiUpload, output);
    }


//    public void pointsClean() {
//        files = new ArrayList<>();
//    }
//
    public void fileAdd(FileStored fileStored) {
        files.add(fileStored);
        fileStoredGrid.setItems(files);
        fileStoredListDataProvider = (ListDataProvider<FileStored>) fileStoredGrid.getDataProvider();
    }

    public void findAllByDemand(Demand demand) {
        files = fileStoredService.findAllByDemand(demand);
//        if(files.isEmpty()) {
//            fileAdd(new FileStored("/","_",demand));
//        }
        fileStoredGrid.setItems(files);
        fileStoredListDataProvider = (ListDataProvider<FileStored>) fileStoredGrid.getDataProvider();
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public void saveFiles() {
        for(Map.Entry<String, String> entry: filesToSave.entrySet()) {
            FileStored file = new FileStored(entry.getValue(),entry.getKey(),demand);
            file.setDemand(demand);
            fileStoredService.update(file);
        }
    }

    public void deleteFiles() throws IOException {
        for(Map.Entry<String, String> entry: filesToSave.entrySet()) {
            Path fileToDeletePath = Paths.get(uploadPath + entry.getValue());
            Files.delete(fileToDeletePath);        }
    }
}
