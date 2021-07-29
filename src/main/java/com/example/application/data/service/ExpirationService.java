package com.example.application.data.service;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.Expiration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class ExpirationService extends CrudService<Expiration, Long> {
    private final ExpirationRepository expirationRepository;

    public ExpirationService(ExpirationRepository expirationRepository) {
        this.expirationRepository = expirationRepository;
    }


    @Override
    protected ExpirationRepository getRepository() {
        return expirationRepository;
    }

    public List<Expiration> findAllByDemand(Demand demand) {
        return expirationRepository.findAllByDemand(demand);
    }
}
