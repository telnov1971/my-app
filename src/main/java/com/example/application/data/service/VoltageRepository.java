package com.example.application.data.service;

import com.example.application.data.entity.Reason;
import com.example.application.data.entity.Voltage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoltageRepository extends JpaRepository<Voltage, Long> {
    public Optional<Voltage> findById(Long id);
    @Query("select v from Voltage v " +
            "where v.optional=:optional")
    List<Voltage> findAllByOptional(@Param("optional") Boolean optional);
}