package com.example.application.data.service;

import com.example.application.data.AbstractDictionary;
import com.example.application.data.entity.Demand;
import com.example.application.data.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;
import java.util.Objects;

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
        history = history + "Заявитель: " +
                createHistory(demand.getDemander(),oldDemand.getDemander()) + "\n";
        history = history + "Паспорт серия: " +
                createHistory(demand.getPassportSerries(),oldDemand.getPassportSerries()) + "\n";
        history = history + "Паспорт номер: " +
                createHistory(demand.getPassportNumber(),oldDemand.getPassportNumber()) + "\n";
        history = history + "Реквизиты заявителя: " +
                createHistory(demand.getInn(),oldDemand.getInn()) + "\n";
        history = history + "Адрес регистрации: " +
                createHistory(demand.getAddressRegistration(),oldDemand.getAddressRegistration()) + "\n";
        history = history + "Адрес фактический: " +
                createHistory(demand.getAddressActual(),oldDemand.getAddressActual()) + "\n";
        history = history + "Номер телефона: " +
                createHistory(demand.getContact(),oldDemand.getContact()) + "\n";
        history = history + "Причина обращения: " +
                createHistory(demand.getReason(),oldDemand.getReason()) + "\n";
        history = history + "Объект подключения: " +
                createHistory(demand.getObject(),oldDemand.getObject()) + "\n";
        history = history + "Адрес объекта: " +
                createHistory(demand.getAddress(),oldDemand.getAddress()) + "\n";
        history = history + "Характер нагрузки: " +
                createHistory(demand.getSpecification(),oldDemand.getSpecification()) + "\n";
        history = history + "Гарантирующий поставщик: " +
                createHistory(demand.getGarant(),oldDemand.getGarant()) + "\n";
        history = history + "План выплат: " +
                createHistory(demand.getPlan(),oldDemand.getPlan()) + "\n";
        history = history + "Временный срок: " +
                createHistory(demand.getPeriod(),oldDemand.getPeriod()) + "\n";
        history = history + "Реквизиты договора: " +
                createHistory(demand.getContract(),oldDemand.getContract()) + "\n";
        return history;
    }

    private String createHistory(String strNew, String strOld){
        String history = " - ";
        if(strNew!=null){
            if(strOld!=null){
                if(!strNew.equals(strOld)){
                    history = strOld + " изменилось на: " + strNew;
                }
            } else {
                history = " изменилось на: " + strNew;
            }
        }
        return history;
    }

    private String createHistory(Double dbNew, Double dbOld){
        String history = " - ";
        if(dbNew!=null){
            if(dbOld!=null){
                if(!dbNew.equals(dbOld)){
                    history = dbOld + " изменилось на: " + dbNew;
                }
            } else {
                history = " изменилось на: " + dbNew;
            }
        }
        return history;
    }

    private String createHistory(AbstractDictionary dcNew, AbstractDictionary dcOld){
        String history = " - ";
        if(dcNew!=null){
            if(dcOld!=null){
                if(!Objects.equals(dcNew.getId(), dcOld.getId())){
                    history = dcOld.getName() + " изменилось на: " + dcNew.getName();
                }
            } else {
                history = " изменилось на: " + dcNew.getName();
            }
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
