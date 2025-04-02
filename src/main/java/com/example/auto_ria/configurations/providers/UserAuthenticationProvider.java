package com.example.auto_ria.configurations.providers;

import java.util.Collection;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.auto_ria.dao.users.UserDaoSQL;
import com.example.auto_ria.exceptions.auth.InvalidCredentialsException;
import com.example.auto_ria.models.user.UserSQL;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final UserDaoSQL userDaoSQL;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserSQL user = userDaoSQL.findByEmail(username)
                .orElseThrow(() -> new InvalidCredentialsException("Login or password is not valid"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Login or password is not valid");
        }

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        return new UsernamePasswordAuthenticationToken(username, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
