package com.example.application.data.service;

import com.example.application.data.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class PriceService extends CrudService<Price, Long> {
    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @Override
    protected PriceRepository getRepository() {
        return priceRepository;
    }
}
