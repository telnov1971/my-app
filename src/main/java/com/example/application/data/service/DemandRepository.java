package com.example.application.data.service;

import com.example.application.data.entity.Demand;

import com.example.application.data.entity.Garant;
import com.example.application.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DemandRepository extends JpaRepository<Demand, Long> {
    List<Demand> findByUser(User user);
    Page<Demand> findByUser(User user, Pageable pageable);

    Page<Demand> findAllByGarant(Garant garant, Pageable pageable);

    @Query("select d from Demand d " +
            "where (lower(d.object) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.address) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.demander) like lower(concat('%', :searchTerm, '%'))) " +
            "and d.garant.id=:garantId")
        // переданная строка используется как параметр в запросе
    List<Demand> search(@Param("searchTerm") String searchTerm,
                        @Param("garantId") Long garantId);

    @Query("select d from Demand d " +
            "where (lower(d.object) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.address) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.demander) like lower(concat('%', :searchTerm, '%'))) ")
        // переданная строка используется как параметр в запросе
    List<Demand> search(@Param("searchTerm") String searchTerm);

    Optional<Demand> findByIdAndGarant(Long id, Garant garant);
}