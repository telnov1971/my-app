package com.example.application.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;

import com.example.application.data.service.DemandRepository;
import com.example.application.data.entity.Demand;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.vaadin.artur.exampledata.DataType;
import org.vaadin.artur.exampledata.ExampleDataGenerator;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(DemandRepository demandRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (demandRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 Demand entities...");
            ExampleDataGenerator<Demand> demandRepositoryGenerator = new ExampleDataGenerator<>(Demand.class,
                    LocalDateTime.of(2021, 6, 16, 0, 0, 0));
            demandRepositoryGenerator.setData(Demand::setId, DataType.ID);
            demandRepositoryGenerator.setData(Demand::setCreatedate, DataType.DATE_LAST_7_DAYS);
            demandRepositoryGenerator.setData(Demand::setObject, DataType.WORD);
            demandRepositoryGenerator.setData(Demand::setAddress, DataType.WORD);
            demandRepositoryGenerator.setData(Demand::setPoints, DataType.NUMBER_UP_TO_100);
            demandRepositoryGenerator.setData(Demand::setDone, DataType.BOOLEAN_50_50);
            demandRepository.saveAll(demandRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}