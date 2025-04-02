package com.example.auto_ria.services.validation.auth;

import org.springframework.stereotype.Service;

import com.example.auto_ria.dao.auth.UserAuthDaoSQL;
import com.example.auto_ria.exceptions.token.InvalidTokenException;
import com.example.auto_ria.exceptions.token.TokenExpiredException;
import com.example.auto_ria.models.auth.AuthSQL;
import com.example.auto_ria.services.auth.JwtService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TokenValidationService {

    private UserAuthDaoSQL userAuthDaoSQL;
    private JwtService jwtService;

    private void validateTokenPresence(String token) {
        if (token == null) {
            throw new InvalidTokenException("Auth. token absent");
        }
    }

    private void isInDBRefresh(String token) {
        AuthSQL authObject = userAuthDaoSQL.findByRefreshToken(token);

        if (authObject == null) {
            throw new InvalidTokenException("Invalid auth. token");
        }
    }

    private void validateTokenExpiry(String token) {
        if (jwtService.isTokenExprired(token)) {
            throw new TokenExpiredException("Token is expired");
        }
    }

    public void validateRefreshToken(String token) {
        validateTokenPresence(token);
        validateTokenExpiry(token);
        isInDBRefresh(token);
    }
}
