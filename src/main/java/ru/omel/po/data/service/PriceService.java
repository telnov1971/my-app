package ru.omel.po.data.service;

import ru.omel.po.data.entity.Price;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

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

    public List<Price> findAll() {
        return priceRepository.findAll();
    }
}
