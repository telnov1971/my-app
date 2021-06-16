package com.example.application.data.service;

import com.example.application.data.entity.Garant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GarantRepository extends JpaRepository<Garant, Long> {
}