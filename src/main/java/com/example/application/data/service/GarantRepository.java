package com.example.application.data.service;

import com.example.application.data.entity.Garant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GarantRepository extends JpaRepository<Garant, Long> {
    @Query("select g from Garant g " +
            "where g.active=:active")
    List<Garant> findAllByActive(@Param("active") Boolean active);
}