package com.example.application.data.service;

import com.example.application.data.entity.DemandType;
import com.example.application.data.entity.Reason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;
import java.util.Optional;

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

    public Optional<DemandType> findById(long l) {
        return demandTypeRepository.findById(l);
    }

    public List<DemandType> findAllByActive(Boolean active){
        return demandTypeRepository.findAllByActive(active);
    }
}
