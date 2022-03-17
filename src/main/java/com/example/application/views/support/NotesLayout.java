package com.example.application.views.support;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.Note;
import com.example.application.data.service.NoteService;
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
    private final TextArea noteArea = new TextArea("","(введите примечание)");
    private final HorizontalLayout buttonsLayout = new HorizontalLayout();
    //private Editor<Note> editorNote;

    private List<Note> notes;
    private int count = 0;

    private final NoteService NoteService;
    //private final HistoryService historyService;

    public NotesLayout(NoteService NoteService) {
        //this.historyService = historyService;
        this.NoteService = NoteService;

        noteGrid.setHeightByRows(true);
        notes = new ArrayList<>();
        noteGrid.addComponentColumn(note -> new Label(note.getDateTime().
                        format(DateTimeFormatter.ofPattern("uuuu-MM-dd | HH:mm:ss"))))
                .setHeader("Дата и время").setAutoWidth(true);
        noteGrid.addComponentColumn(note -> new Label(note.getClient()?"Клиент":"Омскэлектро"))
                .setHeader("Записал").setAutoWidth(true);
        noteGrid.addColumn(Note::getNote)
                .setHeader("Комментарии").setAutoWidth(true);

        notes.add(new Note());
        noteGrid.setItems(notes);
        noteListDataProvider = (ListDataProvider<Note>) noteGrid.getDataProvider();
        notes.remove(notes.size() - 1);

        noteListDataProvider.refreshAll();

        Button addButton = new Button("Добавить примечание");
        Button removeButton = new Button("Удалить последнее");
        removeButton.setEnabled(false);
        noteArea.setHelperText("Сначала надо ввести текст примечания");

        noteArea.addKeyDownListener(e -> {
//            if(!noteArea.getValue().isEmpty()) {
                addButton.setEnabled(true);
//            }
        });

        addButton.addClickListener(e -> {
            if(noteArea.getValue().isEmpty()) {
                Notification.show("Сначала надо ввести текст примечания", 3000,
                        Notification.Position.BOTTOM_START);
                return;
            }
            notes.add(new Note(demand, noteArea.getValue(), true));
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

    public void saveNotes() {
        for(Note note: notes) {
            note.setDemand(demand);
            note.setClient(true);
            if(note.getId() == null) {
                NoteService.update(note);
            }
        }
    }

    public void setReadOnly(){
        noteArea.setVisible(false);
        buttonsLayout.setVisible(false);
    }
}
