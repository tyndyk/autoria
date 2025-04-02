package com.example.auto_ria.services.validation.users;

import org.springframework.stereotype.Service;

import com.example.auto_ria.dto.requests.register.RegisterRequestUserDTO;
import com.example.auto_ria.exceptions.user.UserAlreadyExistsException;
import com.example.auto_ria.services.otherApi.CitiesService;
import com.example.auto_ria.services.user.UsersServiceSQL;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserValidationService {

    private CitiesService citiesService;
    private UsersServiceSQL usersService;

    private boolean isUserByEmailPresent(String email) {
        return usersService.getByEmail(email).isPresent();
    }

    private boolean isUserByNumberPresent(String number) {
        return usersService.getByNumber(number).isPresent();
    }

    public void handleUserByEmailPresence(String email) {
        if (isUserByEmailPresent(email)) {
            throw new UserAlreadyExistsException("email is already in use");
        }
    }

    private void handleUserByNumberPresence(String number) {
        if (isUserByNumberPresent(number))
            throw new UserAlreadyExistsException("number is already in use");
    }

    private void checkUniqueUserFields(String email, String number) {
        handleUserByEmailPresence(email);
        handleUserByNumberPresence(number);
    }

    public void validateUser(RegisterRequestUserDTO userDto) {
        citiesService.isCityInCountry(userDto.getCity(), userDto.getRegion());
        checkUniqueUserFields(userDto.getEmail(), userDto.getNumber());
        validatePassword(userDto.getPassword());
    }

    public void validatePassword(String password) {
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new IllegalArgumentException("Invalid password. Must contain: " +
                    "uppercase letter, lowercase letter, number, special character. At least 8 characters long");
        }
    }
}
