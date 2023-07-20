package ru.omel.po.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;
import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.DemandType;
import ru.omel.po.data.entity.Role;
import ru.omel.po.data.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DemandService extends CrudService<Demand, Long> {

    private final DemandRepository repository;

    public DemandService(@Autowired DemandRepository repository) {
        this.repository = repository;
    }

    @Override
    protected DemandRepository getRepository() {
        return repository;
    }

    // поиск всех заявок
    public Page<Demand> findAllByUser(User user, DemandType demandType, Pageable pageable) {
        Page<Demand> demandPage = null;
        Set<Role> role = user.getRoles();
        if((role.contains(Role.ADMIN) || role.contains(Role.SALES)) && demandType == null)
            demandPage = repository.findAll(pageable);
        if((role.contains(Role.ADMIN) || role.contains(Role.SALES)) && demandType != null)
            demandPage = repository.findAllByDemandType(demandType, pageable);
        if(role.contains(Role.GARANT) && demandType == null)
            demandPage = repository.findAllByGarant(user.getGarant(), pageable);
        if(role.contains(Role.GARANT) && demandType != null)
            demandPage = repository.findAllByGarantAndDemandType(user.getGarant(), demandType, pageable);
        if(role.contains(Role.USER))
            demandPage = repository.findAllByUser(user, pageable);
        return demandPage;
    }

    // поиск по номеру
    public Optional<Demand> findById(Long id) {
            return repository.findById(id);
    }

    // поиск по номеру и типу
    public Optional<Demand> findByIdAndUserAndDemandType(Long id, User user, DemandType demandType) {
        Optional<Demand> findedDemand = Optional.empty();
        if(demandType == null && user.getRoles().contains(Role.ADMIN))
            findedDemand = repository.findById(id);
        if(demandType != null && user.getRoles().contains(Role.ADMIN))
            findedDemand = repository.findByIdAndDemandType(id, demandType);
        if(demandType == null && user.getRoles().contains(Role.SALES))
            findedDemand = repository.findById(id);
        if(demandType != null && user.getRoles().contains(Role.SALES))
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

    // поиск по тексту и типу
    public List<Demand> findText(String text, User user, DemandType demandType) {
        List<Demand> demandList = null;
        if(demandType == null && user.getRoles().contains(Role.ADMIN))
            demandList = repository.search(text);
        if(demandType != null && user.getRoles().contains(Role.ADMIN))
            demandList = repository.search(text, demandType.getId());
        if(demandType == null && user.getRoles().contains(Role.SALES))
            demandList = repository.search(text);
        if(demandType != null && user.getRoles().contains(Role.SALES))
            demandList = repository.search(text, demandType.getId());
        if(demandType == null && user.getRoles().contains(Role.GARANT))
            demandList = repository.search4Garant(text, user.getGarant().getId());
        if(demandType != null && user.getRoles().contains(Role.GARANT))
            demandList = repository.search4Garant(text, user.getGarant().getId(), demandType.getId());
        if(demandType == null && user.getRoles().contains(Role.USER))
            demandList = repository.search4User(text, user.getId());
        if(demandType != null && user.getRoles().contains(Role.USER))
            demandList = repository.search4User(text, user.getId(), demandType.getId());
        return demandList;
    }

    // поиск по тексту и типу
    public List<Demand> findText(String text, Long demandType) {
        if(demandType == null)
            return repository.search(text);
        else
            return repository.search(text, demandType);
    }

    public List<Demand> findAllByUser(User user) {
        return repository.findAllByUser(user);
    }
}
