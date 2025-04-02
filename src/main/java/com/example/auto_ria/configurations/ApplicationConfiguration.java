package com.example.auto_ria.configurations;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.client.RestTemplate;

import com.example.auto_ria.configurations.providers.UserAuthenticationProvider;
import com.example.auto_ria.dao.users.UserDaoSQL;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class ApplicationConfiguration {

    private UserDaoSQL userDAO;

    private UserAuthenticationProvider userAuthenticationProvider;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userDAO.findUserByEmail(username);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(
                List.of(userAuthenticationProvider));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
