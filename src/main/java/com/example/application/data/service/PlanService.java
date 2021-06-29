package com.example.application.data.service;

import com.example.application.data.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class PlanService extends CrudService<Plan, Long> {
    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    protected PlanRepository getRepository() {
        return planRepository;
    }

    public List<Plan> findAll() {
        return planRepository.findAll();
    }
}
