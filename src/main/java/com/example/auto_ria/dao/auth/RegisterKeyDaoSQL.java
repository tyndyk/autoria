package com.example.auto_ria.dao.auth;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auto_ria.models.auth.RegisterKey;

public interface RegisterKeyDaoSQL extends JpaRepository<RegisterKey, Integer> {
    RegisterKey findByRegisterKey(String key);
    void deleteByRegisterKey(String key);
    void deleteAllByCreatedAtBefore(LocalDate time);

}
