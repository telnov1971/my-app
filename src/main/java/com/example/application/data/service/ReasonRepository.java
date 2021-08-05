package com.example.application.data.service;

import com.example.application.data.entity.Demand;
import com.example.application.data.entity.Reason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReasonRepository extends JpaRepository<Reason, Long> {

    @Query("select d from Demand d " +
            "where (lower(d.object) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.address) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.demander) like lower(concat('%', :searchTerm, '%'))) " +
            "and d.garant.id=:garantId")
        // переданная строка используется как параметр в запросе
    List<Demand> search(@Param("searchTerm") String searchTerm,
                        @Param("garantId") Long garantId);

    @Query("select r from Reason r " +
            "where r.temporal=:temporal")
    List<Reason> findAllByTemporal(@Param("temporal") Boolean temporal);
}