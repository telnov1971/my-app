package ru.omel.po.data.service;

import ru.omel.po.data.entity.Safety;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;
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

    public List<Safety> findAll() {
        return safetyRepository.findAll();
    }
}
