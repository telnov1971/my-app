package com.example.application.data.service;

import com.example.application.data.entity.Demand;

import com.example.application.data.entity.Garant;
import com.example.application.data.entity.Role;
import com.example.application.data.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;
import java.util.Optional;

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
        if(user.getRoles().contains(Role.ADMIN)) {
            return repository.findAll();
        } else {
            return repository.findByUser(user);
        }
    }

    public Page<Demand> findAllByUser(User user, Pageable pageable) {
        if(user.getRoles().contains(Role.ADMIN)) {
            return repository.findAll(pageable);
        } else {
            return repository.findByUser(user, pageable);
        }
    }

    public Page<Demand> findAllByGarant(Garant garant, Pageable pageable) {
        return repository.findAllByGarant(garant,pageable);
    }

    public Optional<Demand> findById(Long id) {
        return repository.findById(id);
    }
    public Optional<Demand> findByIdAndGarant(Long id, Garant garant) {
        return repository.findByIdAndGarant(id, garant);
    }

    public List<Demand> findText(String text, Long garant) {
        return repository.search(text, garant);
    }
    public List<Demand> findText(String text) {
        return repository.search(text);
    }

    public List<Demand> findAll() {
        return repository.findAll();
    }

    public Page<Demand> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
