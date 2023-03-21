package ru.omel.po.data.service;

import ru.omel.po.data.entity.Garant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GarantRepository extends JpaRepository<Garant, Long> {
    @Query("select g from Garant g " +
            "where g.active=:active")
    List<Garant> findAllByActive(@Param("active") Boolean active);
}