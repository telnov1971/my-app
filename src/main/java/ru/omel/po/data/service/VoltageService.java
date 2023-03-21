package ru.omel.po.data.service;

import ru.omel.po.data.entity.Voltage;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;
import java.util.Optional;

@Service
public class VoltageService extends CrudService<Voltage, Long> {
    private final VoltageRepository voltageRepository;

    public VoltageService(VoltageRepository voltageRepository) {
        this.voltageRepository = voltageRepository;
    }

    @Override
    protected VoltageRepository getRepository() {
        return voltageRepository;
    }

    public List<Voltage> findAllByOptional(Boolean optional) {
        return voltageRepository.findAllByOptional(optional);
    }

    public Optional<Voltage> findById(Long i) {
        return voltageRepository.findById(i);
    }
}
