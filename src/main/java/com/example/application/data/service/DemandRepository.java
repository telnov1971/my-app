package com.example.application.data.service;

import com.example.application.data.entity.Demand;

import com.example.application.data.entity.Garant;
import com.example.application.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandRepository extends JpaRepository<Demand, Long> {
    List<Demand> findByUser(User user);

    List<Demand> findAllByGarant(Garant garant);
}