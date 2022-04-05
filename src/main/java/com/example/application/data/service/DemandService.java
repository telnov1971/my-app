package com.example.application.data.service;

import com.example.application.data.entity.*;

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

//    public List<Demand> findAllByUser(User user) {
//        if(user.getRoles().contains(Role.ADMIN)) {
//            return repository.findAll();
//        } else {
//            return repository.findByUser(user);
//        }
//    }

    // поиск всех заявок по пользователю
    public Page<Demand> findAllByUser(User user, DemandType demandType, Pageable pageable) {
        if(user.getRoles().contains(Role.ADMIN)) {
            return repository.findAll(pageable);
        } else {
            return repository.findByUser(user, pageable);
        }
    }

    // поиск всех заявок и по типу для ГП
    public Page<Demand> findAllByGarantAndDemandType(Garant garant, DemandType demandType, Pageable pageable) {
        if(demandType == null)
            return repository.findAllByGarant(garant, pageable);
        else
            return repository.findAllByGarantAndDemandType(garant, demandType, pageable);
    }

    // поиск по номеру
    public Optional<Demand> findById(Long id) {
            return repository.findById(id);
    }

    // поиск по номеру и типу для пользователя
    public Optional<Demand> findByIdAndUserAndDemandType(Long id, User user, DemandType demandType) {
        Optional<Demand> findedDemand = null;
        if(demandType == null && user.getRoles().contains(Role.ADMIN))
            findedDemand = repository.findById(id);
        if(demandType != null && user.getRoles().contains(Role.ADMIN))
            findedDemand = repository.findByIdAndDemandType(id, demandType);
        if(demandType == null && user.getRoles().contains(Role.USER))
            findedDemand = repository.findByIdAndUser(id, user);
        if(demandType != null && user.getRoles().contains(Role.USER))
            findedDemand = repository.findByIdAndUserAndDemandType(id, user, demandType);
        if(demandType == null && user.getRoles().contains(Role.GARANT))
            findedDemand = repository.findByIdAndGarant(id, user.getGarant());
        if(demandType != null && user.getRoles().contains(Role.GARANT))
            findedDemand = repository.findByIdAndGarantAndDemandType(id, user.getGarant(), demandType);
        return findedDemand;
    }

    // поиск по номеру и типу для ГП
    public Optional<Demand> findByIdAndGarantAndDemandType(Long id, Garant garant, DemandType demandType) {
        if(demandType == null)
            return repository.findByIdAndGarant(id, garant);
        else
            return repository.findByIdAndGarantAndDemandType(id, garant, demandType);
    }

    // поиск по тексту и типу для ГП
    public List<Demand> findText(String text, Long garant, Long demandType) {
        if(demandType == null)
            return repository.search4Garant(text, garant);
        else
            return repository.search4Garant(text, garant, demandType);
    }

    // поиск по тексту и типу
    public List<Demand> findText(String text, Long demandType) {
        if(demandType == null)
            return repository.search(text);
        else
            return repository.search(text, demandType);
    }

}
