package com.example.auto_ria.services.auth;

import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.auto_ria.services.user.UsersServiceSQL;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersServiceSQL usersServiceMySQL;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usersServiceMySQL.getByEmail(email)
                .map(user -> {
                    Hibernate.initialize(user.getRoles());
                    return new org.springframework.security.core.userdetails.User(
                            user.getEmail(),
                            user.getPassword(),
                            user.isActivated(),
                            true,
                            true,
                            true,
                            user.getAuthorities()
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
