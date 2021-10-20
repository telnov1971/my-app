package com.example.application.data.service;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByDemand(Demand demand);
}