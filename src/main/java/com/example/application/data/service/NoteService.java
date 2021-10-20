package com.example.application.data.service;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.Note;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService extends CrudService<Note, Long> {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }


    @Override
    protected NoteRepository getRepository() {
        return noteRepository;
    }

    public List<Note> findAllByDemand(Demand demand) {
        return noteRepository.findAllByDemand(demand);
    }

    public Optional<Note> findById(Long id) {
        return noteRepository.findById(id);
    }
}
