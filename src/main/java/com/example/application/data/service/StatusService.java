package com.example.application.data.service;

import com.example.application.data.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class StatusService extends CrudService<Status, Long> {
    private final StatusRepository statusRepository;

    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Override
    protected StatusRepository getRepository() {
        return statusRepository;
    }
}
