package ru.omel.po.data.generator;

import ru.omel.po.data.entity.*;
import ru.omel.po.data.service.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Collections;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

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
                                      VoltageRepository voltageRepository,
                                      UserRepository userRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if(userRepository.count() != 0L) {
                logger.info("Using existing 'Demand' table");
                return;
            } else {
                try {
                    userRepository.save(
                            new User("user", "",true, "User", "666-666", Collections.singleton(Role.USER)));
                    userRepository.save(
                            new User("admin", "",true, "Admin", "007", Collections.singleton(Role.USER)));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
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
//            ExampleDataGenerator<Demand> demandRepositoryGenerator = new ExampleDataGenerator<>(Demand.class,
//                    LocalDateTime.of(2021, 6, 16, 0, 0, 0));
//            demandRepositoryGenerator.setData(Demand::setCreateDate, DataType.DATE_LAST_7_DAYS);
//            demandRepositoryGenerator.setData(Demand::setDemander, DataType.WORD);
//            //DataType<String> PASS_NUMBER = RandomUtils.nextInt(1000,9999);
//            //demandRepositoryGenerator.setData(Demand::setPassportNumber, );
//            demandRepositoryGenerator.setData(Demand::setPassportSerries, DataType.ADDRESS);
//            demandRepositoryGenerator.setData(Demand::setPasportIssued, DataType.BOOK_TITLE);
//
//            demandRepositoryGenerator.setData(Demand::setAddressRegistration, DataType.ADDRESS);
//            demandRepositoryGenerator.setData(Demand::setAddressActual, DataType.ADDRESS);
//            demandRepositoryGenerator.setData(Demand::setContact, DataType.PHONE_NUMBER);
//            demandRepositoryGenerator.setData(Demand::setObject, DataType.WORD);
//            demandRepositoryGenerator.setData(Demand::setAddress, DataType.ADDRESS);
//            demandRepositoryGenerator.setData(Demand::setExecuted, DataType.BOOLEAN_50_50);
//            demandRepository.saveAll(demandRepositoryGenerator.create(10, seed));

            String listDemander[] = {"Ivanov","Petrov","Sidorov","Kozlov","Kalashnikov",
                    "Kuznetsov","Semenov","Savaliev","Arbatov","Nemo"};
            String listIssued[] = {"Moskow","Omsk","Piter","Sochi","Tambov",
                    "Anapa","Khabarovsk","Krasnoyarsk","Ufa","Tumen"};
            String listObject[] = {"House","Home","Office","Enterprise","Factory","Hospital","Restaurant"};
            String listNumber[] = {"123456","654321","153624","452163","145236"};
            String listSeries[] = {"1234","6543","1536","4521","1452"};

            for (int i = 0; i < 100; i++) {
                Demand demand = new Demand();
                demand.setDemander(listDemander[RandomUtils.nextInt(0,9)]);
                demand.setPassportSerries(listSeries[RandomUtils.nextInt(0,4)]);
                demand.setPassportNumber(listNumber[RandomUtils.nextInt(0,4)]);
                demand.setPassportIssued(listIssued[RandomUtils.nextInt(0,9)]);
                demand.setAddressActual(listIssued[RandomUtils.nextInt(0,9)]);
                demand.setObject(listObject[RandomUtils.nextInt(0,6)]);
                demand.setAddress(listIssued[RandomUtils.nextInt(0,9)]);
                demand.setUser(userRepository.findById(1L).get());
            }

            logger.info("Generated demo data");
        };
    }
}