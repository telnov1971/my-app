package com.example.application.data.service;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class HistoryService extends CrudService<History,Long> {
    private final HistoryRepository historyRepository;
    private final DemandService demandService;
    private final PointService pointService;
    private final FileStoredService fileStoredService;
    private final ExpirationService expirationService;
    private String history;
    private Demand oldDemand = new Demand();

    public HistoryService(HistoryRepository historyRepository,
                          DemandService demandService,
                          PointService pointService,
                          FileStoredService fileStoredService,
                          ExpirationService expirationService) {
        this.historyRepository = historyRepository;
        this.demandService = demandService;
        this.pointService = pointService;
        this.fileStoredService = fileStoredService;
        this.expirationService = expirationService;
        this.history = "";
    }

    @Override
    protected JpaRepository<History, Long> getRepository() {
        return historyRepository;
    }

    public String writeHistory(Demand demand) {
        this.history = "";
        if(demand.getId()!=null && demandService.findById(demand.getId()).isPresent()) {
             oldDemand = demandService.findById(demand.getId()).get();
        } else {
            return "Заявка создана";
        }
        if(oldDemand.getDemander()==null) oldDemand.setDemander("");
        if(demand.getDemander()==null) demand.setDemander("");
        if(!demand.getDemander().equals(oldDemand.getDemander())) {
            history = history + "Значение Заявитель: " + oldDemand.getDemander() +
                    " изменилось на: " + demand.getDemander() + "\n";
        }
        if(oldDemand.getPassportSerries()==null) oldDemand.setPassportSerries("");
        if(demand.getPassportSerries()==null) demand.setPassportSerries("");
        if(!demand.getPassportSerries().equals(oldDemand.getPassportSerries())) {
            history = history + "Значение Паспорт серия: " + oldDemand.getPassportSerries() +
                    " изменилось на: " + demand.getPassportSerries() + "\n";
        }
        if(oldDemand.getPassportNumber()==null) oldDemand.setPassportNumber("");
        if(demand.getPassportNumber()==null) demand.setPassportNumber("");
        if(!demand.getPassportNumber().equals(oldDemand.getPassportNumber())) {
            history = history + "Значение Паспорт номер: " + oldDemand.getPassportNumber() +
                    " изменилось на: " + demand.getPassportNumber() + "\n";
        }
        if(oldDemand.getInn()==null) oldDemand.setInn("");
        if(demand.getInn()==null) demand.setInn("");
        if(!demand.getInn().equals(oldDemand.getInn())) {
            history = history + "Значение Реквизитов заявителя: " + oldDemand.getInn() +
                    " изменилось на: " + demand.getInn() + "\n";
        }
        if(oldDemand.getAddressRegistration()==null) oldDemand.setAddressRegistration("");
        if(demand.getAddressRegistration()==null) demand.setAddressRegistration("");
        if(!demand.getAddressRegistration().equals(oldDemand.getAddressRegistration())) {
            history = history + "Значение Адрес регистрации: " + oldDemand.getAddressRegistration() +
                    " изменилось на: " + demand.getAddressRegistration() + "\n";
        }
        if(oldDemand.getAddressActual()==null) oldDemand.setAddressActual("");
        if(demand.getAddressActual()==null) demand.setAddressActual("");
        if(!demand.getAddressActual().equals(oldDemand.getAddressActual())) {
            history = history + "Значение Адрес фактический: " + oldDemand.getAddressActual() +
                    " изменилось на: " + demand.getAddressActual() + "\n";
        }
        if(oldDemand.getContact()==null) oldDemand.setContact("");
        if(demand.getContact()==null) demand.setContact("");
        if(!demand.getContact().equals(oldDemand.getContact())) {
            history = history + "Значение Номер телефона: " + oldDemand.getContact() +
                    " изменилось на: " + demand.getContact() + "\n";
        }
        if(oldDemand.getReason()==null && demand.getReason()!=null) {
            history = history + "Значение Причина обращения: " +
                    " изменилось на: " + demand.getReason().getName() + "\n";
        } else {
            if (oldDemand.getReason()!=null && !demand.getReason().equals(oldDemand.getReason())) {
                history = history + "Значение Причина обращения: " + oldDemand.getReason().getName() +
                        " изменилось на: " + demand.getReason().getName() + "\n";
            }
        }
        if(oldDemand.getObject()==null) oldDemand.setObject("");
        if(demand.getObject()==null) demand.setObject("");
        if(!demand.getObject().equals(oldDemand.getObject())) {
            history = history + "Значение Объект подключения: " + oldDemand.getObject() +
                    " изменилось на: " + demand.getObject() + "\n";
        }
        if(oldDemand.getAddress()==null) oldDemand.setAddress("");
        if(demand.getAddress()==null) demand.setAddress("");
        if(!demand.getAddress().equals(oldDemand.getAddress())) {
            history = history + "Значение Адрес объекта: " + oldDemand.getAddress() +
                    " изменилось на: " + demand.getAddress() + "\n";
        }
        if(oldDemand.getSpecification()==null) oldDemand.setSpecification("");
        if(demand.getSpecification()==null) demand.setSpecification("");
        if(!demand.getSpecification().equals(oldDemand.getSpecification())) {
            history = history + "Значение Характер нагрузки: " + oldDemand.getSpecification() +
                    " изменилось на: " + demand.getSpecification() + "\n";
        }
        if(oldDemand.getGarant()==null && demand.getGarant()!=null) {
            history = history + "Значение Гарантирующий поставщик " +
                    " изменилось на: " + demand.getGarant().getName() + "\n";
        } else {
            if (oldDemand.getGarant()!=null && !demand.getGarant().equals(oldDemand.getGarant())) {
                history = history + "Значение Гарантирующий поставщик: " + oldDemand.getGarant().getName() +
                        " изменилось на: " + demand.getGarant().getName() + "\n";
            }
        }
        if(oldDemand.getPlan()==null && demand.getPlan()!=null) {
            history = history + "Значение План рассчётов: " +
                    " изменилось на: " + demand.getPlan().getName() + "\n";
        } else {
            if (oldDemand.getPlan()!=null && !demand.getPlan().equals(oldDemand.getPlan())) {
                history = history + "Значение План рассчётов: " + oldDemand.getPlan().getName() +
                        " изменилось на: " + demand.getPlan().getName() + "\n";
            }
        }
        if(oldDemand.getPeriod()==null) oldDemand.setPeriod("");
        if(demand.getPeriod()==null) demand.setPeriod("");
        if(!demand.getPeriod().equals(oldDemand.getPeriod())) {
            history = history + "Значение Временный срок: " + oldDemand.getPeriod() +
                    " изменилось на: " + demand.getPeriod() + "\n";
        }
        if(oldDemand.getContract()==null) oldDemand.setContract("");
        if(demand.getContract()==null) demand.setContract("");
        if(!demand.getContract().equals(oldDemand.getContract())) {
            history = history + "Значение Реквизиты договора: " + oldDemand.getContract() +
                    " изменилось на: " + demand.getContract() + "\n";
        }
        return history;
    }

    public History save(History history) {
        return historyRepository.save(history);
    }

    public List<History> findAllByDemand(Demand demand) {
        return historyRepository.findAllByDemand(demand);
    }
}
