package com.example.application.data.service;

import com.example.application.data.entity.Voltage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoltageRepository extends JpaRepository<Voltage, Long> {
}