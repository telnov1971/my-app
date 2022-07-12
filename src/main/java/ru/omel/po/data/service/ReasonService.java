package ru.omel.po.data.service;

import ru.omel.po.data.entity.Reason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class ReasonService extends CrudService<Reason,Long> {
    private final ReasonRepository reasonRepository;

    public ReasonService(ReasonRepository reasonRepository) {
        this.reasonRepository = reasonRepository;
    }

    @Override
    protected JpaRepository<Reason, Long> getRepository() {
        return reasonRepository;
    }

    public List<Reason> findAllByTemporal(Boolean temporal) {
        return reasonRepository.findAllByTemporal(temporal);
    }

  public List<Reason> findAll() {
      return reasonRepository.findAll();
  }
}
