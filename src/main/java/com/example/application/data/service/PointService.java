package com.example.application.data.service;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class PointService extends CrudService<Point, Long> {
    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @Override
    protected PointRepository getRepository() {
        return pointRepository;
    }

    public List<Point> findAllByDemand(Demand demand) {
        return pointRepository.findAllByDemand(demand);
    }
}
