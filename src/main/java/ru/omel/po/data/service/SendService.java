package ru.omel.po.data.service;

import ru.omel.po.data.entity.Send;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

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

    public List<Send> findAll() {
        return sendRepository.findAll();
    }
}
