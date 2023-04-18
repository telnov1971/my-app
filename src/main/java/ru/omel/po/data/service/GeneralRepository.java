package ru.omel.po.data.service;

import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.General;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneralRepository extends JpaRepository<General, Long> {
    List<General> findAllByDemand(Demand demand);

    General findByDemand(Demand demand);
}