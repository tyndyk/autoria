package com.example.auto_ria.services.auth;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.auto_ria.dao.auth.RegisterKeyDaoSQL;
import com.example.auto_ria.models.auth.RegisterKey;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthKeyService {

    private RegisterKeyDaoSQL registerKeyDaoSQL;

    public void saveKey(String key) {
        registerKeyDaoSQL.save(RegisterKey.builder().registerKey(key).build());
    }

    public void deleteKey(String key) {
        registerKeyDaoSQL.deleteByRegisterKey(key);
    }

    @Transactional
    public void deleteUnusedKeys(LocalDate time) {
        registerKeyDaoSQL.deleteAllByCreatedAtBefore(LocalDate.now().plusDays(1));
    }

    public RegisterKey findByRegisterKey(String key) {
        return registerKeyDaoSQL.findByRegisterKey(key);
    }
}
