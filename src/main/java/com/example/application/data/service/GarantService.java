package com.example.application.data.service;

import com.example.application.data.entity.Garant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class GarantService extends CrudService<Garant, Long> {
    private final GarantRepository garantRepository;

    public GarantService(GarantRepository garantRepository) {
        this.garantRepository = garantRepository;
    }

    @Override
    protected GarantRepository getRepository() {
        return garantRepository;
    }

    public List<Garant> findAll() {
        return garantRepository.findAll();
    }
}
