package com.example.application.data.service;

import com.example.application.data.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
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
