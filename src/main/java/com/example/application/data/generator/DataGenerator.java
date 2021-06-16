package com.example.application.data.generator;

import com.example.application.data.entity.*;
import com.example.application.data.service.*;
import com.vaadin.flow.spring.annotation.SpringComponent;

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
    public CommandLineRunner loadData(DemandRepository demandRepository,
                                      DemandTypeRepository demandTypeRepository,
                                      ExpirationRepository expirationRepository,
                                      GarantRepository garantRepository,
                                      PlanRepository planRepository,
                                      PointRepository pointRepository,
                                      PriceRepository priceRepository,
                                      RegionRepository regionRepository,
                                      SafetyRepository safetyRepository,
                                      SendRepository sendRepository,
                                      StatusRepository statusRepository,
                                      VoltageRepository voltageRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (demandRepository.count() != 0L) {
                logger.info("Using existing 'Demand' table");
                return;
            }
            if (demandTypeRepository.count() != 0L) {
                logger.info("Using existing 'DemandType' table");
            } else {
                demandTypeRepository.save(new DemandType("До 15 кВт", "000000001"));
                demandTypeRepository.save(new DemandType("До 150 кВт", "000000002"));
                demandTypeRepository.save(new DemandType("Временное", "000000003"));
                demandTypeRepository.save(new DemandType("До 150 кВт с генерацией", "000000004"));
                demandTypeRepository.save(new DemandType("Для энергоприёма", "000000005"));
            }
            if (garantRepository.count() != 0L) {
                logger.info("Using existing 'Garant' table");
            } else {
                garantRepository.save(new Garant("Омскэлектро", "000000001"));
                garantRepository.save(new Garant("Омскэнерго", "000000002"));
                garantRepository.save(new Garant("Сибэнергосервис", "000000003"));
            }
            if (planRepository.count() != 0L) {
                logger.info("Using existing 'Plan' table");
            } else {
                planRepository.save(new Plan("Вариант 1","0000000001"));
                planRepository.save(new Plan("Вариант 2","0000000002"));
            }
            if (priceRepository.count() != 0L) {
                logger.info("Using existing 'Price' table");
            } else {
                priceRepository.save(new Price("Дёшево", "000000001"));
                priceRepository.save(new Price("Нормально", "000000002"));
                priceRepository.save(new Price("Дорого", "000000003"));
            }
            if (regionRepository.count() != 0L) {
                logger.info("Using existing 'Region' table");
            } else {
                regionRepository.save(new Region("СРЭС","000000001"));
                regionRepository.save(new Region("ЛРЭС","000000002"));
                regionRepository.save(new Region("ЮРЭС","000000003"));
            }
            if (safetyRepository.count() != 0L) {
                logger.info("Using existing 'Safety' table");
            } else {
                safetyRepository.save(new Safety("1 категория","000000001"));
                safetyRepository.save(new Safety("2 категория","000000002"));
                safetyRepository.save(new Safety("3 категория","000000003"));
            }
            if (sendRepository.count() != 0L) {
                logger.info("Using existing 'Send' table");
            } else {
                sendRepository.save(new Send("При визите","000000001"));
                sendRepository.save(new Send("Почтой России","000000002"));
                sendRepository.save(new Send("Курьером","000000003"));
            }
            if (statusRepository.count() != 0L) {
                logger.info("Using existing 'Status' table");
            } else {
                statusRepository.save(new Status("Новая","000000001"));
                statusRepository.save(new Status("В работе","000000002"));
                statusRepository.save(new Status("Отложено","000000003"));
                statusRepository.save(new Status("Выполнено","000000004"));
            }
            if (voltageRepository.count() != 0L) {
                logger.info("Using existing 'Voltage' table");
            } else {
                voltageRepository.save(new Voltage("0,4 кВ","000000001"));
                voltageRepository.save(new Voltage("10 кВ","000000002"));
            }



            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 Demand entities...");
            ExampleDataGenerator<Demand> demandRepositoryGenerator = new ExampleDataGenerator<>(Demand.class,
                    LocalDateTime.of(2021, 6, 16, 0, 0, 0));
            demandRepositoryGenerator.setData(Demand::setCreatedate, DataType.DATE_LAST_7_DAYS);
            demandRepositoryGenerator.setData(Demand::setDemander, DataType.WORD);
            demandRepositoryGenerator.setData(Demand::setAddressRegistration, DataType.ADDRESS);
            demandRepositoryGenerator.setData(Demand::setAddressActual, DataType.ADDRESS);
            demandRepositoryGenerator.setData(Demand::setContact, DataType.PHONE_NUMBER);
            demandRepositoryGenerator.setData(Demand::setObject, DataType.WORD);
            demandRepositoryGenerator.setData(Demand::setAddress, DataType.ADDRESS);
            demandRepositoryGenerator.setData(Demand::setDone, DataType.BOOLEAN_50_50);
            demandRepository.saveAll(demandRepositoryGenerator.create(10, seed));

            logger.info("Generated demo data");
        };
    }

}