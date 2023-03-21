package ru.omel.po.data.service;

import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.FileStored;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;
import java.util.Optional;

@Service
public class FileStoredService extends CrudService<FileStored,Long> {
    private final FileStoredRepository fileStoredRepository;

    public FileStoredService(FileStoredRepository fileStoredRepository) {
        this.fileStoredRepository = fileStoredRepository;
    }

    @Override
    protected JpaRepository<FileStored, Long> getRepository() {
        return fileStoredRepository;
    }

    public List<FileStored> findAllByDemand(Demand demand) {
        return fileStoredRepository.findAllByDemand(demand);
    }

    public Optional<FileStored> findByLink(String key) {
        return fileStoredRepository.findByLink(key);
    }
}
