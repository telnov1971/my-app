package com.example.application.data.service;

import com.example.application.data.entity.Expiration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpirationRepository extends JpaRepository<Expiration, Long> {
}