package com.example.application.data.service;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.FileStored;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

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
}
