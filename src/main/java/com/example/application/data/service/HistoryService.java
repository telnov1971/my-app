package com.example.application.data.service;

import com.example.application.data.AbstractDictionary;
import com.example.application.data.entity.*;
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
        String temp;
        this.history = "";
        if(demand.getId()!=null && demandService.findById(demand.getId()).isPresent()) {
             oldDemand = demandService.findById(demand.getId()).get();
        } else {
            return "Заявка создана";
        }
        temp = createHistory(demand.getDemander(),oldDemand.getDemander());
        history = history + (!temp.equals("") ? "Заявитель: " + temp + "\n": "");
        temp = createHistory(demand.getTypeDemander(),oldDemand.getTypeDemander());
        history = history + (!temp.equals("") ? "Тип заявителя: " + temp + "\n": "");
        temp = createHistory(demand.getPassportSerries(),oldDemand.getPassportSerries());
        history = history + (!temp.equals("") ? "Паспорт серия: " + temp + "\n" : "");
        temp = createHistory(demand.getPassportNumber(),oldDemand.getPassportNumber());
        history = history + (!temp.equals("") ? "Паспорт номер: " + temp + "\n" : "");
        temp = createHistory(demand.getInn(),oldDemand.getInn());
        history = history + (!temp.equals("") ? "Реквизиты заявителя: " + temp + "\n" : "");
        temp = createHistory(demand.getAddressRegistration(),oldDemand.getAddressRegistration());
        history = history + (!temp.equals("") ? "Адрес регистрации: " + temp + "\n" : "");
        temp = createHistory(demand.getAddressActual(),oldDemand.getAddressActual());
        history = history + (!temp.equals("") ? "Адрес фактический: " + temp + "\n" : "");
        temp = createHistory(demand.getContact(),oldDemand.getContact());
        history = history + (!temp.equals("") ? "Номер телефона: " + temp + "\n" : "");
        temp = createHistory(demand.getReason(),oldDemand.getReason());
        history = history + (!temp.equals("") ? "Причина обращения: " + temp + "\n" : "");
        temp = createHistory(demand.getObject(),oldDemand.getObject());
        history = history + (!temp.equals("") ? "Объект подключения: " + temp + "\n" : "");
        temp = createHistory(demand.getAddress(),oldDemand.getAddress());
        history = history + (!temp.equals("") ? "Адрес объекта: " + temp + "\n" : "");
        temp = createHistory(demand.getSpecification(),oldDemand.getSpecification());
        history = history + (!temp.equals("") ? "Характер нагрузки: " + temp + "\n" : "");
        temp = createHistory(demand.getGarant(),oldDemand.getGarant());
        history = history + (!temp.equals("") ? "Гарантирующий поставщик: " + temp + "\n" : "");
        temp = createHistory(demand.getPlan(),oldDemand.getPlan());
        history = history + (!temp.equals("") ? "План выплат: " + temp + "\n" : "");
        temp = createHistory(demand.getPeriod(),oldDemand.getPeriod());
        history = history + (!temp.equals("") ? "Временный срок: " + temp + "\n" : "");
        temp = createHistory(demand.getContract(),oldDemand.getContract());
        history = history + (!temp.equals("") ? "Реквизиты договора: " + temp + "\n" : "");
        return history;
    }

    public String writeHistory(Point point){
        String pointHistory = "";
        String temp;
        if(point!=null) {
            if(point.getId()!=null) {
                if(pointService.findById(point.getId()).isPresent()) {
                    Point oldPoint = pointService.findById(point.getId()).get();
                    temp = createHistory(point.getPowerDemand(), oldPoint.getPowerDemand());
                    pointHistory = pointHistory + (!temp.equals("") ?
                            "Мощность присоединяемая, кВт: " + temp + "\n" : "");
                    temp = createHistory(point.getPowerCurrent(), oldPoint.getPowerCurrent());
                    pointHistory = pointHistory + (!temp.equals("") ?
                            "Мощность ранее присоединённая, кВт: " + temp + "\n" : "");
                    temp = createHistory(point.getVoltage(), oldPoint.getVoltage());
                    pointHistory = pointHistory + (!temp.equals("") ? "Класс напряжения: " + temp + "\n" : "");
                    temp = createHistory(point.getVoltageIn(), oldPoint.getVoltageIn());
                    pointHistory = pointHistory + (!temp.equals("") ?
                            "Уровень напряжения на вводе: " + temp + "\n" : "");
                    temp = createHistory(point.getSafety(), oldPoint.getSafety());
                    pointHistory = pointHistory + (!temp.equals("") ? "Категория надёжности: " + temp + "\n" : "");
                    if (!pointHistory.equals("")) {
                        pointHistory = "Для точки №: " + point.getNumber() + "\n" + pointHistory;
                    }
                }
            } else {
                pointHistory = "Добавлена точка №:" + point.getNumber() + "\n";
                pointHistory = pointHistory + "Мощность присоединяемая, кВт: " + point.getPowerDemand() + "\n";
                pointHistory = pointHistory + "Мощность ранее присоединённая, кВт: " + point.getPowerCurrent() + "\n";
                if(point.getVoltage()!=null)
                    pointHistory = pointHistory + "Класс напряжения: " + point.getVoltage().getName() + "\n";
                if(point.getVoltageIn()!=null)
                    pointHistory = pointHistory + "Уровень напряжения на вводе: "
                            + point.getVoltageIn().getName() + "\n";
                pointHistory = pointHistory + "Категория надёжности: " + point.getSafety().getName() + "\n";
            }
        }
        return pointHistory;
    }

    public String writeHistory(Expiration expiration) {
        String expirationHistory = "";
        String temp;
        if(expiration!=null) {
            if(expiration.getId()!=null) {
                if (expirationService.findById(expiration.getId()).isPresent()) {
                    Expiration oldExpiration = expirationService.findById(expiration.getId()).get();
                    temp = createHistory(expiration.getStep(), oldExpiration.getStep());
                    expirationHistory = expirationHistory + (!temp.equals("") ? "Этап/Очередь: " + temp + "\n" : "");
                    temp = createHistory(expiration.getPlanProject(), oldExpiration.getPlanProject());
                    expirationHistory = expirationHistory + (!temp.equals("") ?
                            "Срок проектирования: " + temp + "\n" : "");
                    temp = createHistory(expiration.getPlanUsage(), oldExpiration.getPlanUsage());
                    expirationHistory = expirationHistory + (!temp.equals("") ? "Срок ввода: " + temp + "\n" : "");
                    temp = createHistory(expiration.getPowerMax(), oldExpiration.getPowerMax());
                    expirationHistory = expirationHistory + (!temp.equals("") ? "Макс.мощность: " + temp + "\n" : "");
                    temp = createHistory(expiration.getSafety(), oldExpiration.getSafety());
                    expirationHistory = expirationHistory + (!temp.equals("") ? "Кат. надёж.: " + temp + "\n" : "");
                    if (!expirationHistory.equals("")) {
                        expirationHistory = "На Этапе/Очереде:" + expiration.getStep() + "\n" + expirationHistory;
                    }
                }
            } else {
                expirationHistory = "Добавлен этап/очередь: " + expiration.getStep() + "\n";
                expirationHistory = expirationHistory + "Срок проектирования: " + expiration.getPlanProject() + "\n";
                expirationHistory = expirationHistory + "Срок ввода: " + expiration.getPlanUsage() + "\n";
                expirationHistory = expirationHistory + "Макс.мощность: " + expiration.getPowerMax() + "\n";
                expirationHistory = expirationHistory + "Кат. надёж.: " + expiration.getSafety().getName() + "\n";
            }
        }
        return expirationHistory;
    }

    public String writeHistory(FileStored file) {
        String fileHistory = "";
        if(file!=null) {
            fileHistory = "Добавлен файл: " + file.getName() + "\n";
        }
        return fileHistory;
    }

    private String createHistory(String strNew, String strOld){
        String history = "";
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
        String history = "";
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
        String history = "";
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

    public <C> void saveHistory(Demand demand, C obj, Class<C> clazz) {
        History history = new History();
        String his = "";
        try {
            switch (obj.getClass().getSimpleName()) {
                case "FileStored" :
                    his = writeHistory((FileStored) obj);
                    break;
                case "Expiration" :
                    his = writeHistory((Expiration) obj);
                    break;
                case "Point" :
                    his = writeHistory((Point) obj);
                    break;
                case "Demand" :
                    his = writeHistory((Demand) obj);
                    break;
                default :
                    his = "";
            }
            history.setHistory(his.substring(0,his.length()-1));
        } catch (Exception e) {System.out.println(e.getMessage());}
        try {
            history.setDemand(demand);
            if(!history.getHistory().equals("")) {
                save(history);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

}
