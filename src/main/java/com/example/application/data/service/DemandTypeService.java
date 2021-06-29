package com.example.application.data.service;

import com.example.application.data.entity.DemandType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;
import java.util.Optional;

@Service
public class DemandTypeService extends CrudService<DemandType, Long> {
    public static final Long TO15 = 1L;
    public static final Long TO150 = 2L;
    public static final Long TEMPORARY = 3L;
    public static final Long RECIVER = 5L;
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
}
