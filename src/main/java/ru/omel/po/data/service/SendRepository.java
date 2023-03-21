package ru.omel.po.data.service;

import ru.omel.po.data.entity.Send;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendRepository extends JpaRepository<Send, Long> {
}