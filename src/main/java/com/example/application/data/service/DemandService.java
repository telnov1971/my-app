package com.example.application.data.service;

import com.example.application.data.entity.Demand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;
import java.time.LocalDate;

@Service
public class DemandService extends CrudService<Demand, Integer> {

    private DemandRepository repository;

    public DemandService(@Autowired DemandRepository repository) {
        this.repository = repository;
    }

    @Override
    protected DemandRepository getRepository() {
        return repository;
    }

}
