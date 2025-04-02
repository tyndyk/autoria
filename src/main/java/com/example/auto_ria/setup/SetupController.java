package com.example.auto_ria.setup;

import java.util.Arrays;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auto_ria.dao.users.UserDaoSQL;
import com.example.auto_ria.dto.requests.register.RegisterRequestUserAuthorityDTO;
import com.example.auto_ria.enums.ERole;
import com.example.auto_ria.models.user.UserSQL;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Profile({ "dev", "staging" })
@RestController
@RequestMapping("/api/setup")
@AllArgsConstructor
public class SetupController {

    private final UserDaoSQL userDaoSQL;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/first-admin")
    public ResponseEntity<String> setupFirstAdmin(
            @Valid @RequestBody RegisterRequestUserAuthorityDTO firstAdminRequest) {
        if (userDaoSQL.countByRole(ERole.ADMIN) > 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("First admin has already been created.");
        }

        String encodedPassword = passwordEncoder.encode(firstAdminRequest.getPassword());

        UserSQL adminUser = new UserSQL();
        adminUser.setName(firstAdminRequest.getName());
        adminUser.setLastName(firstAdminRequest.getLastName());
        adminUser.setEmail(firstAdminRequest.getEmail());
        adminUser.setPassword(encodedPassword);
        adminUser.setRoles(Arrays.asList(ERole.ADMIN, ERole.ADMIN_ROOT));
        adminUser.setActivated(true);

        adminUser.setActivated(true);
        userDaoSQL.save(adminUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("First admin created successfully.");
    }
}
