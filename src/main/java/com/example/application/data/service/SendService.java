package com.example.application.data.service;

import com.example.application.data.entity.Send;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class SendService extends CrudService<Send, Long> {
    private final SendRepository sendRepository;

    public SendService(SendRepository sendRepository) {
        this.sendRepository = sendRepository;
    }

    @Override
    protected SendRepository getRepository() {
        return sendRepository;
    }
}
