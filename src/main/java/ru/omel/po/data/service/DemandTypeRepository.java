package ru.omel.po.data.service;

import ru.omel.po.data.entity.DemandType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DemandTypeRepository extends JpaRepository<DemandType, Long> {
    @Query("select dt from DemandType dt " +
            "where dt.active=:active")
    List<DemandType> findAllByActive(@Param("active") Boolean active);
}