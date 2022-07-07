package ru.omel.po.views.support;

import com.vaadin.flow.component.notification.NotificationVariant;
import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.Note;
import ru.omel.po.data.service.HistoryService;
import ru.omel.po.data.service.NoteService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotesLayout extends VerticalLayout {
    private Demand demand;
    private final Grid<Note> noteGrid = new Grid<>(Note.class,false);
    private ListDataProvider<Note> noteListDataProvider;
    //private Binder<Note> binderNote = new Binder<>(Note.class);
    private final TextArea noteArea = new TextArea("","(введите комментарий)");
    private final HorizontalLayout buttonsLayout = new HorizontalLayout();
    //private Editor<Note> editorNote;

    private List<Note> notes;
    Button addButton = new Button("Добавить комментарий");
    Button removeButton = new Button("Удалить последний");
    private int count = 0;

    private final ru.omel.po.data.service.NoteService NoteService;
    private final HistoryService historyService;
    private int client;

    public NotesLayout(NoteService NoteService, HistoryService historyService, int client) {
        //this.historyService = historyService;
        this.NoteService = NoteService;
        this.historyService = historyService;
        this.client = client;

        noteGrid.setHeightByRows(true);
        notes = new ArrayList<>();
        noteGrid.addComponentColumn(note -> new Label(note.getDateTime().
                        format(DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm:ss"))))
                .setHeader("Дата и время").setAutoWidth(true);
        noteGrid.addComponentColumn(note -> {
                    String str = "";
                    switch (note.getClient()){
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
                .setHeader("Записал").setAutoWidth(true);
        noteGrid.addColumn(Note::getNote)
                .setHeader("Комментарии").setAutoWidth(true);

        notes.add(new Note());
        noteGrid.setItems(notes);
        noteListDataProvider = (ListDataProvider<Note>) noteGrid.getDataProvider();
        notes.remove(notes.size() - 1);

        noteListDataProvider.refreshAll();

        removeButton.setEnabled(false);
        noteArea.setHelperText("Сначала надо ввести текст комментария");

        noteArea.addKeyDownListener(e -> {
//            if(!noteArea.getValue().isEmpty()) {
                addButton.setEnabled(true);
//            }
        });

        addButton.addClickListener(e -> {
            if(noteArea.getValue().isEmpty()) {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText("Сначала надо ввести текст примечания");
                notification.setPosition(Notification.Position.BOTTOM_START);
                notification.setDuration(3000);
                notification.open();
                return;
            }
            notes.add(new Note(demand, noteArea.getValue(), client));
            noteArea.setValue("");
            noteListDataProvider.refreshAll();
            removeButton.setEnabled(true);
            addButton.setEnabled(false);
        });
        addButton.setEnabled(false);

        removeButton.addClickListener(e -> {
            if(notes.toArray().length > count) {
                notes.remove(notes.size() - 1);
                noteListDataProvider.refreshAll();
            } else {
                removeButton.setEnabled(false);
            }
        });

        noteArea.setWidthFull();
        buttonsLayout.add(addButton, removeButton);
        Label notesLabel = new Label("Комментарии: (Любая информация не входящая в заданные поля"+
                " или возникающая при дальнейшем взаимодействии. Можно удалить только ещё не сохранённое)");
        add(notesLabel,noteGrid,noteArea,buttonsLayout);
    }

    public void findAllByDemand(Demand demand) {
        this.demand = demand;
        notes = NoteService.findAllByDemand(demand);
        count = notes.toArray().length;
        noteGrid.setItems(notes);
        noteListDataProvider = (ListDataProvider<Note>) noteGrid.getDataProvider();
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public boolean saveNotes(int client) {
        boolean result = false;
        if(!noteArea.getValue().isEmpty()) {
            notes.add(new Note(demand, noteArea.getValue(), client));
            noteArea.setValue("");
            noteListDataProvider.refreshAll();
            removeButton.setEnabled(true);
            addButton.setEnabled(false);
        }
        for(Note note: notes) {
            note.setDemand(demand);
            note.setClient(client);
            if(note.getId() == null) {
                try{
                    NoteService.update(note);
                } catch (Exception e) {
                    return false;
                }
                result = historyService.saveHistory(client, demand, note, Note.class);
            }
        }
        return result;
    }

    public void setReadOnly(){
        noteArea.setVisible(false);
        buttonsLayout.setVisible(false);
    }

    public void setClient(int client){
        this.client = client;
    }
}
