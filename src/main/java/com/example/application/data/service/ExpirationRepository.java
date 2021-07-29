package com.example.application.data.service;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.Expiration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpirationRepository extends JpaRepository<Expiration, Long> {
    List<Expiration> findAllByDemand(Demand demand);
}