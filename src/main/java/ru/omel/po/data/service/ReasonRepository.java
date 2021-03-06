package ru.omel.po.data.service;

import ru.omel.po.data.entity.Reason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReasonRepository extends JpaRepository<Reason, Long> {

  @Query("select r from Reason r " +
          "where r.temporal=:temporal")
  List<Reason> findAllByTemporal(@Param("temporal") Boolean temporal);
}