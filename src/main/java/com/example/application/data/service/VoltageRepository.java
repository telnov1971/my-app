package com.example.application.data.service;

import com.example.application.data.entity.Voltage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoltageRepository extends JpaRepository<Voltage, Long> {
    public Optional<Voltage> findById(Long id);
}