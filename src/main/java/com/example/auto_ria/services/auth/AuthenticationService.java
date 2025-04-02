package com.example.auto_ria.services.auth;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.auto_ria.configurations.providers.UserAuthenticationProvider;
import com.example.auto_ria.dao.auth.UserAuthDaoSQL;
import com.example.auto_ria.dto.requests.register.RegisterRequestUserAuthorityDTO;
import com.example.auto_ria.dto.requests.register.RegisterRequestUserDTO;
import com.example.auto_ria.enums.ERole;
import com.example.auto_ria.enums.ETokenRole;
import com.example.auto_ria.exceptions.auth.InvalidCredentialsException;
import com.example.auto_ria.exceptions.general.DatabaseOperationException;
import com.example.auto_ria.exceptions.token.InvalidTokenException;
import com.example.auto_ria.exceptions.user.UserAlreadyActivatedException;
import com.example.auto_ria.exceptions.user.UserNotFoundException;
import com.example.auto_ria.mail.MailerService;
import com.example.auto_ria.models.auth.AuthSQL;
import com.example.auto_ria.models.requests.LoginRequest;
import com.example.auto_ria.models.requests.RefreshRequest;
import com.example.auto_ria.models.responses.auth.AuthenticationResponse;
import com.example.auto_ria.models.user.UserSQL;
import com.example.auto_ria.services.user.UsersServiceSQL;
import com.example.auto_ria.services.validation.auth.AuthKeyValidationService;
import com.example.auto_ria.services.validation.auth.TokenValidationService;
import com.example.auto_ria.services.validation.files.FileService;
import com.example.auto_ria.services.validation.users.UserValidationService;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private UsersServiceSQL usersServiceMySQL;
    private UserAuthDaoSQL userAuthDaoSQL;
    private UserAuthenticationProvider userAuthenticationManager;
    private UserValidationService userValidationService;

    private JwtService jwtService;
    private AuthKeyService authKeyService;
    private AuthKeyValidationService authKeyValidationService;
    private TokenValidationService tokenValidationService;
    private PermissionService permissionService;

    private FileService fileService;
    private MailerService mailer;

    private PasswordEncoder passwordEncoder;

    @Transactional
    public String registerUser(RegisterRequestUserDTO userDto) {
        userValidationService.validateUser(userDto);
        String avatar = handleAvatarTransfer(userDto.getAvatar());
        UserSQL user = usersServiceMySQL.createAndSaveAccountUser(userDto, avatar);
        System.out.println("USER AFTER SAVED " + user);
        String activateToken = jwtService.createAndSaveActivationToken(user);
        mailer.handleEmail(() -> mailer.sendActivationKeyEmail(user.getEmail(), activateToken));
        return "Check your email for activation";
    }

    private ERole checkRegisterKey(RegisterRequestUserAuthorityDTO userDto) {
        Claims claims = jwtService.extractAllClaims(userDto.getCode(), ETokenRole.AUTHORITY_REGISTER);
        authKeyValidationService.checkRegisterKey(userDto.getCode(), userDto.getEmail(), ETokenRole.AUTHORITY_REGISTER,
                claims);
        String roleClaim = claims.get("role", String.class);
        if (roleClaim == null || roleClaim.isBlank()) {
            throw new IllegalArgumentException("Invalid token is invalid");
        }

        permissionService.isValidRole(roleClaim);
        return ERole.valueOf(roleClaim);
    }

    public AuthenticationResponse registerAuthority(RegisterRequestUserAuthorityDTO userDto) {
        userValidationService.handleUserByEmailPresence(userDto.getEmail());
        ERole role = checkRegisterKey(userDto);
        userValidationService.validatePassword(userDto.getPassword());
        String avatar = handleAvatarTransfer(userDto.getAvatar());
        UserSQL user = usersServiceMySQL.createAndSaveAccountForAuthority(userDto, avatar, role);

        authKeyService.deleteKey(userDto.getCode());

        mailer.handleEmail(() -> mailer.sendWelcomeEmail(user.getFullName(), user.getEmail()));
        return createAndSaveTokenPair(user, role);
    }

    private AuthenticationResponse generateTokens(UserSQL user) {
        ERole role = user.getFirstPriorityRole();

        Function<UserSQL, AuthenticationResponse> tokenGenerator;
        switch (role) {
            case MANAGER:
                tokenGenerator = jwtService::generateManagerTokenPair;
                break;
            case ADMIN:
                tokenGenerator = jwtService::generateAdminTokenPair;
                break;
            case USER:
            default:
                tokenGenerator = jwtService::generateUserTokenPair;
                break;
        }

        return tokenGenerator.apply(user);
    }

    private AuthenticationResponse createAndSaveTokenPair(UserSQL user, ERole role) {
        AuthenticationResponse authenticationResponse = generateTokens(user);

        try {
            userAuthDaoSQL.save(AuthSQL.builder()
                    .role(role)
                    .personId(user.getId())
                    .accessToken(authenticationResponse.getAccessToken())
                    .refreshToken(authenticationResponse.getRefreshToken())
                    .build());

        } catch (Exception e) {
            throw new DatabaseOperationException("Could not save authentication tokens:", e);
        }

        return authenticationResponse;
    }

    @Async
    private String handleAvatarTransfer(MultipartFile file) {
        if (file == null) {
            return null;
        }
        return fileService.uploadFile(file);

    }

    @Transactional
    public AuthenticationResponse activateUser(String code) {
        String emailToActivate = authKeyValidationService.checkActivationKey(code);
        Optional<UserSQL> userSQL = usersServiceMySQL.getByEmail(emailToActivate);
        System.out.println("ACTIVATE FROM DB " + userSQL);
        if (userSQL.isPresent()) {
            UserSQL user = userSQL.get();

            if (user.isActivated()) {
                throw new UserAlreadyActivatedException("User is already activated");
            }
            user.setActivated(true);
            usersServiceMySQL.save(user);
            System.out.println("ACTIVATE FROM DB AFTER ACTIVATE " + userSQL);

            authKeyService.deleteKey(code);
            mailer.handleEmail(() -> mailer.sendWelcomeEmail(user.getFullName(), user.getEmail()));
            return createAndSaveTokenPair(user, ERole.USER);
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    public String codeManager(String email) {
        userValidationService.handleUserByEmailPresence(email);
        String code = jwtService.generateRegistrationCode(email, ERole.MANAGER);
        mailer.handleEmail(() -> mailer.sendRegisterInviteEmail(email, ERole.MANAGER, code));
        authKeyService.saveKey(code);
        return "Email sent successfully to the provided email address.";
    }

    public String codeAdmin(String email) {
        userValidationService.handleUserByEmailPresence(email);
        String code = jwtService.generateRegistrationCode(email, ERole.ADMIN);
        mailer.handleEmail(() -> mailer.sendRegisterInviteEmail(email, ERole.ADMIN, code));

        authKeyService.saveKey(code);
        return "Email sent successfully to the provided email address.";

    }

    public AuthenticationResponse login(LoginRequest loginRequest) {

        Optional<UserSQL> userSQL = usersServiceMySQL.getByEmail(loginRequest.getEmail());

        UserSQL user = userSQL.orElseThrow(() -> {
            return new InvalidCredentialsException("Login or password is not valid");
        });

        userAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        return createAndSaveTokenPair(user, ERole.USER);
    }

    public AuthenticationResponse refresh(RefreshRequest refreshRequest) {

        String refreshToken = refreshRequest.getRefreshToken();
        tokenValidationService.validateRefreshToken(refreshToken);

        String email = jwtService.extractUsername(refreshToken);
        UserSQL user = usersServiceMySQL.getByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("User from token not found"));

        try {
            userAuthDaoSQL.deleteAllByRefreshToken(refreshToken);

            if (user.getRoles().contains(ERole.MANAGER))
                return createAndSaveTokenPair(user, ERole.MANAGER);
            if (user.getRoles().contains(ERole.ADMIN))
                return createAndSaveTokenPair(user, ERole.ADMIN);
            return createAndSaveTokenPair(user, ERole.USER);
        } catch (Exception e) {
            throw new DatabaseOperationException("Token refresh failed", e);
        }
    }

    public String forgotPassword(String email) {

        Optional<UserSQL> user = usersServiceMySQL.getByEmail(email);

        if (!user.isEmpty()) {
            String forgotPassToken = jwtService.createAndSaveForgotPassToken(user.get());
            mailer.handleEmail(() -> mailer.sendForgotPasswordEmail(user.get().getEmail(), forgotPassToken));
        }

        return "If the email is associated with an account, a password reset link has been sent.";
    }

    @Transactional
    public AuthenticationResponse changePassword(String newPassword) {

        UserSQL user = permissionService.getAuthenticatedUser();
        userAuthDaoSQL.deleteAllByPersonId(user.getId());
        handlePasswordChange(user, newPassword);
        return generateTokens(user);

    }

    private void handlePasswordChange(UserSQL user, String newPassword) {

        userValidationService.validatePassword(newPassword);
        String encoded = passwordEncoder.encode(newPassword);
        user.setPassword(encoded);
        usersServiceMySQL.save(user);
    }

    @Transactional
    public AuthenticationResponse resetPassword(String password, String code) {
        String email = authKeyValidationService.checkForgotPassKey(code);
        Optional<UserSQL> user = usersServiceMySQL.getByEmail(email);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User with this email does not exist");
        }

        handlePasswordChange(user.get(), password);

        userAuthDaoSQL.deleteAllByPersonId(user.get().getId());

        UserSQL userSQL = user.get();
        return generateTokens(userSQL);
    }

    public String signOut() {
        UserSQL userSQL = permissionService.getAuthenticatedUser();
        try {
            userAuthDaoSQL.deleteAllByPersonId(userSQL.getId());
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to sign out: ", e);
        }
        SecurityContextHolder.clearContext();
        return "Signed out";
    }

    public AuthSQL findByAccessToken(String accessToken) {
        return userAuthDaoSQL.findByAccessToken(accessToken);
    }
}
