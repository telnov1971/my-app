package com.example.application.views.support;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.Note;
import com.example.application.data.service.HistoryService;
import com.example.application.data.service.NoteService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotesLayout extends VerticalLayout {
    private Demand demand;
    private HorizontalLayout notesButtonLayout = new HorizontalLayout();
    private List<Note> Notes;
    private final Grid<Note> noteGrid = new Grid<>(Note.class,false);
    private ListDataProvider<Note> NoteListDataProvider;
    private Binder<Note> binderNote = new Binder<>(Note.class);
    private TextArea noteArea = new TextArea();
    private HorizontalLayout buttonsLayout = new HorizontalLayout();
    private Button addButton = new Button("Добавить примечание");
    private Button removeButton = new Button("Удалить последнее");
    //private Editor<Note> editorNote;

    private List<Note> notes;

    private final NoteService NoteService;
    private final HistoryService historyService;

    public NotesLayout(NoteService NoteService
            , HistoryService historyService) {
        this.historyService = historyService;
        this.NoteService = NoteService;

        noteGrid.setHeightByRows(true);
        notes = new ArrayList<>();

        Grid.Column<Note> columnDateTime =
                noteGrid.addComponentColumn(note -> new Label(note.getDateTime().
                        format(DateTimeFormatter.ofPattern("d/MMM/uuuu - HH:mm:ss"))))
                        .setHeader("Дата и время").setAutoWidth(true);
        Grid.Column<Note> columnClient =
                noteGrid.addComponentColumn(note -> new Label(note.getClient()?"Клиент":"Омскэлектро"))
                        .setHeader("Записал").setAutoWidth(true);
        Grid.Column<Note> columnNote =
                noteGrid.addColumn(Note::getNote)
                        .setHeader("Примечание").setAutoWidth(true);
        notes.add(new Note());
        noteGrid.setItems(notes);
        NoteListDataProvider = (ListDataProvider<Note>) noteGrid.getDataProvider();
        notes.remove(notes.size() - 1);

        NoteListDataProvider.refreshAll();
        
        addButton.addClickListener(e -> {
            notes.add(new Note(demand, noteArea.getValue(), true));
            noteArea.setValue("");
            NoteListDataProvider.refreshAll();
        });
        
        removeButton.addClickListener(e -> {
            notes.remove(notes.size() - 1);
            NoteListDataProvider.refreshAll();
        });

        noteArea.setWidthFull();
        buttonsLayout.add(addButton, removeButton);
        add(noteGrid,noteArea,buttonsLayout);
    }

    public void findAllByDemand(Demand demand) {
        this.demand = demand;
        notes = NoteService.findAllByDemand(demand);
        noteGrid.setItems(notes);
        NoteListDataProvider = (ListDataProvider<Note>) noteGrid.getDataProvider();
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
