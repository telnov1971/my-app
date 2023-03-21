package ru.omel.po.data.service;

import ru.omel.po.data.entity.Region;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class RegionService extends CrudService<Region, Long> {
    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    protected RegionRepository getRepository() {
        return regionRepository;
    }
}
