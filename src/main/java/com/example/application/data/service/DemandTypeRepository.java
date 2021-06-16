package com.example.application.data.service;

import com.example.application.data.entity.DemandType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandTypeRepository extends JpaRepository<DemandType, Long> {
}