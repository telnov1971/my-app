package ru.omel.po.data.service;

import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.General;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

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
}
