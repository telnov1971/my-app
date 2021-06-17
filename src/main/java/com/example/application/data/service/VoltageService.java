package com.example.application.data.service;

import com.example.application.data.entity.Voltage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

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

    public List<Voltage> findAll() {
        return voltageRepository.findAll();
    }
}
