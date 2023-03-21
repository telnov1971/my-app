package ru.omel.po.data.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.omel.po.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByActivationCode(String code);
    User findByEmail(String email);

    @Query("select u from User u " +
            "where (lower(u.username) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(u.fio) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(u.email) like lower(concat('%', :searchTerm, '%'))) "
        )
    List<User> search(@Param("searchTerm") String searchTerm);

    @Query("select u from User u")
    Page<User> findAllUsers(Pageable pageable);
//    @Query("select u from User u " +
//            "where u.active = :active " +
//            "order by u.username"
//    )
//    Page<User> findAllAndOrderBy(@Param("searchTerm") boolean active, Pageable pageable);
}