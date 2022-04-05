package com.example.application.data.service;

import com.example.application.data.entity.Demand;

import com.example.application.data.entity.DemandType;
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
    // поиск всех заявок по клиенту
    Page<Demand> findAllByUser(User user, Pageable pageable);
    Page<Demand> findAllByDemandType(DemandType demandType, Pageable pageable);

    // поиск всех заявок для ГП и по типу заявки
    Page<Demand> findAllByGarantAndDemandType(Garant garant, DemandType demandType, Pageable pageable);
    Page<Demand> findAllByGarant(Garant garant, Pageable pageable);

    // поиск по тексту для ГП
    @Query("select d from Demand d " +
            "where (lower(d.object) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.address) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.demander) like lower(concat('%', :searchTerm, '%'))) " +
            "and d.garant.id=:garantId")
        // переданная строка используется как параметр в запросе
    List<Demand> search4Garant(@Param("searchTerm") String searchTerm,
                        @Param("garantId") Long garantId);

    // поиск по тексту
    @Query("select d from Demand d " +
            "where (lower(d.object) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.address) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.demander) like lower(concat('%', :searchTerm, '%'))) ")
        // переданная строка используется как параметр в запросе
    List<Demand> search(@Param("searchTerm") String searchTerm);

    // поиск по тексту и типу для ГП
    @Query("select d from Demand d " +
            "where (lower(d.object) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.address) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.demander) like lower(concat('%', :searchTerm, '%'))) " +
            "and d.garant.id=:garantId and d.demandType.id=:demandTypeId ")
        // переданная строка используется как параметр в запросе
    List<Demand> search4Garant(@Param("searchTerm") String searchTerm
            ,@Param("garantId") Long garantId
            ,@Param("demandTypeId") Long demandTypeId);

    // поиск по тексту и типу
    @Query("select d from Demand d " +
            "where (lower(d.object) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.address) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.demander) like lower(concat('%', :searchTerm, '%'))) " +
            " and d.demandType.id=:demandType")
        // переданная строка используется как параметр в запросе
    List<Demand> search(@Param("searchTerm") String searchTerm
            ,@Param("demandType") Long demandType);

    Optional<Demand> findByIdAndGarant(Long id, Garant garant);

    Optional<Demand> findByIdAndDemandType(Long id, DemandType demandType);

    Optional<Demand> findByIdAndUser(Long id, User user);
    Optional<Demand> findByIdAndUserAndDemandType(Long id, User user, DemandType demandType);

    Optional<Demand> findByIdAndGarantAndDemandType(Long id, Garant garant, DemandType demandType);

    // поиск по тексту и типу для клиента
    @Query("select d from Demand d " +
            "where (lower(d.object) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.address) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.demander) like lower(concat('%', :searchTerm, '%'))) " +
            "and d.user.id=:userId and d.demandType.id=:demandTypeId ")
    List<Demand> search4User(@Param("searchTerm") String searchTerm
            ,@Param("userId") Long userId
            ,@Param("demandTypeId") Long demandTypeId);
    // поиск по тексту для клиента
    @Query("select d from Demand d " +
            "where (lower(d.object) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.address) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(d.demander) like lower(concat('%', :searchTerm, '%'))) " +
            "and d.user.id=:userId")
    List<Demand> search4User(@Param("searchTerm") String searchTerm
            ,@Param("userId") Long userId);
}