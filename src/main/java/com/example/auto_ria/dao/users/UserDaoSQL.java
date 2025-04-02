package com.example.auto_ria.dao.users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.auto_ria.enums.ERole;
import com.example.auto_ria.models.user.UserSQL;

public interface UserDaoSQL extends JpaRepository<UserSQL, Integer> {
    Optional<UserSQL> findByEmail(String email);

    @Query("SELECT p FROM UserSQL p WHERE p.email = :email")
    UserSQL findUserByEmail(@Param("email") String email);

    UserSQL findUserByNumber(String number);

    @Query("SELECT u FROM UserSQL u JOIN u.roles r WHERE r = :role")
    Page<UserSQL> findAllByRole(@Param("role") ERole role, Pageable pageable);

    @Query("SELECT u FROM UserSQL u JOIN u.roles r WHERE r = :role")
    List<UserSQL> findAllByRole(@Param("role") ERole role);

    @Query("SELECT COUNT(u) FROM UserSQL u JOIN u.roles r WHERE r = :role")
    Integer countByRole(@Param("role") ERole role);

    @Transactional
    void deleteAllByIsActivatedFalseAndCreatedAtBefore(LocalDateTime before);
}