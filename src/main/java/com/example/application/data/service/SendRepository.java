package com.example.application.data.service;

import com.example.application.data.entity.Send;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendRepository extends JpaRepository<Send, Long> {
}