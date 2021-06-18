package com.example.application.data.service;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findAllByDemand(Demand demand);
}