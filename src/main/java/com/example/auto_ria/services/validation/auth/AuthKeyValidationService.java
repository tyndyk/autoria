package com.example.auto_ria.services.validation.auth;

import org.springframework.stereotype.Service;

import com.example.auto_ria.enums.ETokenRole;
import com.example.auto_ria.exceptions.verification.InvalidCodeException;
import com.example.auto_ria.models.auth.RegisterKey;
import com.example.auto_ria.services.auth.AuthKeyService;
import com.example.auto_ria.services.auth.JwtService;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthKeyValidationService {

    private AuthKeyService authKeyService;
    private JwtService jwtService;

    private void validateKeyPresenceAndExpiry(String key) {
        if (key == null || key.length() == 0) {
            throw new InvalidCodeException("Auth. key is absent");
        }

        RegisterKey registerKey = authKeyService.findByRegisterKey(key);

        if (registerKey == null) {
            throw new InvalidCodeException("Invalid auth. key");
        }
        jwtService.isTokenExprired(key);
    }

    public void checkRegisterKey(String key, String email, ETokenRole role, Claims claims) {

        validateKeyPresenceAndExpiry(key);
        String tokenForEmail = checkOprationClaims(key, ETokenRole.AUTHORITY_REGISTER);
        if (!email.equals(tokenForEmail)) {
            throw new InvalidCodeException("This key was dedicated to another user");
        }
    }

    public String checkActivationKey(String key) {
        validateKeyPresenceAndExpiry(key);
        return checkOprationClaims(key, ETokenRole.USER_ACTIVATE);
    }

    private String checkOprationClaims(String key, ETokenRole role) {
        Claims claims = jwtService.extractAllClaims(key, role);
        String tokenOperation = claims.get("recognition").toString();
        String tokenForEmail = claims.get("email").toString();

        if (!ETokenRole.valueOf(tokenOperation).equals(role)) {
            throw new InvalidCodeException("Current key can not be used for this operation");
        }

        return tokenForEmail;
    }

    public String checkForgotPassKey(String key) {
        validateKeyPresenceAndExpiry(key);
        return checkOprationClaims(key, ETokenRole.FORGOT_PASSWORD);
    }
}
