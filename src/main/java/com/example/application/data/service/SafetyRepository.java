package com.example.application.data.service;

import com.example.application.data.entity.Safety;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafetyRepository extends JpaRepository<Safety, Long> {
}