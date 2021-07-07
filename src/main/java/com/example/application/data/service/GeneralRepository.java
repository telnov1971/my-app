package com.example.application.data.service;

import com.example.application.data.entity.General;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneralRepository extends JpaRepository<General, Long> {
}