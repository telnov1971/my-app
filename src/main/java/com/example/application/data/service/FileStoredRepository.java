package com.example.application.data.service;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.FileStored;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileStoredRepository extends JpaRepository<FileStored, Long> {
    List<FileStored> findAllByDemand(Demand demand);
}