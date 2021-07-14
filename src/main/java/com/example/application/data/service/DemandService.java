package com.example.application.data.service;

import com.example.application.data.entity.Demand;

import com.example.application.data.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;
import java.time.LocalDate;
import java.util.List;

@Service
public class DemandService extends CrudService<Demand, Long> {

    private DemandRepository repository;

    public DemandService(@Autowired DemandRepository repository) {
        this.repository = repository;
    }

    @Override
    protected DemandRepository getRepository() {
        return repository;
    }

    public List<Demand> findAllByUser(User user) {
        return repository.findByUser(user);
    }
}
