package com.example.auto_ria.controllers.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.auto_ria.dto.requests.register.RegisterRequestUserAuthorityDTO;
import com.example.auto_ria.dto.requests.register.RegisterRequestUserDTO;
import com.example.auto_ria.dto.responces.ResponceObj;
import com.example.auto_ria.dto.responces.builder.ResponseBuilder;
import com.example.auto_ria.models.requests.LoginRequest;
import com.example.auto_ria.models.requests.RefreshRequest;
import com.example.auto_ria.models.responses.auth.AuthenticationResponse;
import com.example.auto_ria.services.auth.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Tag(name = "Authentication API", description = "Operations related to authentication")
public class AuthenticationController {

    private AuthenticationService authenticationService;

    @Operation(summary = "Register a new user", description = "Registers a new user to the system", responses = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<ResponceObj<String>> registerUser(
            @ModelAttribute @Valid RegisterRequestUserDTO registerRequestDTO) {
        return ResponseBuilder.buildResponse(authenticationService.registerUser(registerRequestDTO));
    }

    @Operation(summary = "Register a new authority", description = "Registers a new authority to the system", responses = {
            @ApiResponse(responseCode = "200", description = "Authority registered successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register-authority")
    public ResponseEntity<ResponceObj<AuthenticationResponse>> registerAuthority(
            @ModelAttribute @Valid RegisterRequestUserAuthorityDTO registerRequestDTO) {
        return ResponseBuilder.buildResponse(authenticationService.registerAuthority(registerRequestDTO));
    }

    @Operation(summary = "Activate a user", description = "Activate a user by providing a code", responses = {
            @ApiResponse(responseCode = "200", description = "User activated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid code"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/activate-user")
    public ResponseEntity<ResponceObj<AuthenticationResponse>> activateUser(
            @RequestParam("code") String code) {
        return ResponseBuilder.buildResponse(authenticationService.activateUser(code));
    }

    @Operation(summary = "Generate a code for a manager", description = "Generate an activation code for a manager by email", responses = {
            @ApiResponse(responseCode = "200", description = "Activation code sent"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid email or already in use"),
            @ApiResponse(responseCode = "404", description = "Manager not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/code-manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponceObj<String>> codeManager(
            @RequestParam("email") String email) {
        return ResponseBuilder.buildResponse(authenticationService.codeManager(email));
    }

    @Operation(summary = "Generate a code for an admin", description = "Generate an activation code for an admin by email", responses = {
            @ApiResponse(responseCode = "200", description = "Activation code sent"),
            @ApiResponse(responseCode = "400", description = "Invalid email or already in use"),
            @ApiResponse(responseCode = "404", description = "Admin not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/code-admin")
    public ResponseEntity<ResponceObj<String>> codeAdmin(
            @RequestParam("email") String email) {
        return ResponseBuilder.buildResponse(authenticationService.codeAdmin(email));
    }

    @Operation(summary = "Login the user", description = "Authenticate the user and generate a token", responses = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/authenticate")
    public ResponseEntity<ResponceObj<AuthenticationResponse>> loginAll(@RequestBody LoginRequest loginRequest) {
        return ResponseBuilder.buildResponse(authenticationService.login(loginRequest));
    }

    @Operation(summary = "Refresh the authentication token", description = "Refresh the user's authentication token", responses = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid refresh token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/refresh")
    public ResponseEntity<ResponceObj<AuthenticationResponse>> refreshAll(
            @RequestBody @Valid RefreshRequest refreshRequest) {
        return ResponseBuilder.buildResponse(authenticationService.refresh(refreshRequest));
    }

    @Operation(summary = "Change password", description = "Change the user's password", responses = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid password"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @PostMapping("/change-passwords")
    public ResponseEntity<ResponceObj<AuthenticationResponse>> changePassword(
            @RequestParam("newPassword") String newPassword) {
        return ResponseBuilder.buildResponse(authenticationService.changePassword(newPassword));
    }

    @Operation(summary = "Forgot password", description = "Request to reset the password by email", responses = {
            @ApiResponse(responseCode = "200", description = "Password reset link sent"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid email"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponceObj<String>> forgotPassword(
            @RequestParam("email") String email) {
        return ResponseBuilder.buildResponse(authenticationService.forgotPassword(email));
    }

    @Operation(summary = "Sign out the user", description = "Sign out the currently authenticated user", responses = {
            @ApiResponse(responseCode = "200", description = "User signed out successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @PostMapping("/sign-out")
    public ResponseEntity<ResponceObj<Object>> signOut() {
        return ResponseBuilder.buildResponse(authenticationService.signOut());
    }

    @Operation(summary = "Reset password", description = "Reset the user's password using the provided reset code", responses = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid reset code"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<ResponceObj<AuthenticationResponse>> resetPassword(
            @RequestParam("newPassword") String newPassword,
            HttpServletRequest request) {
        String code = request.getHeader("RESET_PASSWORD");
        return ResponseBuilder.buildResponse(authenticationService.resetPassword(newPassword, code));
    }
}
