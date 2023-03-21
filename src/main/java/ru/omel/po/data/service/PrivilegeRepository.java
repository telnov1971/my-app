package ru.omel.po.data.service;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findByDemand(Demand demand);
}