package ru.omel.po.data.service;

import ru.omel.po.data.entity.Garant;
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

    public List<Garant> findAllByActive(Boolean active) {
        return garantRepository.findAllByActive(active);
    }
}
