package ru.omel.po.data.service;

import ru.omel.po.data.entity.DemandType;
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
