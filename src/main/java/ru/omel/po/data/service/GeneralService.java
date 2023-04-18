package ru.omel.po.data.service;

import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.General;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GeneralService extends CrudService<General,Long> {
    private final GeneralRepository generalRepository;

    public GeneralService(GeneralRepository generalRepository) {
        this.generalRepository = generalRepository;
    }

    @Override
    protected JpaRepository<General, Long> getRepository() {
        return generalRepository;
    }

    public List<General> findAllByDemand(Demand demand) {
        return generalRepository.findAllByDemand(demand);
    }

    public General findByDemand(Demand demand) {
        return generalRepository.findByDemand(demand);
    }

    public Optional<General> findById(Long id) {
        return generalRepository.findById(id);
    }
}
