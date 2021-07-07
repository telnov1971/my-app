package com.example.application.data.service;

import com.example.application.data.entity.General;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class GeneralService extends CrudService<General,Long> {
    private final GeneralRepository generalRepository;

    public GeneralService(GeneralRepository generalRepository) {
        this.generalRepository = generalRepository;
    }

    @Override
    protected JpaRepository<General, Long> getRepository() {
        return null;
    }
}
