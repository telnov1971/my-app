package com.example.application.data.service;

import com.example.application.data.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

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
}
