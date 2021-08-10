package com.example.application.data.service;

import com.example.application.data.entity.DemandType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DemandTypeRepository extends JpaRepository<DemandType, Long> {
    @Query("select dt from DemandType dt " +
            "where dt.active=:active")
    List<DemandType> findAllByActive(@Param("active") Boolean active);
}