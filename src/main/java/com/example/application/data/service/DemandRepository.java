package com.example.application.data.service;

import com.example.application.data.entity.Demand;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandRepository extends JpaRepository<Demand, Long> {

}