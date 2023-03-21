package ru.omel.po.data.service;

import ru.omel.po.data.entity.Safety;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafetyRepository extends JpaRepository<Safety, Long> {
}