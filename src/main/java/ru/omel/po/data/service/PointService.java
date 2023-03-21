package ru.omel.po.data.service;

import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.Point;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;
import java.util.Optional;

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

    public Optional<Point> findById(Long id) {
        return pointRepository.findById(id);
    }
}
