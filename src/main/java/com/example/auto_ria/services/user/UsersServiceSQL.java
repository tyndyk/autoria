package com.example.auto_ria.services.user;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.auto_ria.dao.users.UserDaoSQL;
import com.example.auto_ria.dto.requests.register.RegisterRequestUserAuthorityDTO;
import com.example.auto_ria.dto.requests.register.RegisterRequestUserDTO;
import com.example.auto_ria.dto.updateDTO.UserUpdateDTO;
import com.example.auto_ria.enums.ERole;
import com.example.auto_ria.exceptions.general.InternalServerException;
import com.example.auto_ria.exceptions.user.UserNotFoundException;
import com.example.auto_ria.mail.MailerService;
import com.example.auto_ria.models.responses.user.UserResponse;
import com.example.auto_ria.models.user.UserSQL;
import com.example.auto_ria.services.auth.PermissionService;
import com.example.auto_ria.services.validation.files.FileService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsersServiceSQL {

    private final UserDaoSQL userDaoSQL;
    private final MailerService mailer;
    private final PermissionService permissionService;
    private final PasswordEncoder passwordEncoder;
    private FileService fileService;

    public Page<UserResponse> getAll(int page, int pageSize) {
        checkPage(page, pageSize);
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<UserSQL> userSQLPage = userDaoSQL.findAll(pageable);
        Page<UserResponse> userResponsePage = userSQLPage.map(this::createUserResponse);

        return userResponsePage;
    }

    public UserSQL getById(int id) {
        UserSQL user = userDaoSQL.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user;
    }

    public UserResponse getByIdAsResponse(int id) {
        UserSQL userSQL = getById(id);
        permissionService.allowedToFetchUser(userSQL);
        return createUserResponse(userSQL);
    }

    public List<UserSQL> getListByRole(ERole role) {
        return userDaoSQL.findAllByRole(role);
    }

    public Optional<UserSQL> getByEmail(String email) {
        return Optional.ofNullable(userDaoSQL.findUserByEmail(email));
    }

    public Optional<UserSQL> getByNumber(String number) {
        return Optional.ofNullable(userDaoSQL.findUserByNumber(number));
    }

    public Integer countByRole(ERole role) {
        return userDaoSQL.countByRole(role);
    }

    public void deleteById(int id) {
        UserSQL userToDelete = getById(id);
        UserSQL userAuth = permissionService.validatePermissionToModifyUser(userToDelete);
        userDaoSQL.deleteById(id);
        fileService.deleteExistingAvatar(userToDelete);

        if (permissionService.isAuthorityBool(userAuth)) {
            mailer.handleEmail(
                    () -> mailer.sendAccountBannedEmail(userToDelete.getEmail(), userToDelete.getFullName()));
        }

        mailer.handleEmail(() -> mailer.sendPlatformLeaveEmail(userToDelete.getEmail(), userToDelete.getFullName()));
        SecurityContextHolder.clearContext();
    }

    public UserResponse updateUser(int id, UserUpdateDTO userDTO) {
        UserSQL userToPatch = getById(id);
        permissionService.validatePermissionToModifyUser(userToPatch);

        updateUserFields(userDTO, userToPatch);

        UserSQL updatedUser = userDaoSQL.save(userToPatch);
        return createUserResponse(updatedUser);
    }

    private void updateUserFields(UserUpdateDTO userDTO, UserSQL userToPatch) {
        try {
            for (Field field : userDTO.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object fieldValue = field.get(userDTO);

                if (fieldValue != null) {
                    switch (field.getName()) {
                        case "name" -> userToPatch.setName((String) fieldValue);
                        case "lastName" -> userToPatch.setLastName((String) fieldValue);
                        case "number" -> userToPatch.setNumber((String) fieldValue);
                        case "city" -> userToPatch.setCity((String) fieldValue);
                        case "region" -> userToPatch.setCountry((String) fieldValue);
                    }
                }
            }
        } catch (Exception e) {
            throw new InternalServerException("Failed to update user fields");
        }

    }

    public void updateAvatar(int id, MultipartFile avatar) {
        UserSQL userToPatch = getById(id);
        permissionService.validatePermissionToModifyUser(userToPatch);
        handleAvatarUpdate(userToPatch, avatar);
    }

    private void handleAvatarUpdate(UserSQL user, MultipartFile avatar) {
        fileService.deleteExistingAvatar(user);

        String newAvatar = fileService.uploadFile(avatar);
        user.setAvatar(newAvatar);
        userDaoSQL.save(user);
    }

    public UserSQL createAndSaveAccountUser(RegisterRequestUserDTO userDto, String avatar) {
        UserSQL user = UserSQL.builder()
                .name(userDto.getName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles(List.of(ERole.USER))
                .avatar(avatar)
                .city(userDto.getCity())
                .country(userDto.getRegion())
                .number(userDto.getNumber())
                .isActivated(false)
                .build();

        return userDaoSQL.save(user);
    }

    public UserSQL createAndSaveAccountForAuthority(RegisterRequestUserAuthorityDTO userDto, String avatar,
            ERole role) {
        UserSQL user = UserSQL.builder()
                .name(userDto.getName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles(List.of(role))
                .avatar(avatar)
                .isActivated(true)
                .build();

        return userDaoSQL.save(user);
    }

    public UserSQL save(UserSQL userSQL) {
        return userDaoSQL.save(userSQL);
    }

    private int checkPage(int page, int pageSize) {
        if (page < 0)
            throw new IllegalArgumentException("Page number cannot be negative");
        if (pageSize <= 0)
            throw new IllegalArgumentException("Page size must be a positive number");
        return Math.max(pageSize, 10);
    }

    public UserResponse createUserResponse(UserSQL userSQL) {
        return UserResponse.builder()
                .id(userSQL.getId())
                .name(userSQL.getName())
                .lastName(userSQL.getLastName())
                .country(userSQL.getCountry())
                .city(userSQL.getCity())
                .number(userSQL.getNumber())
                .avatar(userSQL.getAvatar())
                .createdAt(userSQL.getCreatedAt())
                .role(userSQL.getRoles())
                .build();
    }
}
