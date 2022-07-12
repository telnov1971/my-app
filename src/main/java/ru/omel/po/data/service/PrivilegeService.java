package ru.omel.po.data.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;
import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.Privilege;

@Service
public class PrivilegeService extends CrudService<Privilege,Long> {
    private final PrivilegeRepository privilegeRepository;

    public PrivilegeService(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    protected JpaRepository<Privilege,Long> getRepository() {
        return privilegeRepository;
    }

    public Privilege findByDemand(Demand demand) {
        return privilegeRepository.findByDemand(demand);
    }
}
