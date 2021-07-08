package com.example.application.data.service;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.General;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneralRepository extends JpaRepository<General, Long> {
    List<General> findAllByDemand(Demand demand);
}