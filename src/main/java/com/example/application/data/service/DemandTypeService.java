package com.example.application.data.service;

import com.example.application.data.entity.DemandType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class DemandTypeService extends CrudService<DemandType, Long> {
    private final DemandTypeRepository demandTypeRepository;

    public DemandTypeService(DemandTypeRepository demandTypeRepository) {
        this.demandTypeRepository = demandTypeRepository;
    }

    @Override
    protected DemandTypeRepository getRepository() {
        return demandTypeRepository;
    }

    public List<DemandType> findAll() {
        return demandTypeRepository.findAll();
    }
}
