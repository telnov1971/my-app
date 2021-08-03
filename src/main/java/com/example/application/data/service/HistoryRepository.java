package com.example.application.data.service;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findAllByDemand(Demand demand);
}