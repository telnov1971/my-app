package com.example.application.data.service;

import com.example.application.data.entity.Safety;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import javax.persistence.Tuple;
import java.util.Optional;

@Service
public class SafetyService extends CrudService<Safety, Long> {
    private final SafetyRepository safetyRepository;

    public SafetyService(SafetyRepository safetyRepository) {
        this.safetyRepository = safetyRepository;
    }

    @Override
    protected SafetyRepository getRepository() {
        return safetyRepository;
    }

    public Optional<Safety> findById(long l) {
        return safetyRepository.findById(l);
    }
}
