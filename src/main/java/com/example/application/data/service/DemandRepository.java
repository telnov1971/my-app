package com.example.application.data.service;

import com.example.application.data.entity.Demand;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface DemandRepository extends JpaRepository<Demand, Long> {

}