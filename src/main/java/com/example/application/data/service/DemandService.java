package com.example.application.data.service;

import com.example.application.data.entity.Demand;

import com.example.application.data.entity.Garant;
import com.example.application.data.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;
import java.time.LocalDate;
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
        return repository.findByUser(user);
    }

    public Page<Demand> findAllByUser(User user, Pageable pageable) {
        return repository.findByUser(user, pageable);
    }

    public List<Demand> findAllByGarant(Garant garant) {
        return repository.findAllByGarant(garant);
    }

    public Page<Demand> findAllByGarant(Garant garant, Pageable pageable) {
        return repository.findAllByGarant(garant,pageable);
    }

    public int countAllByGarant(Garant garant) {
        return repository.countAllByGarant(garant);
    }

    public int countAllByUser(User user) {
        return repository.countAllByUser(user);
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
}
